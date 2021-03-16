package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.LogService;
import com.wisdom.web.service.PartyBuildingService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/partyBuilding")
public class PartyBuildingController {

    @Autowired
    PartyBuildingService partyBuildingService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LogService logService;

    @Autowired
    NoticeController noticeController;

    @Autowired
    InformController informController;

    @RequestMapping("/main")
    public String partyBuilding(Model model, String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        return "/inlet/partyBuilding";
    }

    @RequestMapping("/htmledit")
    public String htmledit() {
        return "/inlet/htmledit";
    }

    @RequestMapping("/getPartyBuilding")
    @ResponseBody
    public Page<Tb_partybuilding> getPartyBuilding(int page, int limit, String condition, String operator,
                                                   String content, String sort, String type){
        return partyBuildingService.getPartyBuilding(page,limit,condition,operator,content,sort,type);
    }

    @RequestMapping(value = "/electronicsFile/{partybuildingID}", method = RequestMethod.POST)
    @ResponseBody
    public List<Tb_electronic> electronicsFile(@PathVariable String partybuildingID) {
        return partyBuildingService.getFile(partybuildingID);
    }

    @RequestMapping("/addPartyBuilding")
    @ResponseBody
    public ExtMsg addPartyBuilding(String partybuildingID,String[] eleids,String title,String organ,String publishstate,String stick,String html){
        String msg = "";
        if(noticeController.getStrLength(html)>=5000){
            return new ExtMsg(false,"内容过多过长，请修改！",null);
        }
        Tb_partybuilding partybuilding = partyBuildingService.addPartyBuilding(partybuildingID,eleids,title,organ,publishstate,stick,html);
        if(null!=partybuildingID&&!"".equals(partybuildingID)){
            msg = "党风廉政建设;修改:"+title+"条目id:"+partybuildingID;
        }else {
            msg = "党风廉政建设;新增:"+partybuilding.getTitle()+"条目id:"+partybuilding.getPartybuildingID();
        }
        logService.recordTextLog("党风廉政建设",msg);
        if (partybuilding != null){
            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    @RequestMapping(value = "/electronics/{eleids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deletefile(@PathVariable String eleids) {
        Integer num = partyBuildingService.deleteElectronic(eleids);
        if (num > 0) {
            return new ExtMsg(true, "删除成功", num);
        }
        return new ExtMsg(false, "删除失败", null);
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

    @RequestMapping(value = "/electronicsPartyBuilding", method = RequestMethod.POST)
    @ResponseBody
    public void electronicsPartyBuilding(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = parse(request);
        if ((boolean) params.get("mutipart")) {
            if (params.get("chunk") != null) { // 文件分片上传
                partyBuildingService.uploadchunk(params);
            } else { // 文件单片上传
                partyBuildingService.uploadfileInform(params);
            }
        }
    }

    @RequestMapping(value = "/electronics/{filename}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg uploadztadd(@PathVariable String filename) {
        Map<String, Object> map = partyBuildingService.saveElectronic(null, filename);
        ExtMsg msg = new ExtMsg(true, "", map);
        return msg;
    }

    @RequestMapping("/setStick")
    @ResponseBody
    public ExtMsg setStick(String[] ids,String level){
        ExtMsg msg = new ExtMsg(true,"置顶成功",null);
        try {
            partyBuildingService.setStick(ids,level);
            webSocketService.noticeRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            msg.setMsg("置顶失败");
        }
        return msg;
    }

    @RequestMapping("/cancelStick")
    @ResponseBody
    public ExtMsg cancelStick(String[] ids){
        ExtMsg msg = new ExtMsg(true,"取消置顶成功",null);
        try {
            boolean state = partyBuildingService.cancelStick(ids);
            if(state){
                webSocketService.noticeRefresh();
            }else{
                msg.setMsg("非置顶问卷");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setMsg("取消置顶失败");
        }
        return msg;
    }

    @RequestMapping("/publish")
    @ResponseBody
    public ExtMsg publish(String[] partybuildingIDs,String state){
        Integer result = partyBuildingService.publish(partybuildingIDs,state);
        if (result < 1){
            return new ExtMsg(false,"操作失败",null);
        }
        return new ExtMsg(true,"操作成功",null);
    }

    @RequestMapping("/deletePartyBuilding")
    @ResponseBody
    public ExtMsg deletePartyBuilding(String[] partybuildingIDs){
        boolean result = partyBuildingService.deletePartyBuilding(partybuildingIDs);
        if (result){
            return new ExtMsg(true,"删除成功！",null);
        }
        return new ExtMsg(false,"删除失败！",null);
    }

    @RequestMapping("/findPartyBuilding")
    @ResponseBody
    public ExtMsg findPartyBuilding(String id){
        List<Tb_partybuilding> partybuilding = partyBuildingService.findPartyBuilding(id);
        if (partybuilding != null){
            return new ExtMsg(true,"操作成功",partybuilding);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    @RequestMapping("/getPartyBuildings")
    @ResponseBody
    public IndexMsg getPartyBuildings(int page, int limit, String sort, String condition, String operator, String content){
        Page<Tb_partybuilding> partyBuildingPage = partyBuildingService.getPartyBuilding(page,limit,condition,operator,content,sort,"1");
        List<Tb_partybuilding> partyBuildingList = partyBuildingPage.getContent();
        List<Tb_partybuilding> returnList = new ArrayList<>();
        for(Tb_partybuilding partybuilding : partyBuildingList){
            Tb_partybuilding partybuildingReturn = new Tb_partybuilding();
            BeanUtils.copyProperties(partybuilding,partybuildingReturn);
            Tb_user user = userRepository.findByUserid(partybuilding.getUserID());
            partybuildingReturn.setUserID(user.getRealname());
            returnList.add(partybuildingReturn);
        }
        return new IndexMsg(true,"200","成功",returnList);
    }

    //上传图片
    @RequestMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("fileToUpload") MultipartFile[] multfiles) {
        return informController.uploadImage(multfiles);
    }
}
