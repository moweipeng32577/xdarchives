package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.secondaryDataSource.repository.SxElectronicBrowseRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/8/16.
 */
@Service
@Transactional
public class StApproveService {

	@Autowired
	UserService userService;
	
	@Autowired
	ElectronApproveService electronApproveService;

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
	UserNodeRepository userNodeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	JyAdminsService jyAdminsService;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	TextOpenRepository textOpenRepository;

	@Autowired
	CaptureMetadataService captureMetadataService;

	@Autowired
	SecondaryEntryIndexRepository secondaryEntryIndexRepository;

	@Autowired
	SxElectronicBrowseRepository sxElectronicBrowseRepository;

	public Tb_flows getFlows(String taskid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return flowsRepository.findByTaskidAndSpman(taskid, userDetails.getLoginname());
	}

	public List<Tb_entry_index> getEntryIndex(String taskid) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);
		String[] entryids = GainField.getFieldValues(borrowmsgs, "groupid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(borrowmsgs, "entryid");
		return entryIndexRepository.findByEntryidIn(entryids);
	}

	public Tb_borrowdoc getBorrowDoc(String taskid) {
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
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

	public List<Tb_node> getNodes(String nodeId) {
		Tb_node node = nodeRepository.findByNodeid(nodeId);
		return nodeRepository.getNodes(node.getNextid().split(","));
	}

	/**
	 * 获取下一个审批人
	 * 
	 * @param nodeId
	 *            节点id
	 * @return
	 */
	public List<Tb_user> getNextSpman(String nodeId) {
		Tb_node node = nodeRepository.findByNodeid(nodeId);
		List<Tb_user_node> user_nodes = userNodeRepository.findByNodeidIn(node.getNextid().split(","));
		String[] userids = GainField.getFieldValues(user_nodes, "groupid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_nodes, "userid");
		// userids = userService.getFilteredUseridByOrgan(userids);//过滤其它机构用户
		return userRepository.findByUseridIn(userids);
	}

	/**
	 * 设置利用权限
	 * 
	 * @param taskid
	 *            任务id
	 * @param dataids
	 *            勾选记录id
	 * @param lyqx
	 *            利用权限
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
	 *            任务id
	 * @param nodeId
	 *            节点id
	 * @param borrowtyts
	 *            同意查档天数
	 * @return
	 */
	public String approveSubmit(String textArea,String nextNode,String nextSpman,String taskid,String nodeId,int borrowtyts) {
		String hintText = "";
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);

		///////////////////////////////////验证库存份数是否合法///////////////////////////////////
		String[] entryids = new String[borrowmsgs!=null?borrowmsgs.size():0];
		for (int j=0;j<borrowmsgs.size();j++){//获取条目id用户查找库存份数不为0的条目
			entryids[j] = borrowmsgs.get(j).getEntryid();
		}

		//获取库存份数为0的条目
		List<Tb_entry_index> entry_indices =  entryIndexRepository.findByKccountAndEntryidIn("0",entryids);
		if(entry_indices != null && entry_indices.size() > 0){//判断库存份数为0的数目是否等于借出条目数
			int status = 0;
			for(Tb_entry_index index : entry_indices){
				for(Tb_borrowmsg borrowmsg : borrowmsgs){
					if(index.getEntryid().equals(borrowmsg.getEntryid()) && "借出".equals(borrowmsg.getLyqx())){
						status +=1;
					}
				}
				hintText += "、"+index.getArchivecode();
			}
			if(status > 0){//如果利用权限全为借出且库存份数都不足的情况下返回提示
				hintText = "档号为["+hintText.substring(1)+"]的条目库存份数为0,无法授权借出，请修改其权限为拒绝。";
				return hintText;
			}
		}

		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String str = "已通过";
		int i = 0;
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);//获取单据修改批示,同意查档天数
		borrowdoc.setApprove(textArea);
		borrowdoc.setBorrowtyts(borrowtyts);
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));

		Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid,userDetails.getUserid());//获取本流程修改状态及流程完成时间
		Tb_task task1 = taskRepository.findByTaskid(taskid);//获取任务修改状态
		Tb_node node = nodeRepository.findByNodeid(nextNode);
		if(node.getOrders()==3){ //第一审批环节更新受理时间
			borrowdoc.setAcceptdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		if(!"".equals(nextSpman)){//下一环节
			String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
			//更新上一个环节的任务信息
			electronApproveService.updateElectroInfo(node.getText(), nextSpmanRealname, taskid);
			//创建下一环节的任务信息
			Tb_task task = new Tb_task();
			task.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
			task.setTime(new Date());
			task.setLoginname(nextSpman);
			task.setText(task1.getText());
			task.setType(task1.getType());
			task.setApprovetext(node.getText());
			task.setApproveman(nextSpmanRealname);
			task.setLastid(taskid);
			Tb_task task2 = taskRepository.save(task);//下一审批人任务
			//更新上一环节的工作流信息
			//flows.setSpman(nextSpman);
			//flows.setText(node.getText());
			flows.setState(Tb_flows.STATE_FINISHED);
			flows.setDate(dateInt);
			//创建下一环节的工作流信息
			Tb_flows flows1 = new Tb_flows();
			flows1.setNodeid(node.getId());
			flows1.setText(node.getText());
			flows1.setSpman(nextSpman);
			flows1.setTaskid(task2.getId());
			flows1.setMsgid(flows.getMsgid());
			flows1.setState(Tb_flows.STATE_HANDLE);//处理中
			flows1.setDate(dateInt);
			flowsRepository.save(flows1);//下一流程
			//更新实体查档信息
			borrowdoc.setState("已送审");
			borrowdoc.setApprovetext(node.getText());//更新下一审批环节
			borrowdoc.setApproveman(nextSpmanRealname);//更新当前审批人
			hintText = "审批完成";
		}else{//单据审批完成
			for(Tb_borrowmsg borrowmsg:borrowmsgs){
				if("拒绝".equals(borrowmsg.getLyqx())||borrowmsg.getLyqx()==null){
					i += 1;
					borrowmsg.setState("");
				}else{
					borrowmsg.setState("未归还");
					borrowmsg.setJybackdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));//审批完成时间
				}
			}
			if(i==borrowmsgs.size()){//当全部为拒绝时候状态为不通过
				str = "不通过";
			}
			//更新上一环节工作流信息
			//flows.setSpman(userDetails.getUserid());
			flows.setState(Tb_flows.STATE_FINISHED);//完成
			flows.setDate(dateInt);

			List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
			Tb_node node1 = nodes.get(nodes.size()-1);
			//完成单据审批
			Tb_flows flows1 = new Tb_flows();
			flows1.setNodeid(node1.getId());
			flows1.setText(node1.getText());
			flows1.setDate(dateInt);
			flows1.setTaskid(taskid);
			flows1.setMsgid(flows.getMsgid());
			flows1.setState(Tb_flows.STATE_FINISHED);//完成
			flowsRepository.save(flows1);
			if("已通过".equals(str)){
				setEntryIndexKc(taskid);//当审批结束并且通过时修改库存份数
			}
			hintText = "审批完成";
			borrowdoc.setState(str);
			borrowdoc.setApprovetext(node1.getText());
			borrowdoc.setReturnstate("未归还");
			borrowdoc.setApproveman(userDetails.getRealname());
			borrowdoc.setClearstate("1");//通知申请人
			borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
		}
		if("已通过".equals(str)){//审批通过时修改条目查档天数
			for(Tb_borrowmsg borrowmsg:borrowmsgs){
				captureMetadataService.saveServiceMetadata(borrowmsg.getEntryid(),"利用平台","实体查档");
				borrowmsg.setJyts(borrowtyts);
			}
		}
		return hintText;
	}

	/**
	 * 获取审批通过的数据
	 * 
	 * @param taskid
	 */
	public void setEntryIndexKc(String taskid) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			if (!"拒绝".equals(borrowmsg.getLyqx())) {
				Tb_entry_index entry_index = entryIndexRepository.findByEntryid(borrowmsg.getEntryid());
				entry_index.setKccount(String.valueOf(Integer.valueOf(entry_index.getKccount().trim()) - 1));
			}
		}
	}

	public Integer deleteTask(String taskId) {
		jyAdminsService.changeTaskState();// 判断/删除本机构下到期通知,并通知客户端更新
		return taskRepository.deleteByTaskid(taskId);
	}

	public boolean getWorkText(String taskid){
		Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		Tb_flows flows = flowsRepository.findByTaskidAndState(taskid,"处理中");
		Tb_node node = nodeRepository.getNode("查档审批");
		if("提交".equals(borrowdoc.getSubmitstate())&&node.getText().equals(flows.getText())){
			return true;
		}
		return false;
	}


	/**
	 * 导入选择的条目到单据（去掉重复条目）
	 *
	 * @param dataids
	 * @param taskid
	 * @return
	 */
	public String searchAdd(String[] dataids, String taskid,String type) {
		Tb_borrowdoc doc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		List<Tb_borrowmsg> existList = borrowMsgRepository.getBorrowmsgsByBorrowdocid(doc.getId());
		Set<String> existIdSet = new HashSet<>();
		for (Tb_borrowmsg borrowmsg : existList) {
			existIdSet.add(borrowmsg.getEntryid().trim());
		}
		List<String> findList = new ArrayList<>();
		for (String dataid : dataids) {
			if (!existIdSet.contains(dataid.trim())) {
				findList.add(dataid.trim());
			}
		}
		String[] findAr = new String[findList.size()];
		findList.toArray(findAr);
		int repNum = dataids.length - findAr.length;
        if(findAr.length==0)return repNum+"";
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_borrowmsg> borrowmsgList = new ArrayList<>();
		List<Tb_entry_index> eiList=new ArrayList<>();
		if("soundimage".equals(type)){
			List<Tb_entry_index_sx> sxList=secondaryEntryIndexRepository.findByEntryidIn(findAr);
			for (Tb_entry_index_sx entryIndexSx : sxList) {
				Tb_entry_index entry_index=new Tb_entry_index();
				BeanUtils.copyProperties(entryIndexSx,entry_index);
				eiList.add(entry_index);
			}
		}else {
			eiList = entryIndexRepository.findByEntryidIn(findAr);
		}
		for (Tb_entry_index ei : eiList) {
			Tb_borrowmsg borrowmsg = new Tb_borrowmsg();
			borrowmsg.setBorrowcode(doc.getBorrowcode());
			borrowmsg.setBorrowman(doc.getBorrowman());
			borrowmsg.setBorrowmantel(doc.getBorrowmantel());
			borrowmsg.setEntryid(ei.getEntryid());
			borrowmsg.setState("");
			borrowmsg.setBorrowdate(doc.getBorrowdate());
			borrowmsg.setJyts(doc.getBorrowts());
			borrowmsg.setApprover(userDetails.getUserid());
			borrowmsgList.add(borrowmsg);
			borrowmsg.setLyqx("查看");
			if("soundimage".equals(type)){
				borrowmsg.setType("电子查档");
				Tb_electronic_browse_sx electroniclist = sxElectronicBrowseRepository.findByEntryid(ei.getEntryid());
				List<Tb_textopen> textopenlist = new ArrayList<>();
				Tb_textopen textopen = new Tb_textopen();
				textopen.setBorrowcode(doc.getBorrowcode());
				textopen.setEleid(electroniclist.getEleid());
				textopen.setState("查看");
				textopen.setEntryid(ei.getEntryid());
				textopenlist.add(textopen);
				textOpenRepository.save(textopenlist);
			}else if(ei.getEleid()!=null&&!"".equals(ei.getEleid())){
				borrowmsg.setType("电子查档");
				List<Tb_electronic> electroniclist = electronicRepository.findByEntryid(ei.getEntryid());
				List<Tb_textopen> textopenlist = new ArrayList<>();
				for (int j = 0; j < electroniclist.size(); j++) {
					Tb_textopen textopen = new Tb_textopen();
					textopen.setBorrowcode(doc.getBorrowcode());
					textopen.setEleid(electroniclist.get(j).getEleid());
					textopen.setState("查看");
					textopen.setEntryid(ei.getEntryid());
					textopenlist.add(textopen);
				}
				textOpenRepository.save(textopenlist);
			}else{
				if(Integer.valueOf(ei.getKccount().trim())<1){
					borrowmsg.setLyqx("拒绝");
				}else{
					ei.setKccount(String.valueOf(Integer.valueOf(ei.getKccount().trim()) - 1));
				}
				borrowmsg.setType("实体查档");
			}
		}
		borrowMsgRepository.save(borrowmsgList);// 添加查档具体信息
		return repNum + "";
	}

	/**
	 * 删除审批的数据
	 *
	 * @param dataids
	 * @param taskid
	 * @return
	 */
	public int deleteEntries(String[] dataids, String taskid) {
		Tb_borrowdoc doc = borrowDocRepository.getBorrowDocByTaskid(taskid);
		textOpenRepository.deleteByBorrowcodeAndEntryidIn(doc.getBorrowcode(), dataids);
		return borrowMsgRepository.deleteByBorrowcodeAndEntryidIn(doc.getBorrowcode(), dataids);
	}
}