package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.service.BranchAuditServiec;
import com.wisdom.web.service.ProjectRateService;
import com.wisdom.web.service.ThematicService;
import com.xdtech.project.lot.mjj.message.type.Int;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2020/9/17.
 */
@Controller
@RequestMapping("/branchAudit")
public class BranchAuditController {

    @Autowired
    BranchAuditServiec branchAuditServiec;

    @Autowired
    ThematicService thematicService;

    @RequestMapping("/main")
    public String index(Model model){
        return "/inlet/branchAudit";
    }

    //分馆审核
    @RequestMapping("/auditSubmit")
    @ResponseBody
    public ExtMsg auditSubmit(String ids, String approveresult, String areaText) {
        int count = 0;
        if("通过".equals(approveresult)){//审核通过更改状态为"已通过"
            if(thematicService.releaseThmatic(ids,"已通过")){
                //更新任务状态
                branchAuditServiec.updateTask(ids,"分管领导审核");
                return new ExtMsg(true, "审核完成", null);
            }else{
                return new ExtMsg(false, "审核失败", null);
            }
        }else {//审核不通过退回
            count = branchAuditServiec.updateThematicPublishstate(ids,"已退回",areaText);
            if(count>0){
                //更新任务状态
                branchAuditServiec.updateTask(ids,"分管领导审核");
                return new ExtMsg(true, "审核完成", null);
            }
            return new ExtMsg(false, "审核失败", null);
        }
    }
}
