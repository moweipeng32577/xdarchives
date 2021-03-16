package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.GuidanceSafeKeepRepository;
import com.wisdom.web.service.SupervisionGuidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
@Controller
@RequestMapping(value = "/supervisionGuidance")
public class SupervisionGuidanceController {


    @Autowired
    SupervisionGuidanceService supervisionGuidanceService;

    @Autowired
    GuidanceSafeKeepRepository guidanceSafeKeepRepository;

    @RequestMapping("/main")
    public String index(){
        return "/inlet/supervisionGuidance";
    }

    @RequestMapping("/getSelectYear")
    @ResponseBody
    public List getSelectYear(){
        return supervisionGuidanceService.getSelectYear();
    }

    @RequestMapping("/getGuidanceLeaders")
    @ResponseBody
    public Page<Tb_guidance_leader> getGuidanceLeaders(String organid, String selectyear, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return supervisionGuidanceService.getGuidanceLeaders(organid,selectyear,page,limit,sortobj);
    }

    @RequestMapping("/getGuidanceOrgans")
    @ResponseBody
    public Page<Tb_guidance_organ> getGuidanceOrgans(String organid, String selectyear, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return supervisionGuidanceService.getGuidanceOrgans(organid,selectyear,page,limit,sortobj);
    }

    @RequestMapping("/getGuidanceFileUsers")
    @ResponseBody
    public Page<Tb_guidance_fileuser> getGuidanceFileUsers(String organid, String selectyear, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return supervisionGuidanceService.getGuidanceFileUsers(organid,selectyear,page,limit,sortobj);
    }

    @RequestMapping("/getGuidanceWorkFundss")
    @ResponseBody
    public Page<Tb_guidance_workfunds> getGuidanceWorkFundss(String organid, String selectyear, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return supervisionGuidanceService.getGuidanceWorkFundss(organid,selectyear,page,limit,sortobj);
    }

    @RequestMapping("/getGuidanceWorkPlans")
    @ResponseBody
    public Page<Tb_guidance_workplan> getGuidanceWorkPlans(String organid, String selectyear, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return supervisionGuidanceService.getGuidanceWorkPlans(organid,selectyear,page,limit,sortobj);
    }

    @RequestMapping("/getGuidanceSafeKeep")
    @ResponseBody
    public ExtMsg getGuidanceSafeKeep(String organid, String selectyear){
        Tb_guidance_safekeep safekeep = guidanceSafeKeepRepository.findByOrganidAndSelectyear(organid,selectyear);
        if(safekeep!=null){
            return new ExtMsg(true,"",guidanceSafeKeepRepository.findByOrganidAndSelectyear(organid,selectyear));
        }else{
            Tb_guidance_safekeep safekeepnew = new Tb_guidance_safekeep();
            return new ExtMsg(true,"",safekeepnew);
        }
    }


    @RequestMapping("/setGuidances")
    @ResponseBody
    public ExtMsg setGuidances(Tb_guidance_safekeep safekeep, String organid, String selectyear, String leaderData, String organData, String fileuserData, String workfundsData, String workplanData){
        supervisionGuidanceService.setGuidances(safekeep,organid,selectyear,leaderData,organData,fileuserData,workfundsData,workplanData);
        return new ExtMsg(true,"",null);
    }
    @RequestMapping("/deleteSuperGuidanceByType")
    @ResponseBody
    public ExtMsg deleteSuperGuidanceByType(String[] ids, String type){
        supervisionGuidanceService.deleteSuperGuidanceByType(ids,type);
        return new ExtMsg(true,"",null);
    }
}
