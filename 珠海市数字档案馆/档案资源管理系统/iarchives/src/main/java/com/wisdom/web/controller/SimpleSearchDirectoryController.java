package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.entity.Tb_index_detail_sx;
import com.wisdom.web.entity.Tb_index_detail_manage;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.SimpleSearchDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2019/6/26.
 */
@Controller
@RequestMapping(value = "/simpleSearchDirectory")
public class SimpleSearchDirectoryController {


    @Autowired
    SimpleSearchDirectoryService simpleSearchDirectoryService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Value("${system.instantSearch.opened}")
    private String instantSearch;//判断是否开启即时搜索

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

    @RequestMapping("/main")
    public String index(Model model, String flag) {
        model.addAttribute("buttonflag", flag);
        model.addAttribute("reportServer",reportServer);
        model.addAttribute("systemLoginType",systemLoginType);
        return "/inlet/simpleSearchDirectory";
    }

    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page findBySearch(String datasoure,int page, int limit, String isCollection, String condition,
                                                     String operator, String content, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page list = simpleSearchDirectoryService.mergePage(datasoure,page, limit, isCollection, condition, operator,
                content, sortobj);
        return list;
    }

    @RequestMapping("/getSxEntry/{entryid}")
    @ResponseBody
    public Tb_index_detail_sx getSxEntry(@PathVariable String entryid) {
        return simpleSearchDirectoryService.getSxEntry(entryid);
    }
}
