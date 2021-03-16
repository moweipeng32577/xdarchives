package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.BorrowDocRepository;
import com.wisdom.web.repository.TaskRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.service.ElectronApproveService;
import com.xdtech.smsclient.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 电子查档审批控制器
 * Created by Administrator on 2017/10/30 0030.
 */
@Controller
@RequestMapping(value = "/electronApprove")
public class ElectronApproveController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    WorkRepository workRepository;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String index(Model model, String taskid, String flag, String type) {
    	List<Tb_flows> flows = electronApproveService.getFlowsInfo(taskid, type);
    	if (flows.size() > 1) {
    		model.addAttribute("info","查档");
    		return "/inlet/missionAdmins";
    	}
        model.addAttribute("flowsText",flows.get(0).getText());
        model.addAttribute("nodeId",flows.get(0).getNodeid());
        model.addAttribute("taskid",taskid);
        model.addAttribute("iflag",flag);
        model.addAttribute("type",type);
        Tb_work workBorrrow = workRepository.findByWorktext("查档审批");
        if(workBorrrow!=null){
            model.addAttribute("borrrowSendmsg","1".equals(workBorrrow.getSendmsgstate())?true:false);  //查档是否短信通知
        }else{
            model.addAttribute("borrrowSendmsg",false);
        }
        model.addAttribute("reportServer",reportServer);
        return "/inlet/electronApprove";
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index_borrow> getEntryIndex(int page, int start, int limit, String taskid) {
        logger.info("page:" + page + "start:" + start + "limt:" + limit);
        return electronApproveService.getEntryIndex(page,limit,taskid);
    }

    /**
     * 获取表单数据
     * @param taskid 任务id
     * @return
     */
    @RequestMapping("/getBorrowDocByTaskid")
    @ResponseBody
    public ExtMsg getBorrowDocByTaskid(String taskid) {
        return new ExtMsg(true,"成功",electronApproveService.getBorrowDocByTaskid(taskid));
    }

    /**
     * 添加批示
     * @param taskid
     * @param approve
     * @return
     */
    @RequestMapping("/approveAdd")
    @ResponseBody
    public ExtMsg approveAdd(String taskid,String approve) {
        return new ExtMsg(true,"成功",electronApproveService.getBorrowDocByTaskid(taskid));
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String nodeId,String taskid,String type) {
        return electronApproveService.getNodes(nodeId,taskid,type);
    }

    @RequestMapping("/getNextSpman")
    @ResponseBody
    public List<Tb_user> getNextSpman(String nodeId,String organid,String workText) {
        if("采集移交审核".equals(workText)&&nodeId==null){
            return new ArrayList<>();
        }
        return electronApproveService.getNextSpman(nodeId,organid,workText);
    }

    /**
     * 设置环节权限
     * @param taskid 任务id
     * @param dataids 选择数据id
     * @param lyqx 利用全
     * @return
     */
    @RequestMapping("/setQxAddSubmit")
    @ResponseBody
    public ExtMsg setQxAddSubmit(String taskid,String[] dataids,String lyqx) {
        electronApproveService.setQxAddSubmit( taskid, dataids, lyqx);
        return new ExtMsg(true,"",null);
    }

	/**
	 * 审批提交
	 * 
	 * @param textArea
	 *            批示
	 * @param nextNode
	 *            下一节点
	 * @param nextSpman
	 *            下一审批人
	 * @param taskid
	 *            任务Id
	 * @param nodeId
	 *            节点id
	 * @return
	 */
	@RequestMapping("/approveSubmit")
	@ResponseBody
	public ExtMsg approveSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,
			int borrowtyts,String sendMsg) {
		String msg=electronApproveService.approveSubmit(textArea, nextNode, nextSpman, taskid, nodeId, borrowtyts);
		if(!"".equals(msg)){
            return new ExtMsg(false,"需添加条目审批才能点击“完成”",null);
        }
		webSocketService.noticeRefresh();
        Tb_user spuser = userRepository.findByUserid(nextSpman);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"审批完成，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"审批完成",null);
        }else{
            return new ExtMsg(true,"审批完成，短信发送结果为："+returnStr,null);
        }
	}

    @RequestMapping("/returnBorrow")
    @ResponseBody
    public ExtMsg returnBorrow(String textarea,String taskid,String nodeId){
        electronApproveService.returnBorrow(textarea,taskid,nodeId);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/nofindBorrow")
    @ResponseBody
    public ExtMsg nofindBorrow(String textarea,String taskid,String nodeId){
        electronApproveService.nofindBorrow(textarea,taskid,nodeId);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/setQxAddSubmitEle")
    @ResponseBody
    public ExtMsg setQxAddSubmitEle(String taskid,String[] dataids,String lyqx,String dataSourceType) {
        electronApproveService.setQxAddSubmitEle( taskid, dataids, lyqx,dataSourceType);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/setelectronicyes")
    @ResponseBody
    public ExtMsg setTextOpenYes(String eleids,String taskid,String entryid){
        boolean flag = electronApproveService.setTextopenYes(eleids,taskid,entryid);
        List<BackElectronic> backElectroniclist = electronApproveService.getTextopen(entryid,taskid);
        if(flag) {
            return new ExtMsg(true, "", backElectroniclist);
        }else{
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/setelectronicno")
    @ResponseBody
    public ExtMsg setTextOpenNo(String eleids,String taskid,String entryid){
        boolean flag = electronApproveService.setTextopenNo(eleids,taskid);
        List<BackElectronic> backElectroniclist = electronApproveService.getTextopen(entryid,taskid);
        electronApproveService.getTextopens(entryid,taskid);
        if(flag) {
            return new ExtMsg(true, "", backElectroniclist);
        }else{
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/gettextopen")
    @ResponseBody
    public ExtMsg getTextOpen(String entryid,String taskid){
        List<BackElectronic> backElectroniclist = electronApproveService.getTextopen(entryid,taskid);
        if(backElectroniclist!=null) {
            return new ExtMsg(true, "", backElectroniclist);
        }else{
            return new ExtMsg(false, "没有原文", null);
        }
    }

    @RequestMapping("/getTaskInfo")
    @ResponseBody
    public ExtMsg findByTaskid(String taskid){
        Tb_task task = taskRepository.findByTaskid(taskid);//获取任务修改状态
        if (task != null) {
            return new ExtMsg(true, "获取信息成功", task);
        }
        return new ExtMsg(false, "无任务信息", null);
    }

    @RequestMapping("/getElectronWorkText")
    @ResponseBody
    public ExtMsg getElectronWorkText(String taskid){
        boolean flag = electronApproveService.getElectronWorkText(taskid);
        if(flag){
            return new ExtMsg(true,"",flag);
        }
        return new ExtMsg(false,"",flag);
    }

    @RequestMapping("/getSubmitstate")
    @ResponseBody
    public ExtMsg getSubmitstate(String taskid){
        return new ExtMsg(true,"",electronApproveService.getSubmitstate(taskid).getSubmitstate());
    }

    @RequestMapping("/getEvidencetextCount")
    @ResponseBody
    public ExtMsg getEvidencetextCount(String taskid,String borrowcode){
        return new ExtMsg(true,"",electronApproveService.getEvidencetextCount(taskid,borrowcode));
    }

    @RequestMapping("/getBorrowcode")
    @ResponseBody
    public ExtMsg getBorrowcode(String taskid){
        return new ExtMsg(true,"",electronApproveService.getSubmitstate(taskid).getBorrowcode());
    }

    @RequestMapping("/getEvidenceText")
    @ResponseBody
    public ExtMsg getEvidenceText(String borrowcode){
        return new ExtMsg(true,"",borrowDocRepository.findByBorrowcode(borrowcode).getEvidencetext());
    }

    //设置查档类型
    @RequestMapping("/setBorrowType")
    @ResponseBody
    public ExtMsg setBorrowType(String[] entryids,String settype,String taskid,String dataSourceType){
        String msg = electronApproveService.setBorrowType(entryids,settype,taskid,dataSourceType);
        return new ExtMsg(true,msg,null);
    }

    /**
     * 获取当前环节位置
     *
     * @param taskid
     *            任务Id
     * @return
     */
    @RequestMapping("/approvesort")
    @ResponseBody
    public ExtMsg getapproveSort(String taskid) {
        ExtMsg extMsg = new ExtMsg();
        extMsg = electronApproveService.getapproveSort(taskid);
        return extMsg;
    }
}