package com.xdtech.project.lot.mjj;

import com.xdtech.project.lot.mjj.message.MJJMessageContent;
import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.MJJMessagePackage;
import com.xdtech.project.lot.mjj.message.data.*;
import com.xdtech.project.lot.mjj.message.type.Int16;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.util.ToHexUtil;
import com.xdtech.project.lot.util.BinaryToHex;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MJJBridgeAPI {

    public static final int STORE_DIRECTION_LEFT = 0x01;//档案存放方向（左边）
    public static final int STORE_DIRECTION_RIGHT= 0x02;////档案存放方向（右边）

    public static final int LEFT = 0x01;//左移
    public static final int RIGHT = 0x02;//右移

    public static final int OPEN_BY_LEFT = 0x81; //固定列在左侧打开指令
    public static final int OPEN_BY_RIGHT = 0x82; //固定列在右侧打开指令
    public static final int CLOSE = 0x84; //关闭密集架
    public static final int VENTILATE  = 0x8F; //密集架通风
    public static final int COLUMN_STATUS  = 0x81; //密集架指定列状态
    public static final int MOVING  = 0x8C; //密集架开始移动
    public static final int END_MOVE  = 0x8B; //密集架移动到位
    public static final int WARN  = 0x89; //密集架告警


    private static final Logger logger = LoggerFactory.getLogger(MJJBridgeAPI.class);

    //根据密集架地址、端口的存储工厂，每存储一个开启一个处理线程
    private static final Map<InetSocketAddress, MJJBridgeAPIFactory> factories = new HashMap<InetSocketAddress, MJJBridgeAPIFactory>();

    private MJJBridgeAPIFactory factory;

    private Thread fetch_thread;//主动抓取线程

    private volatile Thread fetch_blinker;//主动抓取线程

    private MJJMessageData result;

    private boolean resultInit = false;

    private MJJReceiveProcess htProcess;//温湿度处理线程

    private MJJReceiveProcess statusProcess;

    private MJJReceiveProcess warnProcess;

    private MJJReceiveProcess moveProcess;

    private InetSocketAddress address;//密集架设备地址

    private int layout;//密集架移动方向

    private int code;//设备硬件码

    private int cols;//密集架列数

    private MJJBridgeAPI(MJJBridgeAPIFactory factory) {
        this.factory = factory;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public int getCode() {
        return code;
    }

    public int getCols() {
        return cols;
    }

    public int getLayout() {
        return layout;
    }

    public static MJJBridgeAPIFactory createFactory(String localhost, int port) {
        InetSocketAddress address = new InetSocketAddress(localhost, port);
        MJJBridgeAPIFactory factory = factories.get(address);

        if (factories.get(address) == null) {
            factory = new MJJBridgeAPIFactory(localhost, port);
            factories.put(address, factory);
        }

        return factory;
    }

    public static class MJJBridgeAPIFactory {

        private DatagramSocket socket;

        private final Set<MJJBridgeAPI> instances = new HashSet<MJJBridgeAPI>();

        private InetSocketAddress address;//密集架服务地址、端口信息

        private MJJBridgeAPIFactory(String localhost, int port) {
            this.address = new InetSocketAddress(localhost, port);
        }

        public MJJBridgeAPI getInstance() throws IOException {
            if (socket == null) {
                socket = new DatagramSocket(this.address);
                //此线程用来接收密集架传送过来的数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (socket != null) {

                            try {
                                byte[] buf = new byte[2048];
                                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                                socket.receive(dp);
                                InetSocketAddress remote = (InetSocketAddress) dp.getSocketAddress();
                                //处理多次消息合并发送的问题。
                                byte[] data = null;
                                int offset = 0;
                                do {
                                    if (new Int8(buf[offset]).toInt() == 0x55) {
                                        int len = new Int8(buf[offset + 1]).toInt();
                                        data = ArrayUtils.subarray(buf, offset, offset + len + 3);
                                        offset += len + 3;
                                        logger.info("接收到的指令:[" + BinaryToHex.binaryToHex(data) +"]");
                                        MJJMessagePackage msg = new MJJMessagePackage();
                                        msg.load(data);
                                        for (MJJBridgeAPI instance : instances) {
                                            if (remote.equals(instance.getAddress()) && msg.CONTENT.CODE.toInt() == instance.getCode()) {
                                                //读取密集架设备发送过来的数据
                                                instance.receive(msg.CONTENT);
                                            }
                                        }
                                    } else {
                                        data = null;
                                    }
                                } while (data != null && data.length > 0);

                            } catch (SocketException e) {
                                if ("socket closed".equals(e.getMessage())) {
                                    break;
                                } else {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }
            return new MJJBridgeAPI(this);
        }

        public void destroy() {
            instances.clear();
            socket.close();
            socket = null;
        }
    }

    /**
     * 数据接收总处理方法
     * @param content
     */
    private void receive(final MJJMessageContent content) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //发送指令给密集后回复结果
                if (result != null && content.CMD_KEY.toInt() == result.getCmdKey().toInt()) {
                    result.load(content.DATA.toBytes());
                    resultInit = true;
                }
                //处理温湿度数据
                if (content.CMD_KEY.toInt() == 0x0e && htProcess != null) {
                    htProcess.process(content);
                }else if ((content.CMD_KEY.toInt() == 0x8B || content.CMD_KEY.toInt() == 0x8C) && moveProcess != null) {
                    moveProcess.process(content);
                } else if (content.CMD_KEY.toInt() == 0x89 && warnProcess != null) {
                    warnProcess.process(content);
                }/*else if (content.CMD_KEY.toInt() == 0x03 && statusProcess != null) {
                    statusProcess.process(content);
                } */
            }
        }).start();
    }

    /**
     * 返回结果处理方法
     *
     * @param result
     * @return
     */
    private synchronized boolean waitForResult(MJJMessageData result) {
        this.result = result;
        this.resultInit = false;
        int timeout = 0;

        while (!resultInit && timeout < 3000) {
            try {
                Thread.sleep(100);
                timeout += 100;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.result = null;
        this.resultInit = false;

        return timeout < 3000;
    }

    /****************************密集架开启与关闭start************************/
    public synchronized void connect(String remote, int port, int code, int cols, int layout) {
        this.address = new InetSocketAddress(remote, port);
        this.code = code;
        this.cols = cols;
        this.layout = layout;

        synchronized (factory.instances) {
            factory.instances.add(this);
        }
    }

    public synchronized void disconnect() {

        synchronized (factory.instances) {

            factory.instances.remove(this);

            if (factory.instances.isEmpty()) {
                factory.socket.close();
                factory.socket = null;
            }
        }
    }

    public synchronized int open(int col) {
        return open(col, 1,1);
    }

    /**
     * 打开指定列密集架
     * @param col
     * @param layer
     * @param section
     * @return
     */
    public synchronized int open(int col, int layer,int section) {

        MJJOpenData data = new MJJOpenData();
        int ipos = MJJBridgeAPI.OPEN_BY_LEFT;//固定列为左侧
        if(getLayout() == MJJBridgeAPI.LEFT){//移动方向为左,固定列为右
            ipos = MJJBridgeAPI.OPEN_BY_RIGHT;//固定列为右侧
        }
        data.direction = new Int8(MJJBridgeAPI.STORE_DIRECTION_RIGHT);
        data.layer = new Int8(layer);
        data.section = new Int8(section);
        send(col,ipos, data);

        MJJOpenResultData result = new MJJOpenResultData();
        if (waitForResult(result)) {
            return result.IPOS.toInt();
        }
        return -1;
    }

    /**
     * 关闭密集架
     * @return
     */
    public synchronized int close() {
        send(getCols(),MJJBridgeAPI.CLOSE);
        MJJCloseResultData result = new MJJCloseResultData();
        if (waitForResult(result)) {//等待回复结果不超过3秒
            return result.IPOS.toInt();
        }
        return -1;
    }

    /**
     * 密集架通风
     */
    public  synchronized int ventilate(){
        send(getCols(), MJJBridgeAPI.VENTILATE);
        MJJVentilateResultData result = new MJJVentilateResultData();
        if(waitForResult(result)){
            return result.IPOS.toInt();
        }
        return -1;
    }

    /****************************密集架开启与关闭end************************/

    /**
     * 主动抓取
     *
     * @param millis
     */

    /*public void useFetchMode(final long millis) {

        if (fetch_thread != null && fetch_thread.isAlive()) {
            fetch_blinker = null;
        }

        fetch_blinker = fetch_thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (fetch_blinker == fetch_thread) {
                    //发送获取开启列请求；
                    send(getCols(), ipos, new MJJStatusData());

                    //发送获取温湿度请求；
                    send(getCols(), ipos, new MJJHTData());

                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

        fetch_thread.start();
    }*/

    /****************************主动抓取密集架列温湿度start************************/
    public synchronized double[] getHT() {
        MJJHTData data = new MJJHTData();

        send(getCols(), 0x8E, data);

        MJJHTResultData result = new MJJHTResultData();
        if (waitForResult(result)) {
            return new double[]{result.HUMI.toInt() / 10.0, result.TEMP.toInt() / 10.0};
        } else {
            return null;
        }
    }
    /****************************主动抓取密集架列温湿度end************************/

    /****************************主动抓取密集架列、区状态start************************/
    /*public synchronized int[] getStatus() {
        MJJStatusData data = new MJJStatusData();

        send(getCols(), ipos, data);

        MJJStatusResultData result = new MJJStatusResultData();
        if (waitForResult(result)) {
            int[] effects = new int[result.VALUES.length];
            for (int i = 0; i < effects.length; i++) {
                effects[i] = result.VALUES[i].toInt();
            }

            return effects;
        } else {
            return null;
        }


    }*/

    /**
     *获取指定列状态
     * @param col 列号
     * @return
     */
    public synchronized int getColumnStatus(int col) {
        send(col, MJJBridgeAPI.COLUMN_STATUS, null);
        MJJColumnStatusResultData result = new MJJColumnStatusResultData();
        if (waitForResult(result)) {
            return result.VALUE.toInt();
        } else {
            return -1;
        }
    }

    /****************************主动抓取密集架列、区状态end************************/

    /****************************设置温湿度、警告等线程start************************/

    public static interface MJJReceiveProcess {
        public void process(MJJMessageContent content);
    }

    public void onReceiveHT(MJJReceiveProcess process) {
        this.htProcess = process;
    }

    public void onReceiveStatus(MJJReceiveProcess process) {
        this.statusProcess = process;
    }

    public void onReceiveWarn(MJJReceiveProcess process) {
        this.warnProcess = process;
    }

    public void onReceiveMove(MJJReceiveProcess process) {
        this.moveProcess = process;
    }

    /****************************设置温湿度、警告等线程end************************/

    protected void send(int cols,int cmd){
        send(cols, cmd,null);
    }
    /**
     * 字节码发送
     * @param col
     * @param cmd
     * @param data
     */
    protected void send(int col, int cmd, MJJMessageData data) {
        MJJMessageContent content = new MJJMessageContent();

        content.CODE = new Int8(getCode());
        content.COL = new Int8(col);
        content.CMD_KEY = new Int8(cmd);
        content.DATA = data;

        MJJMessagePackage msg = new MJJMessagePackage();
        msg.CONTENT = content;
        byte[] bytes = msg.toBytes();
        logger.info("发送出去的指令:["+ ToHexUtil.bytetoHexUtil(bytes)+ "]");
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();

            DatagramPacket dpmessage = new DatagramPacket(bytes, bytes.length, getAddress());
            socket.send(dpmessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}
