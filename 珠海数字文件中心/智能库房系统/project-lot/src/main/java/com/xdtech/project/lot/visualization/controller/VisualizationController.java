package com.xdtech.project.lot.visualization.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 可视化控制器
 * 用于进行智能设备可视化管理
 * Created by Rong on 2019-01-16.
 */
@Controller
@RequestMapping("/visualization")
public class VisualizationController {

    /**
     * 进入可视化管理主页面
     * @return
     */
    @RequestMapping("/main")
    public String visualization(){
        return "/inlet/lot/visualization";
    }

}
