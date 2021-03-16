package com.wisdom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 焦点图管理控制器
 * Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/focusImg")
public class FocusImgController {

    @RequestMapping("/main")
    public String acquisition(Model model) {
        return "/inlet/focusImg";
    }
}
