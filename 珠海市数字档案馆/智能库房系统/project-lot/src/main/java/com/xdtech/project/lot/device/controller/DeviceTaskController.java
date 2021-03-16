package com.xdtech.project.lot.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.titansoft.znkg.dcp.client.DCPClient;
import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.mjj.DCPService;
import com.xdtech.project.lot.request.MJJRequest;
import com.xdtech.project.lot.util.ResponseMsg;
import com.xdtech.project.lot.util.RetrofitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wujy 2019/09/23
 */
@Controller
@RequestMapping("/deviceTask")
public class DeviceTaskController {

    @Value("${system.mjjcontroler.path}")
    private String mjjPath;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    ZonesRepository zonesRepository;

    @Autowired
    DCPService dcpService;
    /**
     * 打开密集架电源
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/openPower")
    @ResponseBody
    public ExtMsg openPower(String deviceId,String version,int qNumber){
        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.openPower(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"打开电源成功",null);
        }
        return new ExtMsg(true,"打开电源失败",null);
    }

    /**
     * 关闭密集架电源
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/shutDown")
    @ResponseBody
    public ExtMsg shutDown(String deviceId,String version,int qNumber){
        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.shutDown(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"关闭电源成功",null);
        }
        return new ExtMsg(true,"关闭电源失败",null);
    }

    /**
     * 指定区号的密集架进入禁止状态，强制停止密集架的所有运动，并禁止其他运动控制操作
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/fobiddenExercise")
    @ResponseBody
    public ExtMsg fobiddenExercise(String deviceId,String version,int qNumber){
        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.fobiddenExercise(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"禁止移动成功",null);
        }
        return new ExtMsg(true,"禁止移动失败",null);
    }

    /**
     * 暂停指定区号的密集架的当前运动，不同于禁止操作，该操作不禁止其他运动控制操作。
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/stopExercise")
    @ResponseBody
    public ExtMsg stopExercise(String deviceId,String version,int qNumber){
        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.stopExercise(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"停止移动成功",null);
        }
        return new ExtMsg(true,"停止移动失败",null);
    }

    /**
     * 解除指定区号的密集架的禁止状态
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/cleanFobiddenExercise")
    @ResponseBody
    public ExtMsg cleanFobiddenExercise(String deviceId,String version,int qNumber){
        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.cleanFobiddenExercise(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"解除禁止成功",null);
        }
        return new ExtMsg(true,"解除禁止失败",null);
    }

    /**
     * 密集架通风
     * @param qNumber 区号
     * @return
     */
    @RequestMapping(value = "/ventilation")
    @ResponseBody
    public ExtMsg ventilation(String deviceId,String version,int qNumber){
        if("new".equals(version)){//新版密集架
            dcpService.ventilate(deviceId);
            return new ExtMsg(true,"通风成功",null);
        }

        //创建网络请求接口对象实例
        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
        //发送http请求,并返回数据
        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.ventilation(deviceId,qNumber));
        if(jsonResult!= null && jsonResult.isSuccess()){
            return new ExtMsg(true,"通风成功",null);
        }
        return new ExtMsg(true,"通风失败",null);
    }

    /**
     * 打开密集架的指定列
     * @param model_device_deviceCode 设备编码
     * @param model_value 操作命令
     * @return
     */
    @RequestMapping(value = "/openAssginColumn")
    @ResponseBody
    public ExtMsg openAssginColumn(String model_device_deviceCode,String model_value,String model_prop_code){
        DCPClient client = new DCPClient(mjjPath);
        Map map = new HashMap();
        map.put(model_prop_code,model_value);
        int i = client.updateProps(model_device_deviceCode,map);

//        if("new".equals(version)){//新版密集架
//            dcpService.open(deviceId, col);
//            return new ExtMsg(true,"打开列成功",null);
//        }
//        //创建网络请求接口对象实例
//        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
//        //发送http请求,并返回数据
//        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.openAssginColumn(deviceId,qNumber,col));

        if(i == 0){
            return new ExtMsg(true,"打开列成功",null);
        }
        return new ExtMsg(true,"打开列失败",null);
    }

    /**
     * 打开密集架的指定列
     * @param deviceid 设备编码
     * @param col 行
     * @return
     */
    @RequestMapping(value = "/openAssginColumnByDeviceId")
    @ResponseBody
    public ExtMsg openAssginColumnByDeviceId(String deviceid,String col){
        DCPClient client = new DCPClient(mjjPath);
        Map map = new HashMap();
        Device device = deviceRepository.findById(deviceid);
        JSONObject jsonObject = JSONObject.parseObject(device.getProp());
        String model_prop_code= "MJJ_CTRL";
        String layout =  jsonObject.getString("layout");
        String model_value =col + "-0000-"+layout ;

        map.put(model_prop_code,model_value);
        int i = client.updateProps(device.getCode(),map);

//        if("new".equals(version)){//新版密集架
//            dcpService.open(deviceId, col);
//            return new ExtMsg(true,"打开列成功",null);
//        }
//        //创建网络请求接口对象实例
//        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
//        //发送http请求,并返回数据
//        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.openAssginColumn(deviceId,qNumber,col));

        if(i == 0){
            return new ExtMsg(true,"打开列成功",null);
        }
        return new ExtMsg(true,"打开列失败",null);
    }

    /**
     * 打开密集架的指定列
     * @param zoneid 密集架ID
     * @param col 行
     * @return
     */
    @RequestMapping(value = "/openAssginColumnByZoneId")
    @ResponseBody
    public ExtMsg openAssginColumnByZoneId(String zoneid,String col){
        DCPClient client = new DCPClient(mjjPath);
        Map map = new HashMap();
        Zones zones = zonesRepository.findByZoneid(zoneid);
        Device device = deviceRepository.findById(zones.getDevice());
        JSONObject jsonObject = JSONObject.parseObject(device.getProp());
        String model_prop_code= "MJJ_CTRL";
        String layout =  jsonObject.getString("layout");
        String model_value =col + "-0000-"+layout ;

        map.put(model_prop_code,model_value);
        int i = client.updateProps(device.getCode(),map);

//        if("new".equals(version)){//新版密集架
//            dcpService.open(deviceId, col);
//            return new ExtMsg(true,"打开列成功",null);
//        }
//        //创建网络请求接口对象实例
//        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
//        //发送http请求,并返回数据
//        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.openAssginColumn(deviceId,qNumber,col));

        if(i == 0){
            return new ExtMsg(true,"打开列成功",null);
        }
        return new ExtMsg(true,"打开列失败",null);
    }

    /**
     * 闭合指定区号的密集架中所有已打开的通道
     * @param model_device_deviceCode 设备编码
     * @param model_value 操作命令
     * @return
     */
    @RequestMapping(value = "/closeColumn")
    @ResponseBody
    public ExtMsg closeColumn(String model_device_deviceCode,String model_value,String model_prop_code){

        DCPClient client = new DCPClient(mjjPath);
        Map map = new HashMap();
        map.put(model_prop_code,model_value);
        int i = client.updateProps(model_device_deviceCode,map);

//        if("new".equals(version)){//新版密集架
//            dcpService.close(deviceId);
//            return new ExtMsg(true,"关闭所有列成功",null);
//        }
//        //创建网络请求接口对象实例
//        MJJRequest mjjRequest = RetrofitUtil.getRetrofit().create(MJJRequest.class);
//        //发送http请求,并返回数据
//        ResponseMsg jsonResult = RetrofitUtil.getJsonResult(mjjRequest.closeColumn(deviceId,qNumber));
        if(i==0){
            return new ExtMsg(true,"关闭所有列成功",null);
        }
        return new ExtMsg(true,"关闭所有列失败",null);
    }

    /**
     * 查看指定区号的密集架运行状态
     * @param qNumber 区号
//     * @param outStr 返回查询结果内容 （状态查询命令返回参数<0xC0><0x03><区号><命令><当前状态代码><异常类型><当前温度>
     *               <当前湿度><用户IP,4字节><用户类型><状态参数><0xC0>）
     * @return
     */
    @RequestMapping(value = "/getStatus")
    @ResponseBody
    public ExtMsg getStatus(int qNumber){

        return null;
    }

}
