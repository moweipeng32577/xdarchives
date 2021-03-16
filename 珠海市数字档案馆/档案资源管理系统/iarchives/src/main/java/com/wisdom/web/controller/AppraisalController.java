package com.wisdom.web.controller;

import com.wisdom.util.GuavaCache;
import com.wisdom.util.GuavaUsedKeys;
import com.wisdom.util.TimeScheduled;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.AppraisalService;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.NodesettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 到期鉴定管理控制器
 * Created by yl on 2017/10/25.
 */
@Controller
@RequestMapping(value = "/appraisal")
public class AppraisalController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AppraisalService appraisalService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    WorkRepository workRepository;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    TimeScheduled timeScheduled;

    @RequestMapping("/main")
    public String main(Model model) {
        model.addAttribute("reportServer",reportServer);
        Tb_work workAppraisal = workRepository.findByWorktext("销毁审批");
        model.addAttribute("appraisalNumber",getAppraisalNumber());//过期鉴定数量
        if(workAppraisal!=null){
            model.addAttribute("appraisalSendmsg","1".equals(workAppraisal.getSendmsgstate())?true:false);  //销毁是否短信通知
        }else{
            model.addAttribute("appraisalSendmsg",false);
        }
        return "/inlet/appraisal";
    }

    //根据用户数据权限算出节点数以及鉴定条目数(防止当天修改权限节点导致到期鉴定数据不匹配)
    public Integer[] getAppraisalNumber(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_data_node> userNodeList=nodesettingService.findUserNodeList(userDetails.getUserid());//用户个人权限节点
        List<Tb_data_node> userRoleNodeList=nodesettingService.findRoleNodeList(userDetails.getUserid());//用户角色权限节点
        userRoleNodeList.removeAll(userNodeList);
        userNodeList.addAll(userRoleNodeList);
        Integer[] returnData=new Integer[]{0,0};
        timeScheduled.saveAppraisalNode();
        Map<String,Integer> dataMap=(Map<String,Integer>) GuavaCache.getValueByKey(GuavaUsedKeys.APPRAISAL_NODE);
        userNodeList.parallelStream().forEach(node->{
            Object o=dataMap.get(node.getNodeid().trim());
            if(o!=null&&Integer.valueOf(o.toString())>0){
                returnData[0]+=Integer.parseInt(o.toString());
                returnData[1]++;
            }
        });
        return returnData;
    }

    @RequestMapping("/updateAppraisal")
    @ResponseBody
    public ExtMsg updateAppraisal() {
        timeScheduled.updateAppraisal();
        GuavaCache.removeValueByKey(GuavaUsedKeys.APPRAISAL_NODE);
        return new ExtMsg(true, "更新节点树成功，需重新打开页面", null);
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_index_detail> getEntryIndex(int page, int start, int limit, String condition, String operator, String content,String nodeid,String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return entryIndexService.findByNodeid(page,start,limit,condition, operator,content,nodeid,sortobj);
    }

    @RequestMapping("/againAppraisal")
    @ResponseBody
    public ExtMsg againAppraisal(String approvaldate, String entryid) {
        return entryIndexService.againAppraisal(approvaldate,entryid);
    }

    @RequestMapping("/getNodeBill")
    @ResponseBody
    public Page<Tb_bill> getNodeBill(String nodeid, int page, int start, int limit, String condition, String operator, String content) {
        logger.info("nodeid:"+nodeid+";page:" + page + ";start:" + start + ";limt:" + limit);
        return appraisalService.findBillBySearch(page, limit, condition, operator, content, nodeid);
    }

    @RequestMapping("/billEntry")
    @ResponseBody
    public Page<Tb_bill_entry> getBillEntry(String billid, int page, int start, int limit, String condition, String operator, String content) {
        logger.info("billid:"+billid+";page:" + page + ";start:" + start + ";limt:" + limit);
        String[] entryidData = appraisalService.getEntryidsByBillid(billid);
        return appraisalService.findBillEntryBySearch(page, limit, condition, operator, content, entryidData);
    }
}
