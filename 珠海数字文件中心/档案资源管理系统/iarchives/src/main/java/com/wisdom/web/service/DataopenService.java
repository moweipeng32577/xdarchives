package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tanly on 2017/12/2 0002.
 */
@Service
@Transactional
public class DataopenService {

	@Autowired
	OpenboxRepository openboxRepository;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	NodeRepository nodeRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	OpendocRepository opendocRepository;

	@Autowired
	FlowsRepository flowsRepository;

	@Autowired
	OpenmsgRepository openmsgRepository;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	JyAdminsService jyAdminsService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	UserDataNodeRepository userDataNodeRepository;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	ClassifySearchService classifySearchService;


	@Autowired
	private ElectronicRepository electronicRepository;

	public String addtobox(String[] dataids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String msg = "";
		List<Tb_entry_index> entry_indices = entryIndexRepository.findByEntryidIn(dataids);
		int successadd = entry_indices.size();
		//查找已经存在的送审条目
		Set<String> entryidSet=openboxRepository.findByUseridAndAndEntryidIn(userDetails.getUserid(), dataids);
		for (Tb_entry_index entry_index : entry_indices) {
			Tb_openbox openbox = new Tb_openbox();
			openbox.setEntryid(entry_index.getEntryid());
			openbox.setUserid(userDetails.getUserid());
			if(entryidSet.size()>0 && entryidSet.contains(entry_index.getEntryid())){//已经存在该条目
				successadd--;
			}else{
				openboxRepository.save(openbox);
			}
		}
		int repeat = entry_indices.size() - successadd;
		msg = "成功添加 " + successadd + " 条数据";
		msg = repeat == 0 ? msg : msg + "\t重复 " + repeat + " 条数据";
		return msg;
	}

	public Page<Tb_entry_index> getBoxEntryIndex(int page, int limit) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_openbox> openboxes = openboxRepository.findByUserid(userDetails.getUserid());
		String[] entryids = GainField.getFieldValues(openboxes, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(openboxes, "entryid");
        List<Tb_entry_index> entry_indexs = new ArrayList<>();
        List<String[]> subAry = new InformService().subArray(entryids, 1000);// 处理ORACLE1000参数问题
        for (String[] ary : subAry) {
            entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ary));
        }
        return getFullName(entry_indexs, new PageRequest(page - 1, limit, new Sort("archivecode")));
	}

	//获取数据节点全名
	private Page<Tb_entry_index> getFullName(List<Tb_entry_index> result, PageRequest pageRequest) {
        long totalElements = result.size();
		List<Tb_entry_index> subResult;
		if (totalElements > pageRequest.getPageSize()){
			if (totalElements -((pageRequest.getPageNumber()) * pageRequest.getPageSize()) >= pageRequest.getPageSize()){
				subResult = result.subList(
						pageRequest.getPageNumber() * pageRequest.getPageSize(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize()
				);
			} else {
				subResult = result.subList(
						pageRequest.getPageNumber() * pageRequest.getPageSize(), (int) ((pageRequest.getPageNumber() * pageRequest.getPageSize()) + totalElements - ((pageRequest.getPageNumber()) * pageRequest.getPageSize()))
				);
			}
		}else {
			subResult = result;
		}
		List<Tb_entry_index> returnResult = new ArrayList<>();
		Map<String,String> nodeNameMap=new HashMap<>();//存放nodeid-nodeid数据节点的全名
		String nodefullname ="";
		for (Tb_entry_index entryIndex : subResult) {
			Tb_entry_index entry_index = new Tb_entry_index();
			BeanUtils.copyProperties(entryIndex, entry_index);
			if(entry_index.getTdn()!=null){
				String nodeid = entry_index.getTdn().getNodeid();
				//判断map中是否已存在对应nodeid的节点全名
				if(nodeNameMap.containsKey(nodeid)){//存在的话直接设置
					nodefullname =nodeNameMap.get(nodeid);
				}else{//不存在时再获取
					nodefullname = nodesettingService.getNodefullnameLoop(nodeid, "_", "");
					nodeNameMap.put(nodeid,nodefullname);//将新获取的节点全名加入map
				}
				entry_index.setNodefullname(nodefullname);
				returnResult.add(entry_index);

			}
		}
		return new PageImpl(returnResult, pageRequest, totalElements);
	}

	public List<Tb_node> getNode(String workname) {
		String[] nextids = nodeRepository.getStartNode(workname).getNextid().split(",");
		List<Tb_node> nodeList = new ArrayList<>();
		for (String nodeid : nextids) {
			nodeList.add(nodeRepository.findByNodeid(nodeid));
		}
		return nodeList;
	}

	public void sendformSubmit(Tb_opendoc opendoc, String taskname, String nodeid, String nodeuserid,
			String datanodeid, String userid) {
		String[] entryids = opendoc.getId().split(",");
		long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		Tb_task task = new Tb_task();
		task.setLoginname(nodeuserid);
		task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
		task.setText(taskname);
		task.setType("数据开放");
		task.setTime(new Date());
		task = taskRepository.save(task);

		// 启动完成
		Tb_node node = nodeRepository.findByNodeid(nodeid);
		Tb_flows flows = new Tb_flows();
		flows.setText("启动");
		flows.setState(Tb_flows.STATE_FINISHED);// 完成
		flows.setDate(dateInt);
		flows.setTaskid(task.getId());
		flows.setMsgid(opendoc.getBatchnum());
		flows.setNodeid(nodeid);
		flowsRepository.save(flows);

		// 进入审批环节
		Tb_flows sendflows = new Tb_flows();
		sendflows.setText(node.getText());
		sendflows.setState(Tb_flows.STATE_HANDLE);// 处理中
		sendflows.setTaskid(task.getId());
		sendflows.setMsgid(opendoc.getBatchnum());
		sendflows.setSpman(nodeuserid);
		sendflows.setDate(dateInt);
		sendflows.setNodeid(nodeid);
		flowsRepository.save(sendflows);

		// 删去送审单中已送审的数据
        List<String[]> subAry = new InformService().subArray(entryids, 1000);// 处理ORACLE1000参数问题
        for (String[] ary : subAry) {
            openboxRepository.deleteByEntryidInAndUserid(ary, userid);
        }

		opendoc.setState(Tb_opendoc.STATE_SEND_AUDIT);// 已送审
		opendoc.setNodeid(datanodeid);
		opendoc.setOpened("1");// 暂时未用到“是否开放”字段，默认设置为是
        opendoc.setId(null);
		opendoc.setSubmitterid(userid); //设置申请人id
		Tb_opendoc savedOpendoc = opendocRepository.save(opendoc);

		String opentype = savedOpendoc.getOpentype();

		// 保存开放单据条目中间表
		for (int i = 0; i < entryids.length; i++) {
			Tb_openmsg openmsg = new Tb_openmsg();
			openmsg.setBatchnum(opendoc.getBatchnum());
			openmsg.setEntryid(entryids[i]);
			openmsg.setResult(opentype);
			openmsgRepository.save(openmsg);
		}
        for (String[] ary : subAry) {
            entryIndexRepository.setOpenLock("开放送审中", ary);
        }
	}

	public void submitForm(Tb_opendoc opendoc, String datanodeid) {
		String[] entryids = opendoc.getId().split(",");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		opendoc.setState(Tb_opendoc.STATE_FINISH_AUDIT);// 已审核
		opendoc.setOpendate(df.format(System.currentTimeMillis()));
		opendoc.setNodeid(datanodeid);
		opendoc.setApprove("直接开放");// 添加数据开放的批示,来区别直接开放跟审批开放的单据
		opendoc.setOpened("1");// 暂时未用到“是否开放”字段，默认设置为是
		Tb_opendoc savedOpendoc = opendocRepository.save(opendoc);
		
		// 根据数据的开放，更新在数据管理表中的开放时间
		entryIndexRepository.updateOpenDateByEntryidIn(df.format(System.currentTimeMillis()), entryids);

		String opentype = savedOpendoc.getOpentype();
		// 保存开放单据条目中间表
		for (int i = 0; i < entryids.length; i++) {
			Tb_openmsg openmsg = new Tb_openmsg();
			openmsg.setBatchnum(opendoc.getBatchnum());
			openmsg.setEntryid(entryids[i]);
			openmsg.setResult(opentype);
			openmsgRepository.save(openmsg);
		}
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_openbox> openboxes = openboxRepository.findByUserid(userDetails.getUserid());
		String[] entryidList = GainField.getFieldValues(openboxes, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(openboxes, "entryid");// 查找到当前用户待送审条目信息
		List<String> idStrings = new ArrayList<>();
		for (int i = 0; i < entryids.length; i++) {
			for (int j = 0; j < entryidList.length; j++) {
				if (entryids[i].equals(entryidList[j])) {
					idStrings.add(entryids[i]);
				}
			}
		}
		String[] opendocId = idStrings.toArray(new String[idStrings.size()]);
		openboxRepository.deleteByEntryidInAndUserid(opendocId, userDetails.getUserid());
		entryIndexRepository.setOpenLock(opendoc.getOpentype(), entryids);// 默认设置条目开放
	}

	public Page<Tb_index_detail> getEntriesByOpenNew(String[] nodeids, String opentype, int page, int limit,
												  String condition, String operator, String content, Sort sort) {
		String nodeidStr=" nodeid in ('"+String.join("','",nodeids)+"') ";//数据节点
		String openStr=getSearchOpenStr(opentype);//开放条件
		String searchCondition="";
		if (content != null) {//输入框检索
			searchCondition=classifySearchService.getSqlByConditionsto(condition, content, "sid",operator);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String sortStr="";//排序
		int sortInt=0;//判断是否副表表排序
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			if("eleid".equals(order.getProperty())){
				sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
			}else {
				sortStr = " order by " + order.getProperty() + " " + order.getDirection();
			}
			sortInt=entryIndexService .checkFilecode(order.getProperty());
		}else{
			sortStr = " order by archivecode desc, descriptiondate desc ";
		}

		String table="v_index_detail";
		String countTable="v_index_detail";
		if(condition==null||entryIndexService.checkFilecode(condition)==0){//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
			countTable="tb_entry_index";
			if(sortInt==0){//非副表表字段排序
				table="tb_entry_index";
			}
		}
		String sql = "select sid.entryid from "+table+" sid where "+nodeidStr+openStr+searchCondition;
		String countSql="select count(nodeid) from "+countTable+" sid where "+nodeidStr+openStr+searchCondition;
        return entryIndexService.getPageListTwo(sql,sortStr,countSql,page,limit,pageRequest);
	}

	public Page<Tb_index_detail> getEntriesByOpen(String[] nodeids, String opentype, int page, int limit,
			String condition, String operator, String content, Sort sort) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if("1".equals(userDetails.getUsertype())){
			Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
			Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
			String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
			Tb_right_organ organuser = rightOrganRepository.findOne(organid);
			if ("unit".equals(organuser.getOrgantype())) {
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (organuser.getOrganid().equals(organnode.getOrganid())) {
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
					return index_details;
				}else{
					List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(),nodeids[0]);
					if(userDataNodeList.size()>0){
						//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
						//return entryIndexPage;
						Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
						return index_details;
					}
				}
			} else if ("department".equals(organuser.getOrgantype())) {
				boolean flag = false;
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					if (organuser.getOrganid().equals(organnode.getOrganid())) {
						flag = true;
						break;
					}
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (flag) {
					//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
					//return entryIndexPage;
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}else{
					List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(),nodeids[0]);
					if(userDataNodeList.size()>0){
						//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
						//return entryIndexPage;
						Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
						return index_details;
					}
				}
			}

		}else {
			Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
			Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
			String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
			Tb_right_organ organuser = rightOrganRepository.findOne(organid);
			if ("unit".equals(organuser.getOrgantype())) {
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (organuser.getOrganid().equals(organnode.getOrganid())) {
					//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
					//return entryIndexPage;
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}
			} else if ("department".equals(organuser.getOrgantype())) {
				boolean flag = false;
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					if (organuser.getOrganid().equals(organnode.getOrganid())) {
						flag = true;
						break;
					}
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (flag) {
					//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);
					//return entryIndexPage;
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}
			}
		}
		return null;
	}

	public Page<Tb_index_detail> getEntriesByPower(String[] nodeids, int page, int limit,
												 String condition, String operator, String content, Sort sort) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if("1".equals(userDetails.getUsertype())){
			Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
			Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
			String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
			Tb_right_organ organuser = rightOrganRepository.findOne(organid);
			if ("unit".equals(organuser.getOrgantype())) {
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (organuser.getOrganid().equals(organnode.getOrganid())) {
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
					//Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);

				}else{
					List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(),nodeids[0]);
					if(userDataNodeList.size()>0){
						Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
						return index_details;
					}
				}
			} else if ("department".equals(organuser.getOrgantype())) {
				boolean flag = false;
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					if (organuser.getOrganid().equals(organnode.getOrganid())) {
						flag = true;
						break;
					}
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (flag) {
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}else{
					List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(),nodeids[0]);
					if(userDataNodeList.size()>0){
						Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
						return index_details;
					}
				}
			}

		}else {
			Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
			Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
			String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
			Tb_right_organ organuser = rightOrganRepository.findOne(organid);
			if ("unit".equals(organuser.getOrgantype())) {
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (organuser.getOrganid().equals(organnode.getOrganid())) {
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}
			} else if ("department".equals(organuser.getOrgantype())) {
				boolean flag = false;
				while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
					if (organuser.getOrganid().equals(organnode.getOrganid())) {
						flag = true;
						break;
					}
					organnode = rightOrganRepository.findOne(organnode.getParentid());
				}
				if (flag) {
					Page<Tb_index_detail> index_details =entryIndexService.getEntrybaseto(nodeids,condition,operator,content,page,limit,sort);
					return index_details;
				}
			}
		}
		return null;
	}

	public Page<Tb_entry_index> getEntriesByOpenCode(String[] nodeids, String opentype, int page, int limit,
												 String condition, String operator, String content, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit,
				sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
		Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndex(nodeids);
		Specification<Tb_entry_index> searchOpenNullCondition = getSearchOpenNullCondition();
		Specification<Tb_entry_index> searchOpenCondition = getSearchOpenCondition(opentype);
		Specifications specifications = Specifications.where(searchNodeidCondition);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		if (("").equals(opentype) || null == opentype) {
			specifications = specifications.and(searchOpenNullCondition);
		} else {
			specifications = specifications.and(searchOpenCondition);
		}
		return entryIndexRepository.findAll(specifications, pageRequest);
	}

	public Page<Tb_entry_index> getEntriesByPowerCode(String[] nodeids, int page, int limit,
													 String condition, String operator, String content, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit,
				sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
		Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndex(nodeids);
		Specification<Tb_entry_index> searchOpenNullCondition = getSearchOpenNullCondition();
		Specifications specifications = Specifications.where(searchNodeidCondition);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return entryIndexRepository.findAll(specifications, pageRequest);
	}

	public void dontopen(String[] dataids) {
		List<Tb_entry_index> entry_indexList = entryIndexRepository.findByEntryidIn(dataids);
		for (Tb_entry_index entry_index : entry_indexList) {
			entry_index.setFlagopen("不开放");
		}
	}

	public void cancelban(String[] dataids) {
		List<Tb_entry_index> entry_indexList = entryIndexRepository.findByEntryidIn(dataids);
		for (Tb_entry_index entry_index : entry_indexList) {
			entry_index.setFlagopen("");
			entry_index.setOpendate(null);
		}
	}

	public void deleteOpenbox(String[] ids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		openboxRepository.deleteByEntryidInAndUserid(ids, userDetails.getUserid());
	}

	public Page<Tb_entry_index> getEntryIndexById(String dataids, int page, int limit) {
		List<Tb_entry_index> entry_indexs = new ArrayList<>();
		List<String[]> subAry = new InformService().subArray(dataids.split(","), 1000);// 处理ORACLE1000参数问题
		for (String[] ary : subAry) {
			entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ary));
		}
		return getFullName(entry_indexs, new PageRequest(page - 1, limit, new Sort("archivecode")));
	}

	public Page<Tb_opendoc> findOpendocBySearch(String type, int page, int limit, String condition,
			String operator, String content, String nodeid, Sort sort) {
//		Specification<Tb_opendoc> searchNodeidCondition = getSearchNodeidCondition(new String[] { nodeid });
		Specifications specifications = null;
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		Specification<Tb_opendoc> searchSubmitterCondition = getSearchSubmitterCondition(
				LogAop.getCurrentOperateuserRealname());
			specifications.where(searchSubmitterCondition);
		PageRequest pageRequest = new PageRequest(page - 1, limit,
				sort == null ? new Sort(Sort.Direction.DESC, "submitdate") : sort);
		Page<Tb_opendoc> opendocList = opendocRepository.findAll(specifications, pageRequest);
		return opendocList;
	}

	public static Specification<Tb_entry_index> getSearchOpenNullCondition() {
		Specification<Tb_entry_index> searchOpenNullCondition = new Specification<Tb_entry_index>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate[] predicates = new Predicate[2];
				predicates[0] = criteriaBuilder.equal(root.get("flagopen"), "");
				predicates[1] = criteriaBuilder.isNull(root.get("flagopen"));
				return criteriaBuilder.or(predicates);
			}
		};
		return searchOpenNullCondition;
	}

	public  String getSearchOpenStr(String opentype){
		String  openStr="";
		if (("").equals(opentype) || null == opentype) {
			openStr=" and (flagopen is null or flagopen='') ";
		}else{
			String[] openarr = opentype.split(",");
			openStr=" and flagopen in('"+String.join("','",openarr)+"') ";
		}

		return openStr;
	}

	public static Specification<Tb_entry_index> getSearchOpenCondition(String opentype) {
		Specification<Tb_entry_index> searchOpenCondition = new Specification<Tb_entry_index>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("flagopen"));
				String[] openarr = opentype.split(",");
				for (String ele : openarr) {
					in.value(ele);
				}
				return criteriaBuilder.or(in);
			}
		};
		return searchOpenCondition;
	}

	public static Specification<Tb_opendoc> getSearchSubmitterCondition(String submitter) {
		Specification<Tb_opendoc> searchSubmitterCondition = null;
		searchSubmitterCondition = new Specification<Tb_opendoc>() {
			@Override
			public Predicate toPredicate(Root<Tb_opendoc> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("submitter"), submitter);
				return criteriaBuilder.or(p);
			}
		};
		return searchSubmitterCondition;
	}

	public static Specification<Tb_opendoc> getSearchNodeidCondition(String[] nodeids) {
		Specification<Tb_opendoc> searchNodeID = null;
		if (nodeids != null) {
			if (nodeids.length > 0) {
				searchNodeID = new Specification<Tb_opendoc>() {
					@Override
					public Predicate toPredicate(Root<Tb_opendoc> root, CriteriaQuery<?> criteriaQuery,
							CriteriaBuilder criteriaBuilder) {
						Predicate[] predicates = new Predicate[nodeids.length];
						for (int i = 0; i < nodeids.length; i++) {
							predicates[i] = criteriaBuilder.equal(root.get("nodeid"), nodeids[i]);
						}
						return criteriaBuilder.or(predicates);
					}
				};
			}
		}
		return searchNodeID;
	}

	public String[] getEntryidsByBatchnum(String batchnum) {
		List<Tb_openmsg> openmsgList = openmsgRepository.findByBatchnum(batchnum);
		return GainField.getFieldValues(openmsgList, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(openmsgList, "entryid");
	}

	public Page getDealDetails(String opendocid){
		Tb_opendoc opendoc = opendocRepository.getDocumentInfo(opendocid);
		if(opendoc!=null){
			return jyAdminsService.getCommonDealDetails(opendoc.getBatchnum(),opendoc.getApprove(),opendoc.getSubmitter());
		}
		return new PageImpl(new ArrayList(),null,0);
	}

	public Tb_datareceive getDataopenDoc(String entryids){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_datareceive datareceive = new Tb_datareceive();
		datareceive.setTransuser(userDetails.getRealname());
		datareceive.setTransorgan(userDetails.getOrganid());
		String[] ids = entryids.split(",");
		//计算条目数量
		datareceive.setTranscount(String.valueOf(ids.length));
		datareceive.setTransferstcount("0");
		return datareceive;
	}
}
