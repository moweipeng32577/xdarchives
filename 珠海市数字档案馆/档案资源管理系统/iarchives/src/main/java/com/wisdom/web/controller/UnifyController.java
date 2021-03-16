package com.wisdom.web.controller;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.FunctionRepository;
import com.wisdom.web.repository.UserFunctionRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.UnifyService;
import com.wisdom.web.service.UserService;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 统一登录平台控制器(菜单,切换)
 * Created by wjh
 */
@Controller
@RequestMapping("/unify")
public class UnifyController {

    @Autowired
    UnifyService unifyService;

    /**
     * 按照系统类型获取菜单数据
     * @param sysType 系统类型
     * @return
     */
    @RequestMapping("/getlist")
    @ResponseBody
    public List getlist(String sysType,String realname,String loginname){
        List<Tb_right_function> functions =  unifyService.getList(sysType,realname,loginname);
        if(functions!=null&&!"4".equals(sysType) && !"8".equals(sysType)&& !"11".equals(sysType)){
            for(Tb_right_function function:functions){
                function.setIsp("1");
            }
        }
        return functions;
    }
}
