package com.xdtech.project.lot.device.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.service.ShelvesService;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.repository.*;
import com.xdtech.project.lot.device.service.DeviceHistoryService;
import com.xdtech.project.lot.device.service.DeviceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 设备管理控制器
 * 用于智能设备的增删查改
 * Created by Rong on 2019-01-16.
 */
@Controller
@RequestMapping("/")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    UserAreaRepository userAreaRepository;

    @Autowired
    UserDeviceRepository userDeviceRepository;

    @Autowired
    DeviceAreaRepository deviceAreaRepository;

    @Autowired
    DeviceHistoryService deviceHistoryService;

    @Autowired
    ShelvesService shelvesService;

    @RequestMapping(value = "/jk")
    public String jkframe() {
        return "/jk";
    }

    @RequestMapping(value = "/oldjk")
    public String oldjkframe() {
        return "/oldjk";
    }

    @RequestMapping(value = "/record")
    public String jkrecord() {
        return "/record";
    }

    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    @ResponseBody
    public List<Device> devices() {
        return deviceRepository.findAll();
    }

    /**
     * 修改设备状态
     *
     * @return
     */
    @RequestMapping(value = "/devicestatus", method = RequestMethod.POST)
    public void updateStatus(int status,String name,HttpServletResponse response) {
        List<Device> areas = deviceRepository.findByAreaName(name);

        for (Device device :areas) {
            device.setStatus(status);
            deviceRepository.save(device);
        }

        response.setStatus(200);
    }

    @RequestMapping(value = "/device/status", method = RequestMethod.POST)
    @ResponseBody
    public Device updateStatus(String deviceid, int status){
        Device device = deviceRepository.findOne(deviceid);
        device.setStatus(status);
        return deviceRepository.save(device);
    }

    /**
     * 查询设备
     * 获取某个设备的详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/device/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Device device(@PathVariable String id) {
        return deviceRepository.findOne(id);
    }

    /**
     * 删除设备
     * 通过页面批量删除设备
     *
     * @param ids
     */
    @RequestMapping(value = "/device/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delDevice(@PathVariable String ids) {
        return deviceService.delDevice(ids.split(","));
    }

    /**
     * 添加or修改设备
     * 通过页面添加设备
     *
     * @param device
     */
    @RequestMapping(value = "/saveDevice", method = RequestMethod.POST)
    @ResponseBody
    public void saveDevice(Device device) {
        device.setEnabled("0");
        deviceRepository.save(device);
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
    @RequestMapping(value = "/device/histories")
    @ResponseBody
    public Page<DeviceHistory> histories(String deviceid, String areaId, String deviceType,String ip,Integer port ,Integer code,
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

    //时间检索
    @RequestMapping(value = "/device/{deviceid}/historiesbysearch/{less}/{great}")
    @ResponseBody
    public Page<DeviceHistory> historiesbysearch(@PathVariable String deviceid, int page, int limit, @PathVariable String less, @PathVariable String great) {
        PageRequest pr = new PageRequest(page - 1, limit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date min = null;
        Date max = null;
        Page<DeviceHistory> pages = null;
        try {
            min = sdf.parse(less);
            max = sdf.parse(great);
            Calendar c = Calendar.getInstance();
            c.setTime(max);
            c.add(Calendar.DAY_OF_MONTH, 1); //利用Calendar 实现 Date日期+1天
            max = c.getTime();
            pages = deviceHistoryRepository.findAllByDevice_IdAndCaptureTimeGreaterThanAndCaptureTimeLessThanOrderByCaptureTimeDesc(deviceid, min, max, pr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return pages;
    }

    //LED时间检索
    @RequestMapping(value = "/device/{deviceid}/historiesbysearchTime/{less}/{great}")
    @ResponseBody
    public Page<DeviceHistory> historiesbysearchTime(@PathVariable String deviceid, int page, int limit, @PathVariable String less, @PathVariable String great) {
        PageRequest pr = new PageRequest(page - 1, limit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date min = null;
        Date max = null;
        Page<DeviceHistory> pages = null;
        try {
            if ("notime".equals(great)) {
                max = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(max);
                c.add(Calendar.DAY_OF_MONTH, 1); //利用Calendar 实现 Date日期+1天
                max = c.getTime();
            } else {
                max = sdf.parse(great);
            }
            min = sdf.parse(less);
            pages = deviceHistoryRepository.findAllByDevice_IdAndCaptureTimeGreaterThanAndCaptureTimeLessThanOrderByCaptureTimeDesc(deviceid, min, max, pr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pages;
    }

    /**
     * 获取密集架设备
     * @return
     */
    @RequestMapping(value = "/device/getMJJ",method = RequestMethod.GET)
    @ResponseBody
    public List<Device> getMJJ(String area){
        return deviceRepository.findByTypeAndArea("MJJ",area);
    }

    @RequestMapping(value = "/device/devices",method = RequestMethod.GET)
    @ResponseBody
    public List<Device> devices(String deviceType){
        return deviceRepository.findByType(deviceType);
    }

    @RequestMapping(value = "/device/getZoneShelves",method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getZoneShelves(String zoneid){
        List<ZoneShelves> zoneShelves = shelvesService.getZoneShelves(zoneid);
        if(zoneShelves.size() > 0){
            return new ExtMsg(true,"ok",zoneShelves);
        }
        return new ExtMsg(false,"error",null);
    }

    @RequestMapping(value = "/enabledOrDisableDevice")
    @ResponseBody
    public List choosableDevice(String areaid) {
      List list = deviceRepository.findByArea(areaid);
      return list;
    }

    @RequestMapping(value = "/seletorDevice")
    @ResponseBody
    public List seletorDevice(String areaid) {
        List list = deviceRepository.findByAreaOrNull(areaid);
        return list;
    }

    //查询启用状态的设备
    @RequestMapping(value = "/areaDevice")
    @ResponseBody
    public List areaDevice(String areaid) {
        List list;
        if(areaid == null){
            list = deviceRepository.findByEnabled();
        }
        else{
            list = deviceRepository.findByAreaAndEnabled(areaid);
        }
        return list;
    }

    @RequestMapping(value = "/jkDevice")
    @ResponseBody
    public List jkDevice() {
        List list=deviceRepository.findByType("JK");
        return list;
    }

    //接入设备
    @RequestMapping(value = "/enabledDevice")
    @ResponseBody
    public ExtMsg enabledDevice(String deviceid,String state,String type) {
        return deviceService.enabledDevice(deviceid,state,type);
    }

    //查询楼层维护设备
    @RequestMapping(value = "/devicePanel")
    @ResponseBody
    public List devicePanel(String areaid,String typeCode) {
        List list;
        if(areaid == null){
            list = deviceRepository.findByType(typeCode);
        }
        else{
            list = deviceRepository.findByArea(areaid);
        }
        return list;
    }


    /**
     * 查询楼层设备列表
     * 用户权限在页面上获取所有智能设备
     *
     * @return
     */
    @RequestMapping(value = "/userDevicesByFloorCode")
    @ResponseBody
    public List userDevices(String floorCode) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid") != null? (String) session.getAttribute("userid") : "4028802d64ea40080164ea5bbfe719cb";
        return deviceRepository.findByUserdeviceByFloor(userid,floorCode);
    }


    /**
     * 查询设备列表（监控）
     * 用户监控智能设备权限
     * @return
     */
    @RequestMapping(value = "/userJKDevices")
    @ResponseBody
    public List<Device> userJKDevices() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid") != null? (String) session.getAttribute("userid") : "4028802d64ea40080164ea5bbfe719cb";
        List list=deviceRepository.findByUserAndType("JK",userid);
        return list;

    }
}