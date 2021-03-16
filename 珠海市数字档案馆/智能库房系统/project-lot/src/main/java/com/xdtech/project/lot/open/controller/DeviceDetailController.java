package com.xdtech.project.lot.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.repository.BorrowRepository;
import com.xdtech.component.storeroom.service.ShelvesService;
import com.xdtech.component.storeroom.service.StorageService;
import com.xdtech.project.lot.device.controller.DeviceTaskController;
import com.xdtech.project.lot.device.entity.*;
import com.xdtech.project.lot.device.repository.DeviceOperateRecordRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.service.DeviceHistoryService;
import com.xdtech.project.lot.device.service.DeviceService;
import com.xdtech.project.lot.mjj.DCPService;
import com.xdtech.project.lot.speed.controller.SensorsController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wujy 2019/10/09
 * 免登录验证接口，可跨域
 */
@CrossOrigin
@Controller
@RequestMapping("/deviceDetail")
public class DeviceDetailController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceHistoryService deviceHistoryService;

    @Autowired
    DeviceOperateRecordRepository deviceOperateRecordRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    ShelvesService shelvesService;

    @Autowired
    DCPService dcpService;

    @Autowired
    DeviceTaskController deviceTaskController;

    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    SensorsController sensorsController;

    //获取温湿度历史数据（失效）
    @RequestMapping(value="/HThistory",method = RequestMethod.GET)
    @ResponseBody
    public IndexMsg getHThistory(String deviceid, String type, int page, int limit) {
        Page<DeviceHistory> HTpage = deviceHistoryService.getHThistory(type,page,limit);
        List<DeviceHistory> HTlist = HTpage.getContent();
        List<Map<String,String>> returnlist = new ArrayList();

        for(DeviceHistory device : HTlist){
            JSONObject jsonObject = JSONObject.parseObject(device.getCaptureValue());
            String tem =  jsonObject.getString("tem");
            String hum =  jsonObject.getString("hum");
            Map map = new HashMap();
            map.put("time", device.getCaptureTime());
            map.put("tem",tem);
            map.put("hum",hum);
            returnlist.add(map);
        }
        return new IndexMsg(true, "0", String.valueOf(HTpage.getTotalElements()),"成功", returnlist);
    }

    //layui table获取门禁历史数据
    @RequestMapping(value="/Accesshistory",method = RequestMethod.GET)
    @ResponseBody
    public IndexMsg getAccesshistory( String type, int page, int limit) {
        Page<DeviceHistory> Accesspage = deviceHistoryService.getHThistory(type,page,limit);
        List<DeviceHistory> list = Accesspage.getContent();
        List<Map<String,String>> returnlist = new ArrayList();

        for(DeviceHistory device : list){
            JSONObject jsonObject = JSONObject.parseObject(device.getCaptureValue());
            String operateMan =  jsonObject.getString("operateMan");
            String operateType =  jsonObject.getString("operateType");
            String door =  jsonObject.getString("door");
            Map map = new HashMap();
            map.put("time", device.getCaptureTime());
            map.put("operateMan",operateMan);
            map.put("operateType",operateType);
            map.put("door",door);
            returnlist.add(map);
        }
        return new IndexMsg(true, "0", String.valueOf(Accesspage.getTotalElements()),"成功", returnlist);
    }

    /*档案分类存量信息
      * @param storeRoomCode 库号
    */
    @RequestMapping(value="/archiveAllType",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> archiveAllType(String ip,Integer port,Integer code) {
       return shelvesService.classifyAndUserCapacity(ip,port,code);
    }

    /**
     * 获取温湿度数据（接口兼容智能馆库后台管理系统，大屏app，密集架固定列屏，注：deviceid适合后台管理系统用，
     * areaid和deviceType适合屏app,ip、port和code适合固定列屏蔽，但也不是绝对的）
     * @param deviceid 温湿度设备id
    * @param areaId 当没有直接把温湿度设备id传过来而是把库房传过来时，就通过areaid去查找到对应的deviceid
     * @param deviceType 设备类型
     * @param ip 密集架ip地址
     * @param port 密集架端口
     * @param code 密集架区号
     * @return
     */
    @RequestMapping(value = "/histories",method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceHistory> histories(String deviceid, String areaId, String deviceType, String ip, Integer port , Integer code,
                                         String startTime, String endTime, Integer page, Integer limit) {
        PageRequest pageRequest = new PageRequest(page-1, limit);
        if(StringUtils.isNotBlank(areaId) && StringUtils.isNotBlank(deviceType)){//通过库房和设备类型来找
            List<Device> deviceList = deviceService.getDeviceByArea(areaId,deviceType);
            if(deviceList.size() > 0){//如果温湿度记录器有多个，就拿出一个做基准即可。
                deviceid = deviceList.get(0).getId();
            }else{//通过库房和设备类型没有找到对应的设备id，那也就不用再去找设备历史数据了。
                return new PageImpl<>(new ArrayList<>(),pageRequest,0);
            }
        }
        return deviceHistoryService.getDeviceHistories(deviceid,deviceType,ip,port,code,startTime,endTime,page,limit);
    }

    /**
     * 获取设备运行记录
     * @return
     */
    @RequestMapping(value = "/getRecords",method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceOperateRecord> getRecords(int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit,new Sort(Sort.Direction.DESC,"operateTime"));
        return deviceOperateRecordRepository.findAll(pageRequest);
    }

    /**
     * 获取设备运行情况
     * @return
     */
    @RequestMapping(value = "/getDevices",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getdevices(){
        //查找在线设备
        List<Object> deviceOnStatus = deviceRepository.findDeviceOnStatus();
        //查找离线设备
        List<Object> deviceOffStatus = deviceRepository.findDeviceOffStatus();
        List<Map<String,Object>> deviceStatusList = new ArrayList<>();
        for (Object onStatus : deviceOnStatus) {
            Map<String,Object> deviceStatusMap = new HashMap<>();
            Object[] onObj = (Object[]) onStatus;
            String onType = (String)onObj[0];//设备类型
            long onCount = (long)onObj[1];//同一类型设备在线数量
            String typeName = (String)onObj[2];//设备类型名
            deviceStatusMap.put("type", onType);
            deviceStatusMap.put("onCount", onCount);
            deviceStatusMap.put("name", typeName);
            deviceStatusMap.put("offCount", 0);
            //以下为查找在线设备中是否还包含离线设备，如果有就与在线合并成一条数据。
            for (Object offStatus : deviceOffStatus) {
                Object[] offObj = (Object[]) offStatus;
                String offType = (String)offObj[0];//设备类型
                long offCount = (long)offObj[1];//同一类型设备离线数量
                if(onType.equals(offType)){
                    deviceStatusMap.put("offCount", offCount);
                    break;
                }
            }
            deviceStatusList.add(deviceStatusMap);
        }
        return deviceStatusList;
    }

    /**
     * 获取档案存量信息
     * @return
     */
    @RequestMapping(value = "/getArchiveStock",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getArchiveStock(String ip,Integer port,Integer code){
        return shelvesService.capacityAndUserCapacity(ip,port,code);
    }

    /**
     * 获取设备告警信息(分页)
     * histories@return
     */
    @RequestMapping(value = "/getDeviceWarning",method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceWarning> getDeviceWarning(Integer page, Integer limit){
        PageRequest pageRequest = new PageRequest(page - 1, limit,new Sort(Sort.Direction.DESC,"warningTime"));
        return deviceService.getDeviceWarning(pageRequest);
    }

    /**
     * 获取全部设备告警信息（不分页）
     * histories@return
     */
    @RequestMapping(value = "/getDeviceWarningAll",method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceWarning> getDeviceWarningAll(){
        return deviceService.getDeviceWarningAll();
    }

    /**
     * 获取设备告警信息
     * histories@return
     */
    @RequestMapping(value = "/getDeviceWarningCount",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getDeviceWarningCount(){
        return deviceService.getDeviceWarningCount();
    }

    /**
     * 获取设备信息表，可以根据指定类型设备获取。
     * @param deviceType 设备类型
     * @return
     */
    @RequestMapping(value = "/getDevicesDetail",method = RequestMethod.GET)
    @ResponseBody
    public Page<Device> getDevices(String deviceType,Integer page,Integer limit){
        return deviceService.findDevice(deviceType,page,limit);
    }

    /**
     * 打开密集架的指定列
     * @param code 区号
     * @param col 列号
     * @return
     */
//    @RequestMapping(value = "/openAssginColumn")
//    @ResponseBody
//    public ExtMsg openAssginColumn(String deviceId,String version,int code, int col){
//        return deviceTaskController.openAssginColumn(deviceId , version,code,col );
//    }

    /**
     * 闭合指定区号的密集架中所有已打开的通道
     * @param code 区号
     * @return
     */
    @RequestMapping(value = "/closeColumn")
    @ResponseBody
    public ExtMsg closeColumn(String deviceId,String version,String code){
        return deviceTaskController.closeColumn(deviceId, version, code);
    }

    /**
     * 密集架通风
     * @param code 区号
     * @return
     */
    @RequestMapping(value = "/ventilation")
    @ResponseBody
    public ExtMsg ventilation(String deviceId,String version,int code){
        return deviceTaskController.ventilation(deviceId, version, code);
    }

    /**
     * 查询密集架当前所有列状态。
     * @param deviceId 设备id
     * @return
     */
    @RequestMapping(value = "/getMJJStatus")
    @ResponseBody
    public ExtMsg getMJJStatus(String deviceId){
        List<MJJStatus> status = dcpService.getStatus(deviceId,null);
        return new ExtMsg(true,null,status);
    }

    /**
     * 查询未阅读的借阅信息
     * @return
     */
    @RequestMapping(value = "/getMessage")
    @ResponseBody
    public ExtMsg getMessage(){
        List list= borrowRepository.findDocGroupByDocid();
        return new ExtMsg(true,null,list);
    }


    /**
     * 查询未阅读的借阅信息
     * @return
     */
    @RequestMapping(value = "/showMessage")
    @ResponseBody
    public IndexMsg showMessage(String docid){
        List zoneList = borrowRepository.findZoneByDocid(docid);
        List<Map<String,String>> returnlist = new ArrayList();
        for(int i =0;i<zoneList.size();i++){
            Map map = new HashMap();
            Object[] zoneObject = (Object[]) zoneList.get(i);
            map.put("archivecode",zoneObject[0]);
            map.put("title",zoneObject[1]);
            StringBuilder place = new StringBuilder();
            place.append(zoneObject[2]).append("-").append(zoneObject[3]).append("-").append(zoneObject[4]).append("-").append(zoneObject[5]).append("-").append(zoneObject[6]).append("-").append(zoneObject[7]);
            map.put("place", place.toString());

            returnlist.add(map);
        }
        return new IndexMsg(true, "0", String.valueOf(zoneList.size()),"成功", returnlist);
    }


    /**
     * 设置消息查看状态
     * @return
     */
    @RequestMapping(value = "/setMessageStatus")
    @ResponseBody
    public ExtMsg setMessageStatus(String docid){
      int idnex = storageService.updateMessageStatue(docid);
      return new ExtMsg(true,"修改成功","");
    }

    /**
     * 获取监控设备。
     * @param deviceId 设备Id
     * @param deviceType 设备类型
     * @param sort 顺序号
     * @return
     */
    @RequestMapping(value = "/getVideoDevices",method = RequestMethod.GET)
    @ResponseBody
    public Device getVideoDevices(String deviceId,String deviceType,String sort){
        return deviceService.findDeviceByIdOrType(deviceId,deviceType,sort);
    }

    /**
     * 获取监控设备最大顺序号。
     * @param deviceType 设备类型
     * @return
     */
    @RequestMapping(value = "/findMsxSort",method = RequestMethod.GET)
    @ResponseBody
    public String findMsxSort(String deviceType){
        return deviceRepository.findMsxSort(deviceType);
    }

    /**
     * 读取思必得温湿度。
     * @param deviceid 设备Id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/findSensorsHT",method = RequestMethod.GET)
    @ResponseBody
    public IndexMsg findSensorsHT(String deviceid,String startTime,String endTime,Integer page,Integer limit){
        Page<Map<String, Object>> HTpage = sensorsController.htHistories(deviceid,startTime,endTime,page,limit);
        List<Map<String, Object>> HTlist = HTpage.getContent();
        return new IndexMsg(true, "0", String.valueOf(HTpage.getTotalElements()),"成功", HTlist);
    }
}
