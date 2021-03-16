package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 编研采集Controller
 */
@Controller
@RequestMapping("/compilationAcquisition")
public class CompilationAcquisitionController {

    @Autowired
    UserController userController;

    @Value("${system.report.server}")
    private String reportServer;//报表服务
    @RequestMapping(value = "main",method = RequestMethod.GET)
    public String index(Model model,String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        Object wjqxFunctionButton = JSON.toJSON(userController.getWJQXbtn());//文件权限
        model.addAttribute("wjqxFunctionButton", wjqxFunctionButton);
        model.addAttribute("functionButton", functionButton);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/compilationAcquisition";
    }
}
