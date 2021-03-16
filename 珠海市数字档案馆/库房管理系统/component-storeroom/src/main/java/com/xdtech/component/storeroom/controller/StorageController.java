package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 实体档案控制器
 *
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping(value = "/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    /**
     * 按档号查询库存位置
     * @param dhCode
     * @return
     */
    @RequestMapping("/shid")
    @ResponseBody
    public ExtMsg findShid(String dhCode){
        String shidMsg=storageService.findshid(dhCode);
        return new ExtMsg(true,shidMsg,null);
    }
}
