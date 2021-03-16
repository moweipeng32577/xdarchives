package com.wisdom.util;

import com.wisdom.web.entity.FunctionButton;
import com.wisdom.web.entity.Tb_right_function;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/16 0016.
 */
@Component
public class FunctionUtil {

    /**
     * 根据模块获取功能按钮
     * @param isp 父级模块key
     * @return
     */
    public static List getQxFunction(String isp){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List functions = new ArrayList();
        Map<String,FunctionButton> status = new HashMap<>();//用于判断二级按钮
//        if(userDetails.getFunctions()==null)
//            new LoginService().index(userDetails);
        for(Tb_right_function function:userDetails.getFunctions()){
            if(isp.equals(function.getIsp())){
                FunctionButton fb = null;
                if(function.getName()!=null&&function.getName().split("-").length>1){
                    String[] names = function.getName().split("-");
                    if(status.containsKey(names[0])){
                        status.get(names[0]).getMenu().add(new FunctionButton(names[1],function.getCode(),function.getIcon()));
                        continue;
                    }else{
                        fb = new FunctionButton(names[0],"parent"+function.getCode(),"");
                        List<FunctionButton> menus = new ArrayList<>();
                        menus.add(new FunctionButton(names[1],function.getCode(),function.getIcon()));
                        fb.setMenu(menus);
                        status.put(names[0],fb);
                    }
                }else{
                    fb = new FunctionButton(function.getName(),function.getCode(),function.getIcon());
                }
                functions.add(fb);
                functions.add("-");
            }
        }
        if(functions.size()!=0)
            functions.remove(functions.size()-1);
        return functions;
    }

}
