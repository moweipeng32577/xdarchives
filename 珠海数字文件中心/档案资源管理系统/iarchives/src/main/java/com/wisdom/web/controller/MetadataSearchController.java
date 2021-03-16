package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.service.MetadataSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by SunK on 2020/4/17 0017.
 */
//元数据检索控制器
@Controller
@RequestMapping(value = "/metadataSearch")
public class MetadataSearchController {

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Autowired
    MetadataSearchService metadataSearchService;

    @RequestMapping(value = "mainly")
    public String indexly(Model model) {
        model.addAttribute("buttonflag","ly");  //平台标志，gl为管理平台，ly为利用平台
        model.addAttribute("reportServer",reportServer);
        return "/inlet/metadataSearch";
    }

    /**
     *
     * @param isCollection
     * @param condition //筛选条件
     * @param operator 比较关键字(比如like)
     * @param content
     * @param sort
     * @param isCompilationManageSystem 请求是否为编研管理系统,因为编研管理系统要把编研采集录入的数据也显示出来.
     * @return
     */
    @RequestMapping("/findBySearchPlatform")
    @ResponseBody
    public Page<Tb_index_detail> findBySearchPlatform(int page, int limit, String isCollection, String condition,
                                                              String operator, String content, String sort,
                                                              boolean isCompilationManageSystem, String entryids,String metadataType) {
        Sort sortobj = WebSort.getSortByJson(sort);
        String flagOpen = "原文开放";
//        if(isCompilationManageSystem){
//            flagOpen += ",编研开放";
//        }
        return metadataSearchService.findBySearchPlatformOpen(page, limit, flagOpen, isCollection, condition, operator,
                content, sortobj,entryids,metadataType);
    }


    @RequestMapping("/queryName")
    @ResponseBody
    public List<Tb_metadata_temp> queryConditionTemplate(String metadataType) {
        List<Tb_metadata_temp> list = metadataSearchService.queryConditionTemplate(metadataType);
        return list;
    }


    @RequestMapping(value = "/entriesByPower", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_entry_detail_capture> getEntriesByPower(String nodeid, int page, int limit, String condition, String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
//        return dataopenService.getEntriesByPower
        return metadataSearchService.getEntriesByPower(new String[]{nodeid}, page, limit, condition, operator, content, sortobj);
    }

    @RequestMapping(value = "/entries/{entryid}", method = RequestMethod.GET)
    @ResponseBody
    public Entry getEntry(@PathVariable String entryid) {
        return metadataSearchService.getEntry(entryid);
    }
}
