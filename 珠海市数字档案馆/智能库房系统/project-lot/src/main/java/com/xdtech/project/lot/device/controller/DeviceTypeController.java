package com.xdtech.project.lot.device.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceType;
import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.service.DeviceTypeService;
import com.xdtech.project.lot.device.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author wangmh
 */
@Controller
@RequestMapping("/deviceType")
public class DeviceTypeController {

    @Autowired
    DeviceTypeService deviceTypeService;

    /**
     * 获取楼层列表
     * @return
     */
    @RequestMapping(value = "/getDeviceType",method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceType> getDeviceType(){
        return deviceTypeService.getDeviceType();
    }

    /**
     * 添加or修改设备
     * 通过页面添加设备
     *
     * @param device
     */
    @RequestMapping(value = "/saveDeviceType", method = RequestMethod.POST)
    @ResponseBody
    public void saveDeviceType(DeviceType device) {
        deviceTypeService.saveDeviceType(device);
    }

    /**
     * 删除设备
     * 通过页面批量删除设备
     *
     * @param ids
     */
    @RequestMapping(value = "/deviceType/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deviceType(@PathVariable String ids) {
        return deviceTypeService.deviceType(ids.split(","));
    }

}
