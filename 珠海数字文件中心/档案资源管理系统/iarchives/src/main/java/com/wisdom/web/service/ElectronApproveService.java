package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.secondaryDataSource.repository.SxElectronicBrowseRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.GainField;
import com.wisdom.util.SolidifyThread;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/8/16.
 */
@Service
@Transactional
public class ElectronApproveService {

	@Autowired
	UserService userService;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	BorrowMsgRepository borrowMsgRepository;

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	FlowsRepository flowsRepository;

	@Autowired
	NodeRepository nodeRepository;

	@Autowired
	OpendocRepository opendocRepository;

	@Autowired
	UserNodeRepository userNodeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	BillApprovalRepository billApprovalRepository;

	@Autowired
	MissionUserRepository missionUserRepository;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	TextOpenRepository textOpenRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	CaptureMetadataService captureMetadataService;

	@Autowired
	UserFunctionRepository userFunctionRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	TransdocRepository transdocRepository;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	SecondaryEntryIndexRepository secondaryEntryIndexRepository;

	@Autowired
	SxElectronicBrowseRepository sxElectronicBrowseRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    OpenmsgRepository openmsgRepository;

	public List<Tb_flows> getFlowsInfo(String taskid, String type) {
		// 当前用户的审批条目信息
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!"完成".equals(type)) {
			type = "处理中";
		}
		List<Tb_flows> flows = flowsRepository.findFlows(taskid, userDetails.getUserid(), type);
        if (flows.size() < 1 && "完成".equals(type)) {
            flows = flowsRepository.findFlows(taskid, userDetails.getUserid(), "退回");
        }
		return flows;
	}

	public List<Tb_flows> getAuditFlowsInfo(String taskid, String type) {
		if (!"完成".equals(type)) {
			type = "处理中";
		}
		List<Tb_flows> flows = flowsRepository.findAuditFlows(taskid, type);
		return flows;
	}

	public Page<Tb_entry_index_borrow> getEntryIndex(int page, int limit, String taskid) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapType = new HashMap<String, String>();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			map.put(borrowmsg.getEntryid(), borrowmsg.getLyqx());
			mapType.put(borrowmsg.getEntryid(), borrowmsg.getType());
		}
		String[] entryids = GainField.getFieldValues(borrowmsgs, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(borrowmsgs, "entryid");
		List<Tb_entry_index> entry_indexs = entryIndexRepository.findByEntryidIn(entryids);
		List<Tb_entry_index_borrow> entry_index_borrows = new ArrayList<>();
		Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
		List<Tb_entry_index_sx> sx_entry_index = secondaryEntryIndexRepository.findByEntryidIn(entryids);
		for (Tb_entry_index_sx sxEntryIndex : sx_entry_index) {
			Tb_entry_index_borrow entry_index_borrow = new Tb_entry_index_borrow();
			BeanUtils.copyProperties(sxEntryIndex, entry_index_borrow);
			entry_index_borrow.setLyqx(map.get(entry_index_borrow.getEntryid()));
			entry_index_borrow.setType(mapType.get(entry_index_borrow.getEntryid()));
			String fullname = nodesettingService.getSxNodefullnameLoop(sxEntryIndex.getNodeid(), "_", "");
			entry_index_borrow.setNodefullname(fullname);
			entry_index_borrows.add(entry_index_borrow);
		}
		for (Tb_entry_index entry_index : entry_indexs) {
			Tb_entry_index_borrow entry_index_borrow = new Tb_entry_index_borrow();
			BeanUtils.copyProperties(entry_index, entry_index_borrow);
			entry_index_borrow.setLyqx(map.get(entry_index_borrow.getEntryid()));
			entry_index_borrow.setType(mapType.get(entry_index_borrow.getEntryid()));
			String fullname = getFullnameByNodeid(parentmap,entry_index.getNodeid());
			entry_index_borrow.setNodefullname(fullname);
			entry_index_borrows.add(entry_index_borrow);
		}
		return new PageImpl(entry_index_borrows, new PageRequest(page - 1, limit), entry_index_borrows.size());
	}

	public List<Tb_node> getNodes(String nodeId,String taskid,String type) {
		Tb_node node = nodeRepository.findByNodeid(nodeId);
		String[] nextids = node.getNextid().split(",");
		List<Tb_node> lists = new ArrayList<>();
		for(String nextid: nextids){
			Tb_node tb_node = nodeRepository.findByNodeid(nextid);
			if(tb_node!=null){
				/*List<String> userids = userNodeRepository.findUserids(tb_node.getId());
				//判断下一环节是否存在权限用户
				if(userids.size() > 0){
					String userid = "";
					if("borrow".equals(type)){   //借阅
						Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
						userid = borrowdoc.getBorrowmanid();
					}else if("audit".equals(type)){   //采集移交审核
						Tb_transdoc transdoc = transdocRepository.getByTaskid(taskid);
						userid = transdoc.getApprovemanid();
					}
					if(userids.contains(userid)){
						lists.add(tb_node);
					}
				}else {
					lists.add(tb_node);
				}*/
				lists.add(tb_node);
			}
		}
		return lists;
	}

	public Tb_borrowdoc getBorrowDocByTaskid(String taskid) {
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		if (borrowdoc != null) {
			if (borrowdoc.getBorrowts() <= 0) {// 若查档者输入查档天数为0或负数，则默认设置同意天数为3
				borrowdoc.setBorrowtyts(3);
			} else {// 若查档者输入查档天数为正数，则默认设置同意天数为申请查档天数
				borrowdoc.setBorrowtyts(
						borrowdoc.getBorrowtyts() == 0 ? borrowdoc.getBorrowts() : borrowdoc.getBorrowtyts());
			}
		}
		// String[] approves =
		// borrowdoc.getApprove()!=null?borrowdoc.getApprove().split("#"):new
		// String[]{};
		// String approve = "";
		// for(String str:approves){
		// approve += str+"\r\n";
		// }
		// borrowdoc.setApprove(approve);
		return borrowdoc;
	}

	public List<Tb_node> getStNodes(String nodeId) {
		Tb_node node = nodeRepository.findByNodeid(nodeId);
		String[] nextids = node.getNextid().split(",");
		return nodeRepository.getNodes(nextids);
	}

	public List<Tb_user> getNextSpman(String nodeId,String organid,String workText) {
		Tb_node node = nodeRepository.findByNodeid(nodeId);
		List<Tb_user_node> user_nodes = userNodeRepository.findUserNodesIn(node.getId().split(","));
		String[] userids = GainField.getFieldValues(user_nodes, "userid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_nodes, "userid");
		//userids = userService.getFilteredUseridByOrgan(userids);// 过滤其它机构用户
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (int i = 0; i < userids.length; i++) {
			if (userids[i].equals(userDetails.getUserid())) {
				userids[i] = "";
			}
		}
		List<Tb_user> userList = new ArrayList<>();
		for(int i=0;i<userids.length;i++){  //排序用户
			Tb_user user = userRepository.findByUserid(userids[i]);
			if(user!=null){ //过滤掉不是用户
				userList.add(user);
			}
		}
//		List<Tb_user> userList = userRepository.findByUseridIn(userids);
		List<Tb_user> backUsers = new ArrayList<>();
		for (Tb_user user : userList) {
			String findorganid = userRepository.findOrganidByUserid(user.getUserid());
			Tb_right_organ organ = rightOrganRepository.findOne(findorganid);
			//找到当前用户的所在单位
			while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
				organ = rightOrganRepository.findOne(organ.getParentid());
			}
			if (organid != null && organid.trim().contains(organ.getOrganid().trim())) {
				backUsers.add(user);  //如果在这单位下，就返回
			}
//			if("采集移交审核".equals(workText)){//暂不根据单位过滤
//				backUsers.add(user);
//			}else{//选择当前单位的用户
//
//			}
		}
		List<String> users = new ArrayList<String>();
		if (backUsers.size() > 0) {
			for (int i = 0; i < backUsers.size(); i++) {
				Tb_user user = backUsers.get(i);
				users.add(user.getRealname() + "-" + user.getUserid());
			}
		}
		String[] strings = new String[users.size()];
		String[] arrStrings = users.toArray(strings);
		// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
//		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
//		Arrays.sort(arrStrings, comparator);
		List<Tb_user> returnList = new ArrayList<>();
		for (int i = 0; i < arrStrings.length; i++) {
			String[] info = arrStrings[i].split("-");
			Tb_user userinfo = new Tb_user();
			userinfo.setUserid(info[1]);
			userinfo.setRealname(info[0]);
			returnList.add(userinfo);
		}
		return returnList;
	}

	/**
	 * 设置环节权限
	 * 
	 * @param taskid
	 *            任务id
	 * @param dataids
	 *            选择数据id
	 * @param lyqx
	 *            利用权限
	 * @return
	 */
	public void setQxAddSubmit(String taskid, String[] dataids, String lyqx) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeInAndEntryidIn(new String[] { borrowcode },
				dataids);
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			borrowmsg.setLyqx(lyqx);
		}
		// return null;
	}

	public void updateTaskInfo(String taskid, String text, String nextSpmanRealname, String textarea, long dateInt,
			String userid, String type) {
		Tb_task task = taskRepository.findByTaskid(taskid);
		if (type != null) {
			if ("查档".equals(type)) {
				Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(task.getId());// 获取单据修改状态
				borrowdoc.setState("退回");
				borrowdoc.setApprove(textarea);
			} else if ("销毁".equals(type)) {
				Tb_bill_approval billApproval = billApprovalRepository.findByTaskidContains(task.getId().trim());
				billApproval.setState(Tb_bill_approval.STATE_SEND_BACK);// 退回
				billApproval.setApprove(textarea);
			} else if ("开放".equals(type)) {
				Tb_opendoc opendoc = opendocRepository.getOpendoc(task.getId());
				opendoc.setState(Tb_opendoc.STATE_SEND_BACK);// 已退回
				opendoc.setApprove(textarea);
			}
			updateElectroInfo("结束", nextSpmanRealname, taskid);
		} else {
			// 更新上一审批任务信息
			updateElectroInfo(text, nextSpmanRealname, taskid);
		}
	}

	// 更新当前task与每层上级task信息
	public void updateElectroInfo(String approvetext, String realname, String taskid) {
		taskRepository.updateInfoByTaskid(approvetext, realname, Tb_task.STATE_FINISHED, taskid);
		Tb_task task = taskRepository.findByTaskid(taskid);
		if (task.getLastid() != null) {
			updateElectroInfo(approvetext, realname, task.getLastid());
		}
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
	public String approveSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,
			int borrowtyts) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);
		if(borrowmsgs==null||borrowmsgs.size()==0){
			return "未添加条目";
		}
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task1 = taskRepository.findByTaskid(taskid);
		Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
		Tb_node node = nodeRepository.findByNodeid(nextNode);// 拿到当前节点

		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取任务单据设置批示
		borrowdoc.setApprove(textArea);
		borrowdoc.setBorrowtyts(borrowtyts);
		if(node.getOrders()==3){ //第一审批环节更新受理时间
			borrowdoc.setAcceptdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		String str = "已通过";
		int i = 0;
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			if ("拒绝".equals(borrowmsg.getLyqx()) || borrowmsg.getLyqx() == null) {
				i += 1;
			}
			borrowmsg.setJybackdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 审批完成时间
			if("实体查档".equals(borrowmsg.getType())||"电子、实体查档".equals(borrowmsg.getType())){
				borrowmsg.setState("未归还");
			}
			else if("调档".equals(borrowmsg.getType())){
                borrowmsg.setState("未调档");
            }
		}
		if (i == borrowmsgs.size()) {
			str = "不通过";// 当全部数据利用权限为拒绝的时候状态为不通过
		}
		borrowdoc.setState(str);
		if (!"".equals(nextSpman)) {// 存在下一审批环节
			String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
			// 更新上一个环节的任务信息
			updateElectroInfo(node.getText(), nextSpmanRealname, taskid);

			// 创建下一环节的任务信息
			Tb_task task = new Tb_task();
			task.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
			task.setTime(new Date());
			task.setLoginname(nextSpman);
			task.setText(task1.getText());
			task.setType(task1.getType());
			task.setApprovetext(node.getText());
			task.setApproveman(nextSpmanRealname);
			task.setLastid(taskid);
			Tb_task task2 = taskRepository.save(task);// 下一审批人任务
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
			flowsRepository.save(flows1);// 下一流程
			// 更新电子查档信息
			borrowdoc.setState(Tb_borrowdoc.STATE_FINISH_AUDIT);
			borrowdoc.setApprovetext(node.getText());// 更新下一审批环节
			borrowdoc.setApproveman(nextSpmanRealname);// 更新当前审批人
		} else { // 单据审批完毕
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

			boolean flag = false;
			boolean flag1 = false;
			int stcount = 0;
			if (str.equals("已通过")) {
				List<String> idList = new ArrayList<>();
				for (Tb_borrowmsg borrowmsg : borrowmsgs) {
					if(("实体查档".equals(borrowmsg.getType())|| "电子、实体查档".equals(borrowmsg.getType()))&&"查看".equals(borrowmsg.getLyqx())){
						flag = true;
						stcount++;
					}
					if ("拒绝".equals(borrowmsg.getLyqx()) || borrowmsg.getLyqx() == null||"实体查档".equals(borrowmsg.getType())){
						continue;
					}
					if("调档".equals(borrowmsg.getType())){
                        flag1 = true;
                    }
					idList.add(borrowmsg.getEntryid());
				}
				String[] idArr = new String[idList.size()];
				idList.toArray(idArr);
				SolidifyThread solidifyThread = new SolidifyThread(idArr, "management","");// 开启固化线程
				solidifyThread.start();
			}
			borrowdoc.setState(str);
			borrowdoc.setApprovetext(node1.getText());
			borrowdoc.setApproveman(userDetails.getRealname());
			if(flag || flag1){  //含有实体查档、调档，提醒出库
                if(flag1){//调档
                    borrowdoc.setReturnstate("未调档");
                }else if(flag){
                    borrowdoc.setReturnstate("未归还");
                }
                borrowdoc.setOutwarestate("未借出");
				List<String> userids = userFunctionRepository.findUseridsByFunctionname("实体档案出库");
				List<String> useridList = userRoleRepository.findUseridsByFunctionname("实体档案出库");
				userids.removeAll(useridList);
				userids.addAll(useridList);  //所有拥有实体档案出库权限的用户
				List<Tb_task> taskOutwares = new ArrayList<>();
				for(String userid : userids){
					Tb_task taskOutware = new Tb_task();
					taskOutware.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
					taskOutware.setTime(new Date());
					taskOutware.setLoginname(userid);
					taskOutware.setText(borrowdoc.getBorrowman() + " 有 "+ stcount +" 份档案待出库！");
					taskOutware.setType("实体出库");
					taskOutware.setBorrowmsgid(borrowdoc.getBorrowcode());
					taskOutwares.add(taskOutware);
				}
				taskRepository.save(taskOutwares);
			}
			borrowdoc.setClearstate("1");
			borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
		}
		if ("已通过".equals(str)) {// 审批通过时修改条目查档天数
			for (Tb_borrowmsg borrowmsg : borrowmsgs) {
				captureMetadataService.saveServiceMetadata(borrowmsg.getEntryid(),"利用平台","电子查档");
				borrowmsg.setJyts(borrowtyts);
			}
		}
		return "";
	}

	/**
	 * 退回
	 * 
	 * @param taskid
	 *            任务id
	 * @param nodeid
	 *            节点id
	 */
	public void returnBorrow(String textarea, String taskid, String nodeid) {
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
		task.setState(Tb_task.STATE_FINISHED);// 完成

		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取单据修改状态
		borrowdoc.setState("退回");
		borrowdoc.setApprove(textarea);
		borrowdoc.setClearstate("1");
		borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);// 获取查档信息修改状态
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			borrowmsg.setLyqx("拒绝");
			borrowmsg.setState("");
		}

		List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);// 获取本流程修改状态
		for (Tb_flows flow : flows) {
			flow.setState(Tb_flows.STATE_FINISHED);// 完成
		}

		Tb_node node = nodeRepository.findByNodeid(nodeid);
		List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
		Tb_node node1 = nodes.get(nodes.size() - 1);
		Tb_flows flows1 = new Tb_flows();
		flows1.setNodeid(node1.getId());
		flows1.setText(node1.getText());
		// flows1.setSpman(user.getLoginname());
		flows1.setDate(dateInt);
		flows1.setTaskid(taskid);
		flows1.setMsgid(flows.get(0).getMsgid());
		flows1.setState(Tb_flows.STATE_FINISHED);// 完成
		flowsRepository.save(flows1);// 结束整个流程
	}

	/**
	 * 查无此档
	 *
	 * @param taskid
	 *            任务id
	 * @param nodeid
	 *            节点id
	 */
	public void nofindBorrow(String textarea, String taskid, String nodeid) {
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
		task.setState(Tb_task.STATE_FINISHED);// 完成

		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取单据修改状态
		borrowdoc.setState("查无此档");
		borrowdoc.setApprove(textarea);
		borrowdoc.setClearstate("1");
		borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);// 获取查档信息修改状态
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			borrowmsg.setLyqx("拒绝");
			borrowmsg.setState("");
		}

		List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);// 获取本流程修改状态
		for (Tb_flows flow : flows) {
			flow.setState(Tb_flows.STATE_FINISHED);// 完成
		}

		Tb_node node = nodeRepository.findByNodeid(nodeid);
		List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
		Tb_node node1 = nodes.get(nodes.size() - 1);
		Tb_flows flows1 = new Tb_flows();
		flows1.setNodeid(node1.getId());
		flows1.setText(node1.getText());
		// flows1.setSpman(user.getLoginname());
		flows1.setDate(dateInt);
		flows1.setTaskid(taskid);
		flows1.setMsgid(flows.get(0).getMsgid());
		flows1.setState(Tb_flows.STATE_FINISHED);// 完成
		flowsRepository.save(flows1);// 结束整个流程
	}

	public void setQxAddSubmitEle(String taskid, String[] dataids, String lyqx,String dataSourceType) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeInAndEntryidIn(new String[] { borrowcode },
				dataids);
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			borrowmsg.setLyqx(lyqx);
		}
		if("soundimage".equals(dataSourceType)){
			for (int j = 0; j < dataids.length; j++) {
				Tb_electronic_browse_sx electroniclist = sxElectronicBrowseRepository.findByEntryid(dataids[j]);
				List<Tb_textopen> textopenlist = textOpenRepository.findByEleidAndBorrowcode(electroniclist.getEleid(), borrowcode);
				if (textopenlist.size() > 0) {
					textopenlist.get(0).setState(lyqx);
				}
			}
		}else {
			for (int j = 0; j < dataids.length; j++) {
				List<Tb_electronic> electroniclist = electronicRepository.findByEntryid(dataids[j]);
				for (int i = 0; i < electroniclist.size(); i++) {
					List<Tb_textopen> textopenlist = textOpenRepository
							.findByEleidAndBorrowcode(electroniclist.get(i).getEleid(), borrowcode);
					if (textopenlist.size() > 0) {
						textopenlist.get(0).setState(lyqx);
					}
				}
			}
		}
	}

	/**
	 * 设置电子审批原文借出
	 *
	 * @param eleids
	 * @param taskid
	 * @return
	 */
	public boolean setTextopenYes(String eleids, String taskid,String entryid) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		String[] eleid = eleids.split(",");
		Tb_borrowmsg borrowmsg = borrowMsgRepository.findByBorrowcodeAndEntryid(borrowcode, entryid);
		boolean flag = false;
		for (int i = 0; i < eleid.length; i++) {
			List<Tb_textopen> textopenList = textOpenRepository.findByEleidAndBorrowcode(eleid[i], borrowcode);
			textopenList.get(0).setState("查看");
			flag = true;
		}
		if("拒绝".equals(borrowmsg.getLyqx())){
			borrowmsg.setLyqx("查看");
		}
		return flag;
	}

	/**
	 * 设置电子审批原文拒绝
	 *
	 * @param eleids
	 * @param taskid
	 * @return
	 */
	public boolean setTextopenNo(String eleids, String taskid) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		String[] eleid = eleids.split(",");
		boolean flag = false;
		for (int i = 0; i < eleid.length; i++) {
			List<Tb_textopen> textopenList = textOpenRepository.findByEleidAndBorrowcode(eleid[i], borrowcode);
			textopenList.get(0).setState("拒绝");
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取电子审批原文数据
	 *
	 * @param entryid
	 * @param taskid
	 * @return
	 */
	public List<BackElectronic> getTextopen(String entryid, String taskid) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		List<Tb_textopen> textopenList = textOpenRepository.findByborrowcodeAndEntryid(borrowcode, entryid);
		List<BackElectronic> backElectroniclist = new ArrayList<>();
		if (textopenList.size() > 0) {
			for (int i = 0; i < textopenList.size(); i++) {
				BackElectronic backElectronic = new BackElectronic();
				Tb_electronic electronic = electronicRepository.findByEleid(textopenList.get(i).getEleid());
				backElectronic.setEleid(textopenList.get(i).getEleid());
				backElectronic.setElename(electronic.getFilename());
				backElectronic.setState(textopenList.get(i).getState());
				backElectroniclist.add(backElectronic);
			}
			return backElectroniclist;
		} else {
			return null;
		}
	}

	public void getTextopens(String entryid, String taskid) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		List<Tb_textopen> textopenList = textOpenRepository.findByborrowcodeAndEntryid(borrowcode, entryid);
		Tb_borrowmsg borrowmsg = borrowMsgRepository.findByBorrowcodeAndEntryid(borrowcode, entryid);
		int count = 0;
		for (Tb_textopen textopen : textopenList) {
			if ("拒绝".equals(textopen.getState())) {
				count++;
			}
		}
		if (textopenList.size() == count) {
			borrowmsg.setLyqx("拒绝");
		}
	}

	public boolean getElectronWorkText(String taskid) {
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		Tb_flows flows = flowsRepository.findByTaskidAndState(taskid, "处理中");
		Tb_node node = nodeRepository.getNode("查档审批");
		if ("提交".equals(borrowdoc.getSubmitstate()) && node.getText().equals(flows.getText())) {
			return true;
		}
		return false;
	}

	public Tb_borrowdoc getSubmitstate(String taskid) {
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		return borrowdoc;
	}

	public int getEvidencetextCount(String taskid,String borrowcode) {
		List<Tb_electronic> electronics = new ArrayList<>();
		if(borrowcode!=null){
			electronics = electronicRepository.findByEntryid(borrowcode);
		}else{
			Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
			electronics = electronicRepository.findByEntryid(borrowdoc.getBorrowcode());
		}
		if (electronics.size() > 0) {
			return electronics.size();
		} else {
			return 0;
		}
	}


	public String setBorrowType(String[] entryids,String settype,String taskid,String dataSourceType) {
		List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
		String borrowcode = flowss.get(0).getMsgid();
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeInAndEntryidIn(new String[] { borrowcode },
				entryids);
		List<Tb_entry_index> hasNotEleOrSt = new ArrayList<>();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(borrowmsg.getEntryid());
            String kount = entryIndex.getKccount()==null||"".equals(entryIndex.getKccount())?"0":entryIndex.getKccount().trim();
			if("电子查档".equals(settype)){
				if(entryIndex.getEleid()==null||"".equals(entryIndex.getEleid())){  //没有电子文件
					hasNotEleOrSt.add(entryIndex);
					continue;
				}else{
					if("实体查档".equals(borrowmsg.getType()) || "调档".equals(borrowmsg.getType())){  //之前不是设置电子查档类型，现在设置电子查档
						List<Tb_electronic> electroniclist = electronicRepository.findByEntryid(borrowmsg.getEntryid());
						if(electroniclist.size()>0){
							List<Tb_textopen> textopenlist = new ArrayList<>();
							for (int j = 0; j < electroniclist.size(); j++) {
								Tb_textopen textopen = new Tb_textopen();
								textopen.setBorrowcode(borrowcode);
								textopen.setEleid(electroniclist.get(j).getEleid());
								textopen.setState("查看");
								textopen.setEntryid(borrowmsg.getEntryid());
								textopenlist.add(textopen);
							}
							textOpenRepository.save(textopenlist);
						}
						entryIndex.setKccount(String.valueOf(Integer.valueOf(kount) + 1));
					}
					if("电子、实体查档".equals(borrowmsg.getType())){
						entryIndex.setKccount(String.valueOf(Integer.valueOf(kount) + 1));
					}
				}
			}else if("电子、实体查档".equals(settype)){
					if("电子查档".equals(borrowmsg.getType())){
						if(Integer.valueOf(kount)<1) {
							hasNotEleOrSt.add(entryIndex);
							continue;
						}
						entryIndex.setKccount(String.valueOf(Integer.valueOf(kount) - 1));
					}else{
						if((entryIndex.getEleid()==null||"".equals(entryIndex.getEleid()))) {
							hasNotEleOrSt.add(entryIndex);
							continue;
						}
					}
					if("实体查档".equals(borrowmsg.getType()) || "调档".equals(borrowmsg.getType())){
						List<Tb_electronic> electroniclist = electronicRepository.findByEntryid(borrowmsg.getEntryid());
						if(electroniclist.size()>0){
							List<Tb_textopen> textopenlist = new ArrayList<>();
							for (int j = 0; j < electroniclist.size(); j++) {
								Tb_textopen textopen = new Tb_textopen();
								textopen.setBorrowcode(borrowcode);
								textopen.setEleid(electroniclist.get(j).getEleid());
								textopen.setState("查看");
								textopen.setEntryid(borrowmsg.getEntryid());
								textopenlist.add(textopen);
							}
							textOpenRepository.save(textopenlist);
						}
					}
			}else if("调档".equals(settype)){
                if (Integer.valueOf(entryIndex.getKccount().trim()) < 1) {
                    //库存份数为0
                    hasNotEleOrSt.add(entryIndex);
                    continue;
                }else{
                    if("电子查档".equals(borrowmsg.getType())){ //之前设置电子查档类型，现在设置调档,删除文件级利用权限
                        entryIndex.setKccount(String.valueOf(Integer.valueOf(kount) - 1));
                        textOpenRepository.deleteByBorrowcodeAndEntryidIn(borrowcode,entryids);
                    }
                    if("电子、实体查档".equals(borrowmsg.getType())){
                        textOpenRepository.deleteByBorrowcodeAndEntryidIn(borrowcode,entryids);
                    }
                }
            }else{
				if(("电子查档".equals(borrowmsg.getType())||("实体查档".equals(borrowmsg.getType())&&"拒绝".equals(borrowmsg.getLyqx())))&&Integer.valueOf(kount)<1){
					//实体查档，利用权限为拒绝，或者是电子查档，库存份数为0
					hasNotEleOrSt.add(entryIndex);
					continue;
				}else{
					if("电子查档".equals(borrowmsg.getType())){ //之前设置电子查档类型，现在设置实体查档,删除文件级利用权限
						entryIndex.setKccount(String.valueOf(Integer.valueOf(kount) - 1));
						textOpenRepository.deleteByBorrowcodeAndEntryidIn(borrowcode,entryids);
					}
					if("电子、实体查档".equals(borrowmsg.getType())){
						textOpenRepository.deleteByBorrowcodeAndEntryidIn(borrowcode,entryids);
					}
				}
			}
			borrowmsg.setType(settype);
			borrowmsg.setLyqx("查看");
		}
		borrowMsgRepository.save(borrowmsgs);
		String msg = "";
		if(hasNotEleOrSt.size()>0){
			for(Tb_entry_index entry : hasNotEleOrSt){
				if("".equals(msg)){
					msg = "档号为" + entry.getArchivecode();
				}else{
					msg = msg + "，"+entry.getArchivecode();
				}
			}
		}
		if("电子查档".equals(settype)){
			msg = settype + "设置失败 "+ hasNotEleOrSt.size()+" 条，" + msg + "等档案没有电子文件";
		}else if("实体查档".equals(settype) || "调档".equals(settype)){
			msg = settype + "设置失败 "+ hasNotEleOrSt.size()+" 条，" + msg + "等档案没有库存份数";
		}else{
			msg = settype + "设置失败 "+ hasNotEleOrSt.size()+" 条，" + msg + "等档案没有电子文件或者没有库存份数";
		}
		int count = borrowmsgs.size() - hasNotEleOrSt.size();
		if(count > 0){
            if(count==borrowmsgs.size()){
                msg = "成功设置 "+count+" 条"+settype;
            }else{
                msg = "成功 "+count+" 条，" + msg;
            }
		}
		return msg;
	}

	/**
	 * 根据nodeid获取相应的分类节点全名
	 * @param parentmap
	 * @param nodeid
	 * @return
	 */
	public String getFullnameByNodeid(Map<String,Object[]> parentmap,String nodeid){
		Tb_data_node node = (Tb_data_node)parentmap.get(nodeid)[0];
		List<Tb_data_node> parents = (List<Tb_data_node>)parentmap.get(nodeid)[1];
		if (node.getNodename() != null && !"".equals(node.getNodename())) {
			StringBuffer nodefullname = new StringBuffer("");
			for(Tb_data_node parent : parents){
				if(parent == null||parent.getNodetype()==1){  //过滤机构节点
					continue;
				}
				nodefullname.insert(0, "_");
				nodefullname.insert(0, parent.getNodename());
			}
			return nodefullname.toString().substring(0,nodefullname.toString().length()-1);
		}
		return "";
	}

    /**
     * 获取当前环节位置
     *
     * @param taskid
     *            任务id
     */
    public ExtMsg getapproveSort(String taskid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        Tb_flows flowspre = flowsRepository.findByTaskidAndSpmanAndState(taskid, userDetails.getUserid(), "处理中");
        Tb_node node = nodeRepository.findByNodeid(flowspre.getNodeid());// 拿到当前节点
        if (node.getOrders() == 2) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    /**
     * 退回上一环节
     *
     * @param taskid
     *            任务id
     * @param textarea
     *            批示
     */
    public void BackPreBorrowEle(String textarea, String taskid, String type) {
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));// 获取当前时间
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        // 查找到当前正在处理的审批流程信息以及对应的工作流程信息
        Tb_task task = taskRepository.findByTaskid(taskid);
        Tb_flows flows = flowsRepository.findByTaskidAndSpmanAndState(taskid, userDetails.getUserid(), "处理中");// 获取当前任务流程设置状态

        Tb_node node = nodeRepository.findByNodeid(flows.getNodeid());// 拿到当前节点
        Tb_node nodepre = nodeRepository.findByWorkidAndSortsequence(node.getWorkid(), node.getOrders() - 1);// 拿到上一个节点

        // 1.先把当前处理的这条数据的info改成‘完成’
        taskRepository.updateByTaskid(taskid);
        // 把对应的工作流流程改成‘退回’
        flowsRepository.updateByTaskid("退回", flows.getText(), flows.getId());


        // 2.找到上一条审批流程信息以及对应的工作流程信息
        Tb_task taskInfo = taskRepository.findByTaskid(task.getLastid());
        Tb_flows flowsInfo = flowsRepository.findByTaskidAndTextAndState(taskInfo.getId(), nodepre.getText(),
                "完成");
        while (flowsInfo==null){
            taskInfo = taskRepository.findByTaskid(taskInfo.getLastid());
            flowsInfo = flowsRepository.findByTaskidAndTextAndState(taskInfo.getId(), nodepre.getText(),
                    "完成");
        }

        // 查找到上一审批流程的的审批人真实姓名
        String realname = userRepository.findByUserid(taskInfo.getLoginname()).getRealname();

        // 通过当前审批流程信息的lastid找到相同环节的所有审批流程信息
        List<String> otherTaskInfo = taskRepository.findTasksByLastid(task.getLastid());
        for (int i = 0; i < otherTaskInfo.size(); i++) {
            List<Tb_flows> otherFlows = flowsRepository.findByTaskid(otherTaskInfo.get(i));
            for (int j = 0; j < otherFlows.size(); j++) {
                flowsRepository.updateByTaskid("退回", otherFlows.get(j).getText(), otherFlows.get(j).getId());
            }
        }

        // 生成一条新的审批流程 - 内容为上一条的信息
        Tb_task rebackTask = new Tb_task();
        BeanUtils.copyProperties(taskInfo, rebackTask);
        rebackTask.setId(null);
        rebackTask.setApprovetext(nodepre.getText());
        rebackTask.setApproveman(realname);
        rebackTask.setTime(new Date());
        rebackTask.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
        rebackTask.setLastid(taskid);
        Tb_task returnTask = taskRepository.save(rebackTask);

        // 生成一条新的工作流程 - 内容为上一条的信息
        Tb_flows rebackFlows = new Tb_flows();
        BeanUtils.copyProperties(flowsInfo, rebackFlows);
        rebackFlows.setId(null);
        rebackFlows.setTaskid(returnTask.getId());
        rebackFlows.setDate(dateInt);
        rebackFlows.setNodeid(nodepre.getId());
        rebackFlows.setState(Tb_flows.STATE_HANDLE);//处理中
        flowsRepository.save(rebackFlows);

//        List<Tb_flows> backFlows = flowsRepository.findByMsgidAndStateIn(flows.getMsgid(), "退回");
//        for (int i = 0; i < backFlows.size(); i++) {
//            updateElectroInfo(nodepre.getText(), realname, backFlows.get(i).getTaskid());
//        }

        // 更新审批的对应实体单据信息
        if (type.indexOf("销毁") > -1) {
            Tb_bill_approval billApproval = billApprovalRepository.findByTaskidContains(taskid.trim());

            List<Tb_flows> flowsList = flowsRepository.findByMsgidAndText(billApproval.getCode(), nodepre.getText());
            String preSpman = flowsList.get(0).getSpman();
            String preSpmanRealname = userRepository.findByUserid(preSpman).getRealname();


            // 更新销毁单据表中对应的审批流程id
            billApproval.setTaskid(billApproval.getTaskid() + "," + returnTask.getId());
            billApproval.setApprove(textarea);
            billApproval.setState(Tb_borrowdoc.STATE_FINISH_AUDIT);
            billApproval.setUsername(preSpmanRealname);
            billApprovalRepository.save(billApproval);
            billRepository.updateByBillid(node.getText(), preSpmanRealname, billApproval.getBillid());
        } else if (type.indexOf("开放") > -1) {
            Tb_opendoc opendoc = opendocRepository.getOpendoc(taskid);
            opendoc.setApprove(textarea);
            List<Tb_openmsg> openmsgList =  openmsgRepository.getOpenmsgsByTask(taskid);
            if("初审".equals(returnTask.getApprovetext())){
                for (int i = 0; i < openmsgList.size(); i++) {
                    openmsgRepository.updatefInfoById(openmsgList.get(i).getId());
                }
            }else if("复审".equals(returnTask.getApprovetext())){
                for (int i = 0; i < openmsgList.size(); i++) {
                    openmsgRepository.updatelInfoById(openmsgList.get(i).getId());
                }
            }
        } else {
            // 更新借阅单据信息
            Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskInfo.getId());// 获取任务单据设置批示
            borrowdoc.setApprove(textarea);

            List<Tb_flows> flowsList = flowsRepository.findByMsgidAndText(borrowdoc.getBorrowcode(), nodepre.getText());
            String preSpman = flowsList.get(0).getSpman();
            String preSpmanRealname = userRepository.findByUserid(preSpman).getRealname();
//            borrowdoc.setApprovemanid(preSpman);// 更新当前审批人id

            borrowdoc.setState(Tb_borrowdoc.STATE_FINISH_AUDIT);
            borrowdoc.setApprovetext(nodepre.getText());// 更新下一审批环节
            borrowdoc.setApproveman(preSpmanRealname);// 更新当前审批人
        }

        webSocketService.noticeRefresh();// 刷新审批人桌面信息
    }
}