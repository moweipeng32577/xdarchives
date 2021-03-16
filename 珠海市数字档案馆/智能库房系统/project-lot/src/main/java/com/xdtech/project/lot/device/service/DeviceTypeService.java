package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceType;
import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.repository.DeviceTypeRepository;
import com.xdtech.project.lot.device.repository.DeviceWorkRepository;
import com.xdtech.project.lot.device.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 设备类型管理业务类
 * Created by wujy on 2019-09-04
 */
@Service
@Transactional
public class DeviceTypeService {

   @Autowired
   DeviceTypeRepository deviceTypeRepository;

    @Autowired
    DeviceWorkRepository deviceWorkRepository;

    public List<DeviceType> getDeviceType() {
        return deviceTypeRepository.findAll();
    }

    public void saveDeviceType(DeviceType device) {
        deviceTypeRepository.save(device);
    }

    /**
     * 设备删除
     *
     * @param ids
     */
    public ExtMsg deviceType(String[] ids) {
        List list = deviceWorkRepository.findDeviceWorkByDeviceId(ids);
        if(list.size() > 0){
            return  new ExtMsg(true,"该设备类型下有设备正在工作，无法删除","");
        }
        else {
            int index = deviceTypeRepository.deleteByIdIn(ids);
            return  new ExtMsg(true,"删除成功","");
        }
    }
}
