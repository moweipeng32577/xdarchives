package com.xdtech.project.lot.device.service;


import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


/**
 * 设备告警类
 * Created by Rong on 2019-01-17.
 */
@Service
@Transactional
public class DeviceAlarmService {

    @Autowired
    DeviceWarningRepository deviceWarningRepository;

    public Integer alarmCheck(String[] Ids,String status){
        int index = deviceWarningRepository.updataStatusByIds(status,Ids);
        return index;
    }
}
