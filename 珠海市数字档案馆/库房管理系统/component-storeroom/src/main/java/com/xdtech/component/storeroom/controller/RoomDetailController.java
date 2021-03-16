package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.RoomDetailServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/roomDetail")
public class RoomDetailController {

    @Autowired
    private RoomDetailServcie roomDetailServcie;

    @Autowired
    private ZonesRepository zonesRepository;

    @RequestMapping(value = "/main",method = RequestMethod.GET)
    public String roomDetail(Model model){
        List<String> nodeids = zonesRepository.findNodeids("文书档案-永久");
        if (nodeids.size() > 0) {
            model.addAttribute("templateNodeid", nodeids.get(0));
        } else {
            model.addAttribute("templateNodeid", "12345678910");//402880226628ed0e0166290464d9000e
        }
        return "/inlet/storeroom/roomDetail";
    }

    @RequestMapping(value = "/findRooms",method = RequestMethod.GET)
    @ResponseBody
    public Page<Zones> findRooms(int page, int limit){
        Pageable pageable = new PageRequest(page-1, limit);
        return  roomDetailServcie.findRooms(pageable);
    }

    @RequestMapping(value = "/findZones",method = RequestMethod.GET)
    @ResponseBody
    public Page<Zones>findZones(String roomDisplay, int page, int limit){
        Pageable pageable = new PageRequest(page-1, limit);

        return roomDetailServcie.findZones(pageable,roomDisplay);
    }

    @RequestMapping(value = "/findColumns",method = RequestMethod.GET)
    @ResponseBody
    public Page<ZoneShelves>findColumns(String zoneid, int page, int limit){
        Pageable pageable = new PageRequest(page-1, limit);

        return roomDetailServcie.findColumns(pageable,zoneid);
    }
}
