package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 收藏夹控制器
 * Created by nick on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/bookmarks")
public class BookmarksController {

    @Autowired
    EntryBookmarksService entryBookmarksService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    AcquisitionController acquisitionController;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 设置收藏状态，判断传入的条目ID是否存在收藏关联记录，存在则提示收藏失败，不存在则新增关联
     * 取消收藏状态，取消选中数据的收藏关联
     */
    //@LogAnnotation(module = "简单检索",sites = "1",startDesc = "收藏/取消收藏操作，条目id为：")
    @RequestMapping("/setBookmarks")
    @ResponseBody
    public ExtMsg setBookmarks(String[] entryids,boolean bookmarkStatus,String type){
        SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId=userDetails.getUserid();
        ExtMsg result;
        String startDesc= "收藏操作";
        List<String> entryidList=new ArrayList<>();//日志记录条目id集合
        if(!bookmarkStatus){//添加至收藏
            result = entryBookmarksService.addBookmarks(entryids,userId,type);
            try{
                entryidList=(List<String>)result.getData();
            }catch(Exception e){
               e.printStackTrace();
            }
        }else{
            result= entryBookmarksService.cancelBookmarks(entryids,userId,type);//取消收藏
            entryidList= Arrays.asList(entryids);
            startDesc= "取消收藏操作";
        }
        if(entryidList.size()>0){//记录日志
            acquisitionController.delWriteLog(entryidList,"简单检索",startDesc);
        }
        return result;
    }

    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page<Tb_entry_index> findBySearch(int page,int start,int limit,String condition,String operator,String content,String sort,String searchtype,String datasoure){
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid=userDetails.getUserid();
        return entryBookmarksService.findBySearch(page, limit, condition, operator, content,userid, sortobj,searchtype,datasoure);
    }

    @RequestMapping("/findBySearchsimple")
    @ResponseBody
    public Page<Tb_entry_index> findBySearchSimple(int page,int start,int limit,String condition,String operator,
                                              String content,String sort,String searchtype,String datasoure){
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);

        SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid=userDetails.getUserid();
        return entryBookmarksService.findBySearchSimple(page, limit, condition, operator, content,userid, sort,
                searchtype,datasoure);
    }

    @RequestMapping("/findBySearchDirectory")
    @ResponseBody
    public Page findBySearchDirectory(String datasoure, int page, int start, int limit, String condition, String operator, String content, String sort){
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid=userDetails.getUserid();
        return entryBookmarksService.findBySearchDirectory(datasoure,page, limit, condition, operator, content,userid, sortobj);
    }
}