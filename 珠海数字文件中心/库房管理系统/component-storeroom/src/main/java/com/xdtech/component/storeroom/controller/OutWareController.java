package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.OutWare;
import com.xdtech.component.storeroom.entity.OutWare_History;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.BorrowRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.OutWareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 出库控制器
 *
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping(value = "/outware")
public class OutWareController {

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    OutWareService outWareService;

    @Autowired
    private ZonesRepository zonesRepository;

    @Autowired
    BorrowRepository borrowRepository;

    @RequestMapping("/main")
    public String outware(Model model,String borrowcode) {
//        List<String> nodeids=zonesRepository.findNodeids("文书档案-永久");
//        if(nodeids.size()>0){
//            model.addAttribute("templateNodeid", nodeids.get(0));
//        }else{
            model.addAttribute("templateNodeid", "12345678910");
            if(borrowcode!=null){
                model.addAttribute("borrowCode", borrowcode);
            }else{
                model.addAttribute("borrowCode", "");
            }
        return "/inlet/storeroom/outware";
    }


    /**
     * 出库记录
     * @return
     */
    @RequestMapping(value = "/outwares", method = RequestMethod.GET)
    @ResponseBody
    public Page<OutWare_History> getOutWares(int page, int limit){
        return outWareService.findAll(page, limit);
    }

    /**
     * 调档出库
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public ExtMsg save(String waretype, String ids, String description,String borrowcodes) {
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String user=(String)session.getAttribute("username");
        Map map = outWareService.save(waretype,ids,description,user,borrowcodes);
        return new ExtMsg(true,"出库成功",map);
    }

    /**
     * 根据档号获取相应出库记录
     * @param dhCode
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/outwares/{dhCode}", method = RequestMethod.GET)
    @ResponseBody
    public Page<OutWare_History> findshelves(@PathVariable String dhCode, int page, int limit,HttpServletResponse httpServletResponse){

        PageRequest pageRequest = new PageRequest(page-1, limit);

        return outWareService.findOutWares(dhCode, pageRequest);
    }
}
