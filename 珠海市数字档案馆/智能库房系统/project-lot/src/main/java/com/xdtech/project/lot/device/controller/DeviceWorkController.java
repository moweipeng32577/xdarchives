package com.xdtech.project.lot.device.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceWork;
import com.xdtech.project.lot.device.service.DeviceWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 设备作业管理控制类
 * @author wangmh
 */
@Controller
@RequestMapping("/deviceWork")
public class DeviceWorkController {
    @Autowired
    DeviceWorkService deviceWorkService;

    /**
     * 获取设备作业数据
     * @param deviceType 设备类型
     * @param deviceId 设备id
     * @return
     */
    @RequestMapping(value = "/getWorks",method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceWork> getWorks(int page,int limit,String deviceType,String deviceId){
        return deviceWorkService.getDeviceWorks(page,limit,deviceType,deviceId);
    }

    /**
     * 保存设备作业数据
     * @param deviceWork
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveDeviceWork(DeviceWork deviceWork,String type){
        return deviceWorkService.saveDeviceWork(deviceWork,type);
    }
    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg deleteDeviceWork(String[] ids){
        deviceWorkService.deleteDeviceWork(ids);
        return new ExtMsg(true,"删除成功",null);
    }
}
