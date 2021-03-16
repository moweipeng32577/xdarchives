package com.wisdom.web.controller;


/**
 * 目录中心-数据统计
 */

/**
 * Created by Administrator on 2020/3/16.
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value = "/statistDirectory")
public class statistDirectoryController {

    @RequestMapping("/main")
    public String statistDirectory(Model model){
        model.addAttribute("reportname", "目录管理分类数量统计表");
        return "/inlet/statistDirectory";
    }
}
