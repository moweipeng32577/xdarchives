package com.xdtech.project.lot.device.controller;

import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import com.xdtech.project.lot.device.service.DeviceAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 设备告警信息管理控制器
 * Created by wang on 2019-01-16.
 */
@Controller
@RequestMapping("/deviceAlarm")
public class DeviceAlarmController {

    @Autowired
    DeviceWarningRepository deviceWarningRepository;

    @Autowired
    DeviceAlarmService deviceAlarmService;

    /**
     * 读取一个设备的告警数据
     * 用于某些设备展示告警数据
     * @param deviceid  设备ID
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/grid/{deviceid}", method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceWarning> deviceGrid(@PathVariable String deviceid, int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit, new Sort(Sort.Direction.DESC,"createTime"));
        return deviceWarningRepository.findAllByDevice_Id(deviceid, pageRequest);
    }

    /**
     * 获取列表
     */
    @RequestMapping(value = "/grid", method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceWarning> grid(int page, int limit) {
        return deviceWarningRepository.findAll( new PageRequest(page - 1, limit,new Sort(Sort.Direction.DESC,"createTime")));
    }

    /**
     * 确认告警信息
     */
    @RequestMapping(value = "/alarmCheck", method = RequestMethod.POST)
    @ResponseBody
    public Integer alarMCheck(String[] ids,String status) {
        return deviceAlarmService.alarmCheck(ids,status);
    }
}
