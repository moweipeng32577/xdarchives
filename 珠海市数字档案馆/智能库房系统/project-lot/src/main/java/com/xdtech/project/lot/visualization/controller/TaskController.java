package com.xdtech.project.lot.visualization.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wujy
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    @RequestMapping(value = "/settingTemperature")
    @ResponseBody
    public void settingTemperature(String temperature){
        System.out.println("Temperature:"+temperature);
    }

    @RequestMapping(value = "/settingHumidity")
    @ResponseBody
    public void settingHumidity(String humidity){
        System.out.println("humidity:"+humidity);
    }

    @RequestMapping(value = "/startOrStop")
    @ResponseBody
    public void startOrStop(String humidity){
        System.out.println("startOrStop:");
    }
}
