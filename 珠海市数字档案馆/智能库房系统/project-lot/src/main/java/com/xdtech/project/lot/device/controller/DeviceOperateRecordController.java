package com.xdtech.project.lot.device.controller;

import com.xdtech.project.lot.device.entity.DeviceOperateRecord;
import com.xdtech.project.lot.device.repository.DeviceOperateRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wujy
 */
@Controller
@RequestMapping("/operateRecord")
public class DeviceOperateRecordController {

    @Autowired
    DeviceOperateRecordRepository deviceOperateRecordRepository;

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
}
