package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.service.PublicUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by RonJiang on 2017/12/5 0005.
 */
@Controller
@RequestMapping(value = "/publicUtil")
public class PublicUtilController {

    @Autowired
    PublicUtilService publicUtilService;

    @RequestMapping("/getNodeid")
    @ResponseBody
    public ExtMsg getNodeid(String parentid){
        String nodeid = publicUtilService.getNodeid(parentid);
        if(nodeid!=null && !("".equals(nodeid))){
            return new ExtMsg(true,"获取节点ID成功",nodeid);
        }
        return new ExtMsg(false,"获取节点ID失败",null);
    }

}
