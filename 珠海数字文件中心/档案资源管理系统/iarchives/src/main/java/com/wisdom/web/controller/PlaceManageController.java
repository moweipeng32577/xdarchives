package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_place_defend;
import com.wisdom.web.entity.Tb_place_manage;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.LogService;
import com.wisdom.web.service.PlaceManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/20.
 */
@Controller
@RequestMapping("placeManage")
public class PlaceManageController {

    @Autowired
    PlaceManageService placeManageService;

    @Autowired
    LogService logService;

    @RequestMapping("/main")
    public String index(){
        return "/inlet/placeManage";
    }

    @RequestMapping("/getPlaceManages")
    @ResponseBody
    public Page<Tb_place_manage> getPlaceManages(int page, int limit, String sort, String condition, String operator, String content){
        PageRequest pageRequest = new PageRequest(page-1,limit);
        Page<Tb_place_manage> placeManages =  placeManageService.getPlaceManages(page,limit,sort,condition,operator,content);
        List<Tb_place_manage> placeManageList = placeManages.getContent();
        List<Tb_place_manage> returnList = new ArrayList<>();
        for(Tb_place_manage placeManage : placeManageList){
            Tb_place_manage place = new Tb_place_manage();
            BeanUtils.copyProperties(placeManage,place);
            if("空闲中".equals(place.getState())){
                place.setState("<span style='color:green'>"+place.getState()+"</span>");
            }else if("使用中".equals(place.getState())){
                place.setState("<span style='color:red'>"+place.getState()+"</span>");
            }
            returnList.add(place);
        }
        return new PageImpl<Tb_place_manage>(returnList,pageRequest,placeManages.getTotalElements());
    }

    @RequestMapping("/placeManageSubmit")
    @ResponseBody
    public ExtMsg placeManageSubmit(Tb_place_manage place_manage){
        Tb_place_manage place_manage1 = placeManageService.placeManageSubmit(place_manage);
        String msg = "";
        if(null!=place_manage.getId()&&!"".equals(place_manage.getId())){
            msg = "场地管理;修改场地信息;楼层:"+place_manage.getFloor()+";条目id:"+place_manage.getId();
        }else {
            msg = "场地管理;新增场地信息;楼层:"+place_manage1.getFloor()+";场地描述:"+place_manage1.getPlacedesc()+";条目id:"+place_manage1.getId();
        }
        logService.recordTextLog("场地管理",msg);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/getPlaceManageByid")
    @ResponseBody
    public ExtMsg getPlaceManageByid(String id){
        Tb_place_manage placeManage = placeManageService.getPlaceManageByid(id);
        if(placeManage!=null){
            return new ExtMsg(true,"",placeManage);
        }else{
            return new ExtMsg(false,"",null);
        }
    }

    @RequestMapping("/deletePlaceManageByid")
    @ResponseBody
    public ExtMsg deletePlaceManageByid(String[] ids){
        int count = 0;
        count = placeManageService.deletePlaceManageByid(ids);
        if(count>0){
            return new ExtMsg(true,"",null);
        }else{
            return new ExtMsg(false,"",null);
        }
    }



    @RequestMapping("/getPlaceDefendByCarId")
    @ResponseBody
    public Page<Tb_place_defend> getPlaceDefendByCarId(String placeid, int page, int limit, String sort, String condition, String operator, String content){
        return placeManageService.getPlaceDefendByCarId(placeid,page,limit,sort,condition,operator,content);
    }

    @RequestMapping("/loadPlaceDefend")
    @ResponseBody
    public ExtMsg loadCarDefend(){
        SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_place_defend defend = new Tb_place_defend();
        defend.setDefenduser(userDetails.getRealname());
        defend.setDefendtime(datastr);
        defend.setPhonenum(userDetails.getPhone());
        return new ExtMsg(true,"",defend);
    }

    @RequestMapping("/placeDefendSubmit")
    @ResponseBody
    public ExtMsg placeDefendSubmit(Tb_place_defend placeDefend,String placeid){
        Tb_place_defend tb_place_defend = placeManageService.placeDefendSubmit(placeDefend,placeid);
        if(tb_place_defend!=null){
            return new ExtMsg(true,"",null);
        }
        return new ExtMsg(false,"",null);
    }

    @RequestMapping("/getPlaceDefendByid")
    @ResponseBody
    public ExtMsg getCarDefendByid(String id){
        Tb_place_defend defend = placeManageService.getPlaceDefendByid(id);
        if(defend!=null){
            return new ExtMsg(true,"",defend);
        }else{
            return new ExtMsg(false,"",null);
        }
    }

    @RequestMapping("/deletePlaceDefendByid")
    @ResponseBody
    public ExtMsg deletePlaceDefendByid(String[] ids){
        int count = 0;
        count = placeManageService.deletePlaceDefendByid(ids);
        if(count>0){
            return new ExtMsg(true,"",null);
        }else{
            return new ExtMsg(false,"",null);
        }
    }
}
