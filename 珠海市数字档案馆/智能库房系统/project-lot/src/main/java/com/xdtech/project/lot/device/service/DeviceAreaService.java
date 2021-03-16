package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.project.lot.device.entity.DeviceArea;
import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.repository.DeviceAreaRepository;
import com.xdtech.project.lot.device.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author wujy 2019/10/09
 */
@Service
@Transactional
public class DeviceAreaService {

    @Resource
    ZonesRepository zonesRepository;

    @Autowired
    DeviceAreaRepository deviceAreaRepository;

    @Autowired
    FloorRepository floorRepository;

    /**
     * 按已有地区和单位名查询所有库房
     * @param
     * @return  库房的密集架区集合
     */
    public List<DeviceArea> findRoomsByUnit(String citydisplay, String unitdisplay){
        String[] rooms = zonesRepository.findRoomsByUnitString(citydisplay, unitdisplay);

        return deviceAreaRepository.findByIdIn(rooms);
    }

    /**
     * 增加分区表单提交
     * @param deviceArea 表单实例
     * @return
     */
    public DeviceArea saveDeviceArea(DeviceArea deviceArea) {
       return  deviceAreaRepository.save(deviceArea);
    }

    /**
     * 修改分区表单提交
     * @param deviceArea 表单实例
     * @return
     */
    public void  modifyDeviceArea(DeviceArea deviceArea) {
        DeviceArea d = deviceAreaRepository.findById(deviceArea.getId());
        d.setName(deviceArea.getName());
        d.setType(deviceArea.getType());
        d.setFloor(deviceArea.getFloor());
        d.setArchivestype(deviceArea.getArchivestype());
        deviceAreaRepository.save(d);
    }

    /**
     * 删除分区
     * @param ids
     * @return
     */
    public ExtMsg deleteDeviceArea(String[] ids) {
        int  returnEvent=   deviceAreaRepository.deleteByIdIn(ids);
        if (returnEvent > 0) {
            return new ExtMsg(true, "删除成功！", null);
        }
            return new ExtMsg(false, "删除失败！", null);
    }

    /**
     * 楼层表单提交
     * @param deviceFloor 表单实例
     * @return
     */
    public Floor saveDeviceFloor(Floor deviceFloor) {
        return  floorRepository.save(deviceFloor);
    }

    /**
     * 删除楼层
     * @param ids
     * @return
     */
    public ExtMsg deleteDeviceFloor(String[] ids) {
         List<DeviceArea>  deviceAreaList =  deviceAreaRepository.findByFloorids(ids);
         if(deviceAreaList.size() > 0){
             return new ExtMsg(true, "删除失败！该楼层已关联区域！", null);
         }
         else{
             int  returnEvent=   floorRepository.deleteByFlooridIn(ids);
             return new ExtMsg(true, "删除成功！", null);
         }
    }

    /**
     * 调整设备分区
     * @param areaId 区域ID
     * @param checkedDeviceId 已选
     * @param checkDeviceId 可选
     * @return
     */
    public ExtMsg seletorSubmit(String areaId, String[] checkedDeviceId,String[] checkDeviceId) {
        if(checkedDeviceId != null && checkedDeviceId.length > 0){
            int  checkedEvent=   deviceAreaRepository.updateAreaByCheckedDeviceIn(areaId,checkedDeviceId);//已选
        }

        if(checkDeviceId != null && checkDeviceId.length > 0){
            int  checkEvent=   deviceAreaRepository.updateAreaByCheckDeviceIn(checkDeviceId);//可选
        }

        return new ExtMsg(true, "提交成功！", null);
    }

}
