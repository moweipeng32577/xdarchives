package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.service.IndexLyService;
import com.wisdom.web.service.SimpleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  利用平台控制器
 * Created by Administrator on 2017/11/4 0004.
 */
@Controller
@RequestMapping(value = "/indexly")
public class IndexLyController {

    @Autowired
    IndexLyService indexLyService;

    @Autowired
    SimpleSearchService simpleSearchService;

    @RequestMapping("/getKfDate")
    @ResponseBody
    public IndexMsg getKfDate(int page,int limit) {
        Page<Tb_index_detail> pages = simpleSearchService.findBySearchPlatformOpen(page, limit,
                "原文开放,条目开放", null, null, null, null,
                new Sort(Sort.Direction.DESC,"opendate","archivecode","title"),null);
        List<Tb_index_detail> entry_indexs = pages.getContent();
        return new IndexMsg(true,"0","成功",entry_indexs);
    }

    @RequestMapping("/getQuestionnaire")
    @ResponseBody
    public IndexMsg getQuestionnaire(){
        List<Tb_questionnaire> questionnaires = indexLyService.getQuestionnaire();
        for(int i=0;i<questionnaires.size();i++){
            Tb_questionnaire questionnaire = questionnaires.get(i);
            if(questionnaire.getStick()!=null){
                String title = questionnaires.get(i).getTitle();
                questionnaires.get(i).setTitle(title+"<span class=\"stick-cls\">置顶</span>");
            }else{
                questionnaire.setStick(10);
            }
        }
        return new IndexMsg(true,"0","成功",questionnaires);
    }

    @RequestMapping("/getInform")
    @ResponseBody
    public IndexMsg getinform(int page,int limit) {
        Page<Tb_inform> pages = indexLyService.getinform(page,limit);
        List<Tb_inform> entry_indexs = pages.getContent();
        for(int i=0;i<entry_indexs.size();i++){
            Tb_inform inform = entry_indexs.get(i);
            if(inform.getStick()!=null){
                String title = entry_indexs.get(i).getTitle();
                entry_indexs.get(i).setTitle(title+"<span class=\"stick-cls\">置顶</span>");
            }else{
                inform.setStick(10);
            }
        }
        List<Tb_inform> entry_indexs_sort = entry_indexs.stream().sorted(Comparator.comparing(Tb_inform::getStick))
                .collect(Collectors.toList());
        List<Tb_inform> entry_indexslist = changetype(entry_indexs_sort);
        return new IndexMsg(true,"0","成功",entry_indexslist);
    }

    @RequestMapping("/getBorrowMsg")
    @ResponseBody
    public ExtMsg getBorrowMsg() {
        List<Tb_borrowdoc> borrowdocs = indexLyService.getBorrowMsg();
        return new ExtMsg(true,"",borrowdocs);
    }

    @RequestMapping("/getBorrowFinishMsg")
    @ResponseBody
    public Page<Tb_borrowdoc> getBorrowFinishMsg(int page,int limit) {
        Page<Tb_borrowdoc> borrowdocs = indexLyService.getBorrowFinishMsg(page,limit);
        return borrowdocs;
    }

    public List<Tb_inform> changetype(List<Tb_inform> informlist) {
        for (int i = 0; i < informlist.size(); i++) {
            String datetime = new SimpleDateFormat("yyyy-MM-dd").format(informlist.get(i).getLimitdate());
            informlist.get(i).setPostedman(datetime);
        }
        return informlist;
    }

    @RequestMapping("/borrowly")
    public String BorrowLy() {
        return "/inlet/borrowfinish";
    }

    @RequestMapping("/getBorrowFinishMsgCount")
    @ResponseBody
    public int getBorrowFinishMsgCount() {
        int count = indexLyService.getBorrowFinishMsgCount();
        return count;
    }

    @RequestMapping("/setBorrowFinish")
    @ResponseBody
    public ExtMsg setBorrowFinish(String borrowdocid) {
        indexLyService.setBorrowFinish(borrowdocid);
        return new ExtMsg(true,"",null);
    }
}
