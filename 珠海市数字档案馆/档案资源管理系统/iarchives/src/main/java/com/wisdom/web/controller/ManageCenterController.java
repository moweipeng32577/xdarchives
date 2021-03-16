package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.service.ManageCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/7/21.
 */
@Controller
@RequestMapping(value = "/manageCenter")
public class ManageCenterController {



    @Autowired
    ManageCenterService manageCenterService;

    @Autowired
    RightOrganRepository rightOrganRepository;


    @RequestMapping("/main")
    public String index(){
        return "/inlet/manageCenter";
    }

    @RequestMapping("/getManageCenterData")
    @ResponseBody
    public Page<MediaEntry> getManageCenterData(int page, int limit) {
        return manageCenterService.getManageCenterData(page, limit);
    }

    @RequestMapping("/getManageCenterUnitNum")
    @ResponseBody
    public Page<ManageCenterTotal> getManageCenterUnitNum(int page, int limit) {
        return manageCenterService.getManageCenterUnitNum(page, limit);
    }

    @RequestMapping("/getManageCenterYearNum")
    @ResponseBody
    public List<ManageCenterTotal> getManageCenterYearNum() {
        return manageCenterService.getManageCenterYearNum();
    }

    @RequestMapping("/getManageCenterTotal")
    @ResponseBody
    public ExtMsg getManageCenterTotal() {
        return new ExtMsg(true,"",manageCenterService.getManageCenterTotal());
    }

    @RequestMapping("/getParentOrganids")
    @ResponseBody
    public ExtMsg getParentOrganids(String organid) {
        List<String> parentOrganids = new ArrayList<>();
        Tb_right_organ organnow = rightOrganRepository.findOne(organid);
        while (organnow.getOrgantype() != null && !"0".equals(organnow.getParentid())) {// 获取单位对象
            organnow = rightOrganRepository.findOne(organnow.getParentid());
            parentOrganids.add(organnow.getOrganid());
        }
        return new ExtMsg(true,"",parentOrganids);
    }
}
