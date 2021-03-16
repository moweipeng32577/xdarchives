package com.wisdom.web.controller;/**
 * Created by yl on 2020/3/17.
 */

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_datareceive;
import com.wisdom.web.entity.Tb_long_retention;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.DatareceiveRepository;
import com.wisdom.web.service.DataReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * 数据接收控制器（接收数据开放-发布政务网的数据包）
 * Created by tanly on 2017/12/1 0001.
 */
@Controller
@RequestMapping(value = "/datareceive")
public class DataReceiveController {

    @Autowired
    DataReceiveService dataReceiveService;

    @Autowired
    DatareceiveRepository datareceiveRepository;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String dataopen(Model model,String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/datareceive";
    }

    @RequestMapping(value = "/getRelease", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_datareceive> getRelease(String state,String type, int page, int start, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return dataReceiveService.getRelease(state,type, page, start, limit, sortobj);
    }

    @RequestMapping("/deletOpenFile")
    @ResponseBody
    public void deletOpenFile(String fileName) {
        dataReceiveService.deletOpenFile(fileName);
    }

    @RequestMapping("/deletReleasedFile")
    @ResponseBody
    public ExtMsg deletReleasedFile(String filenames) {
        return dataReceiveService.deletReleasedFile(filenames);
    }

    @RequestMapping("/deletThematicFile")
    @ResponseBody
    public ExtMsg deletThematicFile(String filenames) {
        return dataReceiveService.deletThematicFile(filenames);
    }

    @RequestMapping("/thematicReceive")
    @ResponseBody
    public ExtMsg thematicReceive(String filenames) {
        return dataReceiveService.thematicReceive(filenames);
    }

    @RequestMapping("/verification")
    @ResponseBody
    public ExtMsg verification(String receiveids) {
        String msg=dataReceiveService.verification(receiveids);
        return new ExtMsg(true, msg, null);
    }

    //上传数据包
    @RequestMapping(value = "/uploadDataPackage")
    @ResponseBody
    public String uploadDataPackage(MultipartFile source , String dataType) throws Exception{
        ExtMsg msg=new ExtMsg(true,"","");
        if("thematic".equals(dataType)){
            msg=dataReceiveService.uploadDataPackage(source,"thematic");
        }else {
            msg=dataReceiveService.uploadDataPackage(source,"dataopen");
        }
        return "{'success':"+msg.isSuccess()+",'msg':'"+msg.getMsg()+"'}";
    }

    //下载数据包
    @RequestMapping(value = "/downloadDataPackage")
    @ResponseBody
    public void downloadDataPackage(String receiveid, HttpServletResponse response) {
        Tb_datareceive datareceive=datareceiveRepository.findByReceiveid(receiveid);
        if(datareceive!=null) {
            File html_file = new File(datareceive.getFilepath());
            if(html_file.exists()) {
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", "attachment; filename="
                            + new String(datareceive.getFilepath().substring(datareceive.getFilepath().lastIndexOf("\\") + 1).getBytes("gbk"), "iso8859-1"));
                    response.setContentType("application/zip");
                    ServletOutputStream out;
                    FileInputStream inputStream = new FileInputStream(html_file);
                    out = response.getOutputStream();
                    int b = 0;
                    byte[] buffer = new byte[1024];
                    while ((b = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, b);
                    }
                    inputStream.close();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/getImplementResult", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_long_retention> getRelease(String receiveid,int page, int start, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return dataReceiveService.getImplementResult(receiveid, page, start, limit, sortobj);
    }
}
