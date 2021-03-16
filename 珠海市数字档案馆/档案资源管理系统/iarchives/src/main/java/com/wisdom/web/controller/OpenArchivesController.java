package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 开放档案控制器
 * Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/openArchies")
public class OpenArchivesController {

    @Autowired
    EntryService entryService;

    @RequestMapping("/main")
    public String management(Model model, String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        return "/inlet/management";
    }

}
