package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 移库控制器
 *
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping(value = "/moveware")
public class MoveWareController {

    @Autowired
    private ShelvesService shelvesService;

    @Autowired
    private ZonesRepository zonesRepository;

    @RequestMapping("/main")
    public String inware(Model model) {
//        List<String> nodeids=zonesRepository.findNodeids("文书档案-永久");
//        if(nodeids.size()>0){
//            model.addAttribute("templateNodeid", nodeids.get(0));
//        }else{
            model.addAttribute("templateNodeid", "12345678910");
//        }
        return "/inlet/storeroom/moveware";
    }

    /**
     * 單元格移库
     * @param sourceStr,targetStr
     * @return
     */
    @RequestMapping("/changeshel")
    @ResponseBody
    public ExtMsg changeShel(String sourceStr, String targetStr,String zoneid){
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String user=(String)session.getAttribute("username");
        shelvesService.changeShel(sourceStr,targetStr,user,zoneid);
        return new ExtMsg(true,"移库成功",null);
    }


    /**
     * 電子檔案移库
     * @param targetStr
     * @return
     */
    @RequestMapping("/entrychangeshel")
    @ResponseBody
    public ExtMsg entryChangeShel( String targetStr,String zoneid,String entryids){
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String user=(String)session.getAttribute("username");
        shelvesService.entryChangeShel(targetStr,user,zoneid,entryids);
        return new ExtMsg(true,"移库成功",null);
    }
}
