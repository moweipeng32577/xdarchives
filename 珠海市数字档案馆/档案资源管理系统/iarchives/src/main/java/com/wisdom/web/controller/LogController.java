package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.repository.SxLogMsgRepository;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_log_msg;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;

/**
 * 日志管理控制器
 * Created by xd on 2017/9/28.
 */
@Controller
@RequestMapping(value = "/log")
public class LogController {

    @Autowired
    LogService logService;

    @Autowired
    SxLogMsgRepository sxlogMsgRepository;

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    @RequestMapping("/main")
    public String index(Model model, String isp) {
    	Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        model.addAttribute("openSxData",openSxData);
		return "/inlet/log";
    }

    /**
     * 根据条件获取日志数据
     * @param page 页码
     * @param limit 每页数
     * @param condition 查询字段
     * @param operator 查询方式
     * @param content 查询内容
     * @return
     */
    @RequestMapping("/findLogDetailBySearch")
    @ResponseBody
    public Page<Object> findLogDetailBySearch(int page, int limit,String flag, String condition, String operator, String content, String sort) {
        /*if("声像系统".equals(flag)||"新闻系统".equals(flag)){
            return logService.findSxBySearch(flag,page, limit, condition, operator, content, sort);
        }else{
            Sort sortobj = WebSort.getSortByJson(sort);
            return logService.findBySearch(page, limit, condition, operator, content, sortobj);
        }*/
        Sort sortobj = WebSort.getSortByJson(sort);
        return logService.findBySearch(page, limit, condition, operator, content, sortobj,flag);
    }

    @RequestMapping("/deleteLogDetail")
    @ResponseBody
    public ExtMsg deleteLogDetail(String[] ids) {
        return logService.deleteLogDetail(ids);
    }

    @RequestMapping("/exportOtherFormat")
    public void exportOhterFormat(String[] ids, HttpServletResponse res){
        logService.exportOtherFormat(ids,res);
    }

    @RequestMapping("/exportParameter")
    @ResponseBody
    public ExtMsg exportParameter(String flag, String lmids, String isSelectAll, String fileName, String sheetName, String
            condition,String operator, String content) {
        List<Object> logMsgs = new ArrayList<>();
        if("true".equals(isSelectAll)){
            List<String> idList = Arrays.asList(lmids.split(","));
            logMsgs=logService.getLogMsgList(flag, condition,
                    operator,content);
            Iterator<Object> it=logMsgs.iterator();
            while(it.hasNext()){
                Tb_log_msg  tb_log_msg =(Tb_log_msg)it.next();
                if (idList.contains(tb_log_msg.getId())) {
                    it.remove();
                }
            }
        }else{
            String[] lmidData = lmids.split(",");
            logMsgs = logService.getFindAllById(flag, lmidData);
        }
        String filePat = logService.exportLogMsg(flag, logMsgs,fileName,sheetName);
        return new ExtMsg(true,"获取成功",filePat);
    }

    @RequestMapping("/downloadLogFile")
    @ResponseBody
    public  void downloadLogFile(HttpServletRequest request, HttpServletResponse response,String filePath) throws Exception{
        File file=new File(filePath);
        InputStream inputStream = new FileInputStream(new File(filePath));
        OutputStream out = response.getOutputStream();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=\"" + new String(file.getName().getBytes(), "iso-8859-1") + "\"");
        byte[] b = new byte[1024 * 1024 * 10];
        int leng = 0;
        while ((leng = inputStream.read(b)) != -1) {
            out.write(b, 0, leng);
        }
        out.flush();
        inputStream.close();
        out.close();
        file.delete();
    }

    /**
     * 根据月份获取总访问人次
     * @return
     */
    @RequestMapping("/visitMain")
    public String visitMain(Model model){
        return "/setting/visitNum";
    }


    /**
     * 根据月份获取总访问人次
     * @return
     */
    @RequestMapping("/getVisitNumAvg")
    @ResponseBody
    public Map<String, Integer> getVisitNumAvg(int month, String date){
       return logService.getVisitNumAvg(month,date);
    }


}