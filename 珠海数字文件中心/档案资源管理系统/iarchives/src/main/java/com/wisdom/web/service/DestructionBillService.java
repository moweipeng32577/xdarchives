package com.wisdom.web.service;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DateUtil;
import com.wisdom.util.DeleteFileUntil;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yl on 2017/10/31.
 */
@Service
@Transactional
public class DestructionBillService {

	@Value("${workflow.destroy.approve.workid}")
	private String destroyWorkid;

	@Autowired
	BillRepository billRepository;

	@Autowired
	BillEntryIndexRepository billEntryIndexRepository;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	BillApprovalRepository billApprovalRepository;
	
	@Autowired
	DestructionBillRepository destructionBillRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	FlowsRepository flowsRepository;

	@Autowired
	NodeRepository nodeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	AppraisalService appraisalService;

	@Autowired
	JyAdminsService jyAdminsService;

	@Autowired
	ElectronApproveService electronApproveService;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	ClassificationRepository classificationRepository;
	
	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	CaptureMetadataService captureMetadataService;

	@Autowired
	StorageRepository storageRespository;

	@Autowired
	StorageService storageService;

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	public Tb_bill saveBill(Tb_bill tb_bill) {
		return billRepository.save(tb_bill);
	}

	public void saveBillEntryIndex(Tb_bill_entry tb_bill_entry) {
		billEntryIndexRepository.save(tb_bill_entry);
	}
	
	public Integer getEntryretentionInfo(String entryretention) {
		if (entryretention.equals("长期") || entryretention.equals("30年")) {
			return 30;
		} else {
			return 10;
		}
	}

	/**
	 * @param page
	 *            第几页
	 * @param limit
	 *            一页获取多少行
	 * @param state
	 *            状态(0-5)
	 * @return
	 */
	public Page<Tb_bill> findByState(int page, int limit, String condition, String operator, String content,
			String state, Sort sort) {
		sort = (sort == null ? new Sort(Sort.Direction.DESC, "approvaldate") : sort);
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		Specification<Tb_bill> searchid = getSearchStateCondition(state);
		Specifications specifications = Specifications.where(searchid);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		// 过滤不是当前登录的用户，当前用户只能看到自己提交的单据
		Specification<Tb_bill> searchSubmitterCondition = appraisalService
				.getSearchSubmitterCondition(LogAop.getCurrentOperateuserRealname());
		return billRepository.findAll(specifications.and(searchSubmitterCondition), pageRequest);
	}

	public Integer deleteAllByBillidIn(String[] billids) {
		Integer count = billRepository.deleteAllByBillidIn(billids);
		// 删除中间表
		if (count > 0) {
			billEntryIndexRepository.deleteAllByBillidIn(billids);
		}
		return count;
	}

	public Page<Tb_bill_entry> findByEntryidIn(int page, int limit, String billId,String condition, String operator,
											   String content, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		// List<Tb_bill_entry> billEntries =
		// billEntryIndexRepository.findByBillid(billId);
		// String[] entryids = new String[billEntries.size()];
		// for (int i = 0; i < billEntries.size(); i++) {
		// entryids[i] = billEntries.get(i).getEntryid();
		// }

		Specifications sp = null;
		if (null != billId && (!"".equals(billId))){
			Specification<Tb_bill_entry> searchid = new Specification<Tb_bill_entry>() {
				@Override
				public Predicate toPredicate(Root<Tb_bill_entry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Predicate state = cb.equal(root.get("billid"), billId);
					return cb.and(state);
				}
			};
			sp = Specifications.where(searchid);
		}
		if(content!=null){
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		return billEntryIndexRepository.findAll(sp,pageRequest);

//		return billEntryIndexRepository.findByBillid(pageRequest, billId);
	}

	public Tb_bill_approval saveBillApproval(String billIds, String userid, String username, String submitusernames,
			String text,String submitterid) {
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(userid);
		task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
		task.setText(submitusernames + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date()) + " 提交销毁");
		task.setType("销毁");
		task.setTime(new Date());
		task = taskRepository.save(task);

		String code = UUID.randomUUID().toString().replace("-", "");
		Tb_bill_approval billApproval = new Tb_bill_approval();
		billApproval.setTaskid(task.getId());
		billApproval.setCode(code);
		billApproval.setBillid(billIds);
		billApproval.setUserid(userid);
		billApproval.setUsername(username);
		billApproval.setSubmitusername(submitusernames);
		billApproval.setSubmitterid(submitterid); //申请人id
		billApproval.setSubmitdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		billApproval.setState(Tb_bill_approval.STATE_FINISH_AUDIT);// 已送审

		Tb_node node = nodeRepository.getNode("销毁审批");
		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);// 完成
		flows.setTaskid(task.getId());
		flows.setMsgid(code);
		flows.setDate(dateInt);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
		flows1.setDate(dateInt);
		flows1.setTaskid(task.getId());
		flows1.setMsgid(code);
		flows1.setSpman(userid);
		flows1.setNodeid(node.getId());

		flowsRepository.save(flows1);

		String[] billid = billIds.split(",");
		for (String id : billid) {
			billRepository.updateStateByBillid(Tb_bill.STATE_WAIT_AUDIT, text, username, id);
		}
		return billApprovalRepository.save(billApproval);
	}

	public void billApprovalSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,String[] billids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task1 = taskRepository.findByTaskid(taskid);
		Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());
		Tb_node node = nodeRepository.findByNodeid(nextNode);

		Tb_bill_approval billApproval = billApprovalRepository.findByTaskidContains(taskid.trim());
		billApproval.setApprove(textArea);
		if (!"".equals(nextSpman)) {// 存在下一审批环节
			String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
			
			//更新审批流程信息
			electronApproveService.updateTaskInfo(taskid, node.getText(), nextSpmanRealname, textArea, dateInt, userDetails.getUserid(), null);

			// 创建下一环节的任务信息
			Tb_task task = new Tb_task();
			task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
			task.setTime(new Date());
			task.setLoginname(nextSpman);
			task.setText(task1.getText());
			task.setType(task1.getType());
			task.setApprovetext(node.getText());
			task.setApproveman(nextSpmanRealname);
			task.setLastid(taskid);
			Tb_task task2 = taskRepository.save(task);

			// 更新上一环节的工作流信息
			flows.setState(Tb_flows.STATE_FINISHED);
			flows.setDate(dateInt);
			// 创建下一环节的工作流信息
			Tb_flows flows1 = new Tb_flows();
			flows1.setNodeid(node.getId());
			flows1.setText(node.getText());
			flows1.setSpman(nextSpman);
			flows1.setTaskid(task2.getId());
			flows1.setMsgid(flows.getMsgid());
			flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
			flows1.setDate(dateInt);
			flowsRepository.save(flows1);
			// 更新电子查档信息
			billApproval.setState(Tb_bill_approval.STATE_WAIT_HANDLE);// 待处理
			String oldTaskId = billApproval.getTaskid();
			billApproval.setTaskid(oldTaskId + "," + task2.getId());// 保存历史任务ID,以逗号隔开
			billApproval.setUsername(nextSpmanRealname);
			billApprovalRepository.save(billApproval);
		} else {// 单据审批完毕
			// 更新上一环节工作流信息
			flows.setState(Tb_flows.STATE_FINISHED);// 完成
			flows.setDate(dateInt);

			List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
			Tb_node node1 = nodes.get(nodes.size() - 1);
			// 完成单据审批
			Tb_flows flows1 = new Tb_flows();
			flows1.setNodeid(node1.getId());
			flows1.setText(node1.getText());
			flows1.setDate(dateInt);
			flows1.setTaskid(taskid);
			flows1.setMsgid(flows.getMsgid());
			flows1.setState(Tb_flows.STATE_FINISHED);// 完成
			flowsRepository.save(flows1);

			billApproval.setState(Tb_bill_approval.STATE_FINISHED);// 已完成
			billApprovalRepository.save(billApproval);

			// 当审核流程到了结束环节，当单据状态不是“不通过”时,更改状态为通过
			List<Tb_bill> bills = getBillByTaskid(taskid);
			for (Tb_bill bill : bills) {
				// 判断是否存于待审核(不通过)
				if (bill.getState().equals(Tb_bill.STATE_WAIT_AUDIT_FAILED)) {
					billRepository.updateBillByBillid(Tb_bill.STATE_AUDIT_FAILED, bill.getBillid());
				} else {
					billRepository.updateBillByBillid(Tb_bill.STATE_FINISH_AUDIT, bill.getBillid());
				}
			}
			electronApproveService.updateElectroInfo(node1.getText(), userDetails.getRealname(), taskid);

			//修改条目表中的到期时间
			List<Tb_bill_entry> billEntrys = billEntryIndexRepository.findByBillidIn(billids);
			for(Tb_bill_entry billEntry:billEntrys){
				Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(billEntry.getEntryid());
				//采集业务元数据
				captureMetadataService.saveServiceMetadata(entryIndex.getEntryid(),"到期鉴定管理","到期鉴定");
				if(billEntry.getState() !=null && "维持".equals(billEntry.getState().substring(0,2))){
					switch (entryIndex.getEntryretention()){
						case "长期":case"30年":{
							retureEntryIndex(entryIndex, 30,30);
							break;
						}
						case "短期":case"10年":{
							retureEntryIndex(entryIndex,10,10);
							break;
						}
					}
				}
				else if(billEntry.getState() !=null && "变更".equals(billEntry.getState().substring(0,2))){
					switch (entryIndex.getEntryretention()){
						case "长期":case"30年":{
							int index1 = billEntry.getState().indexOf("(") + 1;
							int index2 = billEntry.getState().indexOf(")");
							String changeEntryretention = billEntry.getState().substring(index1,index2);
							if(changeEntryretention.equals("长期")||changeEntryretention.equals("30年")) {
								retureEntryIndex(entryIndex,30,30);
							}
							else if(changeEntryretention.equals("短期")||changeEntryretention.equals("10年")) {
								retureEntryIndex(entryIndex,30,10);
							}
							else{
								retureEntryIndex(entryIndex,100,0);
							}
							break;
						}
						case "短期":case"10年":{
							int index1 = billEntry.getState().indexOf("(") + 1;
							int index2 = billEntry.getState().indexOf(")");

							String changeEntryretention = billEntry.getState().substring(index1,index2);
							if(changeEntryretention.equals("长期")||changeEntryretention.equals("30年")) {
								retureEntryIndex(entryIndex,10,30);
							} else if(changeEntryretention.equals("短期")||changeEntryretention.equals("10年")) {
								retureEntryIndex(entryIndex,10,10);
							}else{
								retureEntryIndex(entryIndex,100,0);
							}
							break;
						}
					}
				}else{
                    String duetime;
                    if(entryIndex.getDuetime()!=null){
                        duetime = DateUtil.getAddYearDate(entryIndex.getDuetime(), 100);
                    }else{
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                        duetime =  DateUtil.getAddYearDate(simpleDateFormat.format(date),100);
                    }
					entryIndex.setDuetime(duetime);
					entryIndex.setEntryretention(billEntry.getState());
				}
			}

			//以下代码为插入日志
			List<Tb_bill_entry> debillEntrys = new ArrayList<>();
			List<Tb_bill_entry> debillEntrys2 = new ArrayList<>();
			//寻找变更单据中 状态为“变更”或者为“维持”的
			if(billEntrys.size() > 0 ){
				for(int i =0 ;i<billEntrys.size();i++){
					if(billEntrys.get(i).getState()!= null && "变更".equals(billEntrys.get(i).getState().substring(0,2))){
						debillEntrys.add(billEntrys.get(i));
					}
					if(billEntrys.get(i).getState()!= null && "维持".equals(billEntrys.get(i).getState())){
						debillEntrys2.add(billEntrys.get(i));
					}
				}
			}
			SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			//判断"变更"条目有没有，有得话将变更的条目插入到日志
			if (debillEntrys.size() > 0) {
				//获取entryid
				String[] entryIds = new String[debillEntrys.size()];
				for (int i = 0; i < debillEntrys.size(); i++) {
					entryIds[i] = debillEntrys.get(i).getEntryid();
				}
				// 循环获取entryid，批量插入日志信息
				for (String entryid : entryIds) {
					Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
					Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
							userDetiles.getRealname(), userDetiles.getOrgan().getOrganid(), LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
							"0ms", "变更单据管理", "执行变更条目id： " + entryid + " 标题：" + index.getTitle() + " 档号：" + index
							.getArchivecode());
					entityManager.persist(logMsg);
				}
			}
			//判断"维持"条目有没有，有得话将变更的条目插入到日志
			if (debillEntrys2.size() > 0) {
				//获取entryid
				String[] entryIds = new String[debillEntrys2.size()];
				for (int i = 0; i < debillEntrys2.size(); i++) {
					entryIds[i] = debillEntrys2.get(i).getEntryid();
				}
				// 循环获取entryid，批量插入日志信息
				for (String entryid : entryIds) {
					Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
					Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
							userDetiles.getRealname(), userDetiles.getOrgan().getOrganid(), LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
							"0ms", "维持单据管理", "执行维持条目id： " + entryid + " 标题：" + index.getTitle() + " 档号：" + index
							.getArchivecode());
					entityManager.persist(logMsg);
				}
			}
		}
	}

	private void retureEntryIndex(Tb_entry_index entryIndex,int year,int yearnew){
		if(entryIndex.getDuetime()!=null){
			String duetime =  DateUtil.getAddYearDate(entryIndex.getDuetime(),year);
			entryIndex.setDuetime(duetime);
		}else{
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String duetime =  DateUtil.getAddYearDate(simpleDateFormat.format(date),year+yearnew);
			entryIndex.setDuetime(duetime);
		}
	}

	public void returnBillApproval(String textArea, String taskid, String nodeid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_node node = nodeRepository.findByNodeid(nodeid);
		
		//退回时更新当前审批 & 授权人审批任务信息
		electronApproveService.updateTaskInfo(taskid, node.getText(), userDetails.getRealname(), textArea, dateInt, userDetails.getUserid(), "销毁");
		Tb_bill_approval billApproval = billApprovalRepository.findByTaskidContains(taskid.trim());
		billApproval.setState(Tb_bill_approval.STATE_SEND_BACK);// 退回
		billApproval.setApprove(textArea);

		Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
		flows.setState(Tb_flows.STATE_FINISHED);// 更改上环节完成状态
		flows.setDate(dateInt);// 更改上环节完成时间

		List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
		Tb_node node1 = nodes.get(nodes.size() - 1);
		Tb_flows flows1 = new Tb_flows();
		flows1.setNodeid(node1.getId());
		flows1.setText(node1.getText());
		flows1.setDate(dateInt);
		flows1.setTaskid(taskid);
		flows1.setMsgid(flows.getMsgid());
		flows1.setState(Tb_flows.STATE_FINISHED);// 结束
		flowsRepository.save(flows1);

		for (String billid : billApproval.getBillid().split(",")) {
			billRepository.updateBillByBillid(Tb_bill.STATE_SEND_BACK, billid);
		}
		// 退回时只需要当前用户刷新
		webSocketService.noticeRefresh(userDetails.getUserid());
	}

	public List<Tb_bill> getBillByTaskid(String taskid) {
		Tb_bill_approval billApproval = billApprovalRepository.findByTaskidContains(taskid.trim());
		return billRepository.findByBillidIn(billApproval.getBillid().split(","));
	}

	public Tb_bill_approval getBillApproval(String taskid) {
		return billApprovalRepository.findByTaskidContains(taskid.trim());
	}



	/**
	  更新销毁单据条目的状态信息（销毁，维持，变更）
	*/
	public void updataBillState(String state,String destructionAppraise, String[] billids,String[] entryids) {
		if(entryids == null) {
			for (String billid : billids) {
				List<Tb_bill_entry> billentry = billEntryIndexRepository.findByBillid(billid);
				for (int i = 0; i < billentry.size(); i++) {
					//修改销毁条目状态,销毁依据
					billentry.get(i).setState(state);
				}
			}
		}
		else{
			for (int i = 0; i < entryids.length; i++) {
				//修改条目状态
				Tb_bill_entry billentry = billEntryIndexRepository.findByEntryid(entryids[i]);
				billentry.setState(state);
			}
		}
	}

	public Tb_node getNode() {
		return nodeRepository.findByWorkidAndSortsequence(destroyWorkid, 2);
	}

	public Tb_bill_approval findByBillid(String billid) {
		return billApprovalRepository.findByBillidContains(billid.trim());
	}

	public Tb_entry_index findByEntryid(String entryid) {
		return entryIndexRepository.findByEntryid(entryid);
	}

	public ExtMsg implementBill(String[] billids) {
		ExtMsg extMsg = null;
		List<Tb_bill_entry> debillEntrys = new ArrayList<>();
		List<Tb_bill_entry> billEntrys = billEntryIndexRepository.findByBillidIn(billids);

		//寻找销毁单据中 状态为“销毁”的条目，或者状态为‘’的条目
		if(billEntrys.size() > 0 ){
			for(int i =0 ;i<billEntrys.size();i++){
				if("销毁".equals(billEntrys.get(i).getState()) || null == billEntrys.get(i).getState() || "".equals(billEntrys.get(i).getState())){
					debillEntrys.add(billEntrys.get(i));
				}
			}
		}
		if (debillEntrys.size() > 0) {
			//获取entryid 与 archivecode
			String[] entryIds = new String[debillEntrys.size()];
			String[] archivecodes = new String[debillEntrys.size()];
			for (int i = 0; i < debillEntrys.size(); i++) {
				entryIds[i] = debillEntrys.get(i).getEntryid();
				archivecodes[i] = debillEntrys.get(i).getArchivecode();
			}

			// --------判断销毁单据中的条目是否是 “未归还”---------start-----------------------。
			List<String> titlelist = new ArrayList<>();
			boolean flag = false;
			for (int i = 0; i < entryIds.length; i++) {
				List<Tb_entry_index> entry_indexList = entryIndexRepository.findEntryByBorrowmsg(entryIds[i]);
				if (entry_indexList.size() > 0) {
					titlelist.add(entry_indexList.get(0).getTitle());
					flag = true;
				}
			}
			if (flag){
				extMsg = new ExtMsg(false, "无法执行", titlelist);
			}
			// --------判断销毁单据中的条目是否是 “未归还”---------end-----------------------。

			else {
				SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

				// 循环获取entryid，彻底删除关联的电子原文,批量插入日志信息
				for (String entryid : entryIds) {
					Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
					deleteElectronidByEntryid(entryid);
					Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
							userDetiles.getRealname(), userDetiles.getOrgan().getOrganid(), LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
							"0ms", "销毁单据管理", "执行销毁条目id： " + entryid+" 标题："+index.getTitle()+" 档号："+index.getArchivecode());
					entityManager.persist(logMsg);
				}

				// 根据档号查找有没有卷内条目，有就一并删除
				for ( Tb_bill_entry billEntry : debillEntrys) {
					String classname = classificationRepository.findClassname(billEntry.getEntryid());

					if(classname.indexOf("案卷") != -1) {
						List<Tb_entry_index> jnList = entryIndexRepository.findAllByArchivecodeLike(billEntry.getArchivecode() + "-%");// ?档号分隔符是不定的，不可固定写-
						// 循环获取entryid，彻底删除关联的电子原文
						for (Tb_entry_index entryIndex : jnList) {
							deleteElectronidByEntryid(entryIndex.getEntryid());
						}
						entryIndexRepository.delete(jnList);
					}
				}

				//修改销毁单据
				for (String billid : billids) {
					billRepository.updateBillByBillid(Tb_bill.STATE_FINISH_EXECUTE, billid);
				}

				//删除条目
				int count =entryIndexRepository.deleteByEntryidIn(entryIds);
				captureMetadataService.captureServiceMetadataByZL(entryIds,"销毁单据管理","销毁鉴定");
				entityManager.flush();
				entityManager.clear();
				extMsg = new ExtMsg(true, "执行成功", count);
			}
		}
		else{
			extMsg = new ExtMsg(false, "该单据中不存在需要执行销毁的条目！", null);
		}
		return extMsg;
	}

	public ExtMsg implementKfBill(String[] billids) {
		ExtMsg extMsg = null;
		List<Tb_bill_entry> debillEntrys = new ArrayList<>();
		List<Tb_bill_entry> billEntrys = billEntryIndexRepository.findByBillidIn(billids);

		//寻找销毁单据中 状态为“销毁”的条目，或者状态为‘’的条目
		if(billEntrys.size() > 0 ){
			for(int i =0 ;i<billEntrys.size();i++){
				if("销毁".equals(billEntrys.get(i).getState()) || null == billEntrys.get(i).getState() || "".equals(billEntrys.get(i).getState())){
					debillEntrys.add(billEntrys.get(i));
				}
			}
		}
		if (debillEntrys.size() > 0) {
			//获取entryid 与 archivecode
			String[] entryIds = new String[debillEntrys.size()];
			String[] archivecodes = new String[debillEntrys.size()];
			for (int i = 0; i < debillEntrys.size(); i++) {
				entryIds[i] = debillEntrys.get(i).getEntryid();
				archivecodes[i] = debillEntrys.get(i).getArchivecode();
			}

			// --------判断销毁单据中的条目是否 在库---------start-----------------------。
			List<String> titlelist = new ArrayList<>();
			List<Storage> storages =storageRespository.findByEntryIn(entryIds);
			if(storages.size()<1){
				return new ExtMsg(false, "没有相关的出入库记录", null);
			}

			List<String> entryidList=new ArrayList<>();
			boolean flag = false;
			for (Storage storage:storages) {
				String entry=storage.getEntry();//条目ID
				entryidList.add(entry);
				if (!Storage.STATUS_IN.equals(storage.getStorestatus())) {//不在库
					for(int i = 0; i < entryIds.length; i++){
						if(entryIds[i].equals(entry)){
							titlelist.add(i+1+".档号 "+debillEntrys.get(i).getArchivecode()+"；题名 "+debillEntrys.get(i).getTitle()+"; ");
							break;
						}
					}
					flag = true;
				}
			}// --------判断销毁单据中的条目是否是 “在库”---------end-----------------------。
			if (flag){
				extMsg = new ExtMsg(false, "无法执行", titlelist);
			}else {
				SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

				//删除库存记录
				int count=storageService.delStorages(storages);

				//修改销毁单据
				for (String billid : billids) {
					billRepository.updateBillByBillid(Tb_bill.STATE_FINISH_KF_EXECUTE, billid);
				}

				// 删除关联的库存信息,批量插入日志信息
				for (String entryid : entryidList) {
					for(Tb_bill_entry billEntry: billEntrys){
						if(entryid.equals(billEntry.getEntryid())){
							Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
									userDetiles.getRealname(), userDetiles.getOrgan().getOrganid(), LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
									"0ms", "销毁单据管理", "执行库存销毁条目id： " + entryid+" 标题："+billEntry.getTitle()+" 档号："+billEntry.getArchivecode());
							entityManager.persist(logMsg);
							break;
						}
					}
				}

				extMsg = new ExtMsg(true, "执行成功", count);
			}
		}else{
			extMsg = new ExtMsg(false, "该单据中不存在需要执行库存销毁的条目！", null);
		}
		return extMsg;
	}

	// 根据entryid找出电子文件进行彻底删除，即使是恢复软件恢复了，也不是原有文件内容
	public void deleteElectronidByEntryid(String entryid) {
		try {
			List<Tb_electronic> electronics = electronicRepository.findByEntryidOrderBySortsequence(entryid);
			for (Tb_electronic electronic : electronics) {
				String filePath = rootpath + electronic.getFilepath() + "/" + electronic.getFilename();
				DeleteFileUntil.completeDestroyDocument(filePath);
			}
			// 删除关联的电子原文记录
			electronicRepository.delete(electronics);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String findBothBillEntry(String[] entryids) {
		String msgs = "";
		for (String entryId : entryids) {
			// 判断是否该条目是否已经存在别的单据里面
			if (billEntryIndexRepository.getBothBillEntry(entryId) > 0) {
				String title = "";
				List<Tb_bill> bills = billRepository.getBothBillTitle(entryId);
				for (Tb_bill bill : bills) {
					String state = "";
					switch (bill.getState()) {
					case Tb_bill.STATE_NOT_SEND:
						state = "未送审";
						break;
					case Tb_bill.STATE_WAIT_AUDIT:
						state = "待审核";
						break;
//					case Tb_bill.STATE_FINISH_AUDIT:
//						state = "已审核";
//						break;
					}
					title += "状态为：" + state + "；单据名为：'" + bill.getTitle() + "'";
				}
				msgs += ("'" + entryIndexRepository.findByEntryid(entryId).getTitle() + "'" + "已存在于" + title
						+ "的单据中<br />");
			}
		}
		return msgs;
	}

	public static Specification<Tb_bill> getSearchStateCondition(String state) {
		Specification<Tb_bill> searchStateCondition = new Specification<Tb_bill>() {
			@Override
			public Predicate toPredicate(Root<Tb_bill> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				if ("1".equals(state)) {
					Predicate[] predicates = new Predicate[2];
					predicates[0] = criteriaBuilder.equal(root.get("state"), "1");
					predicates[1] = criteriaBuilder.equal(root.get("state"), "6");
					return criteriaBuilder.or(predicates);
				} else {
					Predicate p = criteriaBuilder.equal(root.get("state"), state);
					return criteriaBuilder.or(p);
				}
			}
		};
		return searchStateCondition;
	}

	public Page<Tb_bill_entry> getDetail(int page, int limit) {
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String staStr = "2";
		List<Tb_bill> billEntries = billRepository.findByState(staStr);
		String[] billids = new String[billEntries.size()];
		for (int i = 0; i < billEntries.size(); i++) {
			billids[i] = billEntries.get(i).getBillid();
		}
		return billEntryIndexRepository.findByBillidIn(pageRequest, billids);
	}

	public Page getDealDetails(String billid) {
		Tb_bill_approval approval = billApprovalRepository.findByBillidContains(billid.trim());
		if (approval != null) {
			return jyAdminsService.getCommonDealDetails(approval.getCode(), approval.getApprove(),
					approval.getSubmitusername());
		}
		return new PageImpl(new ArrayList(), null, 0);
	}
}