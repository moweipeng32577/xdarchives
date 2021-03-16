package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 档案销毁记录显示控制器
 *
 */
@Controller
@RequestMapping("/destroy")
public class DestroyContoller {

    @Autowired
    private StorageService storageService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务


    @RequestMapping("/main")
    public String destroy(Model model) {
        model.addAttribute("reportServer",reportServer);
        return "/inlet/storeroom/destroy";
    }


    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg delAll(String entryids) {
        String[] entryidData = entryids.split(",");
        Integer dels = storageService.delEntry(entryidData);
        if (dels > 0) {
            return new ExtMsg(true, "删除成功", dels);
        }
        return new ExtMsg(false, "删除失败", null);
    }

}
