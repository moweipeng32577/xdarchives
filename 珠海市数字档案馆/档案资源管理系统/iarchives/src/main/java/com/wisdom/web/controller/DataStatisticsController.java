package com.wisdom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Leo on 2020/7/11 0011.
 */
@Controller
@RequestMapping(value = "/dataStatistics")
public class DataStatisticsController {

    //统计報表
    @RequestMapping("/classifyTotal")
    public String classifyTotal(Model model, String reportname) {
//        reportname = reportname.replaceAll("'","");
//        if(reportname.equals("t1")) {
//            reportname="总数统计表";
//        }else if(reportname.equals("t2")){
//            reportname="维护统计表";
//        }else if(reportname.equals("t3")){
//            reportname="公车统计表";
//        }else if(reportname.equals("t4")){
//            reportname="场地统计表";
//        }else if(reportname.equals("t5")){
//            reportname="公告统计表";
//        }else if(reportname.equals("t6")){
//            reportname="设备统计表";
//        }else if(reportname.equals("t7")){
//            reportname="项目统计表";
//        }
//        model.addAttribute("reportname", reportname);
        return "/inlet/reportManagement/dataStatistics";
    }
}
