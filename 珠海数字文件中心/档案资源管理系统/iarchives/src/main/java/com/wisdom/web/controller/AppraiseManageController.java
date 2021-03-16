package com.wisdom.web.controller;

import com.wisdom.web.entity.Tb_feedback;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.AppraiseManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2020/3/23.
 */
@Controller
@RequestMapping("/appraiseManage")
public class AppraiseManageController {


    @Autowired
    AppraiseManageService appraiseManageService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String index(Model model){
        model.addAttribute("reportServer",reportServer);
        return "/inlet/appraiseManage";
    }

    //获取所有评价汇总
    @RequestMapping("/getAppraiseManage")
    @ResponseBody
    public Page<Tb_feedback> getAppraiseManage(int page, int start, int limit, String condition, String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return appraiseManageService.getAppraiseManage(condition,operator,content,page,limit,sortobj);
    }
}
