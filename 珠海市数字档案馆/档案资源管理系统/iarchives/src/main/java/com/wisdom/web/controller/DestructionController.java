package com.wisdom.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 销毁查看控制器
 * Created by yl on 2017/10/26.
 */
@Controller
@RequestMapping(value = "/destruction")
public class DestructionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/main")
    public String main() {
        return "/inlet/destruction";
    }
}
