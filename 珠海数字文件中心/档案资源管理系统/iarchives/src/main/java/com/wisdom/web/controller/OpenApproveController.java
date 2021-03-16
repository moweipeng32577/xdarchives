package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ElectronApproveService;
import com.wisdom.web.service.OpenApproveService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xdtech.smsclient.SMSService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  开放审批控制器
 * Created by tanly on 2017/12/5 0005.
 */
@Controller
@RequestMapping(value = "/openApprove")
public class OpenApproveController {
    @Autowired
    OpenApproveService openApproveService;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    OpenmsgRepository openmsgRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

    @Value("${system.report.server}")
    private String reportServer;//报表服务


    @RequestMapping("/main")
    public String index(Model model, String taskid, String flag,String type) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_flows> flows = electronApproveService.getFlowsInfo(taskid, type);
        if (flows.size() > 1) {
    		model.addAttribute("info","数据开放");
    		return "/inlet/missionAdmins";
    	}
        model.addAttribute("flowsText",flows.get(0).getText());
        model.addAttribute("nodeId",flows.get(0).getNodeid());
        model.addAttribute("taskid",taskid);
        model.addAttribute("iflag",flag);
        model.addAttribute("type",type);
        model.addAttribute("realName",userDetails.getRealname());
        Tb_node node = nodeRepository.findByNodeid(flows.get(0).getNodeid());
        if(node.getText().indexOf("初审")!=-1){  //初审
            model.addAttribute("approveText","Fs");
        }else if(node.getText().indexOf("复审")!=-1){  //复审
            model.addAttribute("approveText","Ls");
        }else if(node.getText().indexOf("鉴定")!=-1){  //最终鉴定环节
            model.addAttribute("approveText","Jd");
        }else { //其他环节
            model.addAttribute("approveText","all");
        }
        model.addAttribute("reportServer",reportServer);
        Tb_work workOpen = workRepository.findByWorktext("开放审批");
        if(workOpen!=null){
            model.addAttribute("openSendmsg","1".equals(workOpen.getSendmsgstate())?true:false);  //开放是否短信通知
        }else{
            model.addAttribute("openSendmsg",false);
        }
        model.addAttribute("systemLoginType",systemLoginType);
        return "/inlet/openApprove";
    }

    @RequestMapping("/getOpenDoc")
    @ResponseBody
    public ExtMsg getOpenDoc(String taskid) {
    	Tb_opendoc task = openApproveService.getOpendocList(taskid).get(0);
    	return new ExtMsg(task != null ? true : false,task != null ? "保存成功" : "保存失败",task);
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index_open> getEntryIndex(String taskid, int page, int limit,String condition, String operator,
                                                   String content, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return openApproveService.getEntryIndex(taskid,page,limit,condition,operator,content,sortobj);
    }

    @RequestMapping("/setQxAddSubmit")
    @ResponseBody
    public ExtMsg setQxAddSubmit(String msgid,String finalresult,String entryunit,String appraisedata,String appraisetext,
                                 String updatetitle,String firstresult,String lastresult,String firstappraiser,String lastappraisetext,
                                 String lastappraiser) {
       openApproveService.setQxAddSubmit(msgid,finalresult,entryunit,appraisedata,appraisetext,updatetitle,firstresult,lastresult,firstappraiser,
               lastappraisetext,lastappraiser);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/setZWWQxAddSubmit")
    @ResponseBody
    public ExtMsg setZWWQxAddSubmit(String taskid,String[] dataids,String kfqx) {
        openApproveService.setZWWQxAddSubmit( taskid, dataids, kfqx);
        return new ExtMsg(true,"",null);
    }

	@RequestMapping("/approveSubmit")
	@ResponseBody
	public ExtMsg approveSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,String sendMsg) {
		openApproveService.approveSubmit(textArea, nextNode, nextSpman, taskid, nodeId);
		webSocketService.noticeRefresh();
        Tb_user spuser = userRepository.findByUserid(nextSpman);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"审批成功，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"审批成功",null);
        }else{
            return new ExtMsg(true,"审批成功，短信发送结果为："+returnStr,null);
        }
	}

    @RequestMapping("/returnOpen")
    @ResponseBody
    public ExtMsg returnOpen(String taskid,String nodeId,String textArea){
        openApproveService.returnOpen(taskid,nodeId,textArea);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/getOpenApproveMsg")
    @ResponseBody
    public ExtMsg getOpenApproveMsg(String id){
        return new ExtMsg(true,"",openmsgRepository.findByMsgid(id));
    }

    @RequestMapping("/getSelectAllEntry")
    @ResponseBody
    public ExtMsg getSelectAllEntry(String taskid,String[] deEntryIds){
        List<Tb_openmsg> openmsgList = new ArrayList<>();
        if(deEntryIds!=null){
            openmsgList = openmsgRepository.getOpenmsgsByEntryIds(taskid,deEntryIds);
        }else{
            openmsgList = openmsgRepository.getOpenmsgs(taskid);
        }
        String[] entryids = new String[openmsgList.size()];
        Map<String, Tb_openmsg> map = new HashMap<String, Tb_openmsg>();
        for (int i=0;i<openmsgList.size();i++) {
            map.put(openmsgList.get(i).getEntryid().trim(), openmsgList.get(i));
            entryids[i] = openmsgList.get(i).getEntryid();
        }
        List<Tb_entry_index> entry_indexs = entryIndexRepository.findByEntryidIn(entryids);
        List<Tb_entry_index> returnList = new ArrayList<>();
        for (Tb_entry_index entry_index : entry_indexs) {
            Tb_entry_index entry_indexNew = new Tb_entry_index();
            BeanUtils.copyProperties(entry_index, entry_indexNew);
            Tb_openmsg openmsg = map.get(entry_index.getEntryid());
            entry_indexNew.setEleid(openmsg.getMsgid());
            returnList.add(entry_indexNew);
        }
        return new ExtMsg(true,"",returnList);
    }

    @RequestMapping("/getApproveEntryId")
    @ResponseBody
    public ExtMsg getApproveEntryId(String batchnum,String type){
        List<Tb_entry_index_open> entryIds=openApproveService.getApproveEntryId(batchnum,type);
        if(entryIds!=null&&entryIds.size()>0){
            return new ExtMsg(true,"",entryIds);
        }else {
            return new ExtMsg(false,"",null);
        }
    }

    /**
     * 审批退回上一环节
     * @param textArea 批示
     * @param taskid 任务Id
     * @return
     */
    @RequestMapping("/openapprovebackpre")
    @ResponseBody
    public ExtMsg OpenapproveBackPre(String textArea,String taskid) {
        openApproveService.BackPreBorrowOpen(textArea,taskid);
        return new ExtMsg(true,"",null);
    }
}