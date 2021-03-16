package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_supervision_work;
import com.wisdom.web.repository.SupervisionWorkRepository;
import com.wisdom.web.service.SupervisionWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2020/10/12.
 */
@Controller
@RequestMapping(value = "/supervisionWork")
public class SupervisionWorkController {



    @Autowired
    SupervisionWorkService supervisionWorkService;

    @Autowired
    SupervisionWorkRepository supervisionWorkRepository;

    @RequestMapping("/main")
    public String index(){
        return "/inlet/supervisionWork";
    }


    @RequestMapping("/getSelectYear")
    @ResponseBody
    public List getSelectYear(){
        return supervisionWorkService.getSelectYear();
    }

    @RequestMapping("/setSupervisionWork")
    @ResponseBody
    public ExtMsg setSupervisionWork(Tb_supervision_work supervisionWork, String organid, String selectyear){
        supervisionWorkService.setSupervisionWork(supervisionWork,organid,selectyear);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/getSupervisionWork")
    @ResponseBody
    public ExtMsg getSupervisionWork(String organid, String selectyear){
        Tb_supervision_work tbSupervisionWork = supervisionWorkRepository.findByOrganidAndSelectyear(organid,selectyear);
        if(tbSupervisionWork!=null){
            return new ExtMsg(true,"",tbSupervisionWork);
        }else{
            Tb_supervision_work supervisionWorknew = new Tb_supervision_work();
            return new ExtMsg(true,"",supervisionWorknew);
        }
    }

    @RequestMapping("/getElectronicCount")
    @ResponseBody
    public ExtMsg getElectronicCount(String organid, String selectyear){
        return new ExtMsg(true,"",supervisionWorkService.getElectronicCount(organid,selectyear));
    }
}
