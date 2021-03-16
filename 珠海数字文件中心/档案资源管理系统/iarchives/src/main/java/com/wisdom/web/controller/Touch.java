package com.wisdom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * @description:
 * @author:ljr
 * @create: 2020-12-22 18-57
 *
 */
@RequestMapping("/touch")
@Controller
public class Touch {

    @RequestMapping("/main")
    public String touch(){
        return "inlet/Touch";
    }
}
