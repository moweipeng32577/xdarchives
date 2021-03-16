package com.xdtech.project.lot.device.service;


import com.alibaba.fastjson.JSON;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceDiagnose;
import com.xdtech.project.lot.device.repository.DeviceDiagnoseRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.util.expUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeviceDiagnoseService {

    @Autowired
    DeviceDiagnoseRepository deviceDiagnoseRepository;
    @Autowired
    DeviceRepository deviceRepository;

    public boolean saveInformation(DeviceDiagnose deviceDiagnose) {
        DeviceDiagnose information = null;
        if (null != deviceDiagnose) {
            SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (null != deviceDiagnose.getId() && !"".equals(deviceDiagnose.getId())) {//修改
                deviceDiagnose.setModifydate(smf.format(new Date()));
                information = deviceDiagnoseRepository.save(deviceDiagnose);
            } else {//增加
                deviceDiagnose.setCreatedate(smf.format(new Date()));
                information = deviceDiagnoseRepository.save(deviceDiagnose);
            }
        }
        return information == null ? false : true;
    }

    public int delInformation(String[] ids) {
        int delCount = 0;
        if (null != ids) {
            List<String[]> list = expUtil.splitAry(ids, 900);
            for (String[] arr : list) {
                delCount = delCount + deviceDiagnoseRepository.deleteByIdIn(arr);
            }
        }
        return delCount;
    }

    public List<DeviceDiagnose> diagnose(String id) {

        if (null != id && !"".equals(id)) {
            //1.根据设备id查出设备
            Device device = deviceRepository.findOne(id);
            if (null != device) {
                if (null == device.getProp()) {// err-1：设备未设置参数
                    List<DeviceDiagnose> list = deviceDiagnoseRepository.findByFaultcauseLike("参数");
                    return list;
                }
                Map<String, Object> deviceProp = (Map<String, Object>) JSON.parse(device.getProp());
                if (null == deviceProp || "".equals(deviceProp.get("ip"))) {
                    List<DeviceDiagnose> list = deviceDiagnoseRepository.findByFaultcauseLike("参数");
                    return list;
                }
                if (null == deviceProp || "".equals(deviceProp.get("port"))) {
                    List<DeviceDiagnose> list = deviceDiagnoseRepository.findByFaultcauseLike("参数");
                    return list;
                }
                //err -2 参数正常 其他故障
                List<DeviceDiagnose> list = deviceDiagnoseRepository.findByFaultcauseNotLike("参数");
                return list;
            }
        }
        return null;
    }
}
