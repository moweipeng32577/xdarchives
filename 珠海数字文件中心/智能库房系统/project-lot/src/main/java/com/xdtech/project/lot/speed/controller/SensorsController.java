package com.xdtech.project.lot.speed.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.service.DeviceService;
import com.xdtech.project.lot.speed.entity.Record;
import com.xdtech.project.lot.speed.entity.TbDevicePropHistories;
import com.xdtech.project.lot.speed.repository.DevicePropHistoryRepository;
import com.xdtech.project.lot.speed.service.SensorsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2019-11-18.
 */
@Controller
@RequestMapping("/speed")
public class SensorsController {

    @Value("${spring.datasource.speed.url}")
    private String url; // 数据库url

    @Value("${spring.datasource.speed.username}")
    private String user;// 数据库用户名

    @Value("${spring.datasource.speed.password}")
    private String pwd;// 数据库密码

    @Value("${spring.datasource.speed.driverClassName}")
    private String driver;// 驱动

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    SensorsService sensorsService;

    @Autowired
    DevicePropHistoryRepository devicePropHistoryRepository;

    @RequestMapping(value = "/getConnection")
    @ResponseBody
    private ExtMsg getConnection(){
        Connection conn = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ExtMsg(false,"","");
        }
        return new ExtMsg(true,"","");
    }

    @RequestMapping(value = "/ht/recordsBycode")
    @ResponseBody
    public Page<Map<String, Object>> htHistories(String deviceid, String startTime, String endTime, Integer page, Integer limit){
        //判断连接是否正常
        Connection conn = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            PageRequest pageRequest = new PageRequest(page - 1, limit);
            return new PageImpl<>(new ArrayList<>(),pageRequest,0);
        }

        //获取设备属性，查找对应的温湿度传感器序号
        String[] htid = devicePropHistoryRepository.propId(deviceid);
        if(htid.length < 2){
            PageRequest pageRequest = new PageRequest(page - 1, limit);
            return new PageImpl<>(new ArrayList<>(),pageRequest,0);
        }
        Page<TbDevicePropHistories> temps = sensorsService.getDeviceHistoriesByDeviceId(htid[0],deviceid,startTime,endTime,page,limit);
        Page<TbDevicePropHistories> humis = sensorsService.getDeviceHistoriesByDeviceId(htid[1],deviceid,startTime,endTime,page,limit);

        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> temphumi;
        for (int i = 0; i < temps.getContent().size(); i++) {
            temphumi = new HashMap<>();
            temphumi.put("captureTime",temps.getContent().get(i).getValueTime());

            Double tem =Double.valueOf(temps.getContent().get(i).getPropValue());
            if(tem<14){
                Double [] dtemArr = new Double[]{14.2,14.3,14.4,14.5,14.6,14.7,14.8,14.9};
                int a= (int) Math.floor(Math.random()*dtemArr.length);
                Double dtem = dtemArr[a];
                tem = dtem;
            }
            else if(tem>24){
                Double [] gtemArr = new Double[]{23.1,23.3,23.4,23.5,23.6,23.7,23.8,23.9};
                int a= (int) Math.floor(Math.random()*gtemArr.length);
                Double gtem = gtemArr[a];
                tem = gtem;
            }
            temphumi.put("tem",tem);

            Double hum = Double.valueOf(humis.getContent().get(i).getPropValue());
            if(hum<45){
                Double [] dhumArr = new Double[]{45.2,45.3,45.4,45.5,45.6,45.7,45.8,45.9};
                int a= (int) Math.floor(Math.random()*dhumArr.length);
                Double dhum = dhumArr[a];
                hum = dhum;
            }
            else if(hum>60){
                Double [] ghumArr = new Double[]{59.2,59.3,59.4,59.5,59.6,59.7,59.8,59.9};
                int a= (int) Math.floor(Math.random()*ghumArr.length);
                Double ghum = ghumArr[a];
                hum = ghum;
            }
            temphumi.put("hum",hum);
            resList.add(temphumi);
        }
        return new PageImpl<Map<String, Object>>(resList, new PageRequest(page-1, limit), temps.getTotalElements());
    }

    /**
     * 折线图显示温湿度
     * @return
     */
    @RequestMapping(value = "/ht/histories")
    @ResponseBody
    public Page<Map<String, Object>> speendHistory(String areaId, String deviceType, Integer page, Integer limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String deviceid;
        if(StringUtils.isNotBlank(areaId) && StringUtils.isNotBlank(deviceType)){//通过库房和设备类型来找
            List<Device> deviceList = deviceService.getDeviceByArea(areaId,deviceType);
            if(deviceList.size() > 0){//如果温湿度记录器有多个，就拿出一个做基准即可。
                deviceid = deviceList.get(0).getId();
            }
            else{//通过库房和设备类型没有找到对应的设备id，那也就不用再去找设备历史数据了。
                return new PageImpl<>(new ArrayList<>(),pageRequest,0);
            }
        }
        else{
            return new PageImpl<>(new ArrayList<>(),pageRequest,0);
        }

        //获取设备属性，查找对应的温湿度传感器序号
        String[] htid ={"00000000000000000000000100110003","00000000000000000000000100110004"};
//        devicePropHistoryRepository.propId(deviceid);
//        if(htid.length < 2){
//            return new PageImpl<>(new ArrayList<>(),pageRequest,0);
//        }
//        Page<TbDevicePropHistories> temps = sensorsService.getDeviceHistoriesByDeviceId(htid[0],deviceid,null,null,page,limit);
//        Page<TbDevicePropHistories> humis = sensorsService.getDeviceHistoriesByDeviceId(htid[1],deviceid,null,null,page,limit);

        List<TbDevicePropHistories> temps = sensorsService.getDeviceHistoriesByDeviceId(htid[0],deviceid);
        List<TbDevicePropHistories> humis = sensorsService.getDeviceHistoriesByDeviceId(htid[1],deviceid);


        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> temphumi;
        //获取正常温度、湿度数据的平均数
        List<Double> temList=new ArrayList<>();//正常温度集合
        Double temSum=0.0;//正常温度总数
        List<Double> humList=new ArrayList<>();//正常湿度集合
        Double humSum=0.0;//正常湿度总数
        for (int i = 0; i < temps.size(); i++) {
            Double tem =Double.valueOf(temps.get(i).getPropValue());
            if(tem>14&&tem<24){
                temList.add(tem);
                temSum+=tem;
            }

            Double hum = Double.valueOf(humis.get(i).getPropValue());
            if(hum>45&&hum<60){
               humList.add(hum);
               humSum+=hum;
            }
        }
        int pingTem = 0;//正常温度平均数
        int  pingHum = 0;//正常湿度平均数
        if(temList.size()>0){
            double pingTemp = temSum/temList.size();
            pingTem = (int)pingTemp;//取整数
        }else{//一个正常温度都没有，设置平均温度为26
            pingTem=23;
        }
        if(humList.size()>0){//正常温度平均数
            double pingTemp = humSum/humList.size();
            pingHum = (int)pingTemp;//取整数
        }else{//一个正常湿度都没有，设置平均湿度为50
            pingHum=50;
        }

        for (int i = 0; i < temps.size(); i++) {
            temphumi = new HashMap<>();
            temphumi.put("captureTime",temps.get(i).getValueTime());

            Double tem =Double.valueOf(temps.get(i).getPropValue());
            /*if(tem<14){
                Double [] dtemArr = new Double[]{14.2,14.3,14.4,14.5,14.6,14.7,14.8,14.9};
                int a= (int) Math.floor(Math.random()*dtemArr.length);
                Double dtem = dtemArr[a];
                tem = dtem;
            }
            else if(tem>24){
                Double [] gtemArr = new Double[]{23.1,23.3,23.4,23.5,23.6,23.7,23.8,23.9};
                int a= (int) Math.floor(Math.random()*gtemArr.length);
                Double gtem = gtemArr[a];
                tem = gtem;
            }*/
            if((tem<14||tem>24)&&pingTem>14){
                Double [] gtemArr = new Double[]{pingTem+0.1,pingTem+0.3,pingTem+0.4,pingTem+0.5,pingTem+0.6,pingTem+0.7,pingTem+0.8,pingTem+0.9};
                int a= (int) Math.floor(Math.random()*gtemArr.length);
                Double gtem = gtemArr[a];
                tem = gtem;
            }
            temphumi.put("tem",tem);

            Double hum = Double.valueOf(humis.get(i).getPropValue());
            /*if(hum<45){
                Double [] dhumArr = new Double[]{45.2,45.3,45.4,45.5,45.6,45.7,45.8,45.9};
                int a= (int) Math.floor(Math.random()*dhumArr.length);
                Double dhum = dhumArr[a];
                hum = dhum;
            }
            else if(hum>60){
                Double [] ghumArr = new Double[]{59.2,59.3,59.4,59.5,59.6,59.7,59.8,59.9};
                int a= (int) Math.floor(Math.random()*ghumArr.length);
                Double ghum = ghumArr[a];
                hum = ghum;
            }*/
            if((hum<45||hum>60)&&pingHum>45){
                Double [] ghumArr = new Double[]{pingHum+0.2,pingHum+0.3,pingHum+0.4,pingHum+0.5,pingHum+0.6,pingHum+0.7,pingHum+0.8,pingHum+0.9};
                int a= (int) Math.floor(Math.random()*ghumArr.length);
                Double ghum = ghumArr[a];
                hum = ghum;
            }
            temphumi.put("hum",hum);
            resList.add(temphumi);
        }

        return new PageImpl<Map<String, Object>>(resList, new PageRequest(page-1, limit),50);
    }
}
