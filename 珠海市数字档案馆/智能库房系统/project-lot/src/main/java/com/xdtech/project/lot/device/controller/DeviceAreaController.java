package com.xdtech.project.lot.device.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceArea;
import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.repository.DeviceAreaRepository;
import com.xdtech.project.lot.device.repository.FloorRepository;
import com.xdtech.project.lot.device.service.DeviceAreaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rong on 2019-06-13.
 */
@Controller
@RequestMapping("/deviceArea")
public class DeviceAreaController {

    @Autowired
    DeviceAreaRepository deviceAreaRepository;

    @Autowired
    DeviceAreaService deviceAreaService;

    @Autowired
    FloorRepository floorRepository;

    @RequestMapping(value = "/devicearea",method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceArea> deviceAreas(String floorid,String areaid){
        if(!StringUtils.isEmpty(floorid) && StringUtils.isEmpty(areaid)){
            return  deviceAreaRepository.findByFloorid(floorid);
        }
        else if(!StringUtils.isEmpty(floorid) && !StringUtils.isEmpty(areaid)){
            List<DeviceArea> list = new ArrayList<>();
            list.add(deviceAreaRepository.findByIdOrderByName(areaid));
            return  list;
        }
        else{
            List<DeviceArea> list = new ArrayList<>();
            list = deviceAreaRepository.findAll(new Sort("name"));
            return list;
        }
    }


    /**
     * 所有楼层
     * @return
     */
    @RequestMapping(value = "/getAllFloor",method = RequestMethod.GET)
    @ResponseBody
    public List<Floor> getAllFloor(){
        List list = floorRepository.findAll();
        return list;
    }

    /**
     * 分区表单提交
     * @param deviceArea 表单实例
     * @param operate 增加或者修改
     * @return
     */
    @RequestMapping("/savaDeviceArea")
    @ResponseBody
    public ExtMsg savaDeviceArea(DeviceArea deviceArea,String operate) {
        if(operate.equals("add")) {
            DeviceArea d = deviceAreaRepository.findByName(deviceArea.getName());
            if (d != null) {
                return new ExtMsg(true, "该分区已存在！！", null);
            }
            deviceAreaService.saveDeviceArea(deviceArea);
        }
        else if(operate.equals("modify")){
            deviceAreaService.modifyDeviceArea(deviceArea);
        }
        return new ExtMsg(true, "保存成功", null);
    }

    /**
     * 删除分区
     * @param ids 选中的数据
     * @return
     */
    @RequestMapping("/deleteDeviceArea")
    @ResponseBody
    public ExtMsg deleteDeviceArea(String[] ids) {
       return deviceAreaService.deleteDeviceArea(ids);
    }


    /**
     * 楼层表单提交
     * @param deviceFloor 表单实例
     * @param operate 增加或者修改
     * @return
     */
    @RequestMapping("/savaDeviceFloor")
    @ResponseBody
    public ExtMsg savaDeviceArea(Floor deviceFloor,String operate) {
        if(operate.equals("add")) {
            DeviceArea d = deviceAreaRepository.findByName(deviceFloor.getFloorName());
            if (d != null) {
                return new ExtMsg(true, "该分区已存在！！", null);
            }
            else{
                deviceAreaService.saveDeviceFloor(deviceFloor);
            }
        }
        else if(operate.equals("modify")){
            deviceAreaService.saveDeviceFloor(deviceFloor);
        }
        return new ExtMsg(true, "保存成功", null);
    }


    /**
     * 删除楼层
     * @param ids 选中的数据
     * @return
     */
    @RequestMapping("/deleteDeviceFloor")
    @ResponseBody
    public ExtMsg deleteDeviceFloor(String[] ids) {
        return deviceAreaService.deleteDeviceFloor(ids);
    }

    /**
     * 根据楼层找到对应的库房
     * @param floorid
     * @return
     */
    @RequestMapping(value = "/getRooms",method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceArea> getRooms(String floorid){
        String type = "kf";//KF表示库房类型
        return deviceAreaRepository.findByFlooridAndType(floorid,type);
    }

    @RequestMapping("/rooms")
    @ResponseBody
    public List<DeviceArea> findRoomsByUnit(String citydisplay, String unitdisplay){

        return deviceAreaService.findRoomsByUnit(citydisplay, unitdisplay);

    }

    @RequestMapping("/seletorSubmit")
    @ResponseBody
    public ExtMsg seletorSubmit(String areaId, String[] checkedDeviceId,String[] checkDeviceId){
        return deviceAreaService.seletorSubmit(areaId,checkedDeviceId,checkDeviceId);
    }

}
