package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_car_defend;
import com.wisdom.web.entity.Tb_car_manage;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.CarManageService;
import com.wisdom.web.service.LogService;
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
 * Created by Administrator on 2020/4/17.
 */
@Controller
@RequestMapping("carManage")
public class CarManageController {


    @Autowired
    CarManageService carManageService;

    @Autowired
    LogService logService;

    @RequestMapping("/main")
    public String index() {
        return "/inlet/carManage";
    }

    @RequestMapping("/getCarManages")
    @ResponseBody
    public Page<Tb_car_manage> getCarManages(int page, int limit, String sort, String condition, String operator, String content) {
        PageRequest pageRequest = new PageRequest(page-1,limit);
        Page<Tb_car_manage> carManages =  carManageService.getCarManages(page, limit, sort, condition, operator, content);
        List<Tb_car_manage> carManageList = carManages.getContent();
        List<Tb_car_manage> returnList = new ArrayList<>();
        for(Tb_car_manage carManage : carManageList){
            Tb_car_manage car = new Tb_car_manage();
            BeanUtils.copyProperties(carManage,car);
            if("空闲中".equals(car.getState())){
                car.setState("<span style='color:green'>"+car.getState()+"</span>");
            }else if("使用中".equals(car.getState())){
                car.setState("<span style='color:red'>"+car.getState()+"</span>");
            }
            returnList.add(car);
        }
        return new PageImpl<Tb_car_manage>(returnList,pageRequest,carManages.getTotalElements());
    }

    @RequestMapping("/carManageSubmit")
    @ResponseBody
    public ExtMsg carManageSubmit(Tb_car_manage car_manage) {
        Tb_car_manage tb_car_manage = carManageService.getCarManageByCarnumber(car_manage.getCarnumber());
        //增加时车牌号不存在 或 修改时车牌号存在一个且为本身修改Tb_car_manage实体类
        if ((tb_car_manage == null) || (tb_car_manage != null && car_manage.getId().equals(tb_car_manage.getId()))) {
            String msg = "";

            Tb_car_manage carManage = carManageService.carManageSubmit(car_manage);
            if (null != car_manage.getId() && !"".equals(car_manage.getId())) {//修改
                msg = "车辆管理;修改信息;条目id:" + car_manage.getId();
            } else {
                msg = "车辆管理;新增信息;车牌号:" + car_manage.getCarnumber() + ";条目id:" + carManage.getId();
            }
            logService.recordTextLog("公车管理",msg);
            return new ExtMsg(true, "", null);
        }
        return new ExtMsg(false, "", null);
    }

    @RequestMapping("/getCarManageByid")
    @ResponseBody
    public ExtMsg getCarManageByid(String id) {
        Tb_car_manage car_manage = carManageService.getCarManageByid(id);
        if (car_manage != null) {
            return new ExtMsg(true, "", car_manage);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/deleteCarManageByid")
    @ResponseBody
    public ExtMsg deleteCarManageByid(String[] ids) {
        int count = 0;
        count = carManageService.deleteCarManageByid(ids);
        if (count > 0) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/getCarDefendByCarId")
    @ResponseBody
    public Page<Tb_car_defend> getCarDefendByCarId(String carid, int page, int limit, String sort, String condition, String operator, String content) {
        return carManageService.getCarDefendByCarId(carid, page, limit, sort, condition, operator, content);
    }

    @RequestMapping("/loadCarDefend")
    @ResponseBody
    public ExtMsg loadCarDefend() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_car_defend defend = new Tb_car_defend();
        defend.setDefenduser(userDetails.getRealname());
        defend.setDefendtime(datastr);
        defend.setPhonenum(userDetails.getPhone());
        return new ExtMsg(true, "", defend);
    }

    @RequestMapping("/carDefendSubmit")
    @ResponseBody
    public ExtMsg carDefendSubmit(Tb_car_defend carDefend, String carid) {
        Tb_car_defend tb_car_defend = carManageService.carDefendSubmit(carDefend, carid);
        if (tb_car_defend != null) {
            return new ExtMsg(true, "", null);
        }
        return new ExtMsg(false, "", null);
    }

    @RequestMapping("/getCarDefendByid")
    @ResponseBody
    public ExtMsg getCarDefendByid(String id) {
        Tb_car_defend defend = carManageService.getCarDefendByid(id);
        if (defend != null) {
            return new ExtMsg(true, "", defend);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/deleteCarDefendByid")
    @ResponseBody
    public ExtMsg deleteCarDefendByid(String[] ids) {
        int count = 0;
        count = carManageService.deleteCarDefendByid(ids);
        if (count > 0) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }
}
