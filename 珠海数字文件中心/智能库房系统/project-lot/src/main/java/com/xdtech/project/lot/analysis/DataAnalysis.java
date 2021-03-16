package com.xdtech.project.lot.analysis;

import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Rong on 2019-03-22.
 */
@Component
public class DataAnalysis {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    public void dataAnalysis(String type, String code, String jsonData){
        //1.解析数据类型，进行数据分类
        //2.根据类型及设备编码获取对应的设备
        Device device = deviceRepository.findByTypeAndCode(type, code);
        //3.构造设备运行记录保存
        DeviceHistory dh = new DeviceHistory();
        dh.setDevice(device);
        dh.setType(type);
        dh.setCaptureTime(DateUtil.getCurrentTime());
        dh.setCaptureValue(jsonData);
        deviceHistoryRepository.save(dh);
    }

}
