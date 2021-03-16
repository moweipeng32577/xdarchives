package com.xdtech.project.lot.device.controller;

import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;

//获取水浸警告消息
@Controller
@RequestMapping("/water")
public class DeviceWaterLeachingController {

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private DeviceWarningRepository deviceWarningRepository;

//    private Map<String,JSONObject> json;

    @RequestMapping("/deviceWarning") //返回水浸警告的分页数据
    @ResponseBody
    public Page<DeviceWarning> deviceWarning (String deviceid, String areaId, String deviceType,String ip,Integer port ,Integer code,
                                                   String startTime, String endTime, Integer page, Integer limit){
        //时间过滤的暂时没写。
        Pageable pageRequest = new PageRequest(page-1, limit);
        Page<DeviceWarning> deviceWarnings = deviceWarningRepository.findAllByWarningTypeOrderByCreateTime("水浸告警",pageRequest);
        return deviceWarnings;
        }










    public static void main(String[] args) {
//        //测试代码块
//        String a = "{\"code\": 92000,\"msg\": \"获取数据成功\",\"data\": {\"主机报警列表\": [{\"主机\": \"设备 1\",\"状态\": \"宕机\",\"最近检查\": \"2015-07-24 17:41:54\",\"持续时间\": \"2 天 1 时 8 分 36 秒\",\"尝试\": \"1/3\",\"抖动\": false,\"安排宕机\": false,\"启用主动检查\": true,\"启用被动检查\": true,\"启用通知\": true,\"已确认\": false,\"状态信息\": \"主机不可达(192.168.1.112)\"}],\"服务报警列表\": [{\"主机\": \"UPS-1\",\"主机状态\": \"运行\",\"主机 IP\": \"192.168.1.110\",\"服务\": \"输入电压\",\"服务 ID\": \"AC_IN\",\"状态\": \"警报\",\"最近检查\": \"2015-07-24 17:41:54\",\"持续时间\": \"0 天 0 时 8 分 36 秒\",\"尝试\": \"3/3\",\"抖动\": false,\"安排宕机\": false,\"启用主动检查\": true,\"启用被动检查\": true,\"启用通知\": true,\"已确认\": false,\"添加注释\": false,\"状态信息\": \"0V\"}]}}";
//        JSONObject message = JSON.parseObject(a);
//        List<DeviceWaterLeachingWarning> list = new ArrayList<DeviceWaterLeachingWarning>();
//        JSONArray warningMessageArray = message.getJSONObject("data").getJSONArray("服务报警列表");
//        for (int i = 0; i < warningMessageArray.size(); i++) {
//            JSONObject j = warningMessageArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//            DeviceWaterLeachingWarning deviceWaterLeachingWarning = new DeviceWaterLeachingWarning();
//            deviceWaterLeachingWarning.setHostName(j.get("主机").toString());
//            deviceWaterLeachingWarning.setStatus(j.get("状态").toString());
//            deviceWaterLeachingWarning.setCheckDate(j.get("最近检查").toString());
//            deviceWaterLeachingWarning.setCheckTime(j.get("持续时间").toString());
//            String flag= j.getBoolean("已确认")?"确认":"未确认";
//            deviceWaterLeachingWarning.setConfirmFlag(flag);
//            deviceWaterLeachingWarning.setConfirmFlag(flag);
//            list.add(deviceWaterLeachingWarning);
//        }
//        System.out.println(list.toString());
        String a = "a主机:水浸警告";
        String [] b = a.split(":");
        String c  = b [0];
        System.out.println(a.split(":")[0]);
    }

}

