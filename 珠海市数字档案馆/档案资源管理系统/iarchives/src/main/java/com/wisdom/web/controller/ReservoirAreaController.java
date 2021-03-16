package com.wisdom.web.controller;

import com.wisdom.web.service.ReservoirAreaService;
import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.project.lot.device.entity.DeviceArea;
import com.xdtech.project.lot.device.repository.DeviceAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.applet.resources.MsgAppletViewer;

import java.util.List;
import java.util.Map;

/**
 * create by wujy 2019/08/08
 */
@Controller
@RequestMapping("/reservoirArea")
public class ReservoirAreaController {

    @Autowired
    private ReservoirAreaService reservoirAreaService;

    @Autowired
    private DeviceAreaRepository deviceAreaRepository;

    @RequestMapping(value = "/main",method = RequestMethod.GET)
    public String main(Model model){
        model.addAttribute("floorAndRoomList",reservoirAreaService.findFloorAndRoom());
        List<DeviceArea> deviceAreaList=  deviceAreaRepository.findAll(new Sort("name"));
        model.addAttribute("deviceAreaList",deviceAreaList);    //档案类别

        model.addAttribute("templateNodeid","12345678910");
        return "/inlet/storeroom/reservoirArea/reservoirArea";
    }

    /**
     * 获取对应的库区位置图
     * @param roomdisplay
     * @return
     */
    @RequestMapping(value = "/getArea",method = RequestMethod.GET)
    public String getArea(String roomdisplay){
        String num  = roomdisplay.substring((roomdisplay.length()-1),roomdisplay.length());//库房名要以数字开头，并且带上“号”如1号库房。
        return "/inlet/storeroom/reservoirArea/storeroomNo"+ num;
    }

    @RequestMapping(value = "/getZoneShelves",method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getZoneShelves(String floordisplay, String roomdisplay, String zonedisplay){
        List<ZoneShelves> zoneShelves = reservoirAreaService.getZoneShelves(floordisplay, roomdisplay, zonedisplay);
        if(zoneShelves.size() > 0){
            return new ExtMsg(true,"ok",zoneShelves);
        }
        return new ExtMsg(false,"error",null);
    }
}
