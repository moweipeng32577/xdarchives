package com.xdtech.project.lot.device.controller;


import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceDiagnose;
import com.xdtech.project.lot.device.repository.DeviceDiagnoseRepository;
import com.xdtech.project.lot.device.service.DeviceDiagnoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/deviceDiagnose")
public class DeviceDiagnoseController {

    @Autowired
    DeviceDiagnoseRepository deviceDiagnoseRepository;
    @Autowired
    DeviceDiagnoseService deviceDiagnoseService;
    /**
     * 获取列表
     */
    @RequestMapping(value = "/grid", method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceDiagnose> grid(int page, int limit) {
        return deviceDiagnoseRepository.findAll( new PageRequest(page - 1, limit,new Sort("createdate")));
    }

    /**
     * 增加
     */
    @RequestMapping(value = "/saveDeviceDiagnose")
    @ResponseBody
    public ExtMsg saveInformation(DeviceDiagnose deviceDiagnose) {
        boolean b = deviceDiagnoseService.saveInformation(deviceDiagnose);
        if (b) {
            return new ExtMsg(true, "保存成功！", null);
        }
        return new ExtMsg(false, "保存失败！", null);
    }

    /**
     *删除设备信息
     */
    @RequestMapping(value = "/delDeviceDiagnose")
    @ResponseBody
    public ExtMsg delInformation(String[] ids) {
        int delCount = 0;
        delCount = delCount +deviceDiagnoseService.delInformation(ids);
        if (delCount>0) {
            return new ExtMsg(true, "删除成功！", null);
        }
        return new ExtMsg(false, "删除失败！", null);
    }

    /**
     * 设备诊断
     */
    @RequestMapping(value = "/diagnose")
    @ResponseBody
    public List<DeviceDiagnose> diagnose(String id){
        List<DeviceDiagnose> list = deviceDiagnoseService.diagnose(id);
        return list;
    }
}
