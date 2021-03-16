package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.NoticeService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.checkerframework.checker.units.qual.A;
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
import java.util.*;

@Controller
@RequestMapping(value = "/notice")

public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InformController informController;

    @RequestMapping("/main")
    public String getNotice(Model model,String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        return "/inlet/notice";
    }

    @RequestMapping("/htmledit")
    public String htmledit() {
        return "/inlet/htmledit";
    }

    @RequestMapping("/getNotices")
    @ResponseBody
    public Page<Tb_notice> getNotices(int page, int limit, String condition, String operator,
                                      String content, String sort, String type){
        return noticeService.getNotices(page,limit,condition,operator,content,sort,type);
    }

    @RequestMapping("/getPublishNotices")
    @ResponseBody
    public List getPublishNotices(){
        List<Tb_notice> noticeList = noticeService.getPublishNotices();
        List<Tb_notice> returnList = new ArrayList<>();
        for (Tb_notice notices : noticeList){
            Tb_notice notice = new Tb_notice();
            BeanUtils.copyProperties(notices,notice);
            Tb_user user = userRepository.findByUserid(notices.getUserID());
            if(user != null){
                notice.setUserID(user.getRealname());
            }
            //notice.setUserID(user.getRealname());
            returnList.add(notice);
        }
        return returnList;
    }

    @RequestMapping("/addNotice")
    @ResponseBody
    public ExtMsg addNotice(String noticeID,String title,String organ,String publishstate,String stick,String html,String[] eleids){
        if(getStrLength(title)>=200){
            return new ExtMsg(false,"标题字符过长，请修改！",null);
        }
        if(getStrLength(html)>=5000){
            return new ExtMsg(false,"内容过多过长，请修改！",null);
        }
        Tb_notice notice = noticeService.addNotice(noticeID,title,organ,publishstate,stick,html,eleids);
        if (notice != null){
            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    @RequestMapping("/deleteNotice")
    @ResponseBody
    public ExtMsg deleteNotice(String[] noticeIDs){
        boolean result = noticeService.deleteNotice(noticeIDs);
        if (result){
            return new ExtMsg(true,"删除成功！",null);
        }
        return new ExtMsg(false,"删除失败！",null);
    }

    @RequestMapping("/setStick")
    @ResponseBody
    public ExtMsg setStick(String[] ids,String level){
        ExtMsg msg = new ExtMsg(true,"置顶成功",null);
        try {
            noticeService.setStick(ids,level);
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
            boolean state = noticeService.cancelStick(ids);
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
    public ExtMsg publish(String[] noticeIDs,String state){
        Integer result = noticeService.publish(noticeIDs,state);
        if (result < 1){
            return new ExtMsg(false,"操作失败",null);
        }
        return new ExtMsg(true,"操作成功",null);
    }

    @RequestMapping("/findNotice")
    @ResponseBody
    public ExtMsg findNotice(String noticeID){
        Tb_notice notice = noticeService.findNotice(noticeID);
        if (notice != null){
            return new ExtMsg(true,"操作成功",notice);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    @RequestMapping(value = "/electronicsFile/{noticeID}", method = RequestMethod.POST)
    @ResponseBody
    public List<Tb_electronic> electronicsFile(@PathVariable String noticeID) {
        return noticeService.getFile(noticeID);
    }

    @RequestMapping(value = "/electronics/{eleids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deletefile(@PathVariable String eleids) {
        Integer num = noticeService.deleteElectronic(eleids);
        if (num > 0) {
            return new ExtMsg(true, "删除成功", num);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    @RequestMapping(value = "/electronics/{filename}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg uploadztadd(@PathVariable String filename) {
        Map<String, Object> map = noticeService.saveElectronic(null, filename);
        ExtMsg msg = new ExtMsg(true, "", map);
        return msg;
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

    @RequestMapping(value = "/electronicsNotice", method = RequestMethod.POST)
    @ResponseBody
    public void electronicsNotice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = parse(request);
        if ((boolean) params.get("mutipart")) {
            if (params.get("chunk") != null) { // 文件分片上传
                noticeService.uploadchunk(params);
            } else { // 文件单片上传
                noticeService.uploadfileInform(params);
            }
        }
    }

    //上传图片
    @RequestMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("fileToUpload") MultipartFile[] multfiles) {
        return informController.uploadImage(multfiles);
    }
}
