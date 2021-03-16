package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_classification_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_user_data_node_sx;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.util.GainField;
import com.wisdom.util.GuavaCache;
import com.wisdom.util.GuavaUsedKeys;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by tanly on 2017/10/27 0027.
 */
@Service
@Transactional
public class NodesettingService {

	@Autowired
	ClassificationRepository classificationRepository;

	@Autowired
	SxClassificationRepository sxClassificationRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	SxDataNodeRepository sxDataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	UserDataNodeRepository userDataNodeRepository;

	@Autowired
	UserOrganRepository userOrganRepository;

	@Autowired
	RoleDataNodeRepository roleDataNodeRepository;

	@Autowired
	RoleOrganRepository roleOrganRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserDataNodeSxRepository userDataNodeSxRepository;
	@Autowired
	SxUserDataNodeRepository sxUserDataNodeRepository;

	@Autowired
	RoleDataNodeSxRepository roleDataNodeSxRepository;

	@Autowired
	SecondaryDataNodeRepository secondaryDataNodeRepository;

	@Autowired
	SxRoleRepository sxRoleRepository;

	@Autowired
	SxRoleDataNodeRepository sxRoleDataNodeRepository;

	@Autowired
	SxRightOrganRepository sxRightOrganRepository;

	private String nodeEditTime;//数据节点更新时间

	public List<String> getNodeidByRefid(String refid) {
		return dataNodeRepository.findNodeidByRefid(refid);
	}

	private List<Tb_data_node> allNodeList = null;

	private List<Tb_data_node_sx> allSecondaryNodeList = null;

	/*private List<Tb_data_node> findAllNodeList(){
		if(allNodeList == null){
			allNodeList = dataNodeRepository.findAll();
		}
		return allNodeList;
	}*/

	private List<Tb_data_node_sx> findSecondaryAllNodeList(){
		if(allSecondaryNodeList == null){
			allSecondaryNodeList = secondaryDataNodeRepository.findAll();
		}
		return allSecondaryNodeList;
	}

	/**
	 * 获取分类ID（pcid）的下级分类
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getClassificationByParentClassId(String pcid,String xtType) {
		List<NodesettingTree> nodeTreeList = new ArrayList<>();
		if("声像系统".equals(xtType)){
			nodeTreeList =getSxClassification(pcid);
		}else{
			List<Tb_classification> classificationList = classificationRepository.findByParentclassidOrderBySortsequence(pcid);
			if (pcid.equals("")) {
				classificationList.addAll(classificationRepository.findByParentclassidIsNull());
			}
			for (int i = 0; i < classificationList.size(); i++) {
				NodesettingTree tree = new NodesettingTree();
				tree.setFnid(classificationList.get(i).getClassid());
				List<Tb_classification> IsLeafOfClassificationList = classificationRepository.findByParentclassidOrderBySortsequence(classificationList.get(i).getClassid());
				if (!IsLeafOfClassificationList.isEmpty()) {// 有子节点
					tree.setCls("folder");
					tree.setLeaf(false);
				} else {
					tree.setCls("file");
					tree.setLeaf(true);
				}
				tree.setText(classificationList.get(i).getClassname());
				nodeTreeList.add(tree);
			}
		}


		return nodeTreeList;
	}

	public List<NodesettingTree> getSxClassification(String pcid){
		List<com.wisdom.secondaryDataSource.entity.Tb_classification_sx> classificationList = sxClassificationRepository.findSxByParentclassidOrderBySortsequence(pcid);
		if (pcid.equals("")) {
			classificationList.addAll(sxClassificationRepository.findSxByParentclassidIsNull());
		}
		List<NodesettingTree> nodeTreeList = new ArrayList<>();
		for (int i = 0; i < classificationList.size(); i++) {
			NodesettingTree tree = new NodesettingTree();
			tree.setFnid(classificationList.get(i).getClassid());
			List<Tb_classification_sx> IsLeafOfClassificationList  = sxClassificationRepository.findSxByParentclassidOrderBySortsequence(classificationList.get(i).getClassid());
			if (!IsLeafOfClassificationList.isEmpty()) {// 有子节点
				tree.setCls("folder");
				tree.setLeaf(false);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			tree.setText(classificationList.get(i).getClassname());
			nodeTreeList.add(tree);
		}
		return nodeTreeList;
	}

	/**
	 * 获取分类ID（pcid）的下级分类( 帶多選框 )
	 *
	 * @param pcid
	 * @return
	 */
	public List<ExtTree> getCheckedClassificationByParentClassId(String pcid) {
		List<NodesettingTree> userNodeTreeList = getNodeByParentId(pcid, true,null);
		List<Tb_classification> classificationList = classificationRepository
				.findByParentclassidOrderBySortsequence(pcid);
		List<Tb_classification> replaceClassificationList = new ArrayList<>();
		if ("".equals(pcid)) {
			for (int i = 0; i < classificationList.size(); i++){
				for (int y = 0; y < userNodeTreeList.size(); y++){
					if(classificationList.get(i).getClassname().equals(userNodeTreeList.get(y).getText())){
						replaceClassificationList.add(classificationList.get(i));
					}
				}
			}
			classificationList = replaceClassificationList;
			if (classificationList.size() == 0){//为避免分类栏重复加载
				classificationList.addAll(classificationRepository.findByParentclassidIsNullOrderBySortsequence());
			}
		}

		List<ExtTree> nodeTreeList = new ArrayList<>();
		for (int i = 0; i < classificationList.size(); i++) {
			ExtTree tree = new ExtTree();
			tree.setFnid(classificationList.get(i).getClassid());
			List<Tb_classification> IsLeafOfClassificationList = classificationRepository
					.findByParentclassidOrderBySortsequence(classificationList.get(i).getClassid());
			if (!IsLeafOfClassificationList.isEmpty()) {// 有子节点
				tree.setCls("folder");
				tree.setLeaf(false);
				tree.setExpanded(true);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
				tree.setExpanded(true);
			}
			tree.setText(classificationList.get(i).getClassname());
			nodeTreeList.add(tree);
		}

		return nodeTreeList;
	}

	/**
	 * 获取pcid下机构树 核心思路： 1、获得pcid下所有权限机构集合A
	 * 2、在集合A中寻找“显示在第一级的机构”（判断依据：该机构的所有父节点都不在集合A中）
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getOrganByParentId(String pcid,String type) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_right_organ> rightOrganList = new ArrayList<>();
		List<Tb_right_organ> organList = rightOrganRepository.getAllOrgan();
		if(type!=null&&"all".equals(type)){  //不做权限过滤
			rightOrganList = organList;
		}else{
			rightOrganList = rightOrganRepository.getMyAuthWithParent(userDetails.getUserid());// 获得userId的所有权限节点
		}

		Map<String, String> map = new HashMap<>();// organid-Parentid
		for (Tb_right_organ organ : organList) {
			map.put(organ.getOrganid(), organ.getParentid());
		}
		List<Tb_user_organ_parents> parents = new ArrayList<>();
		for (Tb_right_organ organ : rightOrganList) {
			Tb_user_organ_parents organParent = new Tb_user_organ_parents();
			organParent.setOrganid(organ.getOrganid());
			organParent.setOrganname(organ.getOrganname());

			String allParent = getOrganParents(organ.getOrganid(), map);
			organParent.setParents(allParent);
			parents.add(organParent);
		}

		List<Tb_user_organ_parents> userOrganParents = findTopOrganOfPcid(pcid, parents);// 获取pcid下的权限
		if (userOrganParents == null || userOrganParents.size() == 0) {
			return null;
		}
		NodesettingTree[] returnTree = getOrganChildren(parents, userOrganParents);
		return Arrays.asList(returnTree);
	}



	/**
	 * 获取pcid下机构树( 帶多選框 ) 核心思路：
	 * 1、pcid 节点id
	 * @param pcid
	 * @return
	 */
	public List<ExtTree> getCheckedOrganByParentId(String pcid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_right_organ> IsCheckLeafOfOrganList = rightOrganRepository
				.findByParentidOrderBySortsequence(userDetails.getReplaceOrganid());
		if(!IsCheckLeafOfOrganList.isEmpty()){ //判断是否有子节点
			List<Tb_right_organ> userOrganList = rightOrganRepository.getMyAuthWithParent(userDetails.getUserid());
			List<Tb_right_organ> organList = rightOrganRepository
					.findByParentidOrderBySortsequence(pcid);
			List<Tb_right_organ> replaceOrganList = new ArrayList<>();
			if (pcid.equals("")) { //查父节点
				for (int i = 0; i < userOrganList.size(); i++){
					if(userOrganList.get(i).getParentid().trim().equals("0")
							&& userOrganList.get(i).getOrganid().trim().equals(userDetails.getReplaceOrganid())){
						organList.add(userOrganList.get(i));
					}else if(userOrganList.get(i).getOrganid().trim().equals(userDetails.getReplaceOrganid())){
						organList.add(userOrganList.get(i));
					}
				}
			}else { //查子节点
				for (int i = 0; i < organList.size(); i++){
					for (int y = 0; y < userOrganList.size(); y++){
						if(organList.get(i).getOrganid().equals(userOrganList.get(y).getOrganid())){
							replaceOrganList.add(organList.get(i));
						}
					}
				}
				organList = replaceOrganList;
			}
			List<ExtTree> nodeTreeList = new ArrayList<>();
			for (int i = 0; i < organList.size(); i++) {
				ExtTree tree = new ExtTree();
				tree.setFnid(organList.get(i).getOrganid());
				List<Tb_right_organ> IsLeafOfOrganList = rightOrganRepository
						.findByParentidOrderBySortsequence(organList.get(i).getOrganid());
				if (!IsLeafOfOrganList.isEmpty()) {// 有子节点
					tree.setCls("folder");
					tree.setLeaf(false);
					tree.setExpanded(true);
				} else {
					tree.setCls("file");
					tree.setLeaf(true);
					tree.setExpanded(true);
				}
				tree.setText(organList.get(i).getOrganname());
				nodeTreeList.add(tree);
			}
			return nodeTreeList;
		}else {
			List<Tb_right_organ> userOrganList = new ArrayList<>(); //无父节点直接返回子节点
			userOrganList.add(rightOrganRepository.findByOrganid(userDetails.getReplaceOrganid()));
			List<ExtTree> nodeTreeList = new ArrayList<>();
			ExtTree tree = new ExtTree();
			tree.setFnid(userOrganList.get(0).getOrganid());
			List<Tb_right_organ> IsLeafOfOrganList = rightOrganRepository
					.findByParentidOrderBySortsequence(userOrganList.get(0).getOrganid());
			if (!IsLeafOfOrganList.isEmpty()) {// 有子节点
				tree.setCls("folder");
				tree.setLeaf(false);
				tree.setExpanded(true);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
				tree.setExpanded(true);
			}
			tree.setText(userOrganList.get(0).getOrganname());
			nodeTreeList.add(tree);
			return nodeTreeList;
		}
	}

	// 获取organId所有父节点，返回字符串
	private String getOrganParents(String organId, Map<String, String> rightOrganMap) {
		StringBuffer parents = new StringBuffer(organId);
		while (!"0".equals(organId.trim())) {// 防止oracle会带空格
			organId = rightOrganMap.get(organId);
			parents.append(",").append(organId);
		}
		return parents.toString();
	}

	// 获取nodeId所有父节点，返回字符串
	public String getNodeParents(String nodeId, Map<String, String> dataNodeMap) {
		StringBuffer parents = new StringBuffer();
		while (nodeId != null && !"".equals(nodeId.trim()) && dataNodeMap.containsKey(nodeId)) {
			nodeId = dataNodeMap.get(nodeId);
			parents.append(nodeId).append(",");
		}
		return parents.length() > 0 ? parents.substring(0, parents.length() - 1) : parents.toString();// 去掉最后一个“，”
	}

	/**
	 * 查询pcid下的第一层权限节点
	 *
	 * @param pcid
	 * @param parents
	 * @return
	 */
	private List<Tb_user_organ_parents> findTopOrganOfPcid(String pcid, List<Tb_user_organ_parents> parents) {
		// 找出属于属于pcid下的，得到集合
		List<Tb_user_organ_parents> allList = new ArrayList<>();
		for (Tb_user_organ_parents userOrganParents : parents) {
			String parent = userOrganParents.getParents().substring(userOrganParents.getParents().indexOf(",") + 1);// 去掉自身
			if (parent.contains(pcid)) {
				allList.add(userOrganParents);
			}
		}
		if (allList.size() == 0) {// 用于判断 是否是叶子节点
			return null;
		}

		// 获取最顶层的节点
		String[] idArray = GainField.getFieldValues(allList, "organid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(allList, "organid");
		List<String> idList = Arrays.asList(idArray);
		List<Tb_user_organ_parents> topIds = new ArrayList<>();// 返回集合topIds
		String[] parentArray;
		String[] temp;
		for (Tb_user_organ_parents userOrganParentList : allList) {
			temp = userOrganParentList.getParents().split(",");
			parentArray = Arrays.copyOf(temp, temp.length + 1);
			parentArray[parentArray.length - 1] = "0";// 最顶层父节点是""

			boolean found = false;
			int i = 0;
			do {
				i++;
				if (idList.contains(parentArray[i])) {
					found = true;
					break;
				}
			} while (!parentArray[i].equals(pcid));
			if (!found) {
				topIds.add(userOrganParentList);// 在pcid下的集合中找不到该节点的父节点，代表该节点为第一层地节点
			}
		}
		return topIds;
	}

	/**
	 * 查询pcid下的第一层权限节点
	 *
	 * @param pcid
	 * @param allNodes
	 * @return
	 */
	private List<Tb_user_node_parents> findTopNodeOfPcid(String pcid, List<Tb_user_node_parents> allNodes) {
		// 获取最顶层的节点
		Set<String> idList = allNodes.parallelStream().map(Tb_user_node_parents::getNodeid).collect(Collectors.toSet());
		List<Tb_user_node_parents> topIds = allNodes.parallelStream().filter(nodeparent -> {
			// 在pcid下的集合中找不到该节点的父节点，代表该节点为第一层的节点
			String[] temp = nodeparent.getParents().split(",");
			String[] array = Arrays.copyOf(temp, temp.length + 1);
			array[array.length - 1] = "";
			boolean found = false;
			int i = -1;
			do {
				i++;
				if (idList.contains(array[i])) {
					found = true;
					break;
				}
			} while (!array[i].equals(pcid));
			return !found;
		}).collect(Collectors.toList());
		return topIds;
	}

	/**
	 * 获得pcid下的所有权限节点
	 *
	 * @param pcid
	 * @param parents
	 * @return
	 */
	public List<Tb_user_node_parents> allNodeOfParent(String pcid, List<Tb_user_node_parents> parents) {
		// 找出属于pcid下的，得到集合
		List<Tb_user_node_parents> allList = parents.parallelStream()
				.filter(nodeparent -> nodeparent.getParents().contains(pcid)).collect(Collectors.toList());
		if (allList.size() == 0) {// 用于判断 是否是叶子节点
			return null;
		} else {
			return allList;
		}
	}


	public List<Tb_user_node_parents> allNodeOfParentOrganNode(String pcid, List<Tb_user_node_parents> parents,String userid) {
		// 找出属于pcid下的，得到集合
		String organid = userRepository.findOrganidByUserid(userid);
		List<Tb_data_node> findAllNode = findAllNodeList();
		Tb_user_node_parents unp = getFindNode(pcid, organid, findAllNode);
		if(unp!=null){
			boolean flag =false;
			for(Tb_user_node_parents parent : parents){
				if(parent.getNodeid().equals(unp.getNodeid())){
					flag = true;
					break;
				}
			}
			if(!flag){
				parents.add(unp);
			}
		}
		List<Tb_user_node_parents> allList = parents.stream()
				.filter(nodeparent -> nodeparent.getParents().contains(pcid)).collect(Collectors.toList());
		if (allList.size() == 0) {// 用于判断 是否是叶子节点
			return null;
		} else {
			return allList;
		}
	}

	/**
	 * 获取pcid下节点树 核心思路： 1、获得pcid下所有权限节点集合A
	 * 2、在集合A中寻找“显示在第一级的节点”（判断依据：该节点的所有父节点都不在集合A中）
	 *
	 * @param pcid
	 * @param isFirstLevel
	 *            是否返回首层节点（优化查询速率）
	 * @return
	 */
	public List<NodesettingTree> getNodeByParentId(String pcid, boolean isFirstLevel,String xtType) {
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		NodesettingTree[] returnTree = getNodeChildren(firstLevelNodes, childAllNodes, isFirstLevel);// 获得树节点
		if ("".equals(pcid)) {
			for (NodesettingTree nodetree : returnTree) {
				nodetree.setExpanded(true);
			}
		}
		return Arrays.asList(returnTree);
	}

	public List<NodesettingTree> getNodeByParentIdOrgan(String pcid, boolean isFirstLevel,String userid) {
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcidOrgan(pcid,userid);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		NodesettingTree[] returnTree = getNodeChildrenOrgan(firstLevelNodes, childAllNodes, isFirstLevel,userid);// 获得树节点
		if ("".equals(pcid)) {
			for (NodesettingTree nodetree : returnTree) {
				nodetree.setExpanded(true);
			}
		}

		return Arrays.asList(returnTree);
	}

	/**
	 * 利用平台-档案利用的树节点； 不做节点权限过滤； 但在分类下，只显示：当前用户的所属单位（或所属部门的所属单位）的机构节点
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getNodesettingKfID(String pcid) {// 数据节点ID
		// 找到他的所属单位
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String organid = userRepository.findOrganidByUserid(userDetails.getUserid());
		Tb_right_organ organ = rightOrganRepository.findByOrganid(organid);
		List<NodesettingTree> nodeTreeList = new ArrayList<>();
		if ("1".equals(userDetails.getUsertype())) {
			nodeTreeList = getNodeByParentIdOrgan(pcid, true,userDetails.getUserid());
		}else {
			Set<String> showSet = new HashSet<>();// 允许显示的机构节点ID
			showSet.add(organid);
			// 所属单位的所有子节点
			List<Tb_right_organ> childOrganList = rightOrganRepository.findByParentid(organid);
			List<Tb_right_organ> filterList = childOrganList.stream().filter(org -> org.getOrgantype().equals("department"))
					.collect(Collectors.toList());
			while (filterList.size() != 0) {
				String[] childids = GainField.getFieldValues(filterList, "organid").length == 0 ? new String[]{""}
						: GainField.getFieldValues(filterList, "organid");
				showSet.addAll(new HashSet<>(Arrays.asList(childids)));
				childOrganList = rightOrganRepository.findByParentidIn(childids);
				filterList = childOrganList.stream().filter(org -> org.getOrgantype().equals("department"))
						.collect(Collectors.toList());
			}
			List<Tb_data_node> returnList = getOpenNodes(pcid, showSet, organ);

			for (Tb_data_node node : returnList) {
				NodesettingTree tree = new NodesettingTree();
				tree.setFnid(node.getNodeid());
				tree.setText(node.getNodename());
				List<Tb_data_node> childNodeList = getOpenNodes(node.getNodeid(), showSet, organ);
				if (childNodeList.size() < 1) {
					tree.setCls("file");
					tree.setLeaf(true);
				} else {
					tree.setCls("folder");
					tree.setLeaf(false);
					if (node.getParentnodeid() == null || "".equals(node.getParentnodeid().trim())) {
						tree.setExpanded(true);
					}
				}
				tree.setRoottype(node.getNodetype() == 1 ? "unit" : "classification");
				nodeTreeList.add(tree);
			}
		}
		return nodeTreeList;
	}

	/**
	 * 库房平台-电子档案的树节点；
	 * 不做节点权限过滤；
	 * 显示有权限机构节点
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getKfNodeByParentId(String pcid) {//数据节点ID
		List<Tb_data_node> returnList = new ArrayList<>();
		List<Tb_data_node> nodesList;
		if (pcid == null || "".equals(pcid)) {
			nodesList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeidOrderBySortsequence("");
		} else {
			nodesList = dataNodeRepository.findByParentnodeidOrderBySortsequence(pcid);
		}
		for (Tb_data_node node : nodesList) {
			if (node.getNodename().contains("未归管理") || node.getNodename().contains("卷内管理")){
				continue;
			}
			returnList.add(node);
		}

		List<NodesettingTree> nodeTreeList = new ArrayList<>();

		for (Tb_data_node node : returnList) {
			NodesettingTree tree = new NodesettingTree();
			tree.setFnid(node.getNodeid());
			tree.setText(node.getNodename());
			tree.setClasslevel(node.getClasslevel());
			tree.setNodeType(node.getNodetype());
			if (node.getLeaf()) {
				tree.setCls("file");
				tree.setLeaf(true);
			} else {
				tree.setCls("folder");
				tree.setLeaf(false);
				if (node.getParentnodeid() == null || "".equals(node.getParentnodeid().trim())||returnList.size()==1) {
					tree.setExpanded(true);
				}
			}
			tree.setRoottype(node.getNodetype() == 1 ? "unit" : "classification");
			nodeTreeList.add(tree);
		}
		return nodeTreeList;
	}

	/**
	 * 利用平台-档案利用的树节点 获取返回的节点
	 *
	 * @param pcid
	 * @param showSet
	 * @param organ
	 * @return
	 */
	public List<Tb_data_node> getOpenNodes(String pcid, Set<String> showSet, Tb_right_organ organ) {
		List<Tb_data_node> returnList = new ArrayList<>();
		List<Tb_data_node> nodesList;
		if (pcid == null || "".equals(pcid)) {
			nodesList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeidOrderBySortsequence("");
		} else {
			nodesList = dataNodeRepository.findByParentnodeidOrderBySortsequence(pcid);
		}
		for (Tb_data_node node : nodesList) {
			if (node.getNodename().contains("未归管理") || node.getNodename().contains("卷内管理"))
				continue;
			if (node.getNodetype() == 1 && !showSet.contains(node.getRefid())) {
				continue;
			}
			returnList.add(node);
		}
		if (returnList.size() == 0 && nodesList.size() != 0
				&& findNodeById(pcid).getNodetype() != 1) {
			List<Tb_data_node> findAllNode = findAllNodeList();
			Tb_user_node_parents unp = getFindNode(pcid, organ.getOrganid(), findAllNode);
			Tb_data_node addNode = new Tb_data_node();
			for (Tb_data_node node : findAllNode) {
				if (node.getNodeid().equals(unp.getNodeid())) {
					addNode = node;
					break;
				}
			}
			returnList.add(addNode);
		}
		return returnList;
	}

	/**
	 * 在某节点下的子孙节点中找到某个分类（机构）节点
	 *
	 * @param parentId
	 * @param findId
	 * @param findAll
	 * @return 挂条目的节点
	 */
	public Tb_user_node_parents getFindNode(String parentId, String findId, List<Tb_data_node> findAll) {
		Map<String, String> map = new HashMap<>();
		for (Tb_data_node node : findAll) {
			map.put(node.getNodeid(), node.getParentnodeid());
		}
		findId=findId.trim();
		List<Tb_user_node_parents> allNodes = new ArrayList<>();
		for (Tb_data_node node : findAll) {
			Tb_user_node_parents nodeParent = new Tb_user_node_parents();
			nodeParent.setNodeid(node.getNodeid());
			nodeParent.setNodename(node.getNodename());
			nodeParent.setRefid(node.getRefid());

			String allParent = getNodeParents(node.getNodeid(), map);
			nodeParent.setParents(allParent);
			allNodes.add(nodeParent);
		}
		for (Tb_user_node_parents userNodeParents : allNodes) {
			// 其父id包含该节点说明该节点在parentId下，再判断refid是否等于findId
			if (userNodeParents.getParents().contains(parentId) && findId.equals(userNodeParents.getRefid())) {
				return userNodeParents;
			}
		}
		return null;
	}

	public List<Tb_user_node_parents> getFindNodeOrgan(String parentId,List<Tb_data_node> findAll,String userid) {
		Map<String, String> map = new HashMap<>();
		for (Tb_data_node node : findAll) {
			map.put(node.getNodeid(), node.getParentnodeid());
		}

		List<Tb_user_node_parents> allNodes = new ArrayList<>();
		for (Tb_data_node node : findAll) {
			Tb_user_node_parents nodeParent = new Tb_user_node_parents();
			nodeParent.setNodeid(node.getNodeid());
			nodeParent.setNodename(node.getNodename());
			nodeParent.setRefid(node.getRefid());

			String allParent = getNodeParents(node.getNodeid(), map);
			nodeParent.setParents(allParent);
			allNodes.add(nodeParent);
		}
		List<Tb_user_node_parents> parents = new ArrayList<>();
		for (Tb_user_node_parents userNodeParents : allNodes) {
			// 其父id包含该节点说明该节点在parentId下，再判断refid是否等于findId
			if (userNodeParents.getParents().contains(parentId)) {
				Tb_data_node nodetest = findNodeById(userNodeParents.getNodeid());
				Tb_right_organ organNode = rightOrganRepository.findByOrganid(nodetest.getOrganid());
				if("unit".equals(organNode.getOrgantype())){
					List<Tb_user_data_node> UserNodeList = userDataNodeRepository.findByUseridAndNodeid(userid, nodetest.getNodeid());
					if (UserNodeList.size() >0) {
						parents.add(userNodeParents);
					}
				}else{
					String organid = userRepository.findOrganidByUserid(userid);
					if(organid.equals(organNode.getOrganid())){
						parents.add(userNodeParents);
					}else{
						boolean flag = false;
						while (organNode.getOrgantype() != null && organNode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
							organNode = rightOrganRepository.findOne(organNode.getParentid());
							if(organid.equals(organNode.getOrganid())){
								flag = true;
								break;
							}
						}
						if(flag){
							parents.add(userNodeParents);
						}else {
							List<Tb_user_data_node> UserNodeList = userDataNodeRepository.findByUseridAndNodeid(userid, nodetest.getNodeid());
							if (UserNodeList.size() >0) {
								parents.add(userNodeParents);
							}
						}
					}
				}
			}
		}
		return parents;
	}

	/**
	 * 在某节点下的子孙节点中找到某个分类（机构）数据权限勾选的节点
	 *
	 * @param parentId
	 * @param userid
	 * @param findAll
	 * @return 挂条目的节点
	 */
	public Tb_user_node_parents getFindNodePower(String parentId,String userid, List<Tb_data_node> findAll) {
		Map<String, String> map = new HashMap<>();
		for (Tb_data_node node : findAll) {
			map.put(node.getNodeid(), node.getParentnodeid());
		}

		List<Tb_user_node_parents> allNodes = new ArrayList<>();
		for (Tb_data_node node : findAll) {
			Tb_user_node_parents nodeParent = new Tb_user_node_parents();
			nodeParent.setNodeid(node.getNodeid());
			nodeParent.setNodename(node.getNodename());
			nodeParent.setRefid(node.getRefid());

			String allParent = getNodeParents(node.getNodeid(), map);
			nodeParent.setParents(allParent);
			allNodes.add(nodeParent);
		}
		for (Tb_user_node_parents userNodeParents : allNodes) {
			// 其父id包含该节点说明该节点在parentId下，再判断refid是否等于findId
			if (userNodeParents.getParents().contains(parentId)) {
				List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userid,userNodeParents.getNodeid());
				if(userDataNodeList.size()>0){
					return userNodeParents;
				}
			}
		}
		return null;
	}

	/**
	 * 数据节点授权
	 *
	 * @param pcid
	 * @param userid
	 * @return
	 */
	public List<ExtTree> getCheckNodeByParentId(String pcid, String userid, String type,String xtType) {
		if("声像系统".equals(xtType)){
			return getSxCheckNodeByParentId(pcid, userid, type, xtType);
		}else{
			Set<String> nodeidSet = new HashSet<>();
			Set<String> rolenodeidSet = new HashSet<>();
			String[] roleids = new String[]{};
			if (type != null) {
				SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				nodeidSet.addAll(userDataNodeRepository.findByUserid(userDetails.getUserid()));
			} else {
				if (userid.split(",").length == 1) {
					/*List<Tb_role> roles = roleRepository.findBygroups(userid);
					roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
					nodeidSet.addAll(userDataNodeRepository.findBynodes(roleids,userid));// 选中用户所拥有的权限*/
					/*List<Tb_role> roles = roleRepository.findBygroups(userid);
					if(roles.size()>0){
						roleids = GainField.getFieldValues(roles,"roleid");
					}*/
					List<Tb_data_node> userNodeList=findUserNodeList(userid);//用户个人权限节点
					List<Tb_data_node> userRoleNodeList=findRoleNodeList(userid);//用户角色权限节点
					userRoleNodeList.forEach(dataNode -> {
						rolenodeidSet.add(dataNode.getNodeid());
					});
					userRoleNodeList.removeAll(userNodeList);
					userNodeList.addAll(userRoleNodeList);// 分开查询优化速率，取并集
					userNodeList.forEach(dataNode -> {
						nodeidSet.add(dataNode.getNodeid());//用户权限节点和角色权限节点集合
					});
				}
			}
			/*Set<String> rolenodeid = new HashSet<>();
			if (roleids.length == 1) {
				//设置了用户组相关的数据权限
				if ("声像系统".equals(xtType)) {
					rolenodeid.addAll(sxUserDataNodeRepository.findBynodes(roleids, ""));
				} else {
					rolenodeid.addAll(userDataNodeRepository.findBynodes(roleids, ""));
				}
			}*/
			List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,xtType);// 获取pcid下的所有权限
			if (childAllNodes == null || childAllNodes.size() == 0) {
				return null;
			}
			List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
			ExtTree[] returnTree = getCheckNodeChildren(firstLevelNodes, childAllNodes, nodeidSet, rolenodeidSet, xtType);// 获得树节点
			return Arrays.asList(returnTree);
		}

	}

	/**
	 * 数据节点授权  声像
	 *
	 * @param pcid
	 * @param userid
	 * @return
	 */
	public List<ExtTree> getSxCheckNodeByParentId(String pcid, String userid, String type,String xtType) {
		Set<String> nodeidSet = new HashSet<>();
		String[] roleids = new String[]{};
		if (type != null) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			nodeidSet.addAll(sxUserDataNodeRepository.findSxByUserid(userDetails.getUserid()));
		} else {
			if (userid.split(",").length == 1) {
				List<Tb_role_sx> roles = sxRoleRepository.findBygroups(userid);
				roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
				nodeidSet.addAll(sxUserDataNodeRepository.findSxBynodes(roleids,userid));// 选中用户所拥有的权限
			}
		}
		Set<String> rolenodeid = new HashSet<>();
		if (roleids.length == 1) {
			//设置了用户组相关的数据权限
			rolenodeid.addAll(sxUserDataNodeRepository.findBynodes(roleids, ""));
		}
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		ExtTree[] returnTree = getCheckNodeChildren(firstLevelNodes, childAllNodes, nodeidSet, rolenodeid, xtType);// 获得树节点
		return Arrays.asList(returnTree);
	}

	/**
	 * 数据节点授权：返回一个完整的树ExtTree
	 *
	 * @param firstLevelNodes
	 * @param allNodes
	 * @param nodeidSet
	 * @return
	 */
	private ExtTree[] getCheckNodeChildren(List<Tb_user_node_parents> firstLevelNodes,
										   List<Tb_user_node_parents> allNodes, Set<String> nodeidSet, Set<String> rolenodeid, String xtType) {
		ExtTree[] nodeTreeList = new ExtTree[firstLevelNodes.size()];
		for (int i = 0; i < firstLevelNodes.size(); i++) {
			Tb_user_node_parents userDataNode = firstLevelNodes.get(i);
			ExtTree tree = new ExtTree();
			tree.setFnid(userDataNode.getNodeid());
			if (rolenodeid!=null&&rolenodeid.size() != 0 && rolenodeid.contains(userDataNode.getNodeid())){
				//比较是否是用户组设置的权限，是则灰色显示
				tree.setText("<span style = 'color:gray;editable:false'>"+userDataNode.getNodename()+"</span>");
			} else {
				tree.setText(userDataNode.getNodename());
			}

			if (nodeidSet.contains(userDataNode.getNodeid())) {
				tree.setChecked(true);
			}
			List<Tb_user_node_parents> childAllNodes = allNodeOfParent(userDataNode.getNodeid(), allNodes);// 判断是否有子节点
			if (childAllNodes != null && childAllNodes.size() != 0) {// 非叶子
				tree.setCls("folder");
				tree.setLeaf(false);
				List<Tb_user_node_parents> childUserNodes = findTopNodeOfPcid(userDataNode.getNodeid(), childAllNodes);
				ExtTree[] extTrees = getCheckNodeChildren(childUserNodes, childAllNodes, nodeidSet, rolenodeid, xtType);// 获得树节点
				tree.setChildren(extTrees);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			if (userDataNode.getNodetype() == 1) {
				tree.setRoottype("unit");
			} else {
				tree.setRoottype("classification");
			}
			nodeTreeList[i] = tree;
		}
		return nodeTreeList;
	}

	/**
	 * 机构授权：返回一个完整的树ExtTree
	 *
	 * @param parents
	 * @param organidList
	 * @param userOrganParents
	 * @return
	 */
	private ExtTree[] getCheckOrganChildren(List<Tb_user_organ_parents> parents, List<String> organidList,
											List<Tb_user_organ_parents> userOrganParents) {
		ExtTree[] organTreeList = new ExtTree[userOrganParents.size()];

		for (int i = 0; i < userOrganParents.size(); i++) {
			Tb_user_organ_parents useRightOrgan = userOrganParents.get(i);
			ExtTree tree = new ExtTree();
			tree.setFnid(useRightOrgan.getOrganid());
			tree.setText(useRightOrgan.getOrganname());

			if (organidList.contains(useRightOrgan.getOrganid())) {
				tree.setChecked(true);
			}
			List<Tb_user_organ_parents> childUserOrgans = findTopOrganOfPcid(useRightOrgan.getOrganid(), parents);// 判断是否有子节点
			if (childUserOrgans != null) {
				tree.setCls("folder");
				tree.setLeaf(false);
				ExtTree[] extTrees = getCheckOrganChildren(parents, organidList, childUserOrgans);
				tree.setChildren(extTrees);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			organTreeList[i] = tree;
		}
		return organTreeList;
	}

	/**
	 * 查询数据节点：返回一个完整的树ExtTree
	 *
	 * @param firstLevelNodes
	 * @param allNodes
	 * @param isFirstLevel
	 *            是否返回首层节点（优化查询速率）
	 * @return
	 */
	private NodesettingTree[] getNodeChildren(List<Tb_user_node_parents> firstLevelNodes,
											  List<Tb_user_node_parents> allNodes, boolean isFirstLevel) {
		NodesettingTree[] nodeTreeList = new NodesettingTree[firstLevelNodes.size()];
		List<NodesettingTree> list = Collections.synchronizedList(new ArrayList<>(firstLevelNodes.size()));
		firstLevelNodes.parallelStream().forEach(userDataNode -> {
			NodesettingTree tree = new NodesettingTree();
			tree.setFnid(userDataNode.getNodeid());
			tree.setText(userDataNode.getNodename());
			tree.setClasslevel(userDataNode.getClasslevel());
			tree.setNodeType(userDataNode.getNodetype());
			tree.setSortsequence(userDataNode.getOrders());
			tree.setOrganid(userDataNode.getOrganid());

			List<Tb_user_node_parents> childAllNodes = allNodeOfParent(userDataNode.getNodeid(), allNodes);// 判断是否有子节点
			if (childAllNodes != null && childAllNodes.size() != 0) {// 非叶子
				tree.setCls("folder");
				tree.setLeaf(false);
				if (!isFirstLevel) {
					List<Tb_user_node_parents> childUserNodes = findTopNodeOfPcid(userDataNode.getNodeid(),
							childAllNodes);
					NodesettingTree[] extTrees = getNodeChildren(childUserNodes, childAllNodes, false);// 获得树节点
					tree.setChildren(extTrees);
				}
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			if (userDataNode.getNodetype() == 1) {
				tree.setRoottype("unit");
			} else {
				tree.setRoottype("classification");
			}
			list.add(tree);
		});
		list.parallelStream().sorted(new Comparator<NodesettingTree>() {// 排序
			public int compare(NodesettingTree arg0, NodesettingTree arg1) {
				long sequence1 = arg0.getSortsequence() != null ? arg0.getSortsequence() : 0;
				long sequence2 = arg1.getSortsequence() != null ? arg1.getSortsequence() : 0;
				return (int) (sequence1 - sequence2==0?-1:(sequence1 - sequence2));
			}
		}).collect(Collectors.toList()).toArray(nodeTreeList);
		return nodeTreeList;
	}

	private NodesettingTree[] getNodeChildrenOrgan(List<Tb_user_node_parents> firstLevelNodes,
												   List<Tb_user_node_parents> allNodes, boolean isFirstLevel,String userid) {
		NodesettingTree[] nodeTreeList = new NodesettingTree[firstLevelNodes.size()];
		List<NodesettingTree> list = Collections.synchronizedList(new ArrayList<>(firstLevelNodes.size()));
		firstLevelNodes.parallelStream().forEach(userDataNode -> {
			NodesettingTree tree = new NodesettingTree();
			tree.setFnid(userDataNode.getNodeid());
			tree.setText(userDataNode.getNodename());
			tree.setClasslevel(userDataNode.getClasslevel());
			tree.setNodeType(userDataNode.getNodetype());
			tree.setSortsequence(userDataNode.getOrders());

			List<Tb_user_node_parents> childAllNodes = new ArrayList<>();
			String organid = userRepository.findOrganidByUserid(userid);

			Tb_data_node nodetest = findNodeById(userDataNode.getNodeid());
			if(nodetest.getOrganid()!=null){
				Tb_right_organ organNow = rightOrganRepository.findByOrganid(nodetest.getOrganid());
				if(organid.equals(organNow.getOrganid())){
					List<Tb_data_node> findAllNode = findAllNodeList();
					childAllNodes = getFindNodeOrgan(userDataNode.getNodeid(), findAllNode,userid);
				}else {
					boolean flag = false;
					while (organNow.getOrgantype() != null && organNow.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
						organNow = rightOrganRepository.findOne(organNow.getParentid());
						if(organid.equals(organNow.getOrganid())){
							flag = true;
							break;
						}
					}
					if(flag){
						List<Tb_data_node> findAllNode = findAllNodeList();
						childAllNodes = getFindNodeOrgan(userDataNode.getNodeid(), findAllNode,userid);
					}else{
						childAllNodes = allNodeOfParentOrganNode(userDataNode.getNodeid(), allNodes, userid);// 判断是否有子节点
					}
				}
			}else {
				childAllNodes = allNodeOfParentOrganNode(userDataNode.getNodeid(), allNodes, userid);// 判断是否有子节点
			}
			if (childAllNodes != null && childAllNodes.size() != 0) {// 非叶子
				tree.setCls("folder");
				tree.setLeaf(false);
				if (!isFirstLevel) {
					List<Tb_user_node_parents> childUserNodes = findTopNodeOfPcid(userDataNode.getNodeid(),
							childAllNodes);
					NodesettingTree[] extTrees = getNodeChildren(childUserNodes, childAllNodes, false);// 获得树节点
					tree.setChildren(extTrees);
				}
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			if (userDataNode.getNodetype() == 1) {
				tree.setRoottype("unit");
			} else {
				tree.setRoottype("classification");
			}
			list.add(tree);
		});
		list.parallelStream().sorted(new Comparator<NodesettingTree>() {// 排序
			public int compare(NodesettingTree arg0, NodesettingTree arg1) {
				long sequence1 = arg0.getSortsequence() != null ? arg0.getSortsequence() : 0;
				long sequence2 = arg1.getSortsequence() != null ? arg1.getSortsequence() : 0;
				return (int) (sequence1 - sequence2);
			}
		}).collect(Collectors.toList()).toArray(nodeTreeList);
		return nodeTreeList;
	}


	/**
	 * 查询机构节点：返回一个完整的树ExtTree
	 *
	 * @param parents
	 * @param userOrganParents
	 * @return
	 */
	private NodesettingTree[] getOrganChildren(List<Tb_user_organ_parents> parents,
											   List<Tb_user_organ_parents> userOrganParents) {
		NodesettingTree[] organTreeList = new NodesettingTree[userOrganParents.size()];

		for (int i = 0; i < userOrganParents.size(); i++) {
			Tb_user_organ_parents userRightOrgan = userOrganParents.get(i);
			NodesettingTree tree = new NodesettingTree();
			tree.setFnid(userRightOrgan.getOrganid());
			tree.setText(userRightOrgan.getOrganname());

			List<Tb_user_organ_parents> childUserOrgans = findTopOrganOfPcid(userRightOrgan.getOrganid(), parents);// 判断是否有子节点
			if (childUserOrgans != null) {
				tree.setCls("folder");
				tree.setLeaf(false);
				NodesettingTree[] extTrees = getOrganChildren(parents, childUserOrgans);
				tree.setChildren(extTrees);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			organTreeList[i] = tree;
		}
		return organTreeList;
	}

	/**
	 * 机构授权
	 *
	 * @param pcid
	 * @param userid
	 * @return
	 */
	public List<ExtTree> getCkeckOrganByParentId(String pcid, String userid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_right_organ> rightOrganList = rightOrganRepository.getMyAuthWithParent(userDetails.getUserid());// 获得userId的所有权限节点
		List<Tb_right_organ> organList = rightOrganRepository.findAll();

		Map<String, String> map = new HashMap<>();// 构造organId-Parentid的键值Map
		for (Tb_right_organ organ : organList) {
			map.put(organ.getOrganid(), organ.getParentid());
		}
		List<Tb_user_organ_parents> parents = new ArrayList<>();
		for (Tb_right_organ organ : rightOrganList) {
			Tb_user_organ_parents organParent = new Tb_user_organ_parents();
			organParent.setOrganid(organ.getOrganid());
			organParent.setOrganname(organ.getOrganname());

			String allParent = getOrganParents(organ.getOrganid(), map);
			organParent.setParents(allParent);
			parents.add(organParent);
		}

		List<String> organidList = new ArrayList<>();// 批量设置
		if (userid.split(",").length == 1) {
			List<Tb_user_organ> udn = userOrganRepository.findByUserid(userid);
			String[] organids = GainField.getFieldValues(udn, "organid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(udn, "organid");
			organidList = java.util.Arrays.asList(organids);
		}

		List<Tb_user_organ_parents> userOrganParents = findTopOrganOfPcid(pcid, parents);// 获取pcid下的权限
		if (userOrganParents == null || userOrganParents.size() == 0) {
			return null;
		}
		ExtTree[] returnTree = getCheckOrganChildren(parents, organidList, userOrganParents);
		return Arrays.asList(returnTree);
	}

	/**
	 * 用户组（角色）节点授权
	 *
	 * @param pcid
	 * @param roleid
	 * @return
	 */
	public List<ExtTree> getUserGroupCheckNodeByParentId(String pcid, String roleid,String xtType) {
		List<String> selectRoleNodeList;
		if("声像系统".equals(xtType)){
			selectRoleNodeList = sxRoleDataNodeRepository.findSxByRoleid(roleid);// 选中用户所拥有的权限
		}else{
			selectRoleNodeList = roleDataNodeRepository.findByRoleid(roleid);// 选中用户所拥有的权限
		}
		Set<String> nodeidSet = new HashSet<>();
		for (String roleNode : selectRoleNodeList) {
			nodeidSet.add(roleNode.trim());
		}

		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		Set<String> rolenodeid = new HashSet<>();
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		ExtTree[] returnTree = getCheckNodeChildren(firstLevelNodes, childAllNodes, nodeidSet, rolenodeid, xtType);// 获得树节点
		return Arrays.asList(returnTree);
	}

	// 暂时未使用
	public List<ExtTree> getUserGroupCheckOrganByParentId(String pcid, String roleid) {
		List<Tb_role_organ> role_organList = roleOrganRepository.findByRoleid(roleid);
		String[] organids = GainField.getFieldValues(role_organList, "organid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(role_organList, "organid");
		List<String> organidList = java.util.Arrays.asList(organids);
		List<Tb_right_organ> organsList = rightOrganRepository.findByParentidOrderBySortsequence(pcid);
		List<ExtTree> nodeTreeList = new ArrayList<>();
		for (int i = 0; i < organsList.size(); i++) {
			ExtTree tree = new ExtTree();
			tree.setFnid(organsList.get(i).getOrganid());
			tree.setText(organsList.get(i).getOrganname());
			nodeTreeList.add(tree);
			if (organidList.contains(organsList.get(i).getOrganid())) {
				tree.setChecked(true);
			}
			List<Tb_right_organ> organHasChild = rightOrganRepository
					.findByParentidOrderBySortsequence(organsList.get(i).getOrganid());
			if (organHasChild.size() == 0) {
				tree.setCls("file");
				tree.setLeaf(true);
			} else {
				tree.setCls("folder");
				tree.setLeaf(false);
			}
		}
		return nodeTreeList;
	}

	/**
	 * 初始数据节点：classification-organ
	 *
	 * @param classList
	 * @param level
	 * @param datanodeid
	 * @param nodecode
	 */
	public void saveClassOrgan(List<Tb_classification> classList, int level, String datanodeid, String nodecode) {
		for (int i = 0; i < classList.size(); i++) {
			Tb_classification classification = classList.get(i);
			Tb_data_node datanode = new Tb_data_node();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, i + 1 + ""));
			datanode.setNodename(classification.getClassname());
			datanode.setNodetype(2);// 2:classification
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(classification.getClassid());
			datanode.setOrders(classification.getSortsequence());
			datanode.setClasslevel(classification.getClasslevel());
			datanode.setClassid(classification.getClassid());
			datanode.setLeaf(false);// 统一设置为非叶子节点，显示下级的机构
			datanode.setLuckstate("0");
			Tb_data_node data_node = dataNodeRepository.save(datanode);// 添加datanode,并获取返回对象的nodeid

			List<Tb_classification> childClassificationList = classificationRepository
					.findByParentclassidOrderBySortsequence(classification.getClassid());

			if (!childClassificationList.isEmpty()) {
				level++;// 下一级
				saveClassOrgan(childClassificationList, level, data_node.getNodeid(), data_node.getNodecode());
				level--;// 退一级
			} else {
				level++;
				List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
				saveOrganNode(childRightOrganList, level, data_node.getNodeid(), datanode.getNodecode(),
						classification.getClassid(), new ArrayList<>());// 机构的一级parentid是0
				level--;
			}
		}
	}

	public List<Tb_data_node> saveOrganNode(List<Tb_right_organ> rightOrganList, int level, String datanodeid,
											String nodecode, String classId, List<Tb_data_node> saveNodeList) {
		int classlevel = 0;
		if (!saveNodeList.isEmpty()) {// 增加节点的案卷标志
			classlevel = saveNodeList.get(0).getClasslevel() == null ? 0 : saveNodeList.get(0).getClasslevel();
		}
		for (int i = 0; i < rightOrganList.size(); i++) {
			Tb_right_organ rightOrgan = rightOrganList.get(i);
			Tb_data_node datanode = new Tb_data_node();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, i + 1 + ""));
			datanode.setNodename(rightOrgan.getOrganname());
			datanode.setNodetype(1);// 1:机构
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(rightOrgan.getOrganid());
			datanode.setOrders(rightOrgan.getSortsequence());
			datanode.setOrganid(rightOrgan.getOrganid());
			datanode.setClassid(classId);
			datanode.setLuckstate("0");
			if (classlevel > 0) {
				datanode.setClasslevel(classlevel);// 增加节点的案卷标志
			}

			List<Tb_right_organ> childRightOrganList = rightOrganRepository
					.findByParentidOrderBySortsequence(rightOrgan.getOrganid());
			if (!childRightOrganList.isEmpty()) {// 有子节点
				datanode.setLeaf(false);
			} else {
				datanode.setLeaf(true);
			}

			Tb_data_node data_node = dataNodeRepository.save(datanode);// 添加datanode
			saveNodeList.add(data_node);

			if (!childRightOrganList.isEmpty()) {
				level++;
				saveNodeList = saveOrganNode(childRightOrganList, level, data_node.getNodeid(), datanode.getNodecode(),
						classId, saveNodeList);
				level--;
			}
		}
		return saveNodeList;
	}

	//声像系统指定分类下增加整套机构节点
	public List<Tb_data_node_sx> saveSxOrganNode(List<Tb_right_organ> rightOrganList, int level, String datanodeid,
												 String nodecode, String classId, List<Tb_data_node_sx> saveNodeList, int classlevel) {
		/*int classlevel = 0;
		if (!saveNodeList.isEmpty()) {// 增加节点的案卷标志
			classlevel = saveNodeList.get(0).getClasslevel() == null ? 0 : saveNodeList.get(0).getClasslevel();
		}*/
		for (int i = 0; i < rightOrganList.size(); i++) {
			Tb_right_organ rightOrgan = rightOrganList.get(i);
			Tb_data_node_sx datanode = new Tb_data_node_sx();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, i + 1 + ""));
			datanode.setNodename(rightOrgan.getOrganname());
			datanode.setNodetype(1);// 1:机构
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(rightOrgan.getOrganid());
			datanode.setOrders(rightOrgan.getSortsequence());
			datanode.setOrganid(rightOrgan.getOrganid());
			datanode.setClassid(classId);
			datanode.setLuckstate("0");
			if (classlevel > 0) {
				datanode.setClasslevel(classlevel);// 增加节点的案卷标志
			}

			List<Tb_right_organ> childRightOrganList = rightOrganRepository
					.findByParentidOrderBySortsequence(rightOrgan.getOrganid());
			if (!childRightOrganList.isEmpty()) {// 有子节点
				datanode.setLeaf(false);
			} else {
				datanode.setLeaf(true);
			}

			Tb_data_node_sx data_node = secondaryDataNodeRepository.save(datanode);// 添加datanode
			saveNodeList.add(data_node);

			if (!childRightOrganList.isEmpty()) {
				level++;
				saveNodeList = saveSxOrganNode(childRightOrganList, level, data_node.getNodeid(), datanode.getNodecode(),
						classId, saveNodeList,classlevel);
				level--;
			}
		}
		return saveNodeList;
	}

	// 拼接nodecode
	public String joint(String nodecode, String str) {
		if (str.length() == 1) {
			str = "00" + str;
		} else if (str.length() == 2) {
			str = "0" + str;
		}
		str = nodecode + str;
		return str;
	}

	public List<NodesettingTree> changeTreeModel(String initModel) {
		roleDataNodeRepository.deleteAll();// 清空角色节点权限表
		userDataNodeRepository.deleteAll();// 清空节点权限表
		dataNodeRepository.deleteAll();// 清空表数据
		if (initModel.equals("classification-organ")) {
			List<Tb_classification> childClassList = classificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			saveClassOrgan(childClassList, 1, "", "");// 初始化节点模型
			initializeNode(findAllNodeList());// 授权
		} else if (initModel.equals("organ-classification")) {
			List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
			List<Tb_classification> childClassList = classificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			saveOrganClass(rightOrganList, 1, "", "", 0, childClassList);
			initializeNode(findAllNodeList());
		}
		return getNodeByParentId("", true,"");
	}

	public ExtMsg changeSxTreeModel(String initModel) {
		sxRoleDataNodeRepository.deleteAll();// 清空角色节点权限表
		sxUserDataNodeRepository.deleteAll();// 清空节点权限表
		secondaryDataNodeRepository.deleteAll();// 清空表数据
		if (initModel.equals("classification-organ")) {
			List<Tb_classification_sx> childClassList = sxClassificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			saveSxClassOrgan(childClassList, 1, "", "");// 初始化节点模型
			initializeSxNode(secondaryDataNodeRepository.findAll(),"");// 授权
		} else if (initModel.equals("organ-classification")) {
			List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
			List<Tb_classification_sx> childClassList = sxClassificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			saveSxOrganClass(rightOrganList, 1, "", "", 0, childClassList);
			initializeSxNode(secondaryDataNodeRepository.findAll(),"");
		}
		return new ExtMsg(true, "成功同步机构到声像系统", null);
	}

	/**
	 * 初始声像数据节点：classification-organ
	 *
	 * @param classList
	 * @param level
	 * @param datanodeid
	 * @param nodecode
	 */
	public void saveSxClassOrgan(List<Tb_classification_sx> classList, int level, String datanodeid, String nodecode) {
		for (int i = 0; i < classList.size(); i++) {
			Tb_classification_sx classification = classList.get(i);
			Tb_data_node_sx datanode = new Tb_data_node_sx();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, i + 1 + ""));
			datanode.setNodename(classification.getClassname());
			datanode.setNodetype(2);// 2:classification
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(classification.getClassid());
			datanode.setOrders(classification.getSortsequence());
			datanode.setClasslevel(classification.getClasslevel());
			datanode.setClassid(classification.getClassid());
			datanode.setLeaf(false);// 统一设置为非叶子节点，显示下级的机构
			datanode.setLuckstate("0");
			Tb_data_node_sx data_node = secondaryDataNodeRepository.save(datanode);// 添加datanode,并获取返回对象的nodeid

			List<Tb_classification_sx> childClassificationList = sxClassificationRepository
					.findByParentclassidOrderBySortsequence(classification.getClassid());

			if (!childClassificationList.isEmpty()) {
				level++;// 下一级
				saveSxClassOrgan(childClassificationList, level, data_node.getNodeid(), data_node.getNodecode());
				level--;// 退一级
			} else {
				level++;
				List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
				saveSxOrganNode(childRightOrganList, level, data_node.getNodeid(), datanode.getNodecode(),
						classification.getClassid(), new ArrayList<>(),classification.getClasslevel());// 机构的一级parentid是0
				level--;
			}
		}
	}

	/**
	 * 初始声像数据节点：organ-classification
	 *
	 * @param rightOrganList
	 * @param level
	 * @param datanodeid
	 * @param nodecode
	 * @param size
	 */
	public void saveSxOrganClass(List<Tb_right_organ> rightOrganList, int level, String datanodeid, String nodecode,
								 int size, List<Tb_classification_sx> childClassList) {//暂不处理
	}

	/**
	 * 声像节点初始化的授权
	 *
	 * @param dataNodeList
	 */
	public void initializeSxNode(List<Tb_data_node_sx> dataNodeList,String userid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userid=userDetails.getUserid();
		// 为安全保密员授权 寻找与当前用户同机构的 同时属于安全保密管理员角色的人
		List<Tb_user> userList = userRepository.getUserByRolenameAndUserid("安全保密管理员", userid);
		userList.add(userRepository.findByUserid(userid));
		Tb_user_data_node_sx userDataNode;
		List<Tb_user_data_node_sx> list = new ArrayList<>();
		for (Tb_data_node_sx dataNode : dataNodeList) {
			for (Tb_user user : userList) {
				userDataNode = new Tb_user_data_node_sx();
				userDataNode.setNodeid(dataNode.getNodeid());
				userDataNode.setUserid(user.getUserid());
				list.add(userDataNode);
			}
		}
		sxUserDataNodeRepository.save(list);
	}

	public int saveClassNode(List<Tb_classification> classList, int level, String datanodeid, String nodecode,
							 String organId) {
		for (int i = 0; i < classList.size(); i++) {
			Tb_classification classification = classList.get(i);
			Tb_data_node datanode = new Tb_data_node();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, i + 1 + ""));
			datanode.setNodename(classification.getClassname());
			datanode.setNodetype(2);// 2:classification
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(classification.getClassid());
			datanode.setOrders(classification.getSortsequence());
			datanode.setOrganid(organId);
			datanode.setClassid(classification.getClassid());
			datanode.setLuckstate("0");
			List<Tb_classification> childClassList = classificationRepository
					.findByParentclassidOrderBySortsequence(classification.getClassid());
			if (!childClassList.isEmpty()) {
				datanode.setLeaf(false);
			} else {
				datanode.setLeaf(true);
			}
			Tb_data_node data_node = dataNodeRepository.save(datanode);// 添加datanode,并获取返回对象的nodeid

			if (!childClassList.isEmpty()) {
				level++;// 下一级
				saveClassNode(childClassList, level, data_node.getNodeid(), data_node.getNodecode(), organId);
				level--;// 退一级
			}
		}
		return classList.size();
	}

	/**
	 * 初始数据节点：organ-classification
	 *
	 * @param rightOrganList
	 * @param level
	 * @param datanodeid
	 * @param nodecode
	 * @param size
	 */
	public void saveOrganClass(List<Tb_right_organ> rightOrganList, int level, String datanodeid, String nodecode,
							   int size, List<Tb_classification> childClassList) {
		for (int i = 0; i < rightOrganList.size(); i++) {
			Tb_right_organ rightOrgan = rightOrganList.get(i);
			Tb_data_node datanode = new Tb_data_node();
			datanode.setNodelevel(level);
			datanode.setNodecode(joint(nodecode, size + i + 1 + ""));
			datanode.setNodename(rightOrgan.getOrganname());
			datanode.setNodetype(1);// 1:rightOrgan
			datanode.setParentnodeid(datanodeid);
			datanode.setRefid(rightOrgan.getOrganid());
			datanode.setOrders(rightOrgan.getSortsequence());
			datanode.setOrganid(rightOrgan.getOrganid());
			datanode.setLeaf(false);// 统一设置为非叶子节点，显示下级
			datanode.setLuckstate("0");
			Tb_data_node data_node = dataNodeRepository.save(datanode);// 添加datanode

			List<Tb_right_organ> childRightOrganList = rightOrganRepository
					.findByParentidOrderBySortsequence(rightOrgan.getOrganid());

			level++;
			// 单位需排在分类后面，根据分类根节点数量以确定机构的起始编码
			int ClassificationList_size = saveClassNode(childClassList, level, data_node.getNodeid(),
					data_node.getNodecode(), rightOrgan.getOrganid());
			level--;

			if (!childRightOrganList.isEmpty()) {
				level++;
				saveOrganClass(childRightOrganList, level, data_node.getNodeid(), data_node.getNodecode(),
						ClassificationList_size, childClassList);
				level--;
			}
		}
	}

	public Tb_data_node getTreeNode(String nodeid) {
		return findNodeById(nodeid);
	}

	public Tb_data_node updateTreeNode(Tb_data_node data_node) {
		return dataNodeRepository.save(data_node);
	}

	public Tb_data_node addTreeNodeSingle(String nodename_real, String selectedid, String refid, String nodecode,
										  String nodetypef_real) {
		Tb_data_node selection_data_node = findNodeById(selectedid);
		Tb_data_node data_node = new Tb_data_node();
		data_node.setNodename(nodename_real);
		data_node.setRefid(refid);
		data_node.setParentnodeid(selection_data_node.getParentnodeid());
		data_node.setNodecode(nodecode);
		data_node.setLeaf(true);// 默认设置为叶子节点
		data_node.setNodelevel(selection_data_node.getNodelevel());
		data_node.setNodetype(Long.parseLong(nodetypef_real));
		data_node.setLuckstate("0");
		Integer maxOrder = dataNodeRepository.getMaxOrder();
		maxOrder = maxOrder == null ? 0 : maxOrder;
		data_node.setOrders(maxOrder + 10);
		data_node = dataNodeRepository.save(data_node);
		authorize(data_node);// 授权

		return data_node;
	}

	public Tb_data_node addChildTreeNodeSingle(String nodename_real, String selectedid, String refid, String nodecode,
											   String nodetypef_real) {
		long parentlevel = 0;
		if (selectedid != null && !("".equals(selectedid))) {
			Tb_data_node selection_data_node = findNodeById(selectedid);
			selection_data_node.setLeaf(false);// 改成非叶子节点
			dataNodeRepository.save(selection_data_node);
			parentlevel = selection_data_node.getNodelevel();
		}

		Tb_data_node data_node = new Tb_data_node();
		data_node.setNodename(nodename_real);
		data_node.setRefid(refid);
		data_node.setParentnodeid(selectedid);
		data_node.setNodecode(nodecode);
		data_node.setLeaf(true);// 默认设置为叶子节点
		data_node.setNodelevel(parentlevel + 1);
		data_node.setNodetype(Long.parseLong(nodetypef_real));
		data_node.setLuckstate("0");
		Integer maxOrder = dataNodeRepository.getMaxOrder();
		maxOrder = maxOrder == null ? 0 : maxOrder;
		data_node.setOrders(maxOrder + 10);
		data_node = dataNodeRepository.save(data_node);
		authorize(data_node);// 授权

		return data_node;
	}

	public Tb_data_node addTreeNode(String nodename_real, String selectedid, String refid, String nodecode,
									String nodetype_real) {
		Tb_data_node data_node = findNodeById(selectedid);
		String parentid = data_node.getParentnodeid();// 同父
		long level = data_node.getNodelevel();// 同级

		if ("1".equals(nodetype_real)) {// 1:rightOrgan
			return addOrganNodeLoop(refid, nodecode, parentid, level, nodename_real);
		} else {
			return addClassNodeLoop(refid, nodecode, parentid, level, nodename_real);
		}
	}

	public Tb_data_node addChildTreeNode(String nodename_real, String selectedid, String refid, String nodecode,
										 String nodetype_real) {
		long parentlevel = 0;
		if (selectedid != null && !("".equals(selectedid))) {// 选中的不是最上面的功能节点
			Tb_data_node selection_data_node = findNodeById(selectedid);
			selection_data_node.setLeaf(false);
			dataNodeRepository.save(selection_data_node);// 事实上service层不用save也已经保存了
			parentlevel = selection_data_node.getNodelevel();
		}
		if ("1".equals(nodetype_real)) {// 1:rightOrgan
			return addOrganNodeLoop(refid, nodecode, selectedid, parentlevel + 1, nodename_real);
		} else {
			return addClassNodeLoop(refid, nodecode, selectedid, parentlevel + 1, nodename_real);
		}
	}

	public Tb_data_node addOrganNodeLoop(String refid, String nodecode, String parentid, long level, String nodename) {
		List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentidOrderBySortsequence(refid);// 寻找refid的子节点
		boolean leaf = rightOrganList.size() < 1;// 是否有子节点

		Tb_data_node data_node = new Tb_data_node();
		data_node.setNodename(nodename);
		data_node.setRefid(refid);
		data_node.setParentnodeid(parentid);
		data_node.setNodecode(nodecode);
		data_node.setLeaf(leaf);
		data_node.setNodelevel(level);
		data_node.setNodetype(1);
		data_node.setLuckstate("0");
		Integer maxOrder = dataNodeRepository.getMaxOrder();
		maxOrder = maxOrder == null ? 0 : maxOrder;
		data_node.setOrders(maxOrder + 10);
		data_node = dataNodeRepository.save(data_node);
		authorize(data_node);// 授权

		for (int i = 0; i < rightOrganList.size(); i++) {
			Tb_right_organ right_organ_temp = rightOrganList.get(i);
			addOrganNodeLoop(right_organ_temp.getOrganid(), joint(nodecode, i + 1 + ""), data_node.getNodeid(),
					level + 1, right_organ_temp.getOrganname());
		}
		return data_node;
	}

	public Tb_data_node addClassNodeLoop(String refid, String nodecode, String parentid, long level, String nodename) {
		List<Tb_classification> classificationList;
		if ("".equals(refid) || null == refid) {
			classificationList = classificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence(refid);
		} else {
			classificationList = classificationRepository.findByParentclassidOrderBySortsequence(refid);
		}
		// List<Tb_classification> classificationList =
		// classificationRepository.findByParentclassidOrderBySortsequence(refid);//寻找refid的子节点
		boolean leaf = classificationList.size() < 1;// 是否有子节点

		Tb_data_node data_node = new Tb_data_node();
		data_node.setNodename(nodename);
		data_node.setRefid(refid);
		data_node.setParentnodeid(parentid);
		data_node.setNodecode(nodecode);
		data_node.setLeaf(leaf);
		data_node.setNodelevel(level);
		data_node.setNodetype(2);
		data_node.setLuckstate("0");
		Integer maxOrder = dataNodeRepository.getMaxOrder();
		maxOrder = maxOrder == null ? 0 : maxOrder;
		data_node.setOrders(maxOrder + 10);
		data_node = dataNodeRepository.save(data_node);// 插入refid
		authorize(data_node);// 授权

		for (int i = 0; i < classificationList.size(); i++) {
			Tb_classification classification_temp = classificationList.get(i);
			addClassNodeLoop(classification_temp.getClassid(), joint(nodecode, i + 1 + ""), data_node.getNodeid(),
					level + 1, classification_temp.getClassname());
		}
		return data_node;
	}

	/**
	 * 删除所有
	 *
	 * @param nodeids
	 */
	public void deleteNodeAll(String[] nodeids) {
		for (String nodeid : nodeids) {
			deleteNode(nodeid);
		}
	}

	/**
	 * 删了该节点及子节点后，判断该节点的父节点是否还有子，没有则设置父节点为叶子节点
	 *
	 * @param nodeid
	 */
	public void deleteNode(String nodeid) {
		String parentid = findNodeById(nodeid).getParentnodeid();
		deleteLoop(nodeid);
		List<Tb_data_node> list;
		if (parentid == null || "".equals(parentid)) {
			list = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		} else {
			list = dataNodeRepository.findByParentnodeid(parentid);
		}
		if (parentid != null && list.size() < 1) {
			Tb_data_node data_node = findNodeById(parentid);
			data_node.setLeaf(true);
		}
	}

	/**
	 * 递归删除
	 *
	 * @param nodeid
	 */

	public void deleteLoop(String nodeid) {
		List<Tb_data_node> data_nodeList;
		if (nodeid == null || "".equals(nodeid)) {
			data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		} else {
			data_nodeList = dataNodeRepository.findByParentnodeid(nodeid);
		}
		for (Tb_data_node data_node : data_nodeList) {
			deleteLoop(data_node.getNodeid());
		}

		userDataNodeRepository.deleteByNodeid(nodeid);// 删除关联该节点的权限
		roleDataNodeRepository.deleteByNodeid(nodeid);
		templateRepository.deleteByNodeid(nodeid);
		codesetRepository.deleteByDatanodeid(nodeid);
		dataNodeRepository.deleteByNodeid(nodeid);
	}

	/**
	 * 递归获取当前节点的所有叶子节点nodeid
	 *
	 * @param nodeid
	 *            选择节点的nodeid
	 * @param ifContainSelfNode
	 *            确定是否查询当前节点下的非叶子节点及当前非叶子节点
	 * @param nodeidList
	 *            递归时用于传递nodeids集合,调用此方法时最后一个参数需传入没有任何元素的空集合
	 */

	public List<String> getNodeidLoop(String nodeid, boolean ifContainSelfNode, List<String> nodeidList) {
		List<Tb_data_node> dataNodeList = dataNodeRepository.findAllNodeidParentid();

		Map<String, String> parentmap = new HashMap<>();
		Map<String, String> parentsmap = new HashMap<>();
		dataNodeList.stream().forEach(datanode -> parentmap.put(datanode.getNodeid(), datanode.getParentnodeid()));
		dataNodeList.stream().forEach(
				datanode -> parentsmap.put(datanode.getNodeid(), getNodeParents(datanode.getNodeid(), parentmap)));

		List<String> nodeids = new ArrayList<>();
		parentsmap.forEach((key, value) -> {
			if (value.contains(nodeid)) {
				nodeids.add(key);
			}
		});
		return nodeids;
	}

	/**
	 * 获取当前节点的所有父级节点
	 * @param nodeid
	 * @param nodeiList
	 * @return
	 */
	public List<Tb_data_node> getParentidLoop(String nodeid, List<Tb_data_node> nodeiList) {
		Tb_data_node node = findNodeById(nodeid);
		if (node != null && node.getParentnodeid() != null && !"".equals(node.getParentnodeid())) {
			Tb_data_node parentNode = findNodeById(node.getParentnodeid());
			nodeiList.add(parentNode);
			return getParentidLoop(node.getParentnodeid(), nodeiList);
		}
		return nodeiList;
	}

	public List<Tb_data_node_sx> getSxParentidLoop(String nodeid, List<Tb_data_node_sx> nodeiList) {
		Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
		if (node != null && node.getParentnodeid() != null && !"".equals(node.getParentnodeid())) {
			Tb_data_node_sx parentNode = secondaryDataNodeRepository.findByNodeid(node.getParentnodeid());
			if(parentNode!=null){
				nodeiList.add(parentNode);
			}
			return getSxParentidLoop(node.getParentnodeid(), nodeiList);
		}
		return nodeiList;
	}

	public Map<String, Object[]> findAllParentOfNode() {
		Map<String, Object[]> resultMap = new HashMap<>();

		Map<String, Tb_data_node> nodemap = new HashMap<>();
		List<Tb_data_node> allNodes = findAllNodeList();
		Map<String, String> parentmap = new HashMap<>();
		allNodes.stream().forEach(dataNode -> {
			parentmap.put(dataNode.getNodeid(), dataNode.getParentnodeid());
			nodemap.put(dataNode.getNodeid(), dataNode);
		});

		nodemap.forEach((key, value) -> {
			String parents = getNodeParents(key, parentmap);
			List<Tb_data_node> parentlist = new ArrayList<>();
			for (String parent : parents.split(",")) {
				parentlist.add(nodemap.get(parent));
			}
			resultMap.put(key, new Object[] { value, parentlist });
		});

		return resultMap;
	}

	//声像库
	public Map<String, Object[]> findAllSecondaryParentOfNode() {
		Map<String, Object[]> resultMap = new HashMap<>();

		Map<String, Tb_data_node_sx> nodemap = new HashMap<>();
		List<Tb_data_node_sx> allSecondaryNodes = findSecondaryAllNodeList();
		Map<String, String> parentmap = new HashMap<>();
		allSecondaryNodes.stream().forEach(dataNode -> {
			parentmap.put(dataNode.getNodeid(), dataNode.getParentnodeid());
			nodemap.put(dataNode.getNodeid(), dataNode);
		});
		nodemap.forEach((key, value) -> {
			String parents = getNodeParents(key, parentmap);
			List<Tb_data_node_sx> parentlist = new ArrayList<>();
			for (String parent : parents.split(",")) {
				parentlist.add(nodemap.get(parent));
			}
			resultMap.put(key, new Object[] { value, parentlist });
		});

		return resultMap;
	}

	/**
	 * 根据节点id递归获取节点全名
	 *
	 * @param nodeid
	 *            选择节点的nodeid
	 * @param splitcode
	 *            分隔符
	 * @param nodefullname
	 *            递归时用于传递nodefullnameStr,调用此方法时最后一个参数需传入一个空串
	 * @return
	 */

	public String getNodefullnameLoop(String nodeid, String splitcode, String nodefullname) {
		String nodefullnameStr = "";
		Tb_data_node n=findNodeById(nodeid);
		String nodename = findNodeById(nodeid) == null ? "":findNodeById(nodeid).getNodename();
		if (nodefullname.length() == 0) {
			nodefullnameStr = nodename;
		} else {
			nodefullnameStr = nodename + splitcode + nodefullname;
		}
		String parentnodeid = findNodeById(nodeid) == null ? "" : findNodeById(nodeid).getParentnodeid();
		String grandparentnodeid = findNodeById(parentnodeid) == null ? "" :
				findNodeById(parentnodeid).getParentnodeid();
		if (grandparentnodeid == null || "".equals(grandparentnodeid.trim())) {
			nodefullnameStr = dataNodeRepository.findNodenameByNodeid(parentnodeid) + splitcode + nodefullnameStr;
		} else {
			nodefullnameStr = getNodefullnameLoop(parentnodeid, splitcode, nodefullnameStr);
		}
		return nodefullnameStr;
	}

	//递归找节点的全路径
	public String getNodefullnameLoop_new(String nodeid){
		String params = "";
		if(nodeid != null){
			//找节点对应的名称
			params = dataNodeRepository.findNodenameByNodeid(nodeid);
			//找父节点id下对应的名称
			String temp = getNodefullnameLoop_new(dataNodeRepository.findParentnodeidByNodeid(nodeid));
			if(!"".equals(temp) && temp != null){
				params = temp+"/"+params;
			}
		}
		return params;
	}

	/**
	 * 根据节点id递归获取节点全名  声像
	 *
	 * @param nodeid
	 *            选择节点的nodeid
	 * @param splitcode
	 *            分隔符
	 * @param nodefullname
	 *            递归时用于传递nodefullnameStr,调用此方法时最后一个参数需传入一个空串
	 * @return
	 */

	public String getSxNodefullnameLoop(String nodeid, String splitcode, String nodefullname) {
		String nodefullnameStr = "";
		Tb_data_node_sx dataNodeSx = secondaryDataNodeRepository.findByNodeid(nodeid);
		String nodename = dataNodeSx.getNodename();
		if (nodefullname.length() == 0) {
			nodefullnameStr = nodename;
		} else {
			nodefullnameStr = nodename + splitcode + nodefullname;
		}
		String parentnodeid = dataNodeSx.getParentnodeid();
		String grandparentnodeid = secondaryDataNodeRepository.findByNodeid(parentnodeid).getParentnodeid();
		if (grandparentnodeid == null || "".equals(grandparentnodeid.trim())) {
			nodefullnameStr = secondaryDataNodeRepository.findNodenameByNodeid(parentnodeid) + splitcode + nodefullnameStr;
		} else {
			nodefullnameStr = getSxNodefullnameLoop(parentnodeid, splitcode, nodefullnameStr);
		}
		return nodefullnameStr;
	}

	public Page<Tb_data_node> findBySearch(int page, int limit, String condition, String operator, String content,
										   String parentnodeid, String xtType, Sort sort) {
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(parentnodeid,xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(parentnodeid, childAllNodes);// 获取pcid下的首层权限

		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		if("声像系统".equals(xtType)){
			return findSxBySearch(condition, operator, content,firstLevelNodes,pageRequest);
		}else{
			Specification<Tb_data_node> searchid = new Specification<Tb_data_node>() {
				@Override
				public Predicate toPredicate(Root<Tb_data_node> root, CriteriaQuery<?> criteriaQuery,
											 CriteriaBuilder criteriaBuilder) {
					CriteriaBuilder.In ci = criteriaBuilder.in(root.get("nodeid"));
					if (firstLevelNodes.size() == 0) {
						ci.value("");
					} else {
						for (Tb_user_node_parents userNodeParents : firstLevelNodes) {
							ci.value(userNodeParents.getNodeid());
						}
					}
					return criteriaBuilder.or(ci);
				}
			};
			Specifications specifications = Specifications.where(searchid);

			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
			}
			return dataNodeRepository.findAll(specifications, pageRequest);
		}
	}

	public Page<Tb_data_node> findSxBySearch(String condition, String operator, String content,List<Tb_user_node_parents> firstLevelNodes,PageRequest pageRequest) {
		Specification<Tb_data_node_sx> searchid = new Specification<Tb_data_node_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_node_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In ci = criteriaBuilder.in(root.get("nodeid"));
				if (firstLevelNodes.size() == 0) {
					ci.value("");
				} else {
					for (Tb_user_node_parents userNodeParents : firstLevelNodes) {
						ci.value(userNodeParents.getNodeid());
					}
				}
				return criteriaBuilder.or(ci);
			}
		};
		Specifications specifications = Specifications.where(searchid);

		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return sxDataNodeRepository.findAll(specifications, pageRequest);
	}

	public void modifyNodeOrder(Tb_data_node node, int target) {
		if (node.getOrders() == null || node.getOrders() < target) {
			// 后移。1.将目标位置包括后面的所有数据后移一个位置；
			dataNodeRepository.modifyNodeOrder(target, Integer.MAX_VALUE);
		} else {
			// 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
			dataNodeRepository.modifyNodeOrder(target, node.getOrders());
		}
		// 2.将当前数据移到目标位置
		node.setOrders(target);
		dataNodeRepository.save(node);
	}

	public static Specification<Tb_data_node> getSearchParentnodeidCondition(String parentnodeid) {
		Specification<Tb_data_node> searchParentnodeidCondition = new Specification<Tb_data_node>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_node> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("parentnodeid"), parentnodeid);
				if (parentnodeid.equals("")) {
					Predicate np = criteriaBuilder.isNull(root.get("parentnodeid"));
					return criteriaBuilder.or(p, np);
				}
				return criteriaBuilder.or(p);
			}
		};
		return searchParentnodeidCondition;
	}

	/**
	 * 为当前用户及其同机构的安全保密员授予 新增节点 权限
	 *
	 * @param data_node
	 */
	private void authorize(Tb_data_node data_node) {
		// 为当前用户 授予 新增节点 权限
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_user_data_node userDataNode = new Tb_user_data_node();
		userDataNode.setUserid(userDetails.getUserid());
		userDataNode.setNodeid(data_node.getNodeid());
		userDataNodeRepository.save(userDataNode);

		// 为安全保密员授权 寻找与当前用户同机构的 同时属于安全保密管理员角色的人
		List<Tb_user> userList = userRepository.getUserByRolenameAndUserid("安全保密管理员", userDetails.getUserid());
		for (Tb_user user : userList) {
			if (!user.getUserid().equals(userDetails.getUserid())) {// 防止安全保密员增加机构
				userDataNode = new Tb_user_data_node();
				userDataNode.setUserid(user.getUserid());
				userDataNode.setNodeid(data_node.getNodeid());
				userDataNodeRepository.save(userDataNode);
			}
		}
	}

	/**
	 * 节点初始化的授权
	 *
	 * @param dataNodeList
	 */
	public void initializeNode(List<Tb_data_node> dataNodeList) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// 为安全保密员授权 寻找与当前用户同机构的 同时属于安全保密管理员角色的人
		List<Tb_user> userList = userRepository.getUserByRolenameAndUserid("安全保密管理员", userDetails.getUserid());
		userList.add(userRepository.findByUserid(userDetails.getUserid()));
		Tb_user_data_node userDataNode;
		List<Tb_user_data_node> list = new ArrayList<>();
		for (Tb_data_node dataNode : dataNodeList) {
			for (Tb_user user : userList) {
				userDataNode = new Tb_user_data_node();
				userDataNode.setNodeid(dataNode.getNodeid());
				userDataNode.setUserid(user.getUserid());
				list.add(userDataNode);
			}
		}
		userDataNodeRepository.save(list);
	}

	/**
	 * 节点初始化的授权  声像系统
	 *
	 * @param dataNodeList
	 */
	public void initializeSxNode(List<Tb_data_node_sx> dataNodeList) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// 为安全保密员授权 寻找与当前用户同机构的 同时属于安全保密管理员角色的人
		List<Tb_user> userList = userRepository.getUserByRolenameAndUserid("安全保密管理员", userDetails.getUserid());
		userList.add(userRepository.findByUserid(userDetails.getUserid()));
		Tb_user_data_node_sx userDataNode;
		List<Tb_user_data_node_sx> list = new ArrayList<>();
		for (Tb_data_node_sx dataNode : dataNodeList) {
			for (Tb_user user : userList) {
				userDataNode = new Tb_user_data_node_sx();
				userDataNode.setNodeid(dataNode.getNodeid());
				userDataNode.setUserid(user.getUserid());
				list.add(userDataNode);
			}
		}
		sxUserDataNodeRepository.save(list);
	}

	/**
	 * 获取当前用户拥有的所有节点权限
	 *
	 * @return
	 */
	public List<Tb_user_node_parents> getAllNodeOfCurUser(String xtType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_user_node_parents> allNodes;
		String userid=userDetails.getUserid();
		if("声像系统".equals(xtType)){
			allNodes = getSxAllNodeOfCurUser(userid);
		}else{
			List<Tb_data_node> dataNodeList =  findUserNodeList(userid);// 获得userId的所有权限节点
			List<Tb_data_node> dataNodeFromRoleList = findRoleNodeList(userid);// 获得userId的所有权限节点
			dataNodeFromRoleList.removeAll(dataNodeList);
			dataNodeList.addAll(dataNodeFromRoleList);// 分开查询优化速率，取并集
			List<Tb_data_node> nodeList;
			nodeList = findAllNodeList();
			Map<String, String> map = new HashMap<>();
			nodeList.stream().forEach(datanode -> map.put(datanode.getNodeid(), datanode.getParentnodeid()));
			allNodes = new ArrayList<>(dataNodeList.size());
			Tb_user_node_parents nodeParent;
			for (Tb_data_node datanode : dataNodeList) {
				nodeParent = new Tb_user_node_parents();
				nodeParent.setNodeid(datanode.getNodeid());
				nodeParent.setNodename(datanode.getNodename());
				nodeParent.setNodetype(datanode.getNodetype());
				nodeParent.setClasslevel(datanode.getClasslevel());
				nodeParent.setOrders(datanode.getOrders());
				nodeParent.setParents(getNodeParents(datanode.getNodeid(), map));
				nodeParent.setOrganid(datanode.getOrganid());
				allNodes.add(nodeParent);
			}
		}

		return allNodes;
	}

	List<Tb_user_node_parents> getSxAllNodeOfCurUser(String userid){
		List<Tb_data_node_sx> dataNodeList = secondaryDataNodeRepository.getSxMyAuth(userid);// 获得userId的所有权限节点
		List<Tb_data_node_sx> dataNodeFromRoleList = secondaryDataNodeRepository.getSxMyAuthFromRole(userid);// 获得userId的所有权限节点
		dataNodeFromRoleList.removeAll(dataNodeList);
		dataNodeList.addAll(dataNodeFromRoleList);// 分开查询优化速率，取并集
		List<Tb_data_node_sx> nodeList= secondaryDataNodeRepository.findSxAllNodeidParentid();
		Map<String, String> map = new HashMap<>();
		nodeList.stream().forEach(datanode -> map.put(datanode.getNodeid(), datanode.getParentnodeid()));
		List<Tb_user_node_parents> allNodes = new ArrayList<>(dataNodeList.size());
		Tb_user_node_parents nodeParent;
		for (Tb_data_node_sx datanode : dataNodeList) {
			nodeParent = new Tb_user_node_parents();
			nodeParent.setNodeid(datanode.getNodeid());
			nodeParent.setNodename(datanode.getNodename());
			nodeParent.setNodetype(datanode.getNodetype());
			nodeParent.setClasslevel(datanode.getClasslevel());
			nodeParent.setOrders(datanode.getOrders());
			nodeParent.setParents(getNodeParents(datanode.getNodeid(), map));
			allNodes.add(nodeParent);
		}
		return allNodes;
	}

	/**
	 * 获得pcid所有节点（方法块 分离出来 供多方调用）
	 *
	 * @param pcid
	 * @return
	 */
	public List<Tb_user_node_parents> getChildNodeOfPcid(String pcid,String xtType) {
		return allNodeOfParent(pcid, getAllNodeOfCurUser(xtType));// 获取pcid下的所有权限
	}

	public List<Tb_user_node_parents> getAllNodeOfOrgan(String pcid) {
		List<Tb_data_node> nodesListNow = new ArrayList<>();
		if("".equals(pcid)||pcid==null){
			nodesListNow = dataNodeRepository.findByParentnodeidIsNullOrParentnodeidOrderBySortsequence("");
		}else{
			nodesListNow = dataNodeRepository.findByParentnodeidOrderBySortsequence(pcid);
		}
		if(nodesListNow.get(0).getOrganid()!=null){
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Tb_data_node> dataNodeList = findUserNodeList(userDetails.getUserid());// 获得userId的所有权限节点
			List<Tb_data_node> dataNodeFromRoleList = findRoleNodeList(userDetails.getUserid());// 获得userId的所有权限节点
			dataNodeFromRoleList.removeAll(dataNodeList);
			dataNodeList.addAll(dataNodeFromRoleList);// 分开查询优化速率，取并集

			List<Tb_data_node> nodeList = findAllNodeList();;
			Map<String, String> map = new HashMap<>();
			nodeList.stream().forEach(datanode -> map.put(datanode.getNodeid(), datanode.getParentnodeid()));

			List<Tb_user_node_parents> allNodes = new ArrayList<>(dataNodeList.size());
			Tb_user_node_parents nodeParent;
			for (Tb_data_node datanode : dataNodeList) {
				nodeParent = new Tb_user_node_parents();
				nodeParent.setNodeid(datanode.getNodeid());
				nodeParent.setNodename(datanode.getNodename());
				nodeParent.setNodetype(datanode.getNodetype());
				nodeParent.setClasslevel(datanode.getClasslevel());
				nodeParent.setOrders(datanode.getOrders());
				nodeParent.setParents(getNodeParents(datanode.getNodeid(), map));
				allNodes.add(nodeParent);
			}
			return allNodes;
		}else{
			List<Tb_data_node> nodesListNew = new ArrayList<>();
			for (Tb_data_node node : nodesListNow) {
				if (node.getNodename().contains("未归管理") || node.getNodename().contains("卷内管理")){
					continue;
				}
				nodesListNew.add(node);
			}

			List<Tb_data_node> nodeList = dataNodeRepository.findAllNodeidParentid();
			Map<String, String> map = new HashMap<>();
			nodeList.stream().forEach(datanode -> map.put(datanode.getNodeid(), datanode.getParentnodeid()));

			List<Tb_user_node_parents> allNodes = new ArrayList<>(nodesListNew.size());
			Tb_user_node_parents nodeParent;
			for (Tb_data_node datanode : nodesListNew) {
				nodeParent = new Tb_user_node_parents();
				nodeParent.setNodeid(datanode.getNodeid());
				nodeParent.setNodename(datanode.getNodename());
				nodeParent.setNodetype(datanode.getNodetype());
				nodeParent.setClasslevel(datanode.getClasslevel());
				nodeParent.setOrders(datanode.getOrders());
				nodeParent.setParents(getNodeParents(datanode.getNodeid(), map));
				allNodes.add(nodeParent);
			}
			return allNodes;
		}
	}

	public List<Tb_user_node_parents> getChildNodeOfPcidOrgan(String pcid,String userid) {
		String organid = userRepository.findOrganidByUserid(userid);
		if("".equals(pcid)||pcid==null){
			return allNodeOfParent(pcid, getAllNodeOfOrgan(pcid));// 获取pcid下的所有权限
		} else {
			Tb_data_node node = findNodeById(pcid);
			if (node.getOrganid() != null) {
				boolean flag = false;
				Tb_right_organ organNow = rightOrganRepository.findByOrganid(node.getOrganid());
				if(organid.equals(organNow.getOrganid())){
					List<Tb_data_node> findAllNode = findAllNodeList();
					return getFindNodeOrgan(pcid, findAllNode,userid);
				}else {
					while (organNow.getOrgantype() != null && organNow.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
						organNow = rightOrganRepository.findOne(organNow.getParentid());
						if(organid.equals(organNow.getOrganid())){
							flag = true;
							break;
						}
					}
					if(flag){
						List<Tb_data_node> findAllNode = findAllNodeList();
						return getFindNodeOrgan(pcid, findAllNode,userid);
					}else{
						return allNodeOfParentOrganNode(pcid, getAllNodeOfOrgan(pcid), userid);// 获取pcid下的所有权限
					}
				}
			} else {
				return allNodeOfParentOrganNode(pcid, getAllNodeOfOrgan(pcid), userid);// 获取pcid下的所有权限
			}
		}
	}

	/**
	 * 获取Pcid下第一层节点
	 *
	 * @param pcid
	 *            父节点
	 * @return 首层节点
	 */
	public List getFirstLevelChildNode(String pcid, String xtType) {
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid, xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		String[] nodeids = new String[firstLevelNodes.size()];
		for (int i = 0; i < firstLevelNodes.size(); i++) {
			nodeids[i] = firstLevelNodes.get(i).getNodeid();
		}
		if("声像系统".equals(xtType)){
			List<String> nodeList = Arrays.asList(nodeids);
			List<Tb_data_node_sx> retutnData= sxDataNodeRepository.findAll().stream().filter(node -> nodeList.contains(node.getNodeid())).collect(Collectors.toList());;
			return retutnData;
		}
		return findNodeById(nodeids);
	}

	/**
	 * 获取所有节点，展开到指定的节点及其父节点
	 *
	 * @param id
	 * @return
	 */
	public List<NodesettingTree> getExpandNodeById(String id, String xtType) {
		// 所有节点
		List<NodesettingTree> oldNodeTreeList = getNodeByParentId("", false,xtType);
		NodesettingTree[] nodeTreeArray = new NodesettingTree[oldNodeTreeList.size()];
		oldNodeTreeList.toArray(nodeTreeArray);
		for (NodesettingTree nodetree : nodeTreeArray) {
			nodetree.setExpanded(false);// 收起所有
		}
		expandById(nodeTreeArray, id);// 展开
		return Arrays.asList(nodeTreeArray);
	}

	/**
	 * 获取所有节点，展开到指定的节点及其父节点（带多选框）
	 *
	 * @param id
	 * @return
	 */
	public List<ExtTree> getCheckExpandNodeByIds(String id,String xtType) {
		Set<String> nodeidSet = new HashSet<>();
		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid("",xtType);// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid("", childAllNodes);// 获取pcid下的首层权限
		ExtTree[] returnTree = getCheckNodeChildren(firstLevelNodes, childAllNodes, nodeidSet, null,xtType);// 获得树节点
		expandCheckByIds(returnTree,id);
		return Arrays.asList(returnTree);
	}

	/**
	 * 展开指定节点及其父节点
	 * @param nodeTree
	 * @param id
	 * @return
	 */
	public boolean expandCheckByIds(ExtTree[] nodeTree, String id) {
		for (ExtTree node : nodeTree) {
			if (id.equals(node.getFnid())) {
				node.setText("<span style = 'color:gray;'>"+node.getText()+"</span>");
				return true;
			} else if (node.getChildren() != null) {
				if (expandCheckByIds(node.getChildren(), id)) {
					node.setExpanded(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 展开指定节点及其父节点
	 *
	 * @param nodeTree
	 * @param id
	 * @return
	 */
	public boolean expandById(NodesettingTree[] nodeTree, String id) {
		for (NodesettingTree node : nodeTree) {
			if (id.equals(node.getFnid())) {
				return true;
			} else if (node.getChildren() != null) {
				if (expandById(node.getChildren(), id)) {
					node.setExpanded(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取pcid下未处理节点树 核心思路： 1、获得pcid下所有权限节点集合A
	 * 2、在集合A中寻找“显示在第一级的节点”（判断依据：该节点的所有父节点都不在集合A中）
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getWCLNodeByParentId(String pcid) {

		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,"");// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		// 获取其中的未处理节点
		NodesettingTree[] returnTree = getNodeChildren(firstLevelNodes, childAllNodes, true);// 获得树节点
		List<NodesettingTree> nodesetting_return = new ArrayList<>();
		if ("".equals(pcid)) {
			for (NodesettingTree nodetree : returnTree) {
				if (nodetree.getText().contains("未归管理")) {
					nodetree.setExpanded(true);
					nodesetting_return.add(nodetree);
				}
			}
		} else {
			nodesetting_return = Arrays.asList(returnTree);
		}

		return nodesetting_return;
	}

	/**
	 * 获取pcid下未处理节点树 核心思路： 1、获得pcid下所有权限节点集合A
	 * 2、在集合A中寻找“显示在第一级的节点”（判断依据：该节点的所有父节点都不在集合A中）
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getSzhWCLNodeByParentId(String pcid) {

		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,"");// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		// 获取其中的未处理节点
		NodesettingTree[] returnTree = getNodeChildren(firstLevelNodes, childAllNodes, true);// 获得树节点
		List<NodesettingTree> nodesetting_return = new ArrayList<>();
		if ("".equals(pcid)) {
			for (NodesettingTree nodetree : returnTree) {
				if (nodetree.getText().contains("未归管理")||nodetree.getText().contains("文件管理")||nodetree.getText().contains("资料管理")){
					continue;
				}
				nodesetting_return.add(nodetree);
			}
		} else {
			nodesetting_return = Arrays.asList(returnTree);
		}

		return nodesetting_return;
	}

	/**
	 * 获取pcid下未处理节点树 核心思路： 1、获得pcid下所有权限节点集合A
	 * 2、在集合A中寻找“显示在第一级的节点”（判断依据：该节点的所有父节点都不在集合A中）
	 *
	 * @param pcid
	 * @return
	 */
	public List<NodesettingTree> getYGDNodeByParentId(String pcid) {

		List<Tb_user_node_parents> childAllNodes = getChildNodeOfPcid(pcid,"");// 获取pcid下的所有权限
		if (childAllNodes == null || childAllNodes.size() == 0) {
			return null;
		}
		List<Tb_user_node_parents> firstLevelNodes = findTopNodeOfPcid(pcid, childAllNodes);// 获取pcid下的首层权限
		// 去掉其中的未处理节点
		NodesettingTree[] returnTree = getNodeChildren(firstLevelNodes, childAllNodes, true);// 获得树节点
		List<NodesettingTree> nodesetting_return = new ArrayList<>();
		if ("".equals(pcid)) {
			for (NodesettingTree nodetree : returnTree) {
				if (!nodetree.getText().contains("未归管理")) {
					nodetree.setExpanded(true);
					nodesetting_return.add(nodetree);
				}
			}
		} else {
			nodesetting_return = Arrays.asList(returnTree);
		}
		return nodesetting_return;
	}

	public NodesettingTree addKfTree(){
		NodesettingTree tree = new NodesettingTree();
		tree.setFnid("12345678910");
		tree.setText("库房模板");
		tree.setNodeType(2);
		tree.setCls("file");
		tree.setLeaf(true);
		tree.setExpanded(true);
		return tree;
	}

	public List<NodesettingTree> getAssemblyOrganByParentId(String pcid) {
		List<Tb_right_organ> organList = rightOrganRepository.getAllOrgan();

		Map<String, String> map = new HashMap<>();// organid-Parentid
		for (Tb_right_organ organ : organList) {
			map.put(organ.getOrganid(), organ.getParentid());
		}
		List<Tb_user_organ_parents> parents = new ArrayList<>();
		for (Tb_right_organ organ : organList) {
			Tb_user_organ_parents organParent = new Tb_user_organ_parents();
			organParent.setOrganid(organ.getOrganid());
			organParent.setOrganname(organ.getOrganname());

			String allParent = getOrganParents(organ.getOrganid(), map);
			organParent.setParents(allParent);
			parents.add(organParent);
		}

		List<Tb_user_organ_parents> userOrganParents = findTopOrganOfPcid(pcid, parents);// 获取pcid下的权限
		if (userOrganParents == null || userOrganParents.size() == 0) {
			return null;
		}
		NodesettingTree[] returnTree = getOrganChildren(parents, userOrganParents);
		return Arrays.asList(returnTree);
	}

	public List<String> getChildNodeId(String[] classifyId) {
		List<String> parentClassify = new LinkedList<>();
		List<String> classifyList = new ArrayList<>(Arrays.asList(classifyId));
		boolean flag = true;
		for (String classis : classifyId) {
			while (flag){
				List<String> classIds = classificationRepository.findClassIdByParent(classis);
				if(classIds != null && classIds.size() > 0){
					parentClassify.add(classis);
					flag = false;
				}else {
					flag = false;
				}
			}
			flag = true;
		}
		classifyList.removeAll(parentClassify);
		return classifyList;
	}

	//更新数据节点更新时间
	public void updateNodeChangeTime(){
		String nowdateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		nodeEditTime=nowdateStr;
	}

	private Tb_data_node findNodeById(String nodeid){
		for (Tb_data_node node : findAllNodeList()) {
			if (nodeid.equals(node.getNodeid())) {
				return node;
			}
		}
		return null;
	}

	private List<Tb_data_node> findNodeById(String[] nodeid){
		List<String> nodeList = Arrays.asList(nodeid);
		return findAllNodeList().stream().filter(node -> nodeList.contains(node.getNodeid())).collect(Collectors.toList());
	}

	/**
	 * 获取所有节点数据，使用缓存
	 * @return
	 */
	private CopyOnWriteArrayList<Tb_data_node> findAllNodeList(){
		if(GuavaCache.getValueByKey(GuavaUsedKeys.NODE_ALL_LIST) == null){
			CopyOnWriteArrayList<Tb_data_node> list = new CopyOnWriteArrayList<>();
			list.addAll(dataNodeRepository.findAll());
			GuavaCache.setKeyValue(GuavaUsedKeys.NODE_ALL_LIST, list);
		}
		return (CopyOnWriteArrayList<Tb_data_node>) GuavaCache.getValueByKey(GuavaUsedKeys.NODE_ALL_LIST);
	}

	/**
	 * 获取用户节点权限数据，使用缓存
	 * @param userid
	 * @return
	 */
	public List<Tb_data_node> findUserNodeList(String userid){
		String userUpdateTime=(String) GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_USER_TIME);//获取非缓存权限节点的时间
		Boolean flag=false;//true表示没有缓存
		if(GuavaCache.getValueByKey(GuavaUsedKeys.NODE_ALL_LIST) == null||GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_USER_LIST_SUFFIX) == null){//1.个人权限节点缓存为空
			flag=true;
		}/*else{
			List<Tb_data_node> nodeList=(List<Tb_data_node>)GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
			if(nodeList.size()==0){//权限节点为空
				flag=true;
			}
		}*/
		if(flag//1.个人权限节点缓存为空则重新设置缓存，
				||(!flag//2.个人权限节点缓存已存在，且节点更新时间比个人获取非缓存权限节点的时间大，则需要更新个人权限数据
				&&userUpdateTime!=null&&nodeEditTime!=null&&nodeEditTime.compareTo(userUpdateTime)>0)){
			CopyOnWriteArrayList<Tb_data_node> list = new CopyOnWriteArrayList<>();
			List<Tb_data_node> dataNodes = dataNodeRepository.getMyAuth(userid);
			//当查不到数据时，返回空集合，不保存在缓存里面
			if(dataNodes.size()==0){
				return new ArrayList<Tb_data_node>();
			}
			list.addAll(dataNodes);
			GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_USER_LIST_SUFFIX, list);

			//同时更新个人角色数据节点权限缓存,基本是两个权限节点一起查询的，获取非缓存的时间可以设为一致
			dataNodes = dataNodeRepository.getMyAuthFromRole(userid);
			//当查不到数据时，不保存在缓存里面
			if (dataNodes.size() > 0) {
				CopyOnWriteArrayList<Tb_data_node> roleList = new CopyOnWriteArrayList<>();
				roleList .addAll(dataNodes);
				GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX, roleList);
			}

			//设置获取非缓存权限节点的时间
			String nowdateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_USER_TIME, nowdateStr);
		}
		return (List<Tb_data_node>) GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
	}

	public List<Tb_data_node> findRoleNodeList(String userid){
		if(GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX) == null){
			CopyOnWriteArrayList<Tb_data_node> list = new CopyOnWriteArrayList<>();
			List<Tb_data_node> dataNodes = dataNodeRepository.getMyAuthFromRole(userid);
			//当查不到数据时，返回空集合，不保存在缓存里面
			if (dataNodes.size() == 0) {
				return new ArrayList<Tb_data_node>();
			}
			list.addAll(dataNodes);
			GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX, list);
		}
		return (List<Tb_data_node>) GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
	}

	//获取数据节点更新时间
	public String geteNodeChangeTime(){
		return nodeEditTime;
	}
}
