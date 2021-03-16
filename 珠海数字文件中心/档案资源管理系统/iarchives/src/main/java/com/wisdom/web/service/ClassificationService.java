package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_classification_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.web.controller.AcceptDirectoryController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tanly on 2017/11/2 0002.
 */
@Service
@Transactional
public class ClassificationService {
	@PersistenceContext
	EntityManager entityManager;
	@PersistenceContext(unitName="entityManagerFactorySecondary")
	EntityManager entityManagerSx;

	@Autowired
	ClassificationRepository classificationRepository;
	@Autowired
	SxClassificationRepository sxClassificationRepository;
	@Autowired
	SecondaryDataNodeRepository secondaryDataNodeRepository;
	@Autowired
	SxUserDataNodeRepository sxUserDataNodeRepository;
	@Autowired
	SxRoleDataNodeRepository sxRoleDataNodeRepository;
	@Autowired
	SxTemplateRepository sxTemplateRepository;
	@Autowired
	SxCodesetRepository sxCodesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	UserDataNodeRepository userDataNodeRepository;
	@Autowired
	TransdocRepository transdocRepository;
	@Autowired
	TransdocEntryRepository transdocEntryRepository;
	@Autowired
	RoleDataNodeRepository roleDataNodeRepository;
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	OpendocRepository opendocRepository;
	@Autowired
	OpenmsgRepository openmsgRepository;
	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;
	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;
	@Autowired
	EntryIndexAccessRepository entryIndexAccessRepository;
	@Autowired
	EntryIndexRepository entryIndexRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	CodesetRepository codesetRepository;
	@Autowired
	BillRepository billRepository;
	@Autowired
	BillApprovalRepository billApprovalRepository;
	@Autowired
	BillEntryIndexRepository billEntryIndexRepository;
	@Autowired
	EntryDetailRepository entryDetailRepository;
	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;
	@Autowired
	EntryDetailAccessRepository entryDetailAccessRepository;
	@Autowired
	EntryBookmarksRepository entryBookmarksRepository;
	@Autowired
	ElectronicSolidRepository electronicSolidRepository;
	@Autowired
	ElectronicLongRepository electronicLongRepository;
	@Autowired
	ElectronicCaptureRepository electronicCaptureRepository;
	@Autowired
	ElectronicRepository electronicRepository;
	@Autowired
	FullTextRepository fullTextRepository;
	@Autowired
	ElectronicRecyclebinRepository electronicRecyclebinRepository;
	@Autowired
	BorrowMsgRepository borrowMsgRepository;
	@Autowired
	DataNodeExtRepository dataNodeExtRepository;
	@Autowired
	TransdocPreviewRepository transdocPreviewRepository;

	@Value("${system.iarchivesx.syncpath}")
	private String iarchivesxSyncPath;//声像数据同步请求地址

	/**
	 * 增加分类预览
	 *
	 * @param classification
	 * @param parentclassid_real
	 * @return
	 */
	public List<NodesettingTree> addClassPreview(Tb_classification classification, String parentclassid_real,String xtType) {
		NodesettingTree[] newTree;
		if("声像系统".equals(xtType)){
			return addSxClassPreview(classification, parentclassid_real, xtType);
		}else{
			List<Tb_data_node> data_nodeList = new ArrayList<>();
			data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
			if (data_nodeList.size() == 0) {// 初始化时有可能为空
				return null;
			}
			if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式:添加1个分类节点+1套完整机构节点
				// List<Tb_classification> classList =
				// classificationRepository.findByParentclassid(parentclassid_real);
				List<Tb_classification> classList;
				if (parentclassid_real == null || "".equals(parentclassid_real)) {
					classList = classificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
				} else {
					classList = classificationRepository.findByParentclassid(parentclassid_real);
				}
				boolean isLeaf = classList.size() < 1;
				// 机构部分
				List<NodesettingTree> organTreeList = nodesettingService.getOrganByParentId("0",null);
				NodesettingTree[] oldOrganTree = new NodesettingTree[organTreeList.size()];
				organTreeList.toArray(oldOrganTree);
				NodesettingTree[] newOrganTree = oldOrganTree;
				if (!isLeaf) {
					newOrganTree = renderPreview(oldOrganTree, "<font color=green>");
				}
				// 添加的分类部分
				NodesettingTree classTree = new NodesettingTree();
				classTree.setFnid("");
				classTree.setText("<font color=green>" + classification.getClassname() + "</font>");
				classTree.setCls("folder");
				classTree.setLeaf(false);
				classTree.setChildren(newOrganTree);
				classTree.setExpanded(true);
				// 所有节点
				List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
				NodesettingTree[] nodeTree = new NodesettingTree[oldNodeTreeList.size()];
				oldNodeTreeList.toArray(nodeTree);
				for (NodesettingTree nodetree : nodeTree) {
					nodetree.setExpanded(false);// 收起
				}
				if (parentclassid_real == null || "".equals(parentclassid_real.trim())) {// 最高级分类
					newTree = Arrays.copyOf(nodeTree, nodeTree.length + 1);
					newTree[nodeTree.length] = classTree;
				} else {
					List<Tb_data_node> parentNodeOfClass = new ArrayList<>();
					parentNodeOfClass = dataNodeRepository.findByRefid(parentclassid_real);
					newTree = insertPreviewClass(nodeTree, classTree, parentNodeOfClass.get(0).getNodeid(), isLeaf);// 唯一
				}
			} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未处理
				return null;
			}
		}

		return Arrays.asList(newTree);
	}

	public List<NodesettingTree> addSxClassPreview(Tb_classification classification, String parentclassid_real,String xtType) {
		NodesettingTree[] newTree;
		List<Tb_data_node_sx> data_nodeList = secondaryDataNodeRepository.findSxByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return null;
		}
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式:添加1个分类节点+1套完整机构节点
			// List<Tb_classification> classList =
			// classificationRepository.findByParentclassid(parentclassid_real);
			List<Tb_classification_sx> classList;
			if (parentclassid_real == null || "".equals(parentclassid_real)) {
				classList = sxClassificationRepository.findSxByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			} else {
				classList = sxClassificationRepository.findSxByParentclassid(parentclassid_real);
			}
			boolean isLeaf = classList.size() < 1;
			// 机构部分
			List<NodesettingTree> organTreeList = nodesettingService.getOrganByParentId("0",null);
			NodesettingTree[] oldOrganTree = new NodesettingTree[organTreeList.size()];
			organTreeList.toArray(oldOrganTree);
			NodesettingTree[] newOrganTree = oldOrganTree;
			if (!isLeaf) {
				newOrganTree = renderPreview(oldOrganTree, "<font color=green>");
			}
			// 添加的分类部分
			NodesettingTree classTree = new NodesettingTree();
			classTree.setFnid("");
			classTree.setText("<font color=green>" + classification.getClassname() + "</font>");
			classTree.setCls("folder");
			classTree.setLeaf(false);
			classTree.setChildren(newOrganTree);
			classTree.setExpanded(true);
			// 所有节点
			List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
			NodesettingTree[] nodeTree = new NodesettingTree[oldNodeTreeList.size()];
			oldNodeTreeList.toArray(nodeTree);
			for (NodesettingTree nodetree : nodeTree) {
				nodetree.setExpanded(false);// 收起
			}
			if (parentclassid_real == null || "".equals(parentclassid_real.trim())) {// 最高级分类
				newTree = Arrays.copyOf(nodeTree, nodeTree.length + 1);
				newTree[nodeTree.length] = classTree;
			} else {
				List<Tb_data_node_sx> parentNodeOfClass = new ArrayList<>();
				parentNodeOfClass = secondaryDataNodeRepository.findSxByRefid(parentclassid_real);
				newTree = insertPreviewClass(nodeTree, classTree, parentNodeOfClass.get(0).getNodeid(), isLeaf);// 唯一
			}
		} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未处理
			return null;
		}
		return Arrays.asList(newTree);
	}

	/**
	 * 增加分类，预览时，渲染名称
	 *
	 * @param nodeTree
	 * @return
	 */
	public NodesettingTree[] renderPreview(NodesettingTree[] nodeTree, String renderFont) {
		for (NodesettingTree node : nodeTree) {
			node.setText(renderFont + node.getText() + "</font>");
			if (node.getChildren() != null) {
				renderPreview(node.getChildren(), renderFont);
			}
		}
		return nodeTree;
	}

	/**
	 * 增加分类，预览时，插入相应节点
	 *
	 * @param nodeTree
	 * @param classTree
	 * @param parentId
	 * @return
	 */
	public NodesettingTree[] insertPreviewClass(NodesettingTree[] nodeTree, NodesettingTree classTree, String parentId,
			boolean isLeaf) {
		for (NodesettingTree node : nodeTree) {
			if (node.getFnid().equals(parentId)) {
				if (isLeaf && node.getChildren().length != 0) {
					NodesettingTree[] newTree = new NodesettingTree[1];
					newTree[0] = classTree;
					node.setChildren(newTree);
					node.setExpanded(true);
				} else {
					NodesettingTree[] newTree = Arrays.copyOf(node.getChildren(), node.getChildren().length + 1);
					newTree[node.getChildren().length] = classTree;
					node.setChildren(newTree);
					node.setExpanded(true);
				}
				break;
			} else if (node.getChildren() != null) {
				insertPreviewClass(node.getChildren(), classTree, parentId, isLeaf);
				for (NodesettingTree child : node.getChildren()) {
					if (child.isExpanded()) {
						node.setExpanded(true);// 展开所有父级
						break;
					}
				}
			}
		}
		return nodeTree;
	}

	/**
	 * 添加分类，并同步生成数据节点，授权等等
	 *
	 * @param classification
	 * @param parentclassid_real
	 * @return
	 */
	public Tb_classification addClass(Tb_classification classification, String parentclassid_real, String xtType) {
		if("声像系统".equals(xtType)){
			return addSxClass(classification, parentclassid_real,xtType);
		}else{
			List<Tb_classification> classList;
			Integer orders;
			Tb_classification parentclass;
			if (parentclassid_real == null || "".equals(parentclassid_real.trim())) {
				classList = classificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
				orders = classificationRepository.findMaxOrdersByParentclassidOrNull(parentclassid_real);
			} else {
				classList = classificationRepository.findByParentclassid(parentclassid_real);
				orders = classificationRepository.findMaxOrdersByParentclassid(parentclassid_real);
				parentclass = classificationRepository.findByClassid(parentclassid_real);
			}
			boolean isLeaf = classList.size() < 1;
			classification.setParentclassid(parentclassid_real);
			orders = ((orders == null || orders < 0) ? 0 : orders) + 1;// 若同级节点的orders最大值为空或负数，则转化为0，再+1
			classification.setSortsequence(orders);
			Tb_classification class_return = classificationRepository.save(classification);// 添加分类
			// 分类排序
			updateCodelevel();
			//updateAjCodelevel();
			class_return = classificationRepository.findByClassid(class_return.getClassid());

			List<Tb_data_node> data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
			if (data_nodeList.size() == 0) {// 初始化时有可能为空
				return class_return;
			}
			List<Tb_data_node> saveNodeList = new ArrayList<>();
			if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式:添加1个分类节点+1套完整机构节点
				Tb_data_node parentNode = new Tb_data_node();
				int level;
				Tb_data_node datanode = new Tb_data_node();
				if ("".equals(class_return.getParentclassid().trim())) {// 最高级分类
					level = 1;
					String maxCode = dataNodeRepository.getMaxNodecodeByParentnodeidOrNull("");
					maxCode = maxCode.substring(maxCode.length() - 3);
					datanode.setNodecode(nodesettingService.joint("", Integer.parseInt(maxCode) + 1 + ""));
					datanode.setParentnodeid("");
				} else {
					parentNode = dataNodeRepository.findByRefid(class_return.getParentclassid()).get(0);// 本模式下结果唯一
					parentNode.setLeaf(false);
					level = (int) parentNode.getNodelevel() + 1;
					String maxCode;
					if (isLeaf) {
						maxCode = "001";
					} else {
						maxCode = dataNodeRepository.getMaxNodecodeByParentnodeid(parentNode.getNodeid());
						maxCode = maxCode.substring(maxCode.length() - 3);
					}
					datanode.setNodecode(
							nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
					datanode.setParentnodeid(parentNode.getNodeid());
				}
				datanode.setOrders(orders);//统一顺序

				datanode.setNodelevel(level);
				datanode.setNodename(class_return.getClassname());
				datanode.setNodetype(2);// 2:分类
				datanode.setRefid(class_return.getClassid());
				datanode.setClasslevel(class_return.getClasslevel());
				datanode.setClassid(class_return.getClassid());
				datanode.setLeaf(false);// 统一设置为非叶子节点，显示下级的机构
				datanode.setLuckstate("0");
				Tb_data_node data_node = dataNodeRepository.save(datanode);// 添加datanode,并获取返回对象的nodeid
				saveNodeList.add(data_node);// 收集，以便授权

				if (isLeaf && !"".equals(class_return.getParentclassid().trim())) {//一整套机构 移到 新增分类的下一级
					List<Tb_user_node_parents> allNodes = nodesettingService.getAllNodeOfCurUser("");
					List<Tb_user_node_parents> childAllNodes = nodesettingService.allNodeOfParent(parentNode.getNodeid(),
							allNodes);// 获取pcid下的所有权限
					String[] updateids = GainField.getFieldValues(childAllNodes, "nodeid").length == 0 ? new String[] { "" }
							: GainField.getFieldValues(childAllNodes, "nodeid");
					//更新机构节点的分类id，以及层次
					dataNodeRepository.moveOrganUpdate(updateids,data_node.getClassid(),classification.getClasslevel());
					List<Tb_data_node> topNodes = dataNodeRepository.findTopNodes(updateids);
					String[] updateparentids = GainField.getFieldValues(topNodes, "nodeid").length == 0
							? new String[] { "" } : GainField.getFieldValues(topNodes, "nodeid");
					dataNodeRepository.updateParentid(data_node.getNodeid(), updateparentids);
					String updateSql = "update tb_data_node set nodecode="
							+ DBCompatible.getInstance().updateNodeCode(updateids);
					entityManager.createNativeQuery(updateSql).executeUpdate();
				} else {
					List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
					saveNodeList = nodesettingService.saveOrganNode(childRightOrganList, level + 1, data_node.getNodeid(),
							data_node.getNodecode(), class_return.getClassid(), saveNodeList);// 拼接上机构
				}
				nodesettingService.initializeNode(saveNodeList);// 授权；分类设置只有最高级三员拥有，默认授权给最高级三员
			} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未测试
				List<Tb_data_node> parentNodeList;
				if ("".equals(class_return.getParentclassid().trim())) {// 最高级分类
					parentNodeList = dataNodeRepository.getOrganRemixNode();// 本模式下结果不唯一
				} else {
					parentNodeList = dataNodeRepository.findByRefid(class_return.getParentclassid());// 本模式下结果不唯一
				}
				for (Tb_data_node parentNode : parentNodeList) {
					parentNode.setLeaf(false);
					String maxCode = dataNodeRepository.getMaxNodecodeByParentnodeid(parentNode.getNodeid());
					maxCode = maxCode.substring(maxCode.length() - 3);
					Tb_data_node data_node = new Tb_data_node();
					data_node.setNodelevel(parentNode.getNodelevel() + 1);
					data_node.setNodecode(
							nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
					data_node.setNodename(class_return.getClassname());
					data_node.setNodetype(2);// 2：分类
					data_node.setParentnodeid(parentNode.getNodeid());
					data_node.setRefid(class_return.getClassid());
					Integer maxOrder = dataNodeRepository.getMaxOrder();
					maxOrder = maxOrder == null ? 0 : maxOrder;
					data_node.setOrders(maxOrder + 1);
					data_node.setLeaf(true);
					data_node.setClassid(class_return.getClassid());
					data_node.setOrganid(parentNode.getRefid());// 疑问//TODO
					data_node.setLuckstate("0");
					saveNodeList.add(data_node);
				}
				dataNodeRepository.save(saveNodeList);
				nodesettingService.initializeNode(saveNodeList);// 授权
			}
			return class_return;
		}

	}
	//更新声像系统分类
	public Tb_classification addSxClass(Tb_classification classification, String parentclassid_real,String xtType){
		List<com.wisdom.secondaryDataSource.entity.Tb_classification_sx> classList;
		Integer orders;
		Tb_classification_sx parentclass;
		Tb_classification_sx class_return=new Tb_classification_sx();
		if (parentclassid_real == null || "".equals(parentclassid_real.trim())) {
			classList = sxClassificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			orders = sxClassificationRepository.findMaxOrdersByParentclassidOrNull(parentclassid_real);
		} else {
			classList = sxClassificationRepository.findByParentclassid(parentclassid_real);
			orders = sxClassificationRepository.findMaxOrdersByParentclassid(parentclassid_real);
			parentclass = sxClassificationRepository.findByClassid(parentclassid_real);
		}
		boolean isLeaf = classList.size() < 1;
		classification.setParentclassid(parentclassid_real);
		orders = ((orders == null || orders < 0) ? 0 : orders) + 1;// 若同级节点的orders最大值为空或负数，则转化为0，再+1
		classification.setSortsequence(orders);
		BeanUtils.copyProperties(classification,class_return);
		class_return = sxClassificationRepository.save(class_return);// 添加分类
		// 分类排序
		//updateCodelevel();

		//class_return = classificationSxRepository.findByClassid(class_return.getClassid());

		List<Tb_data_node_sx> data_nodeList = secondaryDataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			BeanUtils.copyProperties(class_return,classification);
			classification.setCodelevel(iarchivesxSyncPath);
			return classification;
		}
		List<Tb_data_node_sx> saveNodeList = new ArrayList<>();
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式:添加1个分类节点+1套完整机构节点
			Tb_data_node_sx parentNode = new Tb_data_node_sx();
			int level;
			Tb_data_node_sx datanode = new Tb_data_node_sx();
			if ("".equals(class_return.getParentclassid().trim())) {// 最高级分类
				level = 1;
				String maxCode = secondaryDataNodeRepository.getMaxNodecodeByParentnodeidOrNull("");
				maxCode = maxCode.substring(maxCode.length() - 3);
				datanode.setNodecode(nodesettingService.joint("", Integer.parseInt(maxCode) + 1 + ""));
				datanode.setParentnodeid("");
			} else {
				parentNode = secondaryDataNodeRepository.findByRefid(class_return.getParentclassid()).get(0);// 本模式下结果唯一
				parentNode.setLeaf(false);
				level = (int) parentNode.getNodelevel() + 1;
				String maxCode;
				if (isLeaf) {
					maxCode = "001";
				} else {
					maxCode = secondaryDataNodeRepository.getMaxNodecodeByParentnodeid(parentNode.getNodeid());
					maxCode = maxCode.substring(maxCode.length() - 3);
				}
				datanode.setNodecode(
						nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
				datanode.setParentnodeid(parentNode.getNodeid());
			}
			datanode.setOrders(orders);//统一顺序

			datanode.setNodelevel(level);
			datanode.setNodename(class_return.getClassname());
			datanode.setNodetype(2);// 2:分类
			datanode.setRefid(class_return.getClassid());
			datanode.setClasslevel(class_return.getClasslevel());
			datanode.setClassid(class_return.getClassid());
			datanode.setLeaf(false);// 统一设置为非叶子节点，显示下级的机构
			datanode.setLuckstate("0");
			Tb_data_node_sx data_node = secondaryDataNodeRepository.save(datanode);// 添加datanode,并获取返回对象的nodeid
			saveNodeList.add(data_node);// 收集，以便授权

			if (isLeaf && !"".equals(class_return.getParentclassid().trim())) {//一整套机构 移到 新增分类的下一级
				List<Tb_user_node_parents> allNodes = nodesettingService.getAllNodeOfCurUser(xtType);
				List<Tb_user_node_parents> childAllNodes = nodesettingService.allNodeOfParent(parentNode.getNodeid(),
						allNodes);// 获取pcid下的所有权限
				String[] updateids = GainField.getFieldValues(childAllNodes, "nodeid").length == 0 ? new String[] { "" }
						: GainField.getFieldValues(childAllNodes, "nodeid");
				//更新机构节点的分类id，以及层次
				secondaryDataNodeRepository.moveOrganUpdate(updateids,data_node.getClassid(),classification.getClasslevel());
				List<Tb_data_node_sx> topNodes = secondaryDataNodeRepository.findTopNodes(updateids);
				String[] updateparentids = GainField.getFieldValues(topNodes, "nodeid").length == 0
						? new String[] { "" } : GainField.getFieldValues(topNodes, "nodeid");
				secondaryDataNodeRepository.updateParentid(data_node.getNodeid(), updateparentids);
				String updateSql = "update tb_data_node_sx set nodecode="
						+ DBCompatible.getInstance().updateNodeCode(updateids);
				entityManagerSx.createNativeQuery(updateSql).executeUpdate();
			} else {
				List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
				saveNodeList = nodesettingService.saveSxOrganNode(childRightOrganList, level + 1, data_node.getNodeid(),
						data_node.getNodecode(), class_return.getClassid(), saveNodeList,class_return.getClasslevel());// 拼接上机构
			}
			nodesettingService.initializeSxNode(saveNodeList);// 授权；分类设置只有最高级三员拥有，默认授权给最高级三员
		} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未测试
		}
		BeanUtils.copyProperties(class_return,classification);
		classification.setCodelevel(iarchivesxSyncPath);
		return classification;
	}

	/**
	 * 更新预览
	 *
	 * @param name
	 * @param id
	 * @return
	 */
	public List<NodesettingTree> updatePreview(String name, String id,String xtType) {
		// 所有节点
		List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
		NodesettingTree[] nodeTreeArray = new NodesettingTree[oldNodeTreeList.size()];
		oldNodeTreeList.toArray(nodeTreeArray);
		for (NodesettingTree nodetree : nodeTreeArray) {
			nodetree.setExpanded(false);// 收起
		}
		Set<String> nodeIdSet = new HashSet<>();
		if("声像系统".equals(xtType)){
			List<Tb_data_node_sx> dataNodeList = secondaryDataNodeRepository.findSxByRefid(id);// 更新关联的数据节点
			for (Tb_data_node_sx dataNode : dataNodeList) {
				nodeIdSet.add(dataNode.getNodeid());
			}
		}else{
			List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefid(id);// 更新关联的数据节点
			for (Tb_data_node dataNode : dataNodeList) {
				nodeIdSet.add(dataNode.getNodeid());
			}
		}
		nodeTreeArray = updateRenderPreview(nodeTreeArray, nodeIdSet, name);

		return Arrays.asList(nodeTreeArray);
	}

	/**
	 * 更新，预览时，渲染名称
	 *
	 * @param nodeTree
	 * @param nodeIdSet
	 * @param updateName
	 * @return
	 */
	public NodesettingTree[] updateRenderPreview(NodesettingTree[] nodeTree, Set<String> nodeIdSet, String updateName) {
		for (NodesettingTree node : nodeTree) {
			if (nodeIdSet.contains(node.getFnid())) {
				node.setText("<font color=#FF4500>" + updateName + "</font>");
				node.setExpanded(true);
				break;
			} else if (node.getChildren() != null) {
				updateRenderPreview(node.getChildren(), nodeIdSet, updateName);
				for (NodesettingTree child : node.getChildren()) {// 子节点有展开的，自己就展开
					if (child.isExpanded()) {
						node.setExpanded(true);// 展开所有父级
						break;
					}
				}
			}
		}
		return nodeTree;
	}

	/**
	 * 更新分类及其相关联数据
	 *
	 * @param classification
	 * @return
	 */
	public Tb_classification updateClass(Tb_classification classification, String parentclassid_real, String nodename, String xtType) {
		if("声像系统".equals(xtType)){
			return updateSxClass(classification);
		}else{
			Tb_classification returnClass = classificationRepository.save(classification);// 更新分类

			List<Tb_data_node> dataNodeList = dataNodeRepository.findByClassid(returnClass.getClassid());// 更新关联的数据节点
			for (Tb_data_node dataNode : dataNodeList) {
				if(returnClass.getClassid().trim().equals(dataNode.getRefid().trim())){
					dataNode.setNodename(returnClass.getClassname());// 更新分类名称；分类编码在数据节点中用不上
				}
				dataNode.setClasslevel(returnClass.getClasslevel());
				dataNodeRepository.save(dataNode);
			}
			return returnClass;
		}
	}

	/**
	 * 更新分类及其相关联数据  声像系统
	 *
	 * @param classification
	 * @return
	 */
	public Tb_classification updateSxClass(Tb_classification classification) {
		Tb_classification_sx returnClass=new Tb_classification_sx();
		BeanUtils.copyProperties(classification,returnClass);
		returnClass = sxClassificationRepository.save(returnClass);// 更新分类

		List<Tb_data_node_sx> dataNodeList = secondaryDataNodeRepository.findByClassid(returnClass.getClassid());// 更新关联的数据节点
		for (Tb_data_node_sx dataNode : dataNodeList) {
			if(returnClass.getClassid().trim().equals(dataNode.getRefid().trim())){
				dataNode.setNodename(returnClass.getClassname());// 更新分类名称；分类编码在数据节点中用不上
			}
			dataNode.setClasslevel(returnClass.getClasslevel());
			secondaryDataNodeRepository.save(dataNode);
		}
		BeanUtils.copyProperties(returnClass,classification);
		classification.setCodelevel(iarchivesxSyncPath);
		return classification;
	}

	/**
	 * 验证是否存在条目数据
	 *
	 * @param ids
	 * @return
	 */
	private final static int MAXINNUMBER=2000;//SqlServer in的最大参数值
	public boolean deleteValidate(String[] ids) throws InterruptedException, ExecutionException {
		boolean exist = false;
		List<Tb_user_node_parents> allUnp = new ArrayList<>();
		List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefidIn(ids);
		List<Tb_user_node_parents> unp = nodesettingService.getAllNodeOfCurUser("");
		dataNodeList.parallelStream().forEach(dataNode ->{
			unp.parallelStream().forEach(parent ->{
				if(parent.getParents().contains(dataNode.getNodeid())){
					allUnp.add(parent);
				}
			});
		});
		String[] value=GainField.getFieldValues(allUnp, "nodeid");
		String[] nodeIds =value.length == 0 ? new String[] { "" } : value;
		List<Tb_entry_index> entry_indexList =new ArrayList<>();
		if (nodeIds.length > MAXINNUMBER) {//超出最大值则批量查询
			int quotient = nodeIds.length / MAXINNUMBER;
			for (int i = 0; i <= quotient; i++) {
				int dataLength = (i + 1) * MAXINNUMBER > nodeIds.length ? nodeIds.length - i * MAXINNUMBER : MAXINNUMBER;
				String[] nids = new String[dataLength];
				System.arraycopy(nodeIds, i * MAXINNUMBER, nids, 0, dataLength);
				entry_indexList.addAll(entryIndexRepository.getByNodeidIn(nids));
			}
		}else {
			entry_indexList.addAll(entryIndexRepository.getByNodeidIn(nodeIds));
		}
		if (entry_indexList.size() > 0) {
			exist = true;
		} else {
			List<Tb_entry_index_capture> entry_index_captureList = new ArrayList<>();
			if (nodeIds.length > MAXINNUMBER) {//超出最大值则批量查询
				int quotient = nodeIds.length / MAXINNUMBER;
				for (int i = 0; i <= quotient; i++) {
					int dataLength = (i + 1) * MAXINNUMBER > nodeIds.length ? nodeIds.length - i * MAXINNUMBER : MAXINNUMBER;
					String[] nids = new String[dataLength];
					System.arraycopy(nodeIds, i * MAXINNUMBER, nids, 0, dataLength);
					entry_index_captureList.addAll(entryIndexCaptureRepository.getByNodeidIn(nids));
				}
			}else {
				entry_index_captureList.addAll(entryIndexCaptureRepository.getByNodeidIn(nodeIds));
			}
			if (entry_index_captureList.size() > 0) {
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * 删除分类预览
	 *
	 * @param ids
	 * @return
	 */
	public List<NodesettingTree> deletePreview(String[] ids,String xtType) {
		List<Tb_data_node> data_nodeList = new ArrayList<>();
		data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return null;
		}

		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			// 所有节点
			List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
			NodesettingTree[] nodeTreeArray = new NodesettingTree[oldNodeTreeList.size()];
			oldNodeTreeList.toArray(nodeTreeArray);
//			for (NodesettingTree nodetree : nodeTreeArray) {
//				nodetree.setExpanded(false);// 收起
//			}
			List<Tb_data_node> dataNodeList = new ArrayList<>();
			dataNodeList = dataNodeRepository.findByRefidIn(ids);
			dataNodeList.parallelStream().forEach(dataNode->{
				deleteRenderPreview(nodeTreeArray, dataNode.getNodeid(), false);
			});
			Tb_classification classification ;
			classification = classificationRepository.findByClassid(ids[0]);
			if (classification != null) {
				String pcid ;
				pcid = classificationRepository.findByClassid(ids[0]).getParentclassid();
				List<Tb_classification> classList;
				if (pcid == null || "".equals(pcid.trim())) {
					classList = classificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
				} else {
					classList = classificationRepository.findByParentclassid(pcid);
				}
				if ((pcid == null || !"".equals(pcid.trim())) && classList.size() == ids.length) {
					List<NodesettingTree> organTreeList = nodesettingService.getOrganByParentId("0",null);
					NodesettingTree[] oldOrganTree = new NodesettingTree[organTreeList.size()];
					organTreeList.toArray(oldOrganTree);
					NodesettingTree[] newOrganTree = renderPreview(oldOrganTree, "<font color=green>");
					String nodeid;
					nodeid=dataNodeRepository.findByRefid(pcid).get(0).getNodeid();
					delAllChildRender(nodeTreeArray, newOrganTree,nodeid);
				}
			}
			return Arrays.asList(nodeTreeArray);
		} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未测试
			return null;
		}
	}

	/**
	 * 删除分类预览  声像
	 *
	 * @param ids
	 * @return
	 */
	public List<NodesettingTree> deleteSxPreview(String[] ids,String xtType) {
		List<Tb_data_node_sx> data_nodeList = secondaryDataNodeRepository.findSxByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return null;
		}

		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			// 所有节点
			List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
			NodesettingTree[] nodeTreeArray = new NodesettingTree[oldNodeTreeList.size()];
			oldNodeTreeList.toArray(nodeTreeArray);
			for (NodesettingTree nodetree : nodeTreeArray) {
				nodetree.setExpanded(false);// 收起
			}

			List<Tb_data_node_sx> dataNodeList = new ArrayList<>();
			dataNodeList = secondaryDataNodeRepository.findSxByRefidIn(ids);
			for (Tb_data_node_sx dataNode : dataNodeList) {
				nodeTreeArray = deleteRenderPreview(nodeTreeArray, dataNode.getNodeid(), false);
			}
			Tb_classification_sx classification ;
			classification = sxClassificationRepository.findSxByClassid(ids[0]);
			if (classification != null) {
				String pcid ;
				pcid = sxClassificationRepository.findSxByClassid(ids[0]).getParentclassid();
				List<Tb_classification_sx> classList;
				if (pcid == null || "".equals(pcid.trim())) {
					classList = sxClassificationRepository.findSxByParentclassidIsNullOrParentclassidOrderBySortsequence("");
				} else {
					classList = sxClassificationRepository.findSxByParentclassid(pcid);
				}
				if ((pcid == null || !"".equals(pcid.trim())) && classList.size() == ids.length) {
					List<NodesettingTree> organTreeList = nodesettingService.getOrganByParentId("0",null);
					NodesettingTree[] oldOrganTree = new NodesettingTree[organTreeList.size()];
					organTreeList.toArray(oldOrganTree);
					NodesettingTree[] newOrganTree = renderPreview(oldOrganTree, "<font color=green>");
					String nodeid;
					nodeid=secondaryDataNodeRepository.findByRefid(pcid).get(0).getNodeid();
					delAllChildRender(nodeTreeArray, newOrganTree,nodeid);
				}
			}
			return Arrays.asList(nodeTreeArray);
		} else {// Organ-Class模式:每个机构下添加1个分类节点//尚未测试
			return null;
		}
	}

	/**
	 * 删除分类的所有子分类时 渲染添加的机构部分
	 * 
	 * @param nodeTree
	 * @param addOrganTree
	 * @param parentId
	 * @return
	 */
	public NodesettingTree[] delAllChildRender(NodesettingTree[] nodeTree, NodesettingTree[] addOrganTree,
			String parentId) {
		for (NodesettingTree node : nodeTree) {
			if (node.getFnid().equals(parentId)) {
				NodesettingTree[] newTree = new NodesettingTree[node.getChildren().length + addOrganTree.length];
				System.arraycopy(addOrganTree, 0, newTree, 0, addOrganTree.length);
				System.arraycopy(node.getChildren(), 0, newTree, addOrganTree.length, node.getChildren().length);
				node.setChildren(newTree);
				node.setExpanded(true);
				break;
			} else if (node.getChildren() != null) {
				delAllChildRender(node.getChildren(), addOrganTree, parentId);
				for (NodesettingTree child : node.getChildren()) {
					if (child.isExpanded()) {
						node.setExpanded(true);// 展开所有父级
						break;
					}
				}
			}
		}
		return nodeTree;
	}

	/**
	 * 删除分类，预览时，渲染名称
	 *
	 * @param nodeTree
	 * @param nodeId
	 * @param found
	 * @return
	 */
	public NodesettingTree[] deleteRenderPreview(NodesettingTree[] nodeTree, String nodeId, boolean found) {
		List<NodesettingTree> list=Arrays.asList(nodeTree);
		list.parallelStream().forEach(node->{
			if (found || node.getFnid().equals(nodeId)) {// 正好找到或者之前已经找到，开始渲染
				node.setText("<font color=red style='text-decoration: line-through'>" + node.getText() + "</font>");
				if (node.getFnid().equals(nodeId)) {
					node.setExpanded(true);// 只展开第一层
				}
				if (node.getChildren() != null) {
					deleteRenderPreview(node.getChildren(), nodeId, true);
				}
			} else if (node.getChildren() != null) {
				deleteRenderPreview(node.getChildren(), nodeId, false);// 还没找到，接着找
				for (NodesettingTree child : node.getChildren()) {
					if (child.isExpanded()) {
						node.setExpanded(true);// 展开所有父级
						break;
					}
				}
			}
		});
		return nodeTree;
	}

	/**
	 * 删除分类操作执行
	 *
	 * @param ids
	 */
	public Tb_classification deleteClassifications(String[] ids, String xtType) {
		if("声像系统".equals(xtType)){
			return deleteSxClassifications(ids, xtType);
		}else{
			String pcid = classificationRepository.findByClassid(ids[0]).getParentclassid();
			List<Tb_classification> classList;
			if (pcid == null || "".equals(pcid)) {
				classList = classificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
			} else {
				classList = classificationRepository.findByParentclassid(pcid);
			}

			for (String id : ids) {// 逐条执行删除操作
				deleteChildClassification(id);// 删除
			}
			String[] nodeIds = getAllNodeArray(ids,"");// 获取所有节点ID
			deleteAllByNodeids(nodeIds);

			// 删除分类后，其父分类是否没有子分类了，没有则添加一套机构
			if (pcid != null && !"".equals(pcid.trim()) && classList.size() == ids.length) {
				List<Tb_data_node> saveNodeList = new ArrayList<>();
				Tb_data_node pnode = dataNodeRepository.findByRefid(pcid).get(0);
				List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
				saveNodeList = nodesettingService.saveOrganNode(childRightOrganList,
						Integer.parseInt(pnode.getLevel() + "") + 1, pnode.getNodeid(), pnode.getNodecode(), pcid,
						saveNodeList);// 拼接上机构
				nodesettingService.initializeNode(saveNodeList);// 授权；分类设置只有最高级三员拥有，默认授权给最高级三员
			}
			Tb_classification classification=new Tb_classification();
			classification.setCodelevel(iarchivesxSyncPath);
			return classification;
		}
	}

	/**
	 * 删除分类操作执行  声像
	 *
	 * @param ids
	 */
	@Transactional(value = "transactionManagerSecondary")
	public Tb_classification deleteSxClassifications(String[] ids, String xtType) {
		String pcid = sxClassificationRepository.findByClassid(ids[0]).getParentclassid();
		List<Tb_classification_sx> classList;
		if (pcid == null || "".equals(pcid)) {
			classList = sxClassificationRepository.findByParentclassidIsNullOrParentclassidOrderBySortsequence("");
		} else {
			classList = sxClassificationRepository.findByParentclassid(pcid);
		}

		for (String id : ids) {// 逐条执行删除操作
			deleteChildSxClassification(id);// 删除
		}
		String[] nodeIds = getAllNodeArray(ids,xtType);// 获取所有节点ID
		if(nodeIds.length>0){
			deleteAllSxByNodeids(nodeIds);
		}
		// 删除分类后，其父分类是否没有子分类了，没有则添加一套机构
		if (pcid != null && !"".equals(pcid.trim()) && classList.size() == ids.length) {
			List<Tb_data_node_sx> saveNodeList = new ArrayList<>();
			Tb_data_node_sx pnode = secondaryDataNodeRepository.findByRefid(pcid).get(0);
			List<Tb_right_organ> childRightOrganList = rightOrganRepository.findByParentidOrderBySortsequence("0");
			saveNodeList = nodesettingService.saveSxOrganNode(childRightOrganList,
					Integer.parseInt(pnode.getLevel() + "") + 1, pnode.getNodeid(), pnode.getNodecode(), pcid,
					saveNodeList,pnode.getClasslevel());// 拼接上机构
			nodesettingService.initializeSxNode(saveNodeList);// 授权；分类设置只有最高级三员拥有，默认授权给最高级三员
		}
		Tb_classification classification=new Tb_classification();
		classification.setCodelevel(iarchivesxSyncPath);
		return classification;
	}

	/**
	 * 根据数据节点nodeIds 删除所有与节点相关数据
	 * 
	 * @param nodeIds
	 */
	public void deleteAllByNodeids(String[] nodeIds) {
		userDataNodeRepository.deleteAllByNodeidIn(nodeIds);
		List<Tb_transdoc> transdocs = transdocRepository.findByNodeidIn(nodeIds);
		transdocRepository.delete(transdocs);
		String[] docId=GainField.getFieldValues(transdocs, "docid");
		String[] docids = docId.length == 0 ? new String[] { "" } : docId;
		transdocEntryRepository.deleteAllByDocidIn(docids);
		transdocPreviewRepository.deleteAllByNodeidIn(nodeIds);
		roleDataNodeRepository.deleteAllByNodeidIn(nodeIds);
		reportRepository.deleteByNodeidIn(nodeIds);
		List<Tb_opendoc> opendocs = opendocRepository.findByNodeidIn(nodeIds);
		opendocRepository.delete(opendocs);
		String[] batch= GainField.getFieldValues(opendocs, "batchnum");
		String[] batchnums =batch.length == 0 ? new String[] { "" } : batch;
		openmsgRepository.deleteByBatchnumIn(batchnums);
		entryIndexTempRepository.deleteByNodeidIn(nodeIds);
		entryIndexCaptureRepository.deleteByNodeidIn(nodeIds);
		entryIndexAccessRepository.deleteByNodeidIn(nodeIds);
		templateRepository.deleteByNodeidIn(nodeIds);
		codesetRepository.deleteByDatanodeidIn(nodeIds);

		List<Tb_entry_index> entry_indexList = entryIndexRepository.getByNodeidIn(nodeIds);
		entryIndexRepository.delete(entry_indexList);
		String[] entry=GainField.getFieldValues(entry_indexList, "entryid");
		String[] entryids =entry .length == 0 ? new String[] { "" } : entry;
		entryDetailCaptureRepository.deleteByEntryidIn(entryids);
		entryDetailAccessRepository.deleteByEntryidIn(entryids);
		entryDetailRepository.deleteByEntryidIn(entryids);
		entryBookmarksRepository.deleteByEntryidIn(entryids);
		electronicSolidRepository.deleteByEntryidIn(entryids);
		electronicLongRepository.deleteByEntryidIn(entryids);
		electronicCaptureRepository.deleteByEntryidIn(entryids);

		electronicRepository.deleteByEntryidIn(entryids);
		List<Tb_electronic> electronicList = electronicRepository.findByEntryidIn(entryids);
		electronicRepository.delete(electronicList);
		String[] eleId=GainField.getFieldValues(electronicList, "eleid");
		String[] eleids =eleId .length == 0 ? new String[] { "" }
				: eleId;
		fullTextRepository.deleteByEleidIn(eleids);

		electronicRecyclebinRepository.deleteByEntryidIn(entryids);
		borrowMsgRepository.deleteByEntryidIn(entryids);
		List<Tb_bill> billList = billRepository.findByNodeidIn(nodeIds);
		billRepository.delete(billList);
		String[] bill=GainField.getFieldValues(billList, "billid");
		String[] billids = bill.length == 0 ? new String[] { "" } :bill;
		billApprovalRepository.deleteByBillidIn(billids);
		billEntryIndexRepository.deleteAllByBillidIn(billids);

		dataNodeRepository.deleteByNodeidIn(nodeIds);
	}

	/**
	 * 根据数据节点nodeIds 删除所有与节点相关数据  声像
	 *
	 * @param nodeIds
	 */
	@Transactional(value = "transactionManagerSecondary")
	public void deleteAllSxByNodeids(String[] nodeIds) {
		sxUserDataNodeRepository.deleteAllByNodeidIn(nodeIds);
		sxRoleDataNodeRepository.deleteAllByNodeidIn(nodeIds);//暂时屏蔽
		sxTemplateRepository.deleteByNodeidIn(nodeIds);
		sxCodesetRepository.deleteByDatanodeidIn(nodeIds);
		//dataNodeExtRepository.deleteByNodeidIn(nodeIds);
		secondaryDataNodeRepository.deleteByNodeidIn(nodeIds);
	}

	/**
	 * 递归删除分类
	 *
	 * @param id
	 */
	public void deleteChildClassification(String id) {
		List<Tb_classification> classificationList;
		if ("".equals(id) || null == id) {
			classificationList = classificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence(id);
		} else {
			classificationList = classificationRepository.findByParentclassidOrderBySortsequence(id);
		}
		for (Tb_classification classification : classificationList) {
			deleteChildClassification(classification.getClassid());
		}
		classificationRepository.deleteByClassid(id);// 直到找到没子分类的分类
		dataNodeExtRepository.deleteTb_data_node_mdaflagByClassid(id);
	}

	/**
	 * 递归删除分类  声像
	 *
	 * @param id
	 */
	public void deleteChildSxClassification(String id) {
		List<Tb_classification_sx> classificationList;
		if ("".equals(id) || null == id) {
			classificationList = sxClassificationRepository
					.findByParentclassidIsNullOrParentclassidOrderBySortsequence(id);
		} else {
			classificationList = sxClassificationRepository.findByParentclassidOrderBySortsequence(id);
		}
		for (Tb_classification_sx classification : classificationList) {
			deleteChildSxClassification(classification.getClassid());
		}
		sxClassificationRepository.deleteByClassid(id);// 直到找到没子分类的分类
	}

	/**
	 * 获取节点ID
	 *
	 * @param ids
	 * @return
	 */
	public String[] getAllNodeArray(String[] ids,String xtType) {
		List<Tb_user_node_parents> allNodes = nodesettingService.getAllNodeOfCurUser(xtType);

		List<Tb_user_node_parents> newList = new ArrayList<>();
		List<Tb_data_node> parentNodeList = new ArrayList<>();
		if("声像系统".equals(xtType)){
			return getSxAllNodeArray(ids,allNodes);
		}else{
			parentNodeList = dataNodeRepository.findByRefidIn(ids);
			for (Tb_data_node dataNode : parentNodeList) {
				Tb_user_node_parents parent = new Tb_user_node_parents();
				parent.setNodeid(dataNode.getNodeid());
				newList.add(parent);

				List<Tb_user_node_parents> childAllNodes = nodesettingService.allNodeOfParent(dataNode.getNodeid(),
						allNodes);// 获取pcid下的所有权限
				if (childAllNodes == null) {
					continue;
				}
				newList.addAll(childAllNodes);
			}
			return GainField.getFieldValues(newList, "nodeid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(newList, "nodeid");
		}

	}

	/**
	 * 获取声像节点ID
	 *
	 * @param ids
	 * @return
	 */
	public String[] getSxAllNodeArray(String[] ids,List<Tb_user_node_parents> allNodes) {
		List<Tb_user_node_parents> newList = new ArrayList<>();
		List<Tb_data_node_sx> parentNodeList = new ArrayList<>();
		parentNodeList = secondaryDataNodeRepository.findSxByRefidIn(ids);
		for (Tb_data_node_sx dataNode : parentNodeList) {
			Tb_user_node_parents parent = new Tb_user_node_parents();
			parent.setNodeid(dataNode.getNodeid());
			newList.add(parent);

			List<Tb_user_node_parents> childAllNodes = nodesettingService.allNodeOfParent(dataNode.getNodeid(),
					allNodes);// 获取pcid下的所有权限
			if (childAllNodes == null) {
				continue;
			}
			newList.addAll(childAllNodes);
		}
		return GainField.getFieldValues(newList, "nodeid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(newList, "nodeid");
	}

	public Page<Tb_classification> findBySearch(int page, int limit, String condition, String operator, String content,
			String classificationID, String xtType, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		Specification<Tb_classification> searchid = getSearchParentclassidCondition(classificationID);
		Specifications specifications = Specifications.where(searchid);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		if("声像系统".equals(xtType)){
			return sxClassificationRepository.findAll(specifications, pageRequest);
		}else{
			return getMediaNode(classificationRepository.findAll(specifications, pageRequest),pageRequest);
		}
	}

	//获取声像分类节点
	public Page<Tb_classification> getMediaNode(Page<Tb_classification> result,PageRequest pageRequest) {
		List<Tb_classification> content = result.getContent();
		long totalElements = result.getTotalElements();
		List<Tb_classification> returnResult = new ArrayList<>();
		for(Tb_classification classification:content){
			Tb_classification newClassification = new Tb_classification();
			BeanUtils.copyProperties(classification,newClassification);
			String state="否";
			Tb_data_node_mdaflag flag = dataNodeExtRepository.findNodeidByClassid(classification.getClassid());
			if(flag != null && flag.getIs_media() == 1){
				state="是";
			}
			newClassification.setIsMedia(state);
			returnResult.add(newClassification);
		}
		return new PageImpl(returnResult,pageRequest,totalElements);
	}

	public Page<Tb_classification> findByClassids(int page, int limit, String id, Sort sort) {
		List<String> classidList = new ArrayList<>();
		if (id != null && !id.equals("")) {
			for (int i = 0; i < id.split(",").length; i++) {
				classidList.add(id.split(",")[i]);
			}
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		return classificationRepository.findByClassid(classidList, pageRequest);
	}

	public void findBySortquence(String[] classid, int currentcount, String operate) {
		List<Tb_classification> classlist = classificationRepository.findByClassid(classid);
		Tb_classification upclass = classlist.get(currentcount);
		Tb_classification downclass = new Tb_classification();
		if (operate.equals("up")) {
			downclass = classlist.get(currentcount - 1);
		} else if (operate.equals("down")) {
			downclass = classlist.get(currentcount + 1);
		}
		int count = upclass.getSortsequence();
		upclass.setSortsequence(downclass.getSortsequence());
		dataNodeRepository.modifyOrderByRefid(downclass.getSortsequence(), upclass.getClassid());
		downclass.setSortsequence(count);
		dataNodeRepository.modifyOrderByRefid(count, downclass.getClassid());
	}

	public Tb_classification findClassification(String classid) {
		return classificationRepository.findByClassid(classid);
	}

	public void modifyClassOrder(Tb_classification classification, int target, String overid) {
		int sub = target - classificationRepository.findByClassid(overid).getSortsequence();// 0、1
		List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefid(classification.getClassid());
		if (classification.getSortsequence() == null || classification.getSortsequence() < target) {
			// 后移。1.将目标位置包括后面的所有数据后移一个位置；
			classificationRepository.modifyClassOrder(target, Integer.MAX_VALUE);
			for (Tb_data_node dataNode : dataNodeList) {
				dataNodeRepository.modifyOrganNodeOrderByParent(target + sub, Integer.MAX_VALUE, dataNode.getParentnodeid());
				dataNode.setSortsequence(target + sub);
			}
		} else {
			// 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
			classificationRepository.modifyClassOrder(target, classification.getSortsequence());
			for (Tb_data_node dataNode : dataNodeList) {
				dataNodeRepository.modifyOrganNodeOrderByParent(target + sub, classification.getSortsequence(), dataNode.getParentnodeid());
				dataNode.setSortsequence(target + sub);
			}
		}
		// 2.将当前数据移到目标位置
		classification.setSortsequence(target);
		classificationRepository.save(classification);
	}

	public static Specification<Tb_classification> getSearchParentclassidCondition(String classificationID) {
		Specification<Tb_classification> searchParentclassidCondition = new Specification<Tb_classification>() {
			@Override
			public Predicate toPredicate(Root<Tb_classification> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				if ("".equals(classificationID)) {
					Predicate[] predicates = new Predicate[2];
					predicates[0] = criteriaBuilder.equal(root.get("parentclassid"), classificationID);
					predicates[1] = criteriaBuilder.isNull(root.get("parentclassid"));
					return criteriaBuilder.or(predicates);
				} else {
					Predicate p = criteriaBuilder.equal(root.get("parentclassid"), classificationID);
					return criteriaBuilder.or(p);
				}

			}
		};
		return searchParentclassidCondition;
	}

	// 分类排序
	public void updateCodelevel() {
		List<Tb_classification> firstList = classificationRepository.findFirstLevel();
		if (firstList.isEmpty()) {
			return;
		}
		String classid;
		String codelevel;
		int secondLevSize = 0;
		for (int i = 0; i < firstList.size(); i++) {
			int a=0;
			//设置未归管理003、已归管理004、案卷管理002、文件管理006、资料管理005,其他6以上
			if ("未归管理".equals(firstList.get(i).getClassname())) {
				a=3;
			}else if ("已归管理".equals(firstList.get(i).getClassname())) {
				a=4;
			}else if ("案卷管理".equals(firstList.get(i).getClassname())) {
				a=2;
			}else if ("文件管理".equals(firstList.get(i).getClassname())) {
				a=6;
			}else if ("资料管理".equals(firstList.get(i).getClassname())) {
				a=5;
			}else{
				a=6+i;
			}
			classid = firstList.get(i).getClassid();
			codelevel = String.format("%0" + 3 + "d", a).trim();// 按3位在前边补0
			classificationRepository.updateCodeLevel(codelevel, classid);

			// 二级层级号要不一致
			List<Tb_classification> firstSub = classificationRepository.findSubLevel(classid);
			if (firstSub.size() > 0) {
				for (int j = 0; j < firstSub.size(); j++) {
					String subClassid = firstSub.get(j).getClassid();
					String subCodelevel = codelevel + "." + String.format("%0" + 3 + "d", j + 1 + secondLevSize).trim();
					classificationRepository.updateCodeLevel(subCodelevel, subClassid);
					updateSubCodelevel(subCodelevel, subClassid);
				}
				secondLevSize += firstSub.size();
			}

		}
	}

	// 案卷分类排序
	public void updateAjCodelevel() {
		List<Tb_classification> firstList = classificationRepository.findFirstLevel();
		if (firstList.isEmpty()) {
			return;
		}
		String classid;
		String codelevel;
		int secondLevSize = 0;
		for (int i = 0; i < firstList.size(); i++) {

			// 003案卷管理里边的一级子分类和002归档管理里边的一级子分类同分类名的二级层级号要一致
			if ("案卷管理".equals(firstList.get(i).getClassname())) {
				classid = firstList.get(i).getClassid();
				codelevel = String.format("%0" + 3 + "d", i + 1).trim();// 按3位在前边补0
				List<Tb_classification> YgdList = classificationRepository.findYgdFirstLevel();
				List<Tb_classification> listSub = classificationRepository.findSubLevel(classid);
				if (YgdList.isEmpty() || listSub.isEmpty()) {
					return;
				}
				for (Tb_classification ajzl : listSub) {
					for (Tb_classification gdzl : YgdList) {
						if (ajzl.getClassname().equals(gdzl.getClassname())) {// 二级层级号要一致
							String ajClassid = ajzl.getClassid();
							String ajCodelevel = gdzl.getCodelevel() == null ? "" : gdzl.getCodelevel();
							if (ajCodelevel.length() < 7) {
								continue;
							}
							ajCodelevel = codelevel + "." + gdzl.getCodelevel().substring(4, 7);
							classificationRepository.updateCodeLevel(ajCodelevel, ajClassid);
							// 最后给codelevel递归赋值
							updateSubCodelevel(ajCodelevel, ajClassid);
							break;
						}
					}
				}
			}

		}
	}

	public void updateSubCodelevel(String codelevel, String classid) {
		List<Tb_classification> listSub = classificationRepository.findSubLevel(classid);
		if (!listSub.isEmpty()) {
			for (int j = 0; j < listSub.size(); j++) {
				String subClassid = listSub.get(j).getClassid();
				String subCodelevel = codelevel + "." + String.format("%0" + 3 + "d", j + 1).trim();
				classificationRepository.updateCodeLevel(subCodelevel, subClassid);
				updateSubCodelevel(subCodelevel, subClassid);
			}
		} else {
			return;
		}
	}
}
