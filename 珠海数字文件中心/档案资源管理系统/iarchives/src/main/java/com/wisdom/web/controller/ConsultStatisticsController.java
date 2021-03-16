package com.wisdom.web.controller;

import com.wisdom.web.entity.ConsultStandingBookVo;
import com.wisdom.web.entity.ConsultStatistics;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_consult_statistics;
import com.wisdom.web.service.ConsultStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Leo on 2020/7/3 0003.
 */
@Controller
@RequestMapping(value = "/consultStatistics")
public class ConsultStatisticsController {

    @Autowired
    ConsultStatisticsService consultStatisticsService;

    @RequestMapping("/findConsultStatistics")
    @ResponseBody
    public Page<ConsultStandingBookVo> listConsultStatistics(int page, int limit, String sort, String startdate, String enddata){
        return consultStatisticsService.listConsultStatistics(page,limit,sort,startdate,enddata);
    }

    @RequestMapping("/findConsultStatisticsByDateTime")
    @ResponseBody
    public ExtMsg findConsultStatisticsByDateTime(String dateTime){
        return new ExtMsg(true,"",consultStatisticsService.findConsultStatisticsByDateTime(dateTime));
    }

    @RequestMapping("/updateStatistics")
    @ResponseBody
    public ExtMsg updateStatistics(@ModelAttribute("form") ConsultStatistics form, String cType){
        if(consultStatisticsService.addConsultStatistics(form)>0){
            return new ExtMsg(true,"修改成功",null);
        }
        return new ExtMsg(false,"修改失败",null);
    }

    @RequestMapping("/deleteConsultStatistics")
    @ResponseBody
    public ExtMsg deleteConsultStatistics(String dateTime){
        if(consultStatisticsService.deleteConsultStatistics(dateTime)>0){
            return new ExtMsg(true,"删除成功",null);
        }
        return new ExtMsg(false,"删除失败",null);
    }

    @RequestMapping("/consultStatistics")
    @ResponseBody
    public ExtMsg consultStatistics(String dateTime){
        List<Tb_consult_statistics> list= consultStatisticsService.consultStatistics(dateTime);
        return new ExtMsg(false,"统计成功",list);
    }
}
