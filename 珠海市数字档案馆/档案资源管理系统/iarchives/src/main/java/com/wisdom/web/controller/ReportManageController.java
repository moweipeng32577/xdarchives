package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/reportManage")
public class ReportManageController {

    @RequestMapping(value = "/outware",method = RequestMethod.GET)
    public String outware(Model model,String isp){
//        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
//        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportname","出库统计");
        return "/inlet/storeroom/reportManage";
    }
    @RequestMapping(value = "/inware",method = RequestMethod.GET)
    public String inware(Model model,String isp){
//        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
//        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportname", "入库统计");
        return "/inlet/storeroom/reportManage";
    }

    @RequestMapping(value = "/desinfect",method = RequestMethod.GET)
    public String desinfect(Model model,String isp){
//        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
//        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportname", "消毒统计");
        return "/inlet/storeroom/reportManage";
    }
}
