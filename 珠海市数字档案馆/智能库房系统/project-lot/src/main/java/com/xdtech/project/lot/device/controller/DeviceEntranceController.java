package com.xdtech.project.lot.device.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.*;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.service.DeviceHistoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.text.ParseException;


//门禁controller
@Controller
@RequestMapping("/entrance")
//@PropertySource("classpath:application.properties")
public class DeviceEntranceController {

    @Autowired
    private DeviceRepository deviceRepository;

//    @Value("${spring.entrance.url}")
    private String entranceUri ;

    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    private DeviceHistoryService deviceHistoryService;

    @Resource
    private RestTemplate mjTemplate;
    //开门
    @RequestMapping("/open")
    @ResponseBody
    public boolean open(String deviceid) throws Exception {
        String num =deviceRepository.findById(deviceid).getProp();
        String openURI = entranceUri + "open?num=" + num;
        String result = mjTemplate.getForObject(openURI, String.class);
        return Boolean.parseBoolean(result);
    }
    //常开
    @RequestMapping("/NormalOpen")
    @ResponseBody
    public boolean NormalOpen (String deviceid) throws Exception{
        String num =deviceRepository.findById(deviceid).getProp();
        String NormalOpenURI = entranceUri + "permanently/open?num=" + num;
        String result = mjTemplate.getForObject(NormalOpenURI, String.class);
        return Boolean.parseBoolean(result);
    }
    //常关
    @RequestMapping("/NormalClose")
    @ResponseBody
    public boolean NormalClose (String deviceid) throws Exception{
        String num =deviceRepository.findById(deviceid).getProp();

        String NormalCloseURI = entranceUri + "permanently/close?num=" + num;

        String result = mjTemplate.getForObject(NormalCloseURI, String.class);
        return Boolean.parseBoolean(result);
    }

    @RequestMapping("/eventList")//存入门禁记录和读取门禁记录
    @ResponseBody
    Page<DeviceHistory> eventList(String deviceid, String areaId, String deviceType,String ip,Integer port ,Integer code,
                                  String startTime, String endTime, Integer page, Integer limit) throws ParseException {
//        先判断数据库有没有相关的记录，没有就执行插入操作。
        Device device =deviceRepository.findById(deviceid);
        String num = device.getProp();
        String doorName = device.getName();
        String eventURI = entranceUri + "event?num=" + num;  //获取门号
        String result = mjTemplate.getForObject(eventURI, String.class);
        if (StringUtils.isNotBlank(result)||!result.equals("[]")) {
            JSONArray json = JSONArray.parseArray(result);
            for (int i = 0; i < json.size(); i++) {
                JSONObject j = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                //對時間格式處理一下
                String d =j.getString("Date").substring(0,2);
                String m =j.getString("Date").substring(3,5);
                String y =j.getString("Date").substring(6,10);
                String time =j.getString("Date").substring(11);
                String resultTime = y+'-'+m+'-'+d+' '+time;
                //查询数据库，是否已经有了相关记录，直接根据记录时间进行查询
                DeviceHistory deviceHistory = deviceHistoryRepository.findDeviceHistoriesByCaptureTimeAndType(resultTime,"MJ");
                if(deviceHistory==null){
                    //{"operateMan":"陈林","operateType":"刷卡进入","door":"3号库房","Date":"2019-11-06 15:03"}
                    //不为null时，进行构造json结果
                    JSONObject jsonObject = new JSONObject(true);
                    jsonObject.put("operateMan", j.getString("Person"));
                    jsonObject.put("operateType",j.getString("Model"));
                    jsonObject.put("door",doorName);
                    String result1 = jsonObject.toString();
                    DeviceHistory entranceDeviceHistory = new DeviceHistory();
                    entranceDeviceHistory.setCaptureTime(resultTime);
                    entranceDeviceHistory.setCaptureValue(j.toJSONString());
                    entranceDeviceHistory.setType("MJ");
                    entranceDeviceHistory.setDevice(device);  //把设备id也记录一下
                    entranceDeviceHistory.setCaptureValue(result1);
                    deviceHistoryRepository.save(entranceDeviceHistory);
                }
            }
        }
        return  deviceHistoryService.getDeviceHistories(deviceid,deviceType,ip,port,code,startTime,endTime,page,limit);
    }


}
