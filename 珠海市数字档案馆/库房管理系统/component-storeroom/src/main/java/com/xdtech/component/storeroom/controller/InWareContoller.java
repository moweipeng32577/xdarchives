package com.xdtech.component.storeroom.controller;

import com.alibaba.fastjson.JSON;
import com.xdtech.component.storeroom.entity.*;
import com.xdtech.component.storeroom.repository.OutWareRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.InWareService;
import com.xdtech.component.storeroom.service.StorageService;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 入库控制器
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping("/inware")
public class InWareContoller {

    @Autowired
    private InWareService inWareService;

    @Autowired
    OutWareRepository outWareRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    private ZonesRepository zonesRepository;

    @Autowired
    ZoneShelvesRepository zoneShelvesRepository;

    @PersistenceContext
    EntityManager entityManager;

    /*
    * 入库记录
    * */
    @RequestMapping(value = "/inwares", method = RequestMethod.GET)
    @ResponseBody
    public Page<InWare_History> getInWares(int page, int limit){
        //return dataopenService.getBoxEntryIndex(page,limit);
        return inWareService.findAll(page, limit);
    }

    @RequestMapping(value = "/inware/{inids}", method = RequestMethod.GET)
    @ResponseBody
    public InWare getInWare(){
        return null;
    }

    @RequestMapping(value = "/inware", method = RequestMethod.POST)
    @ResponseBody
    public void saveInWare(){

    }

    @RequestMapping(value = "/inwares/{inids}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteInware(){

    }

    @RequestMapping("/main")
    public String inware(Model model) {
//        List<String> nodeids=zonesRepository.findNodeids("文书档案-永久");
//        if(nodeids.size()>0){
//            model.addAttribute("templateNodeid", nodeids.get(0));
//        }else{
            model.addAttribute("templateNodeid", "12345678910");
//        }
        return "/inlet/storeroom/inware";
    }

    @RequestMapping("/main2")
    public String inware2() {
        return "/inlet/storeroom/shelves";
    }

 /**
  * 新增入库
  * @param waretype
  * @param shid
  * @param ids
  * @param description
  * @return
  */
    @RequestMapping("/save")
    @ResponseBody
    public ExtMsg save(String waretype, String shid, String ids, String description){

     //从session获取用户名
     HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
     HttpSession session=request.getSession();
     String user=(String)session.getAttribute("username");

     ZoneShelves sh = new ZoneShelves();
     sh.setShid(shid);
     InWare iw = new InWare();
     iw.setWaretype(waretype);
     iw.setWareuser(user);
     iw.setDescription(description);
     if(iw.getStorages() == null){
      iw.setStorages(new HashSet<Storage>());
     }
     for(String id : ids.split(",")){
      Storage st = new Storage();
      st.setEntry(id);
      st.setZoneShelves(sh);
      st.setStorestatus(Storage.STATUS_IN);
      //需要增加chipcode,类扫描识别码(档号)，暂定存档号
      //st.setChipcode("");
      iw.getStorages().add(st);
     }
     inWareService.save(iw);
     inWareService.updateStore(shid,ids);//更新存储位置
     return new ExtMsg(true,"入库成功",null);
    }

   /**
   * 归还入库
   * @param ids
   * @return
   */
    @RequestMapping("/returnware")
    @ResponseBody
    public ExtMsg returnWare(String ids, String remarkText,String returnware){
     //从session获取用户名
     HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
     HttpSession session=request.getSession();
     String user=(String)session.getAttribute("username");

     //获取根据entryid 获取对应的storage
     InWare iw = new InWare();
     iw.setWaretype("归还入库");
     iw.setWareuser(user);
     iw.setReturnware(returnware);
     iw.setDescription(remarkText);
     if(iw.getStorages() == null){
      iw.setStorages(new HashSet<Storage>());
     }



     for(String entryid:ids.split(",")){
         Storage s=new Storage();
         s=storageRepository.findByEntry(entryid);
         s.setStorestatus(Storage.STATUS_IN);
         iw.getStorages().add(s);
     }

     inWareService.save(iw);
     Map<String ,String > map = storageService.findByEntryid(ids.split(",")[0]);

     return new ExtMsg(true,"归还入库成功",map);
    }


    /**
     * 根据档号获取相应入库记录
     * @param dhCode
     * @param
     * @param
     * @return
     */
    @RequestMapping("/inwares/{dhCode}")
    @ResponseBody
    public Page<InWare_History> findshelves(@PathVariable String dhCode, int page, int limit,HttpServletResponse httpServletResponse){

        PageRequest pageRequest = new PageRequest(page-1, limit);

        return inWareService.findInWares(dhCode, pageRequest);
    }

}
