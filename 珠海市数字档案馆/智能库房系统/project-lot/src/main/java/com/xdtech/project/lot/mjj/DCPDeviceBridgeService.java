package com.xdtech.project.lot.mjj;

import java.util.Map;
import java.util.Properties;

/**
 * 智能大厦设备桥接服务:采用每一个桥接服务实例对象与每一个物理设备一一对应的关系与设备进行通信，这样能
 */
abstract public class DCPDeviceBridgeService {

    /**
     * 智能大厦通信协议事件上送命令
     */
    public static final int EVENT_CMD = 0x0103;

    public static final int CYCLE_CMD = 0x0101;

    /**
     * <p>设备桥接服务主线程对象。</p>
     */
    protected Thread thread;

    /**
     * <p>用于控制设备桥接服务主线程结束的线程引用对象。</p>
     */
    private volatile Thread blinker;

    /**
     * <p>用于标识设备桥接服务实例是否正在初始化中。
     */
    private boolean initializing = true;

    private boolean running = false;

    /**
     * <p>设备通信服务实例引用。</p>
     */
    protected DCPService service;

    /**
     * <p>用于存储智能大厦设备信息对象。</p>
     */
    protected Map<String, Object> device;

    /**
     * <p>用于存储设备服务配置文件信息对象。</p>
     */
    protected Properties props;

    /**
     * <p>提供服务配置文件及智能大厦设备配置信息初始化服务。</p>
     *
     * @param service 设备通信服务实例引用；
     * @param props   服务配置文件；
     * @param device  智能大厦设备配置信息；
     */
    public void init(DCPService service, Properties props, Map<String, Object> device) {
        initializing = true;
        this.service = service;
        this.props = props;
        this.device = device;
    }

    /**
     * <p>启动设备桥接服务。</p>
     */
    public void start() {

        blinker = thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    //调用服务启动生命周期方法。
                    onStart();
                    initializing = false;
                    while (blinker == thread) {
                        try {
                            //根据配置文件的时间间隔循环调用，判断设备与硬件是否关闭或离线的方法，如果返回False正常调用服务回调生命周期方法。
                            if (!isShutdown()) {//在线
                                 onService();
                            } else {//离线
                                 onShutdown();
                            }

                        } catch (Exception e) {
                            onError(e);
                        }
                        Thread.sleep(Integer.parseInt(props.getProperty("dcp.service.device.waittime")) * 1000);//每隔20秒去Ping一下
                    }

                } catch (Exception e) {
                    onError(e);
                } finally {
                    try {
                        onStop();
                    } catch (Exception e) {
                        onError(e);
                    } finally {
                        running = false;
                    }
                }

            }
        });

        thread.start();
        running = true;
    }

    public boolean isInitializing() {
        return initializing;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * <p>停止设备桥接服务。</p>
     */
    public void stop() {

        try {
            if (thread != null && thread.isAlive()) {
                blinker = null;
            }
        } catch (Exception e) {
            onError(e);
        }
    }



    /**
     * <p>设备桥接服务初始周期：在设备桥接服务实例化时，回调该方法。</p>
     *
     * @throws Exception
     */
    abstract protected void onInit() throws Exception;

    /**
     * <p>设备桥接服务启动周期：在设备桥接服务线程启动后，首先会回调该方法。</p>
     *
     * @throws Exception
     */
    abstract protected void onStart() throws Exception;

    /**
     * <p>设备桥接服务回调周期：在设备桥接服务线程根据配置项dcp.service.device.waittime每个n秒后，循环回调该方法。
     * 该回调成功后，会根据返回值向智能大厦平台发送周期上送信息。</p>
     *
     * @return 周期上送消息
     * @throws Exception
     */
    abstract protected void onService() throws Exception;


    /**
     * <p>设备桥接服务停止周期：在设备桥接服务线程关闭后，会回调该方法。</p>
     *
     * @throws Exception
     */
    abstract protected void onStop() throws Exception;

    /**
     * <p>设备桥接服务关机周期：当{@link #isShutdown()} 方法，返回True时调用。</p>
     *
     * @return 周期上送消息
     * @throws Exception
     */
    abstract protected void onShutdown() throws Exception;

    /**
     * <p>设备桥接服务异常周期：在设备桥接服务各生命周期出现异常时，会回调该方法。该法不像其它生命周期的方法会抛出异常。</p>
     *
     * @param e
     */
    abstract protected void onError(Exception e);

    abstract protected boolean onChange(Map<String, Object> value) throws Exception;

    /**
     * <p>设备桥接服务判断与硬件是否关闭或已无法通信的方法，只有该方法返回False，{@link #onService()}方法才会被正常调用。</p>
     *
     * @return
     * @throws Exception
     */
    abstract protected boolean isShutdown() throws Exception;

    abstract protected MJJBridgeAPI getApi();

}
