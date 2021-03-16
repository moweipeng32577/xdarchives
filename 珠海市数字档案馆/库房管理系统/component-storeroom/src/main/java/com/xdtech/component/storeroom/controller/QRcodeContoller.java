package com.xdtech.component.storeroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 档案销毁记录显示控制器
 *
 */
@Controller
@RequestMapping("/QRcode")
public class QRcodeContoller {


    @RequestMapping("/main")
    public String destroy(String archivecode, Model model) {
        model.addAttribute("archivecode",archivecode);
        return "/inlet/storeroom/qrcode";
    }


}
