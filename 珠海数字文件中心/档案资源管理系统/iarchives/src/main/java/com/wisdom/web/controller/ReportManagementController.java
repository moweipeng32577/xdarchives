package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.repository.EntryIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计管理控制器
 */
@Controller
@RequestMapping(value = "/reportmanagement")
public class ReportManagementController {

    @Autowired
    EntryIndexRepository entryIndexRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //统计報表
    @RequestMapping("/classifyTotal")
    public String classifyTotal(Model model, String reportname) {
        reportname = reportname.replaceAll("'","");
        if(reportname.equals("t1"))
        {
            reportname = "档案分类数量统计表";
        }
        else if(reportname.equals("t2"))
        {
            reportname = "电子查档档案统计表";
        }
        else if(reportname.equals("t3"))
        {
            reportname = "实体查档档案统计表";
        }
        else if(reportname.equals("t4"))
        {
            reportname = "统计报表";
        }
        else if(reportname.equals("t5"))
        {
            reportname = "档案利用数量统计表";
        }else if(reportname.equals("t6")) {
            reportname = "档案分类馆藏电子容量统计表";
        }else if(reportname.equals("t8")) {
            reportname = "查档台账统计表";
            return "/inlet/consultStandingBook";
        }else if(reportname.equals("t9")){
            reportname = "前台利用统计";
        }
        model.addAttribute("reportname", reportname);
        return "/inlet/reportManagement/classifyTotal";
    }

    //获取馆藏所有门类
    @RequestMapping("/getEntryIndexClassName")
    @ResponseBody
    public String[] getEntryIndexClassName() {
        String[] name=entryIndexRepository.findAllClassId();
        return name;
    }

    //获取馆藏所有全宗号
    @RequestMapping("/getEntryIndexFunds")
    @ResponseBody
    public String[] getEntryIndexFunds(String className) {
        String[] name=entryIndexRepository.findFundsByClassName(className);
        return name;
    }

    //获取馆藏所有年度
    @RequestMapping("/getEntryIndexFilingYear")
    @ResponseBody
    public String[] getEntryIndexFilingYear(String className,String funds) {
        String[] name=entryIndexRepository.findFilingYearByFundsAndClassName(funds,className);
        return name;
    }

    /*
      该类型报表已在统计报表的搜索量显示
     */
    //馆藏档案总量一览表
    @RequestMapping("/gcda")
    public String gcda() {
        return "/inlet/reportManagement/gcda";
    }

    //年度馆藏文书一览表
    @RequestMapping("/ndgcws")
    public String ndgcws() {
        return "/inlet/reportManagement/ndgcws";
    }

    //全宗档案总数一览表
    @RequestMapping("/qzda")
    public String qzda() {
        return "/inlet/reportManagement/qzda";
    }

    //年度馆藏专门档案一览表
    @RequestMapping("/ndgczm")
    public String ndgczm() {
        return "/inlet/reportManagement/ndgczm";
    }

    //档案利用统计表
    @RequestMapping("/daly")
    public String daly() {
        return "/inlet/reportManagement/daly";
    }

    //档案馆开放档案统计台帐
    @RequestMapping("/dakf")
    public String dakf() {
        return "/inlet/reportManagement/dakf";
    }

    //档案馆鉴定工作统计表
    @RequestMapping("/dajd")
    public String dajd() {
        return "/inlet/reportManagement/dajd";
    }

    @RequestMapping("/getFilingyeartype")
    @ResponseBody
    public List<Map<String,String>> getFilingyeartype(){
        SimpleDateFormat smf = new SimpleDateFormat("YYYY");
        int year = Integer.parseInt(smf.format(new Date()));
        List l = new ArrayList();
        for(int i=year;i>=2017;i--){
            Map m = new HashMap();
            m.put("id",i);
            m.put("name",i);
            l.add(m);
        }
        return l;
    }
}
