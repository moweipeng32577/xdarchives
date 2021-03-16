package com.wisdom.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/returnWare")
public class ReturnWareController {

    @RequestMapping("/main")
    public String returnWare() {
        return "/inlet/returnWare";
    }
}
