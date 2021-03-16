package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 单位管理控制器
 */
@Controller
@RequestMapping(value = "/unit")
public class UnitController {

    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping("/getUnit")
    @ResponseBody
    public List getUnit() {
        List<ExtNcTree> list = new ArrayList<>();
//        Tb_right_organ organ = new Tb_right_organ();
//        organ.setOrganid("123");
//        organ.setOrganname("全宗单位");
//        list.add(organ);
        list = userService.findAllOrgan();
        return list;
    }

}
