package com.xdtech.project.lot.mjj;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.MJJStatus;
import com.xdtech.project.lot.device.service.DeviceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;

/**
 * 设备通信服务对象，该服务程序会根据配置文件所连接的智能大厦数据库读取某一类的设备信息（使用配置文件配置设备类型），
 * 之后根据设备列表，逐一实例化设备桥接服务对象（根据配置文件配置桥接对象实现类），然后启动设备桥接服务实例。同时该
 * 服务还会开启UDP端口与智能大厦平台进行通信。
 */
@Component
public class DCPService{
	
	private static final Logger logger = LoggerFactory.getLogger(DCPService.class);
	
	private static Properties properties;

	private final Map<String,DCPDeviceBridgeService> services = new HashMap<String, DCPDeviceBridgeService>();

	private boolean resultDelivery = false;

	static {
		properties = new Properties();
		try {
			properties.load(DCPService.class.getResourceAsStream("/mjj.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void createOrUpateServiceDeviceInfo(final String deviceid, final Map<String, Object> device){
		
		DCPDeviceBridgeService service = services.get(deviceid);
		if(service == null){
			logger.info("创建：[" + deviceid + "]桥接服务实例...");
			
			try {
				service = new DCPMJJBridgeService();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			try {
				service.init(this,properties, device);
				service.onInit();//创建设备桥接对象
				services.put((String) device.get("id"), service);
				
				logger.info("启动：[" + device.get("name") + "]桥接服务实例...");
				service.start();
				//每创建一个桥接服务实例间接为3秒
				Thread.sleep(3 * 1000);
			} catch (Exception e) {
				logger.error("启动: [" + device.get("name") + "]" +"出现("+ e.getMessage() +")异常");
			}
		} else if(!service.isInitializing()){
			try {
				if(service.onChange(device)){//判断设备信息是否有变更,如果有变就重新连接设备,实例化
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							DCPDeviceBridgeService service = services.get(deviceid);
							service.stop();
							
							while(service.isRunning()){
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									logger.error(e.getMessage(), e);
								}
							}
							try {
								service.init(DCPService.this,properties, device);
								service.onInit();
								
								services.put((String) device.get("id"), service);
								
								logger.info("重启：[" + device.get("name") + "]桥接服务实例，由于重要配置信息变更...");
								service.start();
							} catch (Exception e) {
								service.onError(e);
							}
							
						}
					}).start();
					
				} else {//根据onChange回调方式判断，不需要重启服务的，直接更新设备配置变量。
					logger.info("变更：[" + device.get("name") + "]桥接服务实例，配置信息...");
					service.device = device;
				}
			} catch (Exception e) {
				service.onError(e);
			}
		}
		
	}

	/**
	 * 打开密集架 返回传不等于-1这成功
	 * @param deviceId 设备id
	 * @param col 要打开的列
	 */
	public int open(String deviceId,int col) {
		DCPDeviceBridgeService service = services.get(deviceId);
		MJJBridgeAPI api = service.getApi();
		return api.open(col);
	}

	/**
	 * 关闭密集架 返回传不等于-1这成功
	 * @param deviceId
	 */
	public int close(String deviceId){
		DCPDeviceBridgeService service = services.get(deviceId);
		MJJBridgeAPI api = service.getApi();
		return api.close();
	}
	/**
	 * 密集架通风 返回传不等于-1这成功
	 * @param deviceId
	 */
	public int ventilate(String deviceId){
		DCPDeviceBridgeService service = services.get(deviceId);
		MJJBridgeAPI api = service.getApi();
		return api.ventilate();
	}

	/**
	 * 获取密集架当前状态信息
	 * @param deviceId 设备id号
	 * @return
	 */
	public List<MJJStatus> getStatus(String deviceId,Integer col){
		DCPDeviceBridgeService service = services.get(deviceId);
		MJJBridgeAPI api = service.getApi();
		List<MJJStatus> mjjStatuses = new ArrayList<>();
		if(col == null){//如果没有指定查询某一列就去查询所有列
			for (int i = 1; i <= api.getCols(); i++){
				this.getMJJStatusList(mjjStatuses, api, deviceId, i);
			}
		}else {
			this.getMJJStatusList(mjjStatuses, api, deviceId, col);
		}
		return mjjStatuses;
	}
	private void getMJJStatusList(List<MJJStatus> list,MJJBridgeAPI api,String deviceId,int col){
		int result = api.getColumnStatus(col);
		MJJStatus mjjStatus = new MJJStatus(deviceId,api.getCode(),col,result);
		list.add(mjjStatus);
	}


	/**
	 * 测试专用
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DCPService service = new DCPService();
		List<Device> devices = new ArrayList<>();
		Device de = new Device();
		de.setId("038c6861d7ab4bbf9565de8285f47bb2");
		de.setName("智能密集架");
		de.setProp("{ip:\"127.0.0.1\",port:80,cols:11,fix:7,code:01,layout:\"L\",minhum:45,mintem:14,maxhum:60,maxtem:24,version:\"new\"}");
		devices.add(de);
		List<Map<String,Object>> deviceList = new ArrayList<>();
		for (Device device : devices) {
			Map<String,Object> map = new HashMap<>();
			JSONObject jsonObject = JSONObject.parseObject(device.getProp());
			map.put("id", device.getId());//设备id
			map.put("name", device.getName());//设备名
			map.put("ip", jsonObject.getString("ip"));//ip地址
			map.put("port", jsonObject.getString("port"));//端口
			map.put("code", jsonObject.getString("code"));//库区
			map.put("cols", jsonObject.getString("cols"));//列数
			map.put("layout", jsonObject.getString("layout"));//库区
			deviceList.add(map);
		}
		//循环一次启动每个设备服务的独立线程。 55 08 00 02 06 82 01 67 3F 68
		for(Map<String, Object> device:deviceList){
			service.createOrUpateServiceDeviceInfo((String) device.get("id"),device);
		}
		//service.open("038c6861d7ab4bbf9565de8285f47bb2",1);//测试打开指定列
		//service.close("038c6861d7ab4bbf9565de8285f47bb2");//测试关闭所有列
		//service.ventilate("038c6861d7ab4bbf9565de8285f47bb2");
		service.getStatus("038c6861d7ab4bbf9565de8285f47bb2", 1);
	}

}
