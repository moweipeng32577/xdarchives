package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.EntryService;
import com.wisdom.web.service.OriginalSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原文检索控制器
 * Created by RonJiang on 2017/11/2 0002.
 */
@Controller
@RequestMapping(value = "/originalSearch")
public class OriginalSearchController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OriginalSearchService originalSearchService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryService entryService;

    @RequestMapping("/main")
    public String index(){return "/inlet/originalSearch";}

    @RequestMapping("/getAllEleName")
    @ResponseBody
    public List<ExtTree> getAllEleName(){
        return originalSearchService.getAllEleName();
    }

    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page<Tb_electronic> findBySearch(int page, int start, int limit, String condition, String operator, String content, String sort){
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        return originalSearchService.findBySearch(page, limit, condition, operator, content, sortobj);
    }

    @RequestMapping(value ="/getNodeid/{entryid}", method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getNodeid(@PathVariable String entryid){
        String nodeid = entryIndexService.findNodeidByEntryid(entryid);
        if(nodeid != null && !("".equals(nodeid))){
            return new ExtMsg(true,"获取节点ID成功",nodeid);
        }
        return new ExtMsg(false,"获取节点ID失败",null);
    }

    /**
     *  查看条目信息
     * @param entryid
     * @return
     */
    @RequestMapping(value ="/entry/{entryid}", method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getEntry(@PathVariable String entryid){
        String nodeid = entryIndexService.findNodeidByEntryid(entryid);
        Entry entry = entryService.getEntry(entryid);
        if((entry.getEntryIndex()==null && entry.getEntryDetail()==null) || nodeid==null){
            return new ExtMsg(false,"源条目已被删除",null);
        }
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("nodeid",nodeid);
        result.put("entry",entry);
        return new ExtMsg(true,"获取条目信息成功",result);
    }
}
