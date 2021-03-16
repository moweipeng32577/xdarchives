package com.wisdom.web.service;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.secondaryDataSource.repository.SxElectronicBrowseRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.controller.UserController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.smsclient.SMSService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

/**
 * Created by Administrator on 2017/8/16.
 */
@Service
@Transactional
public class ElectronBorrowService {

	@Value("${workflow.stborrow.approve.workid}")
	private String stborrowWorkid;
	@Value("${workflow.dzborrow.approve.workid}")
	private String dzborrowWorkid;
	@Value("${workflow.destroy.approve.workid}")
	private String destroyWorkid;
	@Value("${workflow.open.approve.workid}")
	private String openWorkid;
	@Value("${system.document.rootpath}")
	private String rootpath;//系统文件根目录

	@Autowired
	UserService userService;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	FlowsRepository flowsRepository;

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	BorrowMsgRepository borrowMsgRepository;

	@Autowired
	NodeRepository nodeRepository;

	@Autowired
	JyPurposeRepository jyPurposeRepository;

	@Autowired
	UserNodeRepository userNodeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	StBoxRepository stBoxRepository;

	@Autowired
	SystemConfigRepository systemConfigRepository;

	@Autowired
	WorkRepository workRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	JyAdminsService jyAdminsService;

	@Autowired
	WebSocketService webSocketService;
	
	@Autowired
	WorkflowService workflowService;

	@Autowired
	RightOrganRepository rightOrganRepository;
	
	@Autowired
	MissionUserRepository missionUserRepository;

	@Autowired
	TextOpenRepository textOpenRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	ElectronicPrintRepository electronicPrintRepository;

	@Autowired
	FileNoneRepository fileNoneRepository;

	@Autowired
	ElectronicSolidRepository electronicSolidRepository;

	@Autowired
	UserController userController;

    @Autowired
    NodesettingService nodesettingService;

	@Autowired
	OpendocRepository opendocRepository;

	@Autowired
	BillApprovalRepository billApprovalRepository;

	@Autowired
	TransdocRepository transdocRepository;

	@Autowired
	CarOrderRepository carOrderRepository;

	@Autowired
	PlaceOrderRepository placeOrderRepository;

	@Autowired
	SecondaryEntryIndexRepository secondaryEntryIndexRepository;

	@Autowired
	SxElectronicBrowseRepository sxElectronicBrowseRepository;

	public Page<Tb_entry_index> getEntryIndex(String dataids, int page, int limit,String isFlag) {
		Page<Tb_entry_index> pageList=null;
		if("1".equals(isFlag)){//判断是否为声像开放
			Page<Tb_entry_index_sx> indexSxPage= secondaryEntryIndexRepository.findByEntryidIn(dataids.split(","),
					new PageRequest(page - 1, limit, new Sort("archivecode")));
			List<Tb_entry_index> returnResult=new ArrayList<>();
			if(indexSxPage.getContent().size()>0){
				indexSxPage.getContent().parallelStream().forEach(entry->{
					Tb_entry_index entry_index=new Tb_entry_index();
					BeanUtils.copyProperties(entry,entry_index);
					BeanUtils.copyProperties(entry.getTdn(),entry_index.getTdn());
					returnResult.add(entry_index);
				});
			}
			return new PageImpl(returnResult, new PageRequest(page - 1, limit), indexSxPage.getTotalElements());
		}else {
			pageList = entryIndexRepository.findByEntryidIn(dataids.split(","),
					new PageRequest(page - 1, limit, new Sort("archivecode")));
		}
		return pageList;
	}

	public Page<Tb_entry_index> getBoxEntryIndex(int page, int limit, String borrowType,String isFlag) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_st_box> st_boxes = stBoxRepository.findByUseridAndBorrowtype(userDetails.getUserid(),borrowType);
		String[] entryids = GainField.getFieldValues(st_boxes, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(st_boxes, "entryid");
        List<Tb_entry_index> entry_indexs = new ArrayList<>();
        List<String[]> subAry = new InformService().subArray(entryids, 1000);// 处理ORACLE1000参数问题
        for (String[] ary : subAry) {
        	if("1".equals(isFlag)){//判断是否为声像数据源
				List<Tb_entry_index_sx> sxList = secondaryEntryIndexRepository.findByEntryidIn(ary);
				if(sxList.size()>0){
					sxList.parallelStream().forEach(entry->{
						Tb_entry_index entry_index=new Tb_entry_index();
						BeanUtils.copyProperties(entry,entry_index);
						BeanUtils.copyProperties(entry.getTdn(),entry_index.getTdn());
						entry_indexs.add(entry_index);
					});
				}
			}else {
				entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ary));
			}
        }
        return getFullName(entry_indexs, new PageRequest(page - 1, limit, new Sort("archivecode")),isFlag);

//		return entryIndexRepository.findByEntryidIn(entryids,
//				new PageRequest(page - 1, limit, new Sort("archivecode")));
	}

    //获取数据节点全名
    private Page<Tb_entry_index> getFullName(List<Tb_entry_index> result, PageRequest pageRequest,String isFlag) {
        long totalElements = result.size();
        List<Tb_entry_index> returnResult = new ArrayList<>();
        Map<String,String> nodeNameMap=new HashMap<>();//存放nodeid-nodeid数据节点的全名
        String nodefullname ="";
        for (Tb_entry_index entryIndex : result) {
			Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex, entry_index);
            if(entry_index.getTdn()!=null){
                String nodeid = entry_index.getTdn().getNodeid();
                //判断map中是否已存在对应nodeid的节点全名
                if(nodeNameMap.containsKey(nodeid)){//存在的话直接设置
                    nodefullname =nodeNameMap.get(nodeid);
                }else{//不存在时再获取
                	if("1".equals(isFlag)){
						nodefullname = nodesettingService.getSxNodefullnameLoop(nodeid, "_", "");
					}else {
						nodefullname = nodesettingService.getNodefullnameLoop(nodeid, "_", "");
					}
                    nodeNameMap.put(nodeid,nodefullname);//将新获取的节点全名加入map
                }
                entry_index.setNodefullname(nodefullname);
                returnResult.add(entry_index);
            }
        }
        return new PageImpl(returnResult, pageRequest, totalElements);
    }

	/**
	 * 电子查档表单提交
	 * 
	 * @param borrowdoc
	 *            表单实例
	 * @return
	 */
	public Tb_borrowdoc addBorrow(Tb_borrowdoc borrowdoc,String[] eleids,String datasourcetype) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		String spman = borrowdoc.getBorrowcode();

		String borrowcode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
		borrowdoc.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		borrowdoc.setBorrowcode(borrowcode);
		borrowdoc.setType("查档");
		borrowdoc.setState("已送审");
		borrowdoc.setBorrowmanid(userDetails.getUserid());
		borrowdoc.setDatasourcetype(datasourcetype);
		List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
		for (int i = 0; i < borrowdoc.getId().split(",").length; i++) {
			Tb_borrowmsg borrowmsg = new Tb_borrowmsg();
			borrowmsg.setBorrowcode(borrowcode);
			borrowmsg.setEntryid(borrowdoc.getId().split(",")[i]);
			borrowmsg.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
			borrowmsg.setJyts(borrowdoc.getBorrowts());
			borrowmsg.setLyqx("查看");
			List<Tb_st_box> st_boxs = stBoxRepository.findByUseridAndEntryidAndBorrowtype(userDetails.getUserid(),borrowdoc.getId().split(",")[i],"查档");
			if(st_boxs==null||st_boxs.size()==0){
				continue;
			}
			String settype = st_boxs.get(0).getSettype();  //查档类型
			borrowmsg.setType(settype);
			if("电子查档".equals(settype)){
				borrowmsg.setState("");
				borrowmsg.setType("电子查档");
				if("soundimage".equals(datasourcetype)){
					Tb_electronic_browse_sx electroniclist = sxElectronicBrowseRepository.findByEntryid(borrowdoc.getId().split(",")[i]);
					List<Tb_textopen> textopenlist = new ArrayList<>();
					Tb_textopen textopen = new Tb_textopen();
					textopen.setBorrowcode(borrowdoc.getBorrowcode());
					textopen.setEleid(electroniclist.getEleid());
					textopen.setState("查看");
					textopen.setEntryid(borrowdoc.getId().split(",")[i]);
					textopenlist.add(textopen);
					textOpenRepository.save(textopenlist);
				}else {
					saveTextOpen(borrowdoc.getId().split(",")[i],borrowcode);
				}
			}else if("电子、实体查档".equals(settype)){
				saveTextOpen(borrowdoc.getId().split(",")[i],borrowcode);
				borrowdoc.setOutwarestate("未借出");
				if("soundimage".equals(datasourcetype)){
					Tb_entry_index_sx entryIndex = secondaryEntryIndexRepository.findAllByEntryid(borrowdoc.getId().split(",")[i]);
					entryIndex.setKccount(String.valueOf(Integer.valueOf(entryIndex.getKccount().trim()) - 1));
				}else {
					Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(borrowdoc.getId().split(",")[i]);
					entryIndex.setKccount(String.valueOf(Integer.valueOf(entryIndex.getKccount().trim()) - 1));
				}
				borrowmsg.setState("待借出");
				borrowmsg.setBorrowman(userDetails.getRealname());
				borrowmsg.setBorrowmantel(borrowdoc.getBorrowmantel());
				borrowmsg.setApprover(spman);
			}else{
				borrowdoc.setOutwarestate("未借出");
				if("soundimage".equals(datasourcetype)){
					Tb_entry_index_sx entryIndex = secondaryEntryIndexRepository.findAllByEntryid(borrowdoc.getId().split(",")[i]);
					entryIndex.setKccount(String.valueOf(Integer.valueOf(entryIndex.getKccount().trim()) - 1));
				}else {
					Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(borrowdoc.getId().split(",")[i]);
					entryIndex.setKccount(String.valueOf(Integer.valueOf(entryIndex.getKccount().trim()) - 1));
				}
				borrowmsg.setState("待借出");
				borrowmsg.setBorrowman(userDetails.getRealname());
				borrowmsg.setBorrowmantel(borrowdoc.getBorrowmantel());
				borrowmsg.setApprover(spman);
			}
			borrowmsgs.add(borrowmsg);
		}
		if(borrowmsgs.size()<=0){
			return null;
		}
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(spman);
		task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
		task.setText(borrowdoc.getBorrowman() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
				+ " 查档申请");
		task.setType("查档");
		task.setTime(new Date());
		task = taskRepository.save(task);// 添加任务

		borrowMsgRepository.save(borrowmsgs);// 添加查档具体信息
		Tb_node node = nodeRepository.getNode("查档审批");
		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);// 完成
		flows.setTaskid(task.getId());
		flows.setMsgid(borrowcode);
		flows.setDate(dateInt);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);// 添加启动流程实例

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
		flows1.setDate(dateInt);
		flows1.setTaskid(task.getId());
		flows1.setMsgid(borrowcode);
		flows1.setSpman(spman);
		flows1.setNodeid(node.getId());

		if (eleids != null) { //更新附件
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(borrowcode)
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(borrowcode);
				electronic.setFilepath(filepath);
			}
		}

		flowsRepository.save(flows1);// 添加下一流程实例
		stBoxRepository.deleteByEntryidInAndUseridAndBorrowtype(borrowdoc.getId().split(","),userDetails.getUserid(),"查档");//删除中间表数据
		return borrowDocRepository.save(borrowdoc);// 添加查档单据
	}

	public void saveTextOpen(String entryid,String borrowcode){
		List<Tb_electronic> electroniclist = electronicRepository.findByEntryid(entryid);
		List<Tb_textopen> textopenlist = new ArrayList<>();
		for (int j = 0; j < electroniclist.size(); j++) {
			Tb_textopen textopen = new Tb_textopen();
			textopen.setBorrowcode(borrowcode);
			textopen.setEleid(electroniclist.get(j).getEleid());
			textopen.setState("查看");
			textopen.setEntryid(entryid);
			textopenlist.add(textopen);
		}
		textOpenRepository.save(textopenlist);
	}

	public List<Tb_Msg> findMsg() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_task> tasks = taskRepository.findByLoginnameAndStateOrderByTasktimeDesc(userDetails.getUserid(),
				Tb_task.STATE_WAIT_HANDLE);// 待处理
		List<Tb_Msg> msgs = new ArrayList<>();
		for (Tb_task task : tasks) {
			Tb_Msg msg = new Tb_Msg();
			msg.setMsgid(task.getId().trim());
			msg.setMsgtype("2");
			if ("实体查档".equals(task.getType())) {
				msg.setMsgtype("3");
			}
			msg.setMsgtypetext(task.getType());
			msg.setMsgtext(task.getText());
			msg.setBorrowmsgid(task.getBorrowmsgid());
			msg.setUrging(task.getUrgingstate());
			msgs.add(msg);
		}
		return msgs;
	}

	public List<Tb_system_config> getJypurpose() {
		return systemConfigRepository.findByParentValue("borrowPurpose");
	}

	public List<Tb_user> getApproveMan(String worktext,boolean istemporary,String organids,String findOrganid) {
		// 通过worktext获取工作流ID
		Tb_work work = workRepository.findByWorktext(worktext);
		String nodeId = "";
		if (work != null && work.getText() != null) {
			Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 1);
			nodeId = node.getNextid().split(",")[0];
		}
		// List<Tb_user_node> user_nodes =
		// userNodeRepository.getUserNodes(workRepository.findByWorktext(worktext).getId());
		List<Tb_user_node> user_nodes = userNodeRepository.findUserNodes(nodeId);
		String[] userids = GainField.getFieldValues(user_nodes, "userid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_nodes, "userid");
		if(organids!=null && !"".equals(organids)){
            userids = userService.getFilteredUseridByOrgans(userids,organids);// 根据档案所属单位过滤其它单位用户
        }
		List<String> idStrings = new ArrayList<>();
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (int i = 0; i < userids.length; i++) {
//			if (!userids[i].equals(userDetails.getUserid())) {
				idStrings.add(userids[i]);
//			}
		}
		if(!istemporary&&!"查档审批".equals(worktext)){
			idStrings.remove(userDetails.getUserid());
		}
		List<Tb_user> users = new ArrayList<>();
		for(int i=0;i<idStrings.size();i++){  //排序用户
			Tb_user user = userRepository.findByUserid(idStrings.get(i));
			if(user!=null){
				users.add(user);
			}
		}
//		List<Tb_user> users = userRepository.findByUseridIn(idStrings);// 获取本单位下的全部用户
		List<Tb_user> backUsers = new ArrayList<>(); // 返回的排序用户
//		List<Tb_user> unitUsers = new ArrayList<>(); // 单位非排序用户
//		List<Tb_user> allDepartmentUsers = new ArrayList<>();// 全部部门非排序用户
//		String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
//		Tb_right_organ organ = rightOrganRepository.findOne(organid);
//		while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
//			organ = rightOrganRepository.findOne(organ.getParentid());
//		}
//		for (Tb_user user : users) {
//			if (organid != null && organid.trim().equals(user.getOrganid().trim())) {
//				backUsers.add(user);// 如果是本部门审批人则放在一级集合
//			} else {
//				if (organ.getOrganid() != null && organ.getOrganid().trim().contains(user.getOrganid().trim())) {
//					unitUsers.add(user);// 如果是本单位审批人则放在二级级集合
//				} else {
//					allDepartmentUsers.add(user);// 剩余放在全部部门集合
//				}
//			}
//		}
        if("年检审核".equals(worktext)){
            backUsers=users;
        }else {
		for (Tb_user user : users) {
			String organid = userRepository.findOrganidByUserid(user.getUserid());
			Tb_right_organ organ = rightOrganRepository.findOne(organid);
			//找到当前用户的所在单位
			while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
				organ = rightOrganRepository.findOne(organ.getParentid());
			}
			if (findOrganid != null && findOrganid.trim().contains(organ.getOrganid().trim())) {
				backUsers.add(user); // 如果是本单位审批人则返回
			}
		}
        }
//		backUsers.addAll(unitUsers);// 合并
//		backUsers.addAll(allDepartmentUsers);
		List<String> lusers = new ArrayList<String>();
		if (backUsers.size() > 0) {
			for (int i = 0; i < backUsers.size(); i++) {
				Tb_user user = backUsers.get(i);
				lusers.add(user.getRealname() + "-" + user.getUserid());
			}
		}
		String[] strings = new String[lusers.size()];
		String[] arrStrings = lusers.toArray(strings);
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

	public String setStJyBox(String[] dataids,String borrowType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		boolean b = true;
		String msg = "";
//		if("实体查档".equals(borrowType)) {
//			List<Tb_entry_index> entry_indices = entryIndexRepository.findByEntryidIn(dataids);
//			for (Tb_entry_index entry_index : entry_indices) {
//				if (entry_index.getKccount()==null||"".equals(entry_index.getKccount())||Integer.valueOf(entry_index.getKccount().trim()) == 0) {
//					b = false;
//					break;
//				}
//			}
//		}
		for (String dataid : dataids) {
			Tb_st_box st_box = new Tb_st_box();
			st_box.setEntryid(dataid);
			st_box.setUserid(userDetails.getUserid());
			st_box.setBorrowtype(borrowType);//设置查档类型
			List<Tb_st_box> list = stBoxRepository.findByUseridAndEntryidAndBorrowtype(userDetails.getUserid(), st_box.getEntryid(), borrowType);
			if (list != null && list.size() < 1) {
				stBoxRepository.save(st_box);
			}
		}
		msg = "成功添加 " + dataids.length + " 条数据";
		return msg;
	}

	public boolean checkKccount(String[] dataids) {
		boolean b = true;
		List<Tb_entry_index> entry_indices = entryIndexRepository.findByEntryidIn(dataids);
		for (Tb_entry_index entry_index : entry_indices) {
			if (Integer.valueOf(entry_index.getKccount()) == 0) {
				b = false;
				break;
			}
		}
		return b;
	}

	/**
	 * 实体查档单据提交
	 * 
	 * @param borrowdoc
	 *            单据实例
	 * @return
	 */
	public int addStBorrow(Tb_borrowdoc borrowdoc,String[] eleids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String spman = borrowdoc.getBorrowcode();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(spman);
		task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
		task.setText(borrowdoc.getBorrowman() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
				+ " 查档申请");
		task.setType("实体查档");
		task.setTime(new Date());

		task = taskRepository.save(task);// 生成任务

		String borrowcode = UUID.randomUUID().toString().replace("-", "");// 查档单号
		// borrowdoc.setLymode("查看");
		borrowdoc.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		borrowdoc.setBorrowcode(borrowcode);
		borrowdoc.setType("实体查档");
		borrowdoc.setState("已送审");
		borrowdoc.setBorrowmanid(userDetails.getUserid());
		borrowdoc.setOutwarestate("未借出");
		borrowDocRepository.save(borrowdoc);// 生成单据

		List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
		for (int i = 0; i < borrowdoc.getId().split(",").length; i++) {
			Tb_borrowmsg borrowmsg = new Tb_borrowmsg();
			borrowmsg.setBorrowcode(borrowcode);
			borrowmsg.setEntryid(borrowdoc.getId().split(",")[i]);
			borrowmsg.setState("待借出");
			borrowmsg.setBorrowman(userDetails.getRealname());
			borrowmsg.setBorrowmantel(borrowdoc.getBorrowmantel());
			borrowmsg.setApprover(spman);
			borrowmsg.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
			borrowmsg.setJyts(borrowdoc.getBorrowts());
			borrowmsg.setLyqx("借出");// 默认赋予借出权限
			borrowmsgs.add(borrowmsg);
		}
		borrowMsgRepository.save(borrowmsgs);// 生成查档详细信息

		Tb_node node = nodeRepository.getNode("实体查档审批");
		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);// 完成
		flows.setDate(dateInt);
		flows.setTaskid(task.getId());
		flows.setMsgid(borrowcode);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
		flows1.setTaskid(task.getId());
		flows1.setMsgid(borrowcode);
		flows1.setSpman(spman);
		flows1.setDate(dateInt);
		flows1.setNodeid(node.getId());

		flowsRepository.save(flows1);// 生成环节流程

		if (eleids != null) { //更新附件
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(borrowcode)
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(borrowcode);
				electronic.setFilepath(filepath);
			}
		}

		stBoxRepository.deleteByEntryidInAndUseridAndBorrowtype(borrowdoc.getId().split(","),userDetails.getUserid(),"实体查档");//删除中间表数据
		return 1;
	}

	/**
	 * 续借
	 * 
	 * @param ids
	 *            数据id数组
	 * @param ts
	 *            续借天数
	 * @param xjapprove
	 *            续借描述
	 * @return
	 */
	public int addStXjBorrow(String[] ids, int ts, String xjapprove, boolean flag) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByMsgidIn(ids);
		List<Tb_borrowmsg> borrowmsgs_copy = new ArrayList<>();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			Tb_borrowmsg tb_borrowmsg = new Tb_borrowmsg();
			BeanUtils.copyProperties(borrowmsg, tb_borrowmsg);
			tb_borrowmsg.setJyts(borrowmsg.getJyts() + ts);
			tb_borrowmsg.setRenewreason(borrowmsg.getRenewreason() != null
					? borrowmsg.getRenewreason() + "\n" + xjapprove : "" + xjapprove);
			borrowmsgs_copy.add(tb_borrowmsg);
		}

		jyAdminsService.changeTaskState();// 判断/删除本机构下到期通知,并通知客户端更新

		return borrowMsgRepository.save(borrowmsgs_copy).size();
	}

	public Page<Tb_entry_index> getTreeEntryIndex(String nodeid, int page, int limit) {
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		return entryIndexRepository.findByNodeid(pageRequest, nodeid);
	}

	public Page<Tb_borrowdoc> getBorrowDoc(int page, int limit) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Specifications sp = Specifications.where(new SpecificationUtil("borrowman","equal",userDetails.getRealname()));
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(new Sort.Order(Sort.Direction.DESC,"borrowdate"));//置顶
		sorts.add(new Sort.Order(Sort.Direction.DESC,"docid"));
		PageRequest pageRequest = new PageRequest(page - 1, limit,new Sort(sorts));
		return borrowDocRepository.findAll(sp,pageRequest);
	}

	public void deleteBorrowbox(String[] ids,String borrowType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if("电子打印".equals(borrowType)){ //删除已经设置打印范围
			electronicPrintRepository.deleteByEntryidInAndBorrowcode(ids,null);
		}
		stBoxRepository.deleteByEntryidInAndUseridAndBorrowtype(ids, userDetails.getUserid(),borrowType);
	}

//	public static void main(String args[]) {
//		String[] strs = new String[] { "1", "2", "3" };
//		String[] approverArray = Arrays.copyOf(strs, strs.length);
//		// 因为过滤会去掉当前用户，加上自己
//		approverArray[approverArray.length - 1] = "4";
//		for (String str : approverArray) {
//			System.out.println(str);
//		}
//	}
	public List<Tb_entry_index> getBorrowEntry(String[] dataids) {
		return entryIndexRepository.findByEntryidIn(dataids);
	}

	/**
	 * 实体查档没有条目单据提交
	 * @param borrowdoc 单据实例
	 * @return
	 */
	public void AddStLookBorrow(Tb_borrowdoc borrowdoc,Tb_user user, String[] eleids,String userid,String dataSourceType){
		//新建临时账号-查档申办单,没有选择用户提交(在表单中读取身份证功能提交)
		if(userid!=null&&userid.equals("add")){
			Tb_user userExist = userRepository.findByLoginname(borrowdoc.getCertificatenumber());
			if(userExist == null) {
				//用户不存在---添加用户
				user.setUserid(null);
				user.setLoginname(borrowdoc.getCertificatenumber());
				user.setRealname(borrowdoc.getBorrowman());
				user.setAddress(borrowdoc.getComaddress());
				user.setInfodate("一周");//默认有限期
				userController.addOutUser(user, "");
				userid = user.getUserid();
			}
			else{
				userid = userExist.getUserid();
			}
		}

		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		Tb_node node = nodeRepository.getNode("查档审批");
		String spman = borrowdoc.getBorrowcode();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(spman);
		task.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
		task.setText(borrowdoc.getBorrowman()+" "+new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())+" 查档申请");
		task.setApproveman(userRepository.findByUserid(spman).getRealname());//保存
		task.setApprovetext(node.getText());
		task.setType("实体查档");
		task.setTime(new Date());
		task = taskRepository.save(task);//生成任务

		String borrowcode = UUID.randomUUID().toString().replace("-", "");//查档单号

		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);//完成
		flows.setDate(dateInt);
		flows.setTaskid(task.getId());
		flows.setMsgid(borrowcode);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);//处理中
		flows1.setTaskid(task.getId());
		flows1.setMsgid(borrowcode);
		flows1.setSpman(spman);
		flows1.setDate(dateInt);
		flows1.setNodeid(node.getId());
		flowsRepository.save(flows1);//生成环节流程

		borrowdoc.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		borrowdoc.setBorrowcode(borrowcode);
		borrowdoc.setType("实体查档");
		borrowdoc.setState("已送审");
		borrowdoc.setApprovetext(node.getText());
		borrowdoc.setApproveman(userRepository.findByUserid(spman).getRealname());
		borrowdoc.setSubmitstate("提交");
		borrowdoc.setDatasourcetype(dataSourceType);
		if(userid!=null && !"".equals(userid)){
			//代替临时账户提交无条目的查档单据时，查档人是临时账户id
			borrowdoc.setBorrowmanid(userid);
		}else {
			borrowdoc.setBorrowmanid(userDetails.getUserid());//
		}
		borrowdoc.setOutwarestate("未借出");
		borrowDocRepository.save(borrowdoc);//生成单据
		if (eleids != null) {
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(borrowcode)
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(borrowcode);
				electronic.setFilepath(filepath);
			}
		}

		stBoxRepository.deleteByEntryidInAndUseridAndBorrowtype(borrowdoc.getId().split(","),userDetails.getUserid(),"实体查档");//删除中间表数据
	}

	/**
	 * 电子查档没有条目单据提交
	 * @param borrowdoc 单据实例
	 * @return
	 */
	public Tb_borrowdoc AddElectronBorrow(Tb_borrowdoc borrowdoc,Tb_user user,String[] eleids,String userid,String dataSourceType){

		//新建临时账号-查档申办单,没有选择用户提交(在表单中读取身份证功能提交)
		if(userid!=null&&userid.equals("add")){
			Tb_user userExist = userRepository.findByLoginname(borrowdoc.getCertificatenumber());
			if(userExist == null) {
				//用户不存在---添加用户
				user.setUserid(null);
				user.setUsertype("0");
				user.setLoginname(borrowdoc.getCertificatenumber());
				user.setRealname(borrowdoc.getBorrowman());
				user.setAddress(borrowdoc.getComaddress());
				user.setInfodate("一周");//默认有限期
				userController.addOutUser(user, "");
				userid = user.getUserid();
			}
			else{
				userid = userExist.getUserid();
			}
		}

		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		Tb_node node = nodeRepository.getNode("查档审批");
		String spman = borrowdoc.getBorrowcode();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(spman);
		task.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
		task.setText(borrowdoc.getBorrowman()+" "+new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())+" 查档申请");
		task.setApproveman(userRepository.findByUserid(spman).getRealname());//保存
		task.setApprovetext(node.getText());
		task.setType("查档");
		task.setTime(new Date());
		task = taskRepository.save(task);//生成任务

		String borrowcode = UUID.randomUUID().toString().replace("-", "");//查档单号

		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);//完成
		flows.setDate(dateInt);
		flows.setTaskid(task.getId());
		flows.setMsgid(borrowcode);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);//处理中
		flows1.setTaskid(task.getId());
		flows1.setMsgid(borrowcode);
		flows1.setSpman(spman);
		flows1.setDate(dateInt);
		flows1.setNodeid(node.getId());
		flowsRepository.save(flows1);//生成环节流程

//		borrowdoc.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		borrowdoc.setBorrowcode(borrowcode);
		borrowdoc.setType("查档");
		borrowdoc.setState("已送审");
		borrowdoc.setApprovetext(node.getText());
		borrowdoc.setApproveman(userRepository.findByUserid(spman).getRealname());
		borrowdoc.setSubmitstate("提交");
		borrowdoc.setDatasourcetype(dataSourceType);
		if(userid!=null && !"".equals(userid)){
			//代替临时账户提交无条目的查档单据时，查档人是临时账户id
			borrowdoc.setBorrowmanid(userid);
		}else {
			borrowdoc.setBorrowmanid(userDetails.getUserid());
		}
		Tb_borrowdoc borrowdoc1 = borrowDocRepository.save(borrowdoc);//生成单据
		if (eleids != null) {
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(borrowcode)
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(borrowcode);
				electronic.setFilepath(filepath);
			}
		}
		return borrowdoc1;
	}

	public void deleEvicence(String[] eleids) {
		electronicRepository.deleteByEleidIn(eleids);
	}

	public String setPrintBox(String[] dataids,String borrowType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String msg = "";
			for(String dataid:dataids){
				Tb_st_box st_box = new Tb_st_box();
				st_box.setEntryid(dataid);
				st_box.setUserid(userDetails.getUserid());
				st_box.setBorrowtype(borrowType);//设置查档类型
				List<Tb_st_box> list = stBoxRepository.findByUseridAndEntryidAndBorrowtype(userDetails.getUserid(),st_box.getEntryid(),borrowType);
				if(list!=null&&list.size()<1){
					stBoxRepository.save(st_box);
				}
				List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByEntryidAndBorrowcode(dataid,null);
				if(electronic_prints.size()<1){
					List<Tb_electronic_solid> electronics = electronicSolidRepository.findByEntryid(dataid);
					List<Tb_electronic_print> electronic_printList = new ArrayList<>();
					for(int j=0;j<electronics.size();j++){
						Tb_electronic_print electronicPrint = new Tb_electronic_print();
						electronicPrint.setEleid(electronics.get(j).getEleid());
						electronicPrint.setEntryid(dataid);
						electronicPrint.setFilename(electronics.get(j).getFilename());
						electronicPrint.setCopies(0); //默认设0份
						electronic_printList.add(electronicPrint);
					}
					electronicPrintRepository.save(electronic_printList);
				}
			}
			msg = "成功添加 " + dataids.length + " 条数据";
		return msg;
	}

	/**
	 * 电子打印表单提交
	 *
	 * @param borrowdoc
	 *            表单实例
	 * @return
	 */
	public Tb_borrowdoc electronPrintSubmit(Tb_borrowdoc borrowdoc,String[] eleids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		String spman = borrowdoc.getBorrowcode();
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(spman);
		task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
		task.setText(borrowdoc.getBorrowman() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
				+ " 打印申请");
		task.setType("电子打印");
		task.setTime(new Date());
		task = taskRepository.save(task);// 添加任务

		String borrowcode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
		borrowdoc.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		borrowdoc.setBorrowcode(borrowcode);
		borrowdoc.setType("电子打印");
		borrowdoc.setState("已送审");
		borrowdoc.setBorrowmanid(userDetails.getUserid());

		List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
		for (int i = 0; i < borrowdoc.getId().split(",").length; i++) {
			Tb_borrowmsg borrowmsg = new Tb_borrowmsg();
			borrowmsg.setBorrowcode(borrowcode);
			borrowmsg.setEntryid(borrowdoc.getId().split(",")[i]);
			borrowmsg.setState("");
			borrowmsg.setBorrowdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
			borrowmsg.setJyts(borrowdoc.getBorrowts());
			borrowmsg.setLyqx("同意");
			borrowmsgs.add(borrowmsg);
		}
		String[] entryids = borrowdoc.getId().split(",");
		List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByEntryidInAndBorrowcode(entryids,null);
		for(Tb_electronic_print electronicPrint : electronic_prints){
			electronicPrint.setBorrowcode(borrowcode);
			if(!"".equals(electronicPrint.getPrintstate())&&electronicPrint.getPrintstate()!=null){
				electronicPrint.setState("同意");
			}
		}
		borrowMsgRepository.save(borrowmsgs);// 添加查档具体信息
		Tb_node node = nodeRepository.getNode("电子打印审批");
		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);// 完成
		flows.setTaskid(task.getId());
		flows.setMsgid(borrowcode);
		flows.setDate(dateInt);
		flows.setNodeid(node.getId());
		flowsRepository.save(flows);// 添加启动流程实例

		Tb_flows flows1 = new Tb_flows();
		flows1.setText(node.getText());
		flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
		flows1.setDate(dateInt);
		flows1.setTaskid(task.getId());
		flows1.setMsgid(borrowcode);
		flows1.setSpman(spman);
		flows1.setNodeid(node.getId());

		if (eleids != null) { //更新附件
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(borrowcode)
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(borrowcode);
				electronic.setFilepath(filepath);
			}
		}

		flowsRepository.save(flows1);// 添加下一流程实例
		stBoxRepository.deleteByEntryidInAndUseridAndBorrowtype(borrowdoc.getId().split(","),userDetails.getUserid(),"电子打印");//删除中间表数据
		return borrowDocRepository.save(borrowdoc);// 添加查档单据
	}

	public List<Tb_electronic_print> getElectronPrint(String[] dataids) {
		return electronicPrintRepository.findByEntryidInAndBorrowcode(dataids,null);
	}

	/**
	 * 保存查无此档表单数据
	 *
	 * @param tb_filenone
	 *            查无此档表单
	 * @return
	 */
	public Tb_filenone savaFileNone(Tb_filenone tb_filenone){
		return fileNoneRepository.save(tb_filenone);
	}

	public String setBorrowType(String[] entryids,String borrowType,String settype,String isFlag) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_st_box> list = stBoxRepository.findByUseridAndEntryidInAndBorrowtype(userDetails.getUserid(), entryids, borrowType);
		List<Tb_entry_index> hasNotEleOrSt = new ArrayList<>();
		for (Tb_st_box st_box : list) {
			Tb_entry_index entryIndex=new Tb_entry_index();
			if("1".equals(isFlag)){
				Tb_entry_index_sx  entryIndexSx = secondaryEntryIndexRepository.findAllByEntryid(st_box.getEntryid());
				BeanUtils.copyProperties(entryIndexSx,entryIndex);
			}else {
				entryIndex = entryIndexRepository.findByEntryid(st_box.getEntryid());
			}
			if("电子查档".equals(settype)){
				if(!"1".equals(isFlag)) {
					if (entryIndex.getEleid() == null || "".equals(entryIndex.getEleid())) {  //没有电子文件
						hasNotEleOrSt.add(entryIndex);
						continue;
					}
				}
			}else if("电子、实体查档".equals(settype)){
				if((entryIndex.getEleid()==null||"".equals(entryIndex.getEleid()))||Integer.valueOf(entryIndex.getKccount().trim())<1){
					hasNotEleOrSt.add(entryIndex);
					continue;
				}
			}else {
				if (Integer.valueOf(entryIndex.getKccount().trim()) < 1) {
					//库存份数为0
					hasNotEleOrSt.add(entryIndex);
					continue;
				}
			}
			st_box.setSettype(settype);
		}
		stBoxRepository.save(list);
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
		}else if("实体查档".equals(settype)){
			msg = settype + "设置失败 "+ hasNotEleOrSt.size()+" 条，" + msg + "等档案没有库存份数";
		}else{
			msg = settype + "设置失败 "+ hasNotEleOrSt.size()+" 条，" + msg + "等档案没有电子文件或者没有库存份数";
		}
		int count = list.size() - hasNotEleOrSt.size();
		if(count > 0){
			if(count==list.size()){
				msg = "成功设置 "+count+" 条"+settype;
			}else{
				msg = "成功 "+count+" 条，" + msg;
			}
		}
		return msg;
	}

	public void saveBorrowType(List<Tb_st_box> stBoxList){
		stBoxRepository.save(stBoxList);
	}

	public List<Tb_Msg> getOutWareMsg() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_task> tasks = taskRepository.getOutWareTask(userDetails.getUserid(),
				Tb_task.STATE_WAIT_HANDLE,"实体出库");// 待处理
		List<Tb_Msg> msgs = new ArrayList<>();
		for (Tb_task task : tasks) {
			Tb_Msg msg = new Tb_Msg();
			msg.setMsgid(task.getId().trim());
			msg.setMsgtype("2");
			msg.setMsgtypetext(task.getType());
			msg.setMsgtext(task.getText());
			msg.setBorrowmsgid(task.getBorrowmsgid());
			msg.setUrging(task.getUrgingstate());
			msgs.add(msg);
		}
		return msgs;
	}

	//获取所有单位机构
	public List<Tb_right_organ> getUnitOrganAll(String type,String taskid,String worktext,String nodeid,String approveType) {
		String organid = "";
		if("approve".equals(type)){ //判断是审批或是提交申请
			if("dataOpen".equals(approveType)){
				Tb_opendoc opendoc = opendocRepository.getOpendoc(taskid);
				organid = userRepository.findOrganidByUserid(opendoc.getSubmitterid());
			}else if("bill".equals(approveType)){
				Tb_bill_approval bill_approval = billApprovalRepository.findByTaskidContains(taskid.trim());
				organid = userRepository.findOrganidByUserid(bill_approval.getSubmitterid());
			}else if("audit".equals(approveType)){  //采集移交审核
				Tb_transdoc transdoc = transdocRepository.getByTaskid(taskid.trim());
				organid = userRepository.findOrganidByUserid(transdoc.getApprovemanid());
			}else if("carOrder".equals(approveType)){  //公车预约
				Tb_car_order carOrder = carOrderRepository.findById(taskid.trim());
				organid = userRepository.findOrganidByUserid(carOrder.getSubmiterid());
			}else if("placeOrder".equals(approveType)){  //场地预约
				Tb_place_order placeOrder = placeOrderRepository.findById(taskid.trim());
				organid = userRepository.findOrganidByUserid(placeOrder.getSubmiterid());
			}else {
				Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
				organid = userRepository.findOrganidByUserid(borrowdoc.getBorrowmanid());
			}
		}else{
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
			organid = userRepository.findOrganidByUserid(userDetails.getUserid());
		}
		Tb_right_organ organ = rightOrganRepository.findOne(organid);
		//找到当前账号的所在单位
		while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
			organ = rightOrganRepository.findOne(organ.getParentid());
		}
		List<Tb_right_organ> returnOrgans = new ArrayList<>();
		Tb_node nodenext = new Tb_node();
		if(worktext!=null&&!"".equals(worktext)){ //判断是审批或是提交申请
			Tb_work work = workRepository.findByWorktext(worktext);
			String nodeId = "";
			if (work != null && work.getText() != null) {
				Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 1);
				nodeId = node.getNextid().split(",")[0];
			}
			nodenext = nodeRepository.findByNodeid(nodeId);
		}else{
			nodenext = nodeRepository.findByNodeid(nodeid);
		}
		//判断下一环节是不是结束环节,若是，则返回空
		if(!"结束".equals(nodenext.getText())){
			if(nodenext.getApprovescope()==null||"".equals(nodenext.getApprovescope())||"仅本单位".equals(nodenext.getApprovescope())){ //判断审批节点的审批范围
				returnOrgans.add(organ);
			}else{
				//根据审批人过滤所在的单位
				List<Tb_right_organ> organs = userRepository.getOrganidByNodeid(nodenext.getId());
				List<Tb_right_organ> organUnit = new ArrayList<>();
				boolean organflag = false;
				for(Tb_right_organ organone : organs){
					//找到当前审批人的所在单位
					while (organone.getOrgantype() != null && organone.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
						organone = rightOrganRepository.findOne(organone.getParentid());
					}
					//去除重复单位
					boolean flag = true;
					for(Tb_right_organ organdf : organUnit){
						if(organdf.getOrganid().trim().equals(organone.getOrganid().trim())){
							flag = false;
							break;
						}
					}
					if(organone.getOrganid().equals(organ.getOrganid())){  //申请人单位是否有审批人
						organflag = true;
					}else{
						if(flag){
							organUnit.add(organone);
						}
					}
				}
				if(organflag){
					//将用户所在的单位排在最前面
					returnOrgans.add(organ);
					returnOrgans.addAll(organUnit);
				}else{
					returnOrgans.addAll(organUnit);
				}
			}
		}
		return returnOrgans;
	}

	public List<Tb_node> getWorkTextNode(String workText) {
 		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		Tb_node node = nodeRepository.getStartNode(workText);
		String[] nextids = node.getNextid().split(",");
		List<Tb_node> nodeList = new ArrayList<>();
		for (String nodeid : nextids) {
			/*List<String> userids = userNodeRepository.findUserids(nodeid);
			//判断下一环节是否存在权限用户
			if(userids.size() > 0){
				if(userids.contains(userDetails.getUserid())){
					nodeList.add(nodeRepository.findByNodeid(nodeid));
				}
			}else{
				nodeList.add(nodeRepository.findByNodeid(nodeid));
			}*/
			nodeList.add(nodeRepository.findByNodeid(nodeid));
		}
		return nodeList;
	}
}