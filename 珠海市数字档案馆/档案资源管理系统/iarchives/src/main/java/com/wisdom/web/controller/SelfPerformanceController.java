package com.wisdom.web.controller;

import com.wisdom.web.entity.BackPerformance;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.SelfPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2020/4/13.
 */
@Controller
@RequestMapping("selfPerformance")
public class SelfPerformanceController {


    @Autowired
    SelfPerformanceService selfPerformanceService;


    @RequestMapping("/main")
    public String index(){
        return "/inlet/selfPerformance";
    }

    @RequestMapping("/getSelfPerformances")
    @ResponseBody
    public Page<BackPerformance> getSelfPerformances(int page,int limit,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sortobj);
        List<BackPerformance> backPerformances = selfPerformanceService.getSelfPerformances();
        return new PageImpl<BackPerformance>(backPerformances,pageRequest,backPerformances.size());
    }
}
