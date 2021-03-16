package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.DestructionBillService;
import com.wisdom.web.service.ElectronApproveService;
import com.wisdom.web.service.TaskService;
import com.wisdom.web.service.WorkflowService;
import com.xdtech.smsclient.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 销毁单据管理控制器 Created by yl on 2017/10/26.
 */
@Controller
@RequestMapping(value = "/destructionBill")
public class DestructionBillController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DestructionBillService destructionBillService;

	@Autowired
	ElectronApproveService electronApproveService;

	@Autowired
	private WebSocketService webSocketService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SMSService smsService;

	@Autowired
	TaskService taskService;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	WorkRepository workRepository;

	@Value("${system.report.server}")
	private String reportServer;//报表服务

	@Value("${workflow.destroy.approve.workid}")
	private String destructionBillWorkId;//销毁审批节点编号

	@RequestMapping("/main")
	public String main( Model model) {
		model.addAttribute("reportServer",reportServer);
		return "/inlet/destructionBill";
	}

	//根据id查询审批节点
	@RequestMapping("/findByWorkId")
	@ResponseBody
	public ExtMsg findByWorkId() {
		Tb_work work= workflowService.findByWorkid(destructionBillWorkId);
		return new ExtMsg(true,"",work);
	}

	@LogAnnotation(module="鉴定与销毁-到期鉴定管理",sites = "1",fields = "title",connect = "##单据题目",startDesc = "新增单据，详细：")
	@RequestMapping("/saveBill")
	@ResponseBody
	public ExtMsg saveBill(@ModelAttribute("form") Tb_bill bill, String[] entryids) throws ParseException {
		ExtMsg extMsg = null;
		String titles = destructionBillService.findBothBillEntry(entryids);
		if ("".equals(titles)) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			bill.setSubmitter(userDetails.getRealname());
			bill.setState(Tb_bill.STATE_NOT_SEND);
			String billid = destructionBillService.saveBill(bill).getBillid();
			for (String entryid : entryids) {
				Tb_bill_entry tb_bill_entry = new Tb_bill_entry();
				Tb_entry_index entryIndex = destructionBillService.findByEntryid(entryid);
				tb_bill_entry.setBillid(billid);
				tb_bill_entry.setEntryid(entryIndex.getEntryid());
				tb_bill_entry.setArchivecode(entryIndex.getArchivecode());
				tb_bill_entry.setFiledate(entryIndex.getFiledate());
				tb_bill_entry.setTitle(entryIndex.getTitle());
				tb_bill_entry.setEntryretention(entryIndex.getEntryretention());
				tb_bill_entry.setEntrystorage(entryIndex.getEntrystorage());//存储位置信息
				destructionBillService.saveBillEntryIndex(tb_bill_entry);
			}
			if (!"".equals(billid)) {
				extMsg = new ExtMsg(true, "保存成功，请在销毁单据管理-未送审中进行相关处理", null);
			} else {
				extMsg = new ExtMsg(false, "保存失败", null);
			}
		} else {
			extMsg = new ExtMsg(false, "保存失败," + "条目中题名为：<br />" + titles, null);
		}
		return extMsg;
	}

	@RequestMapping("/getBill")
	@ResponseBody
	public Page<Tb_bill> getBill(int page, int start, int limit, String condition, String operator, String content,
			String state, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		logger.info("page:" + page + "start:" + start + "limt:" + limit + "state:" + state);
		Page<Tb_bill> bills = destructionBillService.findByState(page, limit, condition, operator, content, state,
				sortobj);
		return bills;
	}

	@LogAnnotation(module="鉴定与销毁-销毁单据管理",sites = "1",startDesc = "删除单据，编号：")
	@RequestMapping("/deleteBill")
	@ResponseBody
	public ExtMsg deleteBill(String[] billids) {
		ExtMsg extMsg = null;
		int count = destructionBillService.deleteAllByBillidIn(billids);
		if (count > 0) {
			extMsg = new ExtMsg(true, "删除成功", null);
		} else {
			extMsg = new ExtMsg(false, "删除失败", null);
		}
		return extMsg;
	}

	@RequestMapping("/getDetailBill")
	@ResponseBody
	public Page<Tb_bill_entry> getDetailBill(int page, int start, int limit, String billId, String sort,String condition, String operator,
											 String content) {
		Sort sortobj = WebSort.getSortByJson(sort);
		logger.info("page:" + page + "start:" + start + "limt:" + limit);
		Page<Tb_bill_entry> billEntries = destructionBillService.findByEntryidIn(page, limit, billId,condition,operator,content,sortobj);
		return billEntries;
	}

	@RequestMapping("/saveBillApproval")
	@ResponseBody
	public ExtMsg saveBillApproval(String billIds, String userid, String username, String submitusernames,
			String text) {
		ExtMsg extMsg = null;
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (destructionBillService.saveBillApproval(billIds, userid, username, submitusernames, text,userDetails.getUserid()) != null) {
			webSocketService.noticeRefresh(userid);// 刷新下一审批人
			extMsg = new ExtMsg(true, "送审成功", null);
		} else {
			extMsg = new ExtMsg(false, "送审失败", null);
		}
		return extMsg;
	}

	@RequestMapping("/billApproval")
	public String index(Model model, String taskid, String flag, String type) {
		List<Tb_flows> flows = electronApproveService.getFlowsInfo(taskid, type);
		if (flows.size() > 1) {
			model.addAttribute("info", "销毁查档");
			return "/inlet/missionAdmins";
		}
		model.addAttribute("flowsText", flows.get(0).getText());
		model.addAttribute("nodeId", flows.get(0).getNodeid());
		model.addAttribute("taskid", taskid);
		model.addAttribute("iflag", flag);
		model.addAttribute("type", type);
		Tb_work workAppraisal = workRepository.findByWorktext("销毁审批");
		if(workAppraisal!=null){
			model.addAttribute("appraisalSendmsg","1".equals(workAppraisal.getSendmsgstate())?true:false);  //销毁是否短信通知
		}else{
			model.addAttribute("appraisalSendmsg",false);
		}
		return "/inlet/billApproval";
	}

	@RequestMapping("/getBillByTaskid")
	@ResponseBody
	public List<Tb_bill> getBillByTaskid(String taskid) {
		return destructionBillService.getBillByTaskid(taskid);
	}

	@RequestMapping("/getBillApproval")
	@ResponseBody
	public ExtMsg getBillApproval(String taskid) {
		return new ExtMsg(true, "成功", destructionBillService.getBillApproval(taskid));
	}

	@RequestMapping("/billApprovalSubmit")
	@ResponseBody
	public ExtMsg billApprovalSubmit(String textArea, String nextNode, String nextSpman, String taskid, String
			nodeId,String[] billids,String sendMsg) {
		destructionBillService.billApprovalSubmit(textArea, nextNode, nextSpman, taskid, nodeId,billids);
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

	//手动催办
	@RequestMapping("/manualUrging")
	@ResponseBody
	public ExtMsg manualUrging(String billids,String sendMsg){
		if(billids==null){
			return new ExtMsg(false, "催办失败", null);
		}
		Tb_bill_approval billApproval= taskService.manualUrgingDestructionBill(billids);
		String returnStr = "";
		if(billApproval!=null) {
			Tb_user spuser = userRepository.findByUserid(billApproval.getUserid());
			if (sendMsg != null && "true".equals(sendMsg) && spuser != null) {
				try {
					returnStr = smsService.SendSMS(spuser.getPhone(), "您有一条档案系统的销毁审批，请登录档案系统管理平台及时处理！");
				} catch (Exception e) {
					e.printStackTrace();
					return new ExtMsg(true, "已催办，短信发送失败", null);
				}
			}
			if ("".equals(returnStr)) {
				return new ExtMsg(true, "已催办", null);
			} else {
				return new ExtMsg(true, "已催办，短信发送结果为：" + returnStr, null);
			}
		}
		return new ExtMsg(true, "催办失败", null);
	}

	@RequestMapping("/returnBillApproval")
	@ResponseBody
	public ExtMsg returnBillApproval(String textArea, String taskid, String nodeId) {
		destructionBillService.returnBillApproval(textArea, taskid, nodeId);
		return new ExtMsg(true, "", null);
	}

	@RequestMapping("/updataBillState")
	@ResponseBody
	public ExtMsg updataBillState(String state,String destructionAppraise, String[] billids,String[] entryids) {
		destructionBillService.updataBillState(state,destructionAppraise, billids,entryids);
		return new ExtMsg(true, "设置成功", null);
	}

	@RequestMapping("/getNextNode")
	@ResponseBody
	public ExtMsg getNextNode() {
		return new ExtMsg(true, "成功", destructionBillService.getNode());
	}

	@RequestMapping("/findByBillid")
	@ResponseBody
	public ExtMsg findByBillid(String billid) {
		return new ExtMsg(true, "成功", destructionBillService.findByBillid(billid));
	}

	@RequestMapping("/implementBill")
	@ResponseBody
	public ExtMsg implementBill(String[] billids) {
		ExtMsg extMsg = destructionBillService.implementBill(billids);
		return extMsg;
	}

	//库房执行销毁
	@RequestMapping("/implementKfBill")
	@ResponseBody
	public ExtMsg implementKfBill(String[] billids) {
		ExtMsg extMsg = destructionBillService.implementKfBill(billids);
		return extMsg;
	}

	@RequestMapping("/checkBill")
	@ResponseBody
	public ExtMsg checkBill(@ModelAttribute("form") Tb_bill bill, String[] entryids) {
		String titles = destructionBillService.findBothBillEntry(entryids);
		if ("".equals(titles)) {
			return new ExtMsg(true, "成功", destructionBillService.getNode());
		} else {
			return new ExtMsg(false, "送审失败," + "条目中题名为：<br />" + titles, null);
		}
	}

	@RequestMapping("/approvalBill")
	@ResponseBody
	public ExtMsg approvalBill(@ModelAttribute("form") Tb_bill bill, String[] entryids, String userid, String username,
			String text,String sendMsg) throws ParseException {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		bill.setApprovetext(text);
		bill.setApproveman(username);
		bill.setSubmitter(userDetails.getRealname());
		bill.setState(Tb_bill.STATE_NOT_SEND);
		String billid = destructionBillService.saveBill(bill).getBillid();
		for (String entryid : entryids) {
			Tb_bill_entry tb_bill_entry = new Tb_bill_entry();
			Tb_entry_index entryIndex = destructionBillService.findByEntryid(entryid);
			tb_bill_entry.setBillid(billid);
			tb_bill_entry.setEntryid(entryIndex.getEntryid());
			tb_bill_entry.setArchivecode(entryIndex.getArchivecode());
			tb_bill_entry.setFiledate(entryIndex.getFiledate());
			tb_bill_entry.setTitle(entryIndex.getTitle());
			tb_bill_entry.setEntryretention(entryIndex.getEntryretention());
			tb_bill_entry.setEntrystorage(entryIndex.getEntrystorage());//存储位置信息
			tb_bill_entry.setFilenumber(entryIndex.getFilenumber());
			tb_bill_entry.setResponsible(entryIndex.getResponsible());
			// 当前时间 - 文件日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	        Date date = new Date();
	        String keepyear = String.valueOf(Integer.valueOf(sdf.format(date)) - Integer.valueOf(entryIndex.getFiledate().substring(0, 4)));
			tb_bill_entry.setKeepdate(keepyear + "年");
			
			if (entryIndex.getFiledate() != null && !"".equals(entryIndex.getFiledate()) 
					&& entryIndex.getEntryretention() != null && !"".equals(entryIndex.getEntryretention())) {
				Integer year = Integer.valueOf(entryIndex.getFiledate().substring(0, 4)) + destructionBillService.getEntryretentionInfo(entryIndex.getEntryretention());
				String filedate = entryIndex.getFiledate().substring(4, entryIndex.getFiledate().length());
				
				tb_bill_entry.setDestroydate(String.valueOf(year) + filedate);
			}
			destructionBillService.saveBillEntryIndex(tb_bill_entry);
		}
		if (!"".equals(billid)) {
			if (destructionBillService.saveBillApproval(billid, userid, username, userDetails.getRealname(),
					text,userDetails.getUserid()) != null) {
				webSocketService.noticeRefresh();//刷新通知
				Tb_user spuser = userRepository.findByUserid(userid);
				String returnStr = "";
				if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
					try {
						returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
					}catch (Exception e){
						e.printStackTrace();
						return new ExtMsg(true,"送审成功，可在销毁单据管理-待审核中进行查看，短信发送失败",null);
					}
				}
				if("".equals(returnStr)){
					return new ExtMsg(true,"送审成功，可在销毁单据管理-待审核中进行查看",null);
				}else{
					return new ExtMsg(true,"送审成功，可在销毁单据管理-待审核中进行查看，短信发送结果为："+returnStr,null);
				}
			} else {
				return new ExtMsg(false, "送审失败", null);
			}
		} else {
			return new ExtMsg(false, "送审失败", null);
		}
	}

	@RequestMapping("/getDetail")
	@ResponseBody
	public Page<Tb_bill_entry> getDetail(int page, int start, int limit) {
		logger.info("page:" + page + "start:" + start + "limt:" + limit);
		Page<Tb_bill_entry> billEntries = destructionBillService.getDetail(page, limit);
		return billEntries;
	}

	/**
	 * 根据单据获取查档办理详情
	 * 
	 * @param billid
	 *            销毁单据id
	 * @return
	 */
	@RequestMapping("/getDealDetails")
	@ResponseBody
	public Page getDealDetails(String billid) {
		return destructionBillService.getDealDetails(billid);
	}
}