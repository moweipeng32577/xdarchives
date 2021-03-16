package com.xdtech.project.lot.mjj;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.entity.MJJStatus;
import com.xdtech.project.lot.device.service.DeviceService;
import com.xdtech.project.lot.mjj.message.MJJMessageContent;
import com.xdtech.project.lot.mjj.message.data.MJJHTResultData;
import com.xdtech.project.lot.mjj.MJJBridgeAPI.MJJBridgeAPIFactory;
import com.xdtech.project.lot.mjj.message.data.MJJMoveResultData;
import com.xdtech.project.lot.mjj.message.data.MJJWarnResultData;
import com.xdtech.project.lot.util.SpringContextUtil;
import com.xdtech.project.lot.websocket.DeviceWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现了基于智能密集架通信的设备桥接服务。
 */
public class DCPMJJBridgeService extends DCPDeviceBridgeService {

    private static final Logger logger = LoggerFactory.getLogger(DCPMJJBridgeService.class);

    private static final Map<String, Map<String, Map<String, Integer>>> kf_ht = new HashMap<String, Map<String, Map<String, Integer>>>();

    private static MJJBridgeAPIFactory factory;

    private MJJBridgeAPI api;

    private byte[] cols = new byte[0];

    private byte[] lastOpen = new byte[2];

    private DeviceService deviceService;
    private DeviceWebSocketService deviceWebSocketService;

    public DCPMJJBridgeService(){
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        deviceService = applicationContext.getBean(DeviceService.class);
        deviceWebSocketService = applicationContext.getBean(DeviceWebSocketService.class);
    }

    /**
     * 初始化设备桥接对象
     * @throws Exception
     */
    @Override
    protected void onInit() throws Exception {
        if (factory == null) {
            factory = MJJBridgeAPI.createFactory(props.getProperty("dcp.service.device.mjj.address"),
                    Integer.parseInt(props.getProperty("dcp.service.device.mjj.port")));
        }

        logger.info("加载：[" + device.get("name") + "]\t" + device.get("ip") + ":" + device.get("port")
                + "/" + device.get("id"));
    }

    @Override
    protected boolean isShutdown() throws Exception {
        //使用Ping命令判断密集架硬件是否在线，可以正常访问与接收消息。
        return !InetAddress.getByName((String) device.get("ip")).isReachable(5000);
    }

    @Override
    protected void onStart() throws Exception {

        api = factory.getInstance();
        api.connect((String) device.get("ip"),(Integer) device.get("port"), (Integer) device.get("code"),
                (Integer) device.get("cols"),
                "L".equals((String) device.get("layout")) ? MJJBridgeAPI.LEFT : MJJBridgeAPI.RIGHT);

/*        //如果密集架自动上送过慢时，可以使用主动抓取模式，按要求请求上送数据。
        if ("1".equals(props.getProperty("dcp.service.device.mjj.fetch.mode"))) {
            api.useFetchMode(Integer.parseInt(props.getProperty("dcp.service.device.mjj.fetch.frequency")) * 1000l);
        }*/

        //处理密集架硬件状态数据上报；
        /*api.onReceiveStatus(new MJJBridgeAPI.MJJReceiveProcess() {
            @Override
            public void process(MJJMessageContent content) {
                MJJStatusResultData rs = new MJJStatusResultData();
                rs.load(content.DATA.toBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    //处理列左布局情况，该情况下列的打开均计算至左列右打开的情况。
                    if ("L".equals((String) device.get("layout"))) {
                        for (int i = 0; i < rs.VALUES.length - 1; i++) {
                            if (rs.VALUES[i].toInt() > 1 && rs.VALUES[i].toInt() < 4) {
                                if ((byte) (i + 2) == lastOpen[1] && (byte) MJJBridgeAPI.LEFT == lastOpen[0]) {
                                    output.write(lastOpen);
                                } else {
                                    output.write(new byte[]{(byte) MJJBridgeAPI.RIGHT, (byte) (i + 1)});
                                }
                            }
                        }
                    } else { //处理列右布局情况，该情况下列的打开均计算至右列左打开的情况。
                        for (int i = 0; i < rs.VALUES.length - 1; i++) {
                            if (rs.VALUES[i].toInt() > 1 && rs.VALUES[i].toInt() < 4) {
                                if ((byte) i == lastOpen[1] && (byte) MJJBridgeAPI.RIGHT == lastOpen[0]) {
                                    output.write(lastOpen);
                                } else {
                                    output.write(new byte[]{(byte) MJJBridgeAPI.LEFT, (byte) (i + 1)});
                                }
                            }
                        }
                    }

                    cols = output.toByteArray();
                    output.close();
                } catch (Exception e) {

                }
            }
        });*/

        //处理密集架温湿度数据上报；
        api.onReceiveHT(new MJJBridgeAPI.MJJReceiveProcess() {

            @Override
            public void process(MJJMessageContent content) {

                MJJHTResultData ht = new MJJHTResultData();
                ht.load(content.DATA.toBytes());

                logger.info("[" + (String) device.get("name") + "]\t硬件温度：" + (ht.TEMP.toInt() / 10.0) + ",硬件湿度：" + (ht.HUMI.toInt() / 10.0));

                double min_mh = (device.get("minhum") == null || "".equals(String.valueOf(device.get("minhum")))) ? 0 : Double.parseDouble(String.valueOf(device.get("minhum")));
                double min_mt = (device.get("mintem") == null || "".equals(String.valueOf(device.get("mintem")))) ? 0 : Double.parseDouble(String.valueOf(device.get("mintem")));
                double max_mh = (device.get("maxhum") == null || "".equals(String.valueOf(device.get("maxhum")))) ? 0 : Double.parseDouble(String.valueOf(device.get("maxhum")));
                double max_mt = (device.get("maxtem") == null || "".equals(String.valueOf(device.get("maxtem")))) ? 0 : Double.parseDouble(String.valueOf(device.get("maxtem")));

                int temp = ht.TEMP.toInt();
                int humi = ht.HUMI.toInt();

                //处理温湿度报警信息
                String deviceId = (String)device.get("id");
                if ((humi / 10.0) < min_mh) {//湿度过低
                    deviceService.saveWarning(deviceId, DeviceWarning.HUMI_OVERLOW + "["+(humi/10.0)+"]");
                }else if(humi / 10.0 > max_mh){//湿度过高
                    deviceService.saveWarning(deviceId, DeviceWarning.HUMI_OVERTOP + "["+(humi/10.0)+"]");
                }
                if ((temp / 10.0) < min_mt) {//温度过低
                    deviceService.saveWarning(deviceId, DeviceWarning.TEMP_OVERLOW + "["+(temp/10.0)+"]");
                }else if(temp / 10.0 > max_mt){//温度过高
                    deviceService.saveWarning(deviceId, DeviceWarning.TEMP_OVERTOP + "["+(temp/10.0)+"]");
                }
                //保存温湿度数据
                if(humi != 0 && temp != 0){
                    String captureValue = "{\"hum:\""+(humi/10.0)+",\"tem:\""+(temp/10.0)+"}";
                    deviceService.saveHistory(deviceId,captureValue, DeviceHistory.HISTORY_TYPE);
                }
            }
        });

        //处理密集架移动状态结果(正在移动或者移动到位)
        api.onReceiveMove(new MJJBridgeAPI.MJJReceiveProcess() {
            @Override
            public void process(MJJMessageContent content) {
                MJJMoveResultData data = new MJJMoveResultData();
                data.load(content.DATA.toBytes());
                MJJStatus mjjStatus = new MJJStatus((String)device.get("id"),content.CODE.toInt(),content.COL.toInt(),data.STATUS.toInt());
                if(content.CMD_KEY.toInt() == MJJBridgeAPI.END_MOVE){//是移动到位回复的指令
                    mjjStatus.setTag(MJJStatus.ENDMOVE);
                }else if(content.CMD_KEY.toInt() == MJJBridgeAPI.MOVING){//是正在移动回复的指令
                    mjjStatus.setTag(MJJStatus.MOVING);
                }
                //把移动消息推送给大屏app
                deviceWebSocketService.sendDevicesStatus(JSONArray.toJSON(JSON.toJSON(mjjStatus)).toString());
            }
        });
        //获取密集架告警信息
        api.onReceiveWarn(new MJJBridgeAPI.MJJReceiveProcess() {
            @Override
            public void process(MJJMessageContent content) {
                MJJWarnResultData warn = new MJJWarnResultData();
                warn.load(content.DATA.toBytes());
                MJJStatus mjjStatus = new MJJStatus((String)device.get("id"),
                        content.CODE.toInt(),content.COL.toInt(),warn.VALUE.toInt(),MJJStatus.WARN);
                //把告警消息推送给大屏app
                deviceWebSocketService.sendDevicesStatus(JSONArray.toJSON(JSON.toJSON(mjjStatus)).toString());
            }
        });
    }

    @Override
    protected void onStop() throws Exception {
        api.disconnect();
    }

    @Override
    protected void onService() throws Exception {
        changeDeviceStatus(Device.STATUS_ONLINE);
    }

    @Override
    protected void onShutdown() throws Exception {
        changeDeviceStatus(Device.STATUS_OFFLINE);
    }

    private void changeDeviceStatus(int status){
        int deviceStatus = (int)device.get("status");
        if(status!= deviceStatus){//当前设备状态与前一状态不同时就去修改数据库
            Device result = deviceService.updateDeviceStatus((String) device.get("id"),status);
            if(result != null){
                device.put("status", status);
            }
        }
    }

    @Override
    protected void onError(Exception e) {
        e.printStackTrace();
    }

    @Override
    protected boolean onChange(Map<String, Object> value) throws Exception {
        logger.info("接收：新设备配置" + value);
        return !device.get("ip").equals(value.get("ip")) ||
                !device.get("port").equals(value.get("port")) ||
                !device.get("code").equals(value.get("code")) ||
                !device.get("cols").equals(value.get("cols")) ||
                !device.get("layout").equals(value.get("layout"));
    }

    public MJJBridgeAPI getApi() {
        return api;
    }

}