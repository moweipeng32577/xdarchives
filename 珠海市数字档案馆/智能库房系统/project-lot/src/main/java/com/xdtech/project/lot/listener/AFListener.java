package com.xdtech.project.lot.listener;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import com.xdtech.project.lot.speed.entity.Record;
import com.xdtech.project.lot.speed.repository.RecordRepository;
import com.xdtech.project.lot.websocket.DeviceWebSocketService;
import com.xdtech.smsclient.SMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安防数据接口
 * 通过定时读取斯必得数据库获取红外传感器状态
 * 并结合当前布防状态，生成告警数据
 *
 * Created by Rong on 2019-11-20.
 */
//@WebListener
public class AFListener implements ServletContextListener {

    DeviceRepository deviceRepository;
    DeviceWarningRepository deviceWarningRepository;
    RecordRepository recordRepository;
    private DeviceWebSocketService deviceWebSocketService;
    private SMSService smsService;

    @Value("${sms.server.address}")
    private String smsAddress;//短信猫信息接收手机号码


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //注入Repository资源
        deviceRepository = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(DeviceRepository.class);
        deviceWarningRepository = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(DeviceWarningRepository.class);
        recordRepository = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(RecordRepository.class);
        deviceWebSocketService =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(DeviceWebSocketService.class);
        smsService = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(SMSService.class);

        //启动数据加载线程
        new Thread(new AFCheckListener()).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    class AFCheckListener implements Runnable {

        //重置上次检测时间
        private Date checktime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取安防传感器编号，及对应设备对象
        private Integer[] sensors = null;
        private Map<Integer, String> sensorDevice = new HashMap<>();
        Object lock =new Object();

        public AFCheckListener(){
            //获取所有红外传感器
            List<Device> devicelist = deviceRepository.findByType("AF");
            sensors = new Integer[devicelist.size()];
            for (int i = 0; i < devicelist.size(); i++) {
                Device device = devicelist.get(i);
                Map<String,Object> map = JSONObject.parseObject(device.getProp(), Map.class);
                sensors[i] = Integer.parseInt(map.get("sensors").toString());
                sensorDevice.put(sensors[i], device.getId());
            }
        }

        @Override
        public void run() {
            while(true){
                try{
                    //获取上次检测到现在的传感器告警运行记录
                    List<Record> records = recordRepository
                            .findAllBySensorIndexInAndTimeAfterAndMessageIsNotNull(sensors,sdf.format(checktime));
                    //更新检测时间
                    checktime = new Date();

                    //重新查询设备，主要是更新设备的即时布防状态
                    Map<String, Device> deviceMap = new HashMap<>();
                    List<Device> devicelist = deviceRepository.findByType("AF");
                    devicelist.stream().forEach(device -> {
                        deviceMap.put(device.getId(), device);
                    });
                    //根据运行记录构造告警信息
                    for(int i =0; i<records.size();i++) {
                        synchronized (lock){
                        Record record = records.get(i);
                        //只有在安防设备处于布防状态时插入告警数据
                        Device device = deviceMap.get(sensorDevice.get(record.getSensorIndex()));
                        if (device.getStatus() == Device.STATUS_PROTECTION) {
                            String maxCreatTime = deviceWarningRepository.findMaxWarningTimeByDeviceId(device.getId());
                            DeviceWarning dw = new DeviceWarning();
                            dw.setWarningType(DeviceWarning.AF_WARNING);
                            dw.setDevice(device);
                            dw.setWarningTime(record.getTime());
                            dw.setCreateTime(record.getTime());
                            dw.setStatus(2);
                            dw.setDescription("未允许通过");
                            try {
                                deviceWarningRepository.save(dw);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //推送大屏报警信息
                            deviceWebSocketService.sendAFStatus(device);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date endDate = sdf.parse(maxCreatTime);
                                Date nowDate = sdf.parse(record.getTime());

                                Calendar endcal = Calendar.getInstance();
                                endcal.setTime(endDate);
                                endcal.add(Calendar.MINUTE, 3);

                                boolean before = nowDate.before(endcal.getTime());
                                if (!before) {
                                    new Thread(new smsThread()).start();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    };

                    //5秒检测一次
                    Thread.sleep(5000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    class smsThread implements Runnable{
        public smsThread(){

        }
        @Override
        public void run() {
            try {
                String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                String a = smsService.SendSMS(smsAddress, "【增城档案馆】安防报警：3楼3号库房有入侵人员！！ " + datestr);
                System.out.print("=============================" + a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}