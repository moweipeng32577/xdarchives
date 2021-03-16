package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.EquipmentDefendService;
import com.wisdom.web.service.EquipmentService;
import com.wisdom.web.service.LogService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 设备管理控制器
 */
@Controller
@RequestMapping(value = "/equipment")
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    LogService logService;

    @Autowired
    EquipmentDefendService equipmentDefendService;

    @RequestMapping("/main")
    public String questionManagement(Model model,String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        return "/inlet/equipment";
    }

    @RequestMapping("/getEquipments")
    @ResponseBody
    public Page<Tb_equipment> getEquipments(int page, int start, int limit, String condition, String operator,
                                            String content, String sort){
      return equipmentService.getEquipments(page,start,limit,condition,operator,content,sort);
    }

    @RequestMapping(value = "/electronicsFile/{equipmentID}", method = RequestMethod.POST)
    @ResponseBody
    public List<Tb_electronic> electronicsFile(@PathVariable String equipmentID) {
        return equipmentService.getEquipmentFile(equipmentID);
    }

    public Map<String, Object> parse(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isMutipart = ServletFileUpload.isMultipartContent(request);
        result.put("mutipart", isMutipart);
        if (isMutipart) {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            result.put("id", req.getParameter("id"));
            result.put("filename", req.getParameter("name"));
            result.put("chunk", req.getParameter("chunk"));
            result.put("chunks", req.getParameter("chunks"));

            Iterator iterator = req.getFileNames();
            while (iterator.hasNext()) {
                MultipartFile file = req.getFile((String) iterator.next());
                result.put("size", file.getSize());
                result.put("content", file.getBytes());
            }
        }
        return result;
    }


    /**
     *
     * @param request
     *            请求对象
     * @param response
     *            响应对象
     * @throws Exception
     */
    @RequestMapping(value = "/electronicsEquipment", method = RequestMethod.POST)
    @ResponseBody
    public void electronicsEquipment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = parse(request);
        if ((boolean) params.get("mutipart")) {
            if (params.get("chunk") != null) { // 文件分片上传
                equipmentService.uploadchunk(params);
            } else { // 文件单片上传
                equipmentService.uploadfileInform(params);
            }
        }
    }

    @RequestMapping(value = "/electronics/{eleids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deletefile(@PathVariable String eleids) {
        Integer num = equipmentService.deleteElectronic(eleids);
        if (num > 0) {
            return new ExtMsg(true, "删除成功", num);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    @RequestMapping(value = "/electronics/{filename}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg uploadztadd(@PathVariable String filename) {
        Map<String, Object> map = equipmentService.saveElectronic(null, filename);
        ExtMsg msg = new ExtMsg(true, "", map);
        return msg;
    }


    @RequestMapping(value = "/addEquipment")
    @ResponseBody
    public ExtMsg addEquipment(String[] eleids,String equipmentID,String acceptancetime,String amount,String brand,String model,String type,
                               String name,String price,String purchasetime,String remarks,String specifications,String ipAddress,String organname){
        Tb_equipment equipment = equipmentService.addEquipment(eleids,equipmentID,acceptancetime,amount,brand,model,type,name,price,purchasetime,remarks,specifications,ipAddress,organname);
        String msg = "";
        if(null!=equipmentID&&!"".equals(equipmentID)){
            msg = "设备管理;修改设备:"+name+"条目id:"+equipmentID;
        }else {
            msg = "设备管理;新增设备:"+name+"条目id:"+equipment.getEquipmentID();;
        }
        logService.recordTextLog("设备管理",msg);
        if (equipment != null ){
            return new ExtMsg(true,"操作成功！",null);
        }
        return new ExtMsg(false,"添加失败！",null);
    }

    @RequestMapping(value = "/deleteEquipment")
    @ResponseBody
    public ExtMsg deleteEquipment(String[] equipmentIDs){
        boolean result = equipmentService.deleteEquipment(equipmentIDs);
        if (result){
            return new ExtMsg(true,"删除成功！",null);
        }
        return new ExtMsg(false,"删除失败！",null);
    }

    @RequestMapping("/getEquipmentDefendByEquipmentId")
    @ResponseBody
    public Page<Tb_equipment_defend> getEquipmentDefendByequipmentId(String equipmentid, int page, int limit, String sort, String condition, String operator, String content) {
        return equipmentDefendService.getEquipmentDefendByequipmentId(equipmentid, page, limit, sort, condition, operator, content);
    }

    @RequestMapping("/getEquipmentDefendById")
    @ResponseBody
    public ExtMsg getEquipmentDefendById(String id) {
        Tb_equipment_defend defend= equipmentDefendService.getEquipmentDefendById(id);
        if (defend != null) {
            return new ExtMsg(true, "", defend);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/loadEquipmentDefend")
    @ResponseBody
    public ExtMsg loadEquipmentDefend() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_equipment_defend defend = new Tb_equipment_defend();
        defend.setDefenduser(userDetails.getRealname());
        defend.setDefendtime(datastr);
        defend.setPhonenum(userDetails.getPhone());
        return new ExtMsg(true, "", defend);
    }

    @RequestMapping("/equipmentDefendSubmit")
    @ResponseBody
    public ExtMsg equipmentDefendSubmit(Tb_equipment_defend equipmentDefend, String equipmentId) {
        Tb_equipment_defend tb_equipment_defend = equipmentDefendService.equipmentDefendSubmit(equipmentDefend, equipmentId);
        if (tb_equipment_defend != null) {
            return new ExtMsg(true, "", null);
        }
        return new ExtMsg(false, "", null);
    }

    @RequestMapping("/deleteEquipmentDefendByid")
    @ResponseBody
    public ExtMsg deleteEquipmentDefendByid(String[] ids) {
        int count = 0;
        count = equipmentDefendService.deleteEquipmentDefendByid(ids);
        if (count > 0) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }


    @RequestMapping(value = "/importEquipment", method = RequestMethod.POST)
    @ResponseBody
    public String importEquipment(MultipartFile equipmentExcel) throws Exception{
        return equipmentService.importEquipment(equipmentExcel);
    }
}
