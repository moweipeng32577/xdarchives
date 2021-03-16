package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.YearlyCheckElectronicRepository;
import com.wisdom.web.repository.YearlyCheckReportRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.BusinessYearlyCheckService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/10/13.
 */
@Controller
@RequestMapping(value = "/businessYearlyCheck")
public class BusinessYearlyCheckController {


    @Autowired
    BusinessYearlyCheckService businessYearlyCheckService;

    @Autowired
    YearlyCheckReportRepository yearlyCheckReportRepository;

    @Autowired
    YearlyCheckElectronicRepository yearlyCheckElectronicRepository;

    @Autowired
    WebSocketService webSocketService;

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录

    @RequestMapping("/main")
    public String index(){
        return "/inlet/businessYearlyCheck";
    }

    @RequestMapping("/addSubmit")
    @ResponseBody
    public Map<String, Object> addSubmit(Tb_yearlycheck_report yearlycheckReport, MultipartFile source) throws Exception {
        Map<String, Object> resMap = new HashMap<String, Object>();
        Tb_yearlycheck_report tbYearlycheckReport = businessYearlyCheckService.addSubmit(yearlycheckReport,source);
        if(tbYearlycheckReport!=null){
            resMap.put("success","true");
        }else {
            resMap.put("success","false");
        }
        return resMap;
    }

    @RequestMapping("/getYearlyCheckReports")
    @ResponseBody
    public Page getYearlyCheckReports(int page,int limit, String condition, String operator, String content, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return businessYearlyCheckService.getYearlyCheckReports(page,limit,condition,operator,content,sortobj);
    }

    @RequestMapping("/getYearlyCheckReportsByState")
    @ResponseBody
    public Page getYearlyCheckReportsByState(String state,int page,int limit, String condition, String operator, String content, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return businessYearlyCheckService.getYearlyCheckReportsByState(state,page,limit,condition,operator,content,sortobj);
    }

    @RequestMapping("/deleteYearlyCheckElectronic")
    @ResponseBody
    public ExtMsg deleteYearlyCheckElectronic(String[] ids) {
        businessYearlyCheckService.deleteYearlyCheckElectronic(ids);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/getYearlyCheckReport")
    @ResponseBody
    public ExtMsg getYearlyCheckReport(String id) {
        return new ExtMsg(true,"",yearlyCheckReportRepository.findById(id));
    }

    @RequestMapping("/saveSubmit")
    @ResponseBody
    public ExtMsg saveSubmit(Tb_yearlycheck_report yearlycheckReport){
        businessYearlyCheckService.saveSubmit(yearlycheckReport);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/downLoadElectronic")
    @ResponseBody
    public void downLoadElectronic(HttpServletRequest request, HttpServletResponse response,String id){
        Tb_yearlycheck_electronic yearlycheckReport = yearlyCheckElectronicRepository.findByReportid(id);
        File eleFile = new File(rootpath+yearlycheckReport.getFilepath(),yearlycheckReport.getFilename());
        try {
            response.setContentType("charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + getOutName(request, eleFile.getName()) + "\"");
            FileInputStream inputStream = new FileInputStream(eleFile);
            OutputStream out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.flush();
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static String getOutName(HttpServletRequest request, String name) throws IOException {
        String outName = MimeUtility.encodeText(name, "UTF8", "B");
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        String browseName = browser.getName()!=null?browser.getName().toLowerCase():"";
        if (browseName.indexOf("internet explorer")>-1) {
            outName = URLEncoder.encode(name, "UTF8");
        }
        return outName;
    }

    @RequestMapping("/getYearlyCheckApproveDoc")
    @ResponseBody
    public ExtMsg getYearlyCheckApproveDoc(){
        Tb_yearlycheck_approvedoc approvedoc = new Tb_yearlycheck_approvedoc();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        String nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        approvedoc.setSubmiter(userDetails.getRealname());
        approvedoc.setSubmittime(nowdate);
        return new ExtMsg(true,"",approvedoc);
    }

    @RequestMapping("/getYearlyCheckApproveReports")
    @ResponseBody
    public Page<Tb_yearlycheck_report> getYearlyCheckApproveReports(String[] ids,int page,int limit,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return businessYearlyCheckService.getYearlyCheckApproveReports(ids,page,limit,sortobj);
    }

    /**
     * 提交
     * @return
     */
    @RequestMapping("/approveDocSubmit")
    @ResponseBody
    public ExtMsg approveDocSubmit(Tb_yearlycheck_approvedoc approvedoc, String spman, String[] ids) {
        Tb_yearlycheck_approvedoc yearlycheckApprovedoc =  businessYearlyCheckService.approveDocSubmit(approvedoc,spman,ids);
        webSocketService.noticeRefresh();
        if(yearlycheckApprovedoc != null){
            return new ExtMsg(true,"单据提交成功",null);
        }
        return new ExtMsg(false,"单据提交失败",null);
    }
}
