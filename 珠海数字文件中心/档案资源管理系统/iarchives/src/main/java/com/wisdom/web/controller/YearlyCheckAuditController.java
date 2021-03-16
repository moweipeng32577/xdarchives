package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.YearlyCheckApproveDocRepository;
import com.wisdom.web.service.YearlyCheckAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2020/10/15.
 */
@Controller
@RequestMapping(value = "/yearlyCheckAudit")
public class YearlyCheckAuditController {




    @Autowired
    YearlyCheckAuditService yearlyCheckAuditService;

    @Autowired
    YearlyCheckApproveDocRepository yearlyCheckApproveDocRepository;

    @Autowired
    WebSocketService webSocketService;


    @RequestMapping("/main")
    public String index(Model model,String taskid){
        if(taskid!=null){
            model.addAttribute("taskId",taskid);
        }else{
            model.addAttribute("taskId","");
        }
        return "/inlet/yearlyCheckAudit";
    }

    @RequestMapping("/getYearlyCheckAuditTasks")
    @ResponseBody
    public Page<Tb_task> getYearlyCheckAuditTasks(int page, int limit, String sort){
        return yearlyCheckAuditService.getYearlyCheckAuditTasks(page,limit,sort);
    }

    @RequestMapping("/getYearlyCheckApproveDoc")
    @ResponseBody
    public ExtMsg getYearlyCheckApproveDoc(String id){
        Tb_yearlycheck_approvedoc approvedoc = yearlyCheckApproveDocRepository.getApproveDocByTaskid(id);
        return new ExtMsg(true,"",approvedoc);
    }

    @RequestMapping("/getYearlyCheckApproveReportsByTaskid")
    @ResponseBody
    public Page<Tb_yearlycheck_report> getYearlyCheckApproveReportsByTaskid(String taskid, int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return yearlyCheckAuditService.getYearlyCheckApproveReportsByTaskid(taskid,page,limit,sortobj);
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String taskid) {
        return yearlyCheckAuditService.getNodes(taskid);
    }

    @RequestMapping("/approveFormSubmit")
    @ResponseBody
    public ExtMsg approveFormSubmit(String textArea, String nextNode, String nextSpman, String taskid) {
        Tb_yearlycheck_approvedoc approvedoc = yearlyCheckAuditService.approveFormSubmit(textArea, nextNode, nextSpman, taskid);
        webSocketService.noticeRefresh();
        if(approvedoc!=null){
            return new ExtMsg(true, "审批完成", null);
        }else{
            return new ExtMsg(true, "审批失败", null);
        }
    }

    //审核-退回
    @RequestMapping("/pproveFormBack")
    @ResponseBody
    public ExtMsg pproveFormBack(String textarea, String taskid){
        yearlyCheckAuditService.pproveFormBack(textarea,taskid);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }
}
