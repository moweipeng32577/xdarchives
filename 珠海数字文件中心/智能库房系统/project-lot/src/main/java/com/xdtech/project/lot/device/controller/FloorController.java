package com.xdtech.project.lot.device.controller;

import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author wujy
 */
@Controller
@RequestMapping("/floor")
public class FloorController {

    @Autowired
    FloorService floorService;

    /**
     * 获取楼层列表
     * @return
     */
    @RequestMapping(value = "/floors",method = RequestMethod.GET)
    @ResponseBody
    public List<Floor> getFloors(){
        return floorService.getFloors();
    }
}
