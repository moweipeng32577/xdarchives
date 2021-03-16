package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.util.MD5;
import com.wisdom.secondaryDataSource.entity.Tb_classification_sx;
import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.secondaryDataSource.entity.Tb_user_data_node_sx;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.codehaus.groovy.util.ListHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tanly on 2017/11/2 0002.
 */
@Service
@Transactional
public class OrganService {

	@Value("${system.iarchivesx.syncpath}")
	private String iarchivesxSyncPath;//声像数据同步请求地址

	@Autowired
	FundsRepository fundsRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	UserOrganRepository userOrganRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	UserNodeRepository userNodeRepository;
	@Autowired
	PersonalizedRepository personalizedRepository;
	@Autowired
	UserFunctionRepository userFunctionRepository;
	@Autowired
	DataNodeRepository dataNodeRepository;
	@Autowired
	ClassificationService classificationService;
	@Autowired
	NodesettingService nodesettingService;
	@Autowired
	UserDataNodeRepository userDataNodeRepository;
	@Autowired
	EntryIndexRepository entryIndexRepository;
	@Autowired
	ElectronicRepository electronicRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	CodesetRepository codesetRepository;
	@Autowired
	DataNodeSxRepository dataNodeSxRepository;
	@Autowired
	UserDataNodeSxRepository userDataNodeSxRepository;
	@Autowired
	TemplateSxRepository templateSxRepository;
	@Autowired
	CodesetSxRepository codesetSxRepository;

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
	SxUserRepository sxUserRepository;
	@Autowired
	SxRightOrganRepository sxRightOrganRepository;
	@Autowired
	SxUserOrganRepository sxUserOrganRepository;
	@Autowired
	SxFundsRepository sxFundsRepository;
	@Autowired
	SxUserRoleRepository sxUserRoleRepository;
	@Autowired
	SxUserGroupRepository sxUserGroupRepository;
	@Autowired
	SxUserNodeRepository sxUserNodeRepository;
	@Autowired
	SxPersonalizedRepository sxPersonalizedRepository;
	@Autowired
	SxUserFunctionRepository sxUserFunctionRepository;


	public Tb_right_organ addOrgan(Tb_right_organ right_organ, String parentid_real) {
		// 增加机构
		right_organ.setParentid(parentid_real);
		right_organ.setServicesid("402789f55d54dc21015d54dccadd0000");
		right_organ.setSystemid("402789f55d54e087015d54e0cfca0000");
		Integer orders = rightOrganRepository.findMaxOrdersByParentid(parentid_real);
		orders = ((orders == null || orders < 0) ? 0 : orders) + 1;// 若同级节点的orders最大值为空或负数，则转化为0，再+1
		right_organ.setSortsequence(orders);
		Tb_right_organ organ_return = rightOrganRepository.save(right_organ);// 增加机构

		//设置返回信息
		Tb_right_organ tro=new Tb_right_organ();
		BeanUtils.copyProperties(organ_return, tro);
		tro.setSystemname(iarchivesxSyncPath);//设置跳转地址

		// 更新机构等级001.001.002
		updateOrganlevel();
		//organ_return = rightOrganRepository.findByOrganid(organ_return.getOrganid());//处理oarcle返回的对应主键id不带空格问题
		Tb_funds funds = new Tb_funds();
		if ("unit".equals(organ_return.getOrgantype())) {
			funds.setOrganid(organ_return.getOrganid());
			funds.setOrganname(organ_return.getOrganname());
			funds=fundsRepository.save(funds);// 增加机构同时，产生一条全宗记录
		}

		// 机构授权
		organAutho(organ_return);

		// 添加相应节点
		List<Tb_data_node> data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			//return new ExtMsg(true, "增加成功", tro);
			return organ_return;
		}

		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式

			//增加相应的节点，模板，档号，节点的用户授权  档案
			addOrganDataNode(organ_return, parentid_real, orders);
			/*//增加相应的节点，模板，档号，节点的用户授权  声像
			addOrganDataNodeSx(organ_return, parentid_real, orders,funds);*/
			return organ_return;

		} else {// Organ-Class模式:尚未处理
			//return new ExtMsg(true, "增加成功", tro);
			return null;
		}
		//return new ExtMsg(true, "增加成功", tro);
	}

	//机构授权
	public void organAutho(Tb_right_organ organ_return){
		List<Tb_user> userList;
		if ("0".equals(organ_return.getParentid())) {// 最顶层机构，默认给最高级三员授权
			userList = userRepository.findByLoginnameIn(new String[] { "aqbm", "xitong" });
		} else {
			userList = userRepository.getUserFromUserOrgan(organ_return.getParentid());// 给拥有该节点的父节点的用户授权
		}
		List<Tb_user_organ> userOrganList = new ArrayList<>();
		for (Tb_user user : userList) {
			Tb_user_organ userOrgan = new Tb_user_organ();
			userOrgan.setUserid(user.getUserid());
			userOrgan.setOrganid(organ_return.getOrganid());
			userOrganList.add(userOrgan);
		}
		userOrganRepository.save(userOrganList);
	}

	//机构授权
	public void organSxAutho(Tb_right_organ_sx organ_return){
		List<Tb_user_sx> userList;
		if ("0".equals(organ_return.getParentid())) {// 最顶层机构，默认给最高级三员授权
			userList = sxUserRepository.findByLoginnameIn(new String[] { "aqbm", "xitong" });
		} else {
			userList = sxUserRepository.getUserFromUserOrgan(organ_return.getParentid());// 给拥有该节点的父节点的用户授权
		}
		List<Tb_user_organ_sx> userOrganList = new ArrayList<>();
		for (Tb_user_sx user : userList) {
			Tb_user_organ_sx userOrgan = new Tb_user_organ_sx();
			userOrgan.setUserid(user.getUserid());
			userOrgan.setOrganid(organ_return.getOrganid());
			userOrganList.add(userOrgan);
		}
		sxUserOrganRepository.save(userOrganList);
	}

	//添加相应节点，节点授权  档案
	public void addOrganDataNode(Tb_right_organ organ_return, String parentid_real, int orders){
		List<Tb_data_node> saveNodeList = new ArrayList<>();
		List<Tb_data_template> saveTemplateList = new ArrayList<>();
		List<Tb_codeset> codesetList = new ArrayList<>();

		List<Tb_data_node> parentNodeList;
		if ("0".equals(parentid_real.trim())) {// 最高级分类
			parentNodeList = dataNodeRepository.getParentOfFirstOrgan(parentid_real);
		} else {
			parentNodeList = dataNodeRepository.findByRefid(parentid_real);
		}
//			Integer maxOrder = dataNodeRepository.getMaxOrder();
//			maxOrder = maxOrder == null ? 0 : maxOrder;

		String[] parentAry = new String[parentNodeList.size()];
		parentNodeList.stream().map(datanode -> datanode.getNodeid()).collect(Collectors.toList()).toArray(parentAry);

		Set<String> hasTemplateSet = null;
		Set<String> hasCodesetSet = null;
		if (parentAry.length != 0){
			hasTemplateSet = templateRepository.getNodeidByNodeidIn(parentAry);//有模板的父节点
			hasTemplateSet = hasTemplateSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
			hasCodesetSet = codesetRepository.getNodeidByNodeidIn(parentAry);//有档号的父节点
			hasCodesetSet = hasCodesetSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
		}

		for (int i = 0; i < parentNodeList.size(); i++) {
			Tb_data_node parentNode = parentNodeList.get(i);
			String pnid = parentNode.getNodeid();
			parentNode.setLeaf(false);
			String maxCode = dataNodeRepository.getMaxNodecodeByParentnodeid(pnid);
			if (maxCode != null) {
				maxCode = maxCode.substring(maxCode.length() - 3);
			} else {
				maxCode = "0";
			}

			Tb_data_node data_node = new Tb_data_node();
			data_node.setNodelevel(parentNode.getNodelevel() + 1);
			data_node.setNodecode(
					nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
			data_node.setNodename(organ_return.getOrganname());
			data_node.setNodetype(1);// 1：机构
			data_node.setParentnodeid(pnid);
			data_node.setRefid(organ_return.getOrganid());
			data_node.setOrders(orders);//统一顺序
			data_node.setLeaf(true);
			data_node.setClassid(parentNode.getClassid());
			data_node.setOrganid(organ_return.getOrganid());
			data_node.setClasslevel(parentNode.getClasslevel());
			data_node.setLuckstate("0");//默认为不能修改
			data_node = dataNodeRepository.save(data_node);
			saveNodeList.add(data_node);

			if (hasTemplateSet.contains(pnid)) {//有模板
				List<Tb_data_template> tempList = templateRepository.findByNodeid(pnid);
				for (Tb_data_template temp : tempList) {
					Tb_data_template tdt = new Tb_data_template();
					BeanUtils.copyProperties(temp, tdt);
					tdt.setTemplateid(null);
					tdt.setNodeid(data_node.getNodeid());
					saveTemplateList.add(tdt);
				}
				if (hasCodesetSet.contains(pnid)) {//有档号
					List<Tb_codeset> codesetlist = codesetRepository.findByDatanodeidOrderByOrdernum(pnid);
					for (Tb_codeset cs : codesetlist) {
						Tb_codeset codeset = new Tb_codeset();
						BeanUtils.copyProperties(cs, codeset);
						codeset.setCodeid(null);
						codeset.setDatanodeid(data_node.getNodeid());
						codesetList.add(codeset);
					}
				}
			}
		}
		templateRepository.save(saveTemplateList);
		codesetRepository.save(codesetList);

		// 节点授权：凡拥有该节点的父节点的用户，同时拥有该节点
		List<Tb_user_data_node> list = new ArrayList<>();
		for (Tb_data_node dataNode : saveNodeList) {
			List<Tb_user> userOwnNode = userRepository.getUserFromUserNode(dataNode.getParentnodeid());// 拥有父节点的人
			Tb_user_data_node userDataNode;
			for (Tb_user user : userOwnNode) {
				userDataNode = new Tb_user_data_node();
				userDataNode.setNodeid(dataNode.getNodeid());
				userDataNode.setUserid(user.getUserid());
				list.add(userDataNode);
			}
		}
		userDataNodeRepository.save(list);
	}

	//添加相应节点，节点授权  声像
	@Transactional(value = "transactionManagerSecondary")
	public void addOrganDataNodeSx(Tb_right_organ organ_return, String parentid_real, int orders,Tb_funds funds){
		//保存新建的机构和全宗
		Tb_right_organ_sx organSx=addSxOrganAndFunds(organ_return, parentid_real, orders, funds);

		List<Tb_data_node_sx> saveNodeList = new ArrayList<>();
		List<Tb_data_template_sx> saveTemplateList = new ArrayList<>();
		List<Tb_codeset_sx> codesetList = new ArrayList<>();

		List<Tb_data_node_sx> parentNodeList;
		if ("0".equals(parentid_real.trim())) {// 最高级分类
			parentNodeList = secondaryDataNodeRepository.getParentOfFirstOrgan(parentid_real);
		} else {
			parentNodeList = secondaryDataNodeRepository.findByRefid(parentid_real);
		}
//			Integer maxOrder = dataNodeRepository.getMaxOrder();
//			maxOrder = maxOrder == null ? 0 : maxOrder;

		String[] parentAry = new String[parentNodeList.size()];
		parentNodeList.stream().map(datanode -> datanode.getNodeid()).collect(Collectors.toList()).toArray(parentAry);
		Set<String> hasTemplateSet = null;
		Set<String> hasCodesetSet = null;
		if(parentAry.length != 0){//防止参数为空时，出现InvalidDataAccessResourceUsageException
			hasTemplateSet = sxTemplateRepository.getNodeidByNodeidIn(parentAry);//有模板的父节点
			hasTemplateSet = hasTemplateSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
			hasCodesetSet = sxCodesetRepository.getNodeidByNodeidIn(parentAry);//有档号的父节点
			hasCodesetSet = hasCodesetSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
		}

		for (int i = 0; i < parentNodeList.size(); i++) {
			Tb_data_node_sx parentNode = parentNodeList.get(i);
			String pnid = parentNode.getNodeid();
			parentNode.setLeaf(false);
			String maxCode = secondaryDataNodeRepository.getMaxNodecodeByParentnodeid(pnid);
			if (maxCode != null) {
				maxCode = maxCode.substring(maxCode.length() - 3);
			} else {
				maxCode = "0";
			}

			Tb_data_node_sx data_node = new Tb_data_node_sx();
			data_node.setNodelevel(parentNode.getNodelevel() + 1);
			data_node.setNodecode(
					nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
			data_node.setNodename(organSx.getOrganname());
			data_node.setNodetype(1);// 1：机构
			data_node.setParentnodeid(pnid);
			data_node.setRefid(organSx.getOrganid());
			data_node.setOrders(orders);//统一顺序
			data_node.setLeaf(true);
			data_node.setClassid(parentNode.getClassid());
			data_node.setOrganid(organSx.getOrganid());
			data_node.setClasslevel(parentNode.getClasslevel());
			data_node.setLuckstate("0");//默认为不能修改
			data_node = secondaryDataNodeRepository.save(data_node);
			saveNodeList.add(data_node);

			if (hasTemplateSet.contains(pnid)) {//有模板
				List<Tb_data_template_sx> tempList = sxTemplateRepository.findByNodeid(pnid);
				for (Tb_data_template_sx temp : tempList) {
					Tb_data_template_sx tdt = new Tb_data_template_sx();
					BeanUtils.copyProperties(temp, tdt);
					tdt.setTemplateid(null);
					tdt.setNodeid(data_node.getNodeid());
					saveTemplateList.add(tdt);
				}
				if (hasCodesetSet.contains(pnid)) {//有档号
					List<Tb_codeset_sx> codesetlist = sxCodesetRepository.findByDatanodeidOrderByOrdernum(pnid);
					for (Tb_codeset_sx cs : codesetlist) {
						Tb_codeset_sx codeset = new Tb_codeset_sx();
						BeanUtils.copyProperties(cs, codeset);
						codeset.setCodeid(null);
						codeset.setDatanodeid(data_node.getNodeid());
						codesetList.add(codeset);
					}
				}
			}
		}
		sxTemplateRepository.save(saveTemplateList);
		sxCodesetRepository.save(codesetList);

		// 节点授权：凡拥有该节点的父节点的用户，同时拥有该节点
		List<Tb_user_data_node_sx> list = new ArrayList<>();
		for (Tb_data_node_sx dataNode : saveNodeList) {
			List<Tb_user_sx> userOwnNode = sxUserRepository.getUserFromSxUserNode(dataNode.getParentnodeid());// 拥有父节点的人
			Tb_user_data_node_sx userDataNode;
			for (Tb_user_sx user : userOwnNode) {
				userDataNode = new Tb_user_data_node_sx();
				userDataNode.setNodeid(dataNode.getNodeid());
				userDataNode.setUserid(user.getUserid());
				list.add(userDataNode);
			}
		}
		sxUserDataNodeRepository.save(list);
	}

	//增加声像的机构和全宗
	private Tb_right_organ_sx addSxOrganAndFunds(Tb_right_organ organ_return, String parentid_real, int orders,Tb_funds funds){
		Tb_right_organ_sx organSx=new Tb_right_organ_sx();
		// 增加机构
		/*organSx.setParentid(parentid_real);
		organSx.setServicesid("402789f55d54dc21015d54dccadd0000");
		organSx.setSystemid("402789f55d54e087015d54e0cfca0000");
		organSx.setSortsequence(orders);
		organSx.setOrganid(organ_return.getOrganid());
		organSx.setOrganname(organ_return.getOrganname());*/
		BeanUtils.copyProperties(organ_return,organSx);
		organSx = sxRightOrganRepository.save(organSx);// 增加机构

		//设置返回信息
		Tb_right_organ tro=new Tb_right_organ();
		BeanUtils.copyProperties(organ_return, tro);
		tro.setSystemname(iarchivesxSyncPath);//设置跳转地址

		// 更新机构等级001.001.002
		updateSxOrganlevel();
		//organ_return = rightOrganRepository.findByOrganid(organ_return.getOrganid());//处理oarcle返回的对应主键id不带空格问题
		Tb_funds_sx fundSx = new Tb_funds_sx();
		if ("unit".equals(organSx.getOrgantype())) {
			fundSx.setOrganid(organSx.getOrganid());
			fundSx.setOrganname(organSx.getOrganname());
			fundSx.setFundsid(funds.getFundsid());

			sxFundsRepository.save(fundSx);// 增加机构同时，产生一条全宗记录
		}

		// 机构授权
		organSxAutho(organSx);
		return organSx;
	}

	//同步档案的机构信息到声像系统
	public List<Tb_right_organ_sx> syncDaOrganToSx(){
		//先删除声像的所有机构信息，然后再同步档案的所有机构信息
		sxRightOrganRepository.deleteAllOrgan();
		List<Tb_right_organ> daList=rightOrganRepository.findAllByOrganlevel();
		List<Tb_right_organ_sx> sxList=new ArrayList<>();
		for(Tb_right_organ daOrgan:daList){
			Tb_right_organ_sx organSx=new Tb_right_organ_sx();
			BeanUtils.copyProperties(daOrgan,organSx);
			organSx = sxRightOrganRepository.save(organSx);// 增加机构
			sxList.add(organSx);
		}
		//生成全宗信息和授权
		authOrgan(sxList);
		return sxList;
	}

	//生成全宗信息和授权
	public void authOrgan(List<Tb_right_organ_sx> organSxList){
		sxFundsRepository.deleteAll();
		List<Tb_funds_sx> fundsList = new ArrayList<>();
		for (Tb_right_organ_sx organ_return : organSxList) {
			if ("unit".equals(organ_return.getOrgantype())) {
				Tb_funds_sx funds = new Tb_funds_sx();
				funds.setOrganid(organ_return.getOrganid());
				funds.setOrganname(organ_return.getOrganname());
				fundsList.add(funds);
			}
		}
		sxFundsRepository.save(fundsList);

		// 授权
		List<Tb_user_organ_sx> userOrganList = new ArrayList<>();
		List<Tb_user_sx> userList= sxUserRepository.findByLoginnameIn(new String[]{"aqbm", "xitong"});// 最顶层机构，默认给最高级三员授权
		for (Tb_right_organ_sx organ_return : organSxList) {
			for (Tb_user_sx user : userList) {
				Tb_user_organ_sx userOrgan = new Tb_user_organ_sx();
				userOrgan.setUserid(user.getUserid());
				userOrgan.setOrganid(organ_return.getOrganid());
				userOrganList.add(userOrgan);
			}
		}
		sxUserOrganRepository.save(userOrganList);
	}

	public ExtMsg addOrgan(Tb_right_organ right_organ, String parentid_real,String xtType) {
		// 增加机构
		right_organ.setParentid(parentid_real);
		right_organ.setServicesid("402789f55d54dc21015d54dccadd0000");
		right_organ.setSystemid("402789f55d54e087015d54e0cfca0000");
		Integer orders = rightOrganRepository.findMaxOrdersByParentid(parentid_real);
		orders = ((orders == null || orders < 0) ? 0 : orders) + 1;// 若同级节点的orders最大值为空或负数，则转化为0，再+1
		right_organ.setSortsequence(orders);
		Tb_right_organ organ_return = rightOrganRepository.save(right_organ);// 增加机构

		//设置返回信息
		Tb_right_organ tro=new Tb_right_organ();
		tro=organ_return;
		tro.setSystemname(iarchivesxSyncPath);//设置跳转地址

		// 更新机构等级001.001.002
		updateOrganlevel();
		//organ_return = rightOrganRepository.findByOrganid(organ_return.getOrganid());//处理oarcle返回的对应主键id不带空格问题
		if ("unit".equals(organ_return.getOrgantype())) {
			Tb_funds funds = new Tb_funds();
			funds.setOrganid(organ_return.getOrganid());
			funds.setOrganname(organ_return.getOrganname());
			fundsRepository.save(funds);// 增加机构同时，产生一条全宗记录
		}

		// 授权
		List<Tb_user_organ> userOrganList = new ArrayList<>();
		List<Tb_user> userList;
		if ("0".equals(organ_return.getParentid())) {// 最顶层机构，默认给最高级三员授权
			userList = userRepository.findByLoginnameIn(new String[] { "aqbm", "xitong" });
		} else {
			userList = userRepository.getUserFromUserOrgan(organ_return.getParentid());// 给拥有该节点的父节点的用户授权
		}
		for (Tb_user user : userList) {
			Tb_user_organ userOrgan = new Tb_user_organ();
			userOrgan.setUserid(user.getUserid());
			userOrgan.setOrganid(organ_return.getOrganid());
			userOrganList.add(userOrgan);
		}
		userOrganRepository.save(userOrganList);

		// 添加相应节点
		List<Tb_data_node> data_nodeList = new ArrayList<>();
		if("声像系统".equals(xtType)){
			data_nodeList=dataNodeRepository.findSxByParentnodeidIsNullOrParentnodeid("");
		}else{
			data_nodeList=dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		}
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return new ExtMsg(true, "增加成功", tro);
		}
		List<Tb_data_node> saveNodeList = new ArrayList<>();
		List<Tb_data_node_sx> saveNodeSxList = new ArrayList<>();
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			List<Tb_data_node> parentNodeList;
			if ("0".equals(parentid_real.trim())) {// 最高级分类
				if("声像系统".equals(xtType)){
					parentNodeList = dataNodeRepository.getSxParentOfFirstOrgan(parentid_real);
				}else{
					parentNodeList = dataNodeRepository.getParentOfFirstOrgan(parentid_real);
				}
			} else {
				if("声像系统".equals(xtType)){
					parentNodeList = dataNodeRepository.findSxByRefid(parentid_real);
				}else{
					parentNodeList = dataNodeRepository.findByRefid(parentid_real);
				}
			}
//			Integer maxOrder = dataNodeRepository.getMaxOrder();
//			maxOrder = maxOrder == null ? 0 : maxOrder;

			String[] parentAry = new String[parentNodeList.size()];
			parentNodeList.stream().map(datanode -> datanode.getNodeid()).collect(Collectors.toList()).toArray(parentAry);

			Set<String> hasTemplateSet =new HashSet<>();
			Set<String> hasCodesetSet =new HashSet<>();
			if("声像系统".equals(xtType)){
				hasTemplateSet = templateRepository.getSxNodeidByNodeidIn(parentAry);//有模板的父节点
				hasCodesetSet = codesetRepository.getSxNodeidByNodeidIn(parentAry);//有档号的父节点
			}else{
				hasTemplateSet = templateRepository.getNodeidByNodeidIn(parentAry);//有模板的父节点
				hasCodesetSet = codesetRepository.getNodeidByNodeidIn(parentAry);//有档号的父节点
			}
			hasTemplateSet = hasTemplateSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
			hasCodesetSet = hasCodesetSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());

			for (int i = 0; i < parentNodeList.size(); i++) {
				Tb_data_node parentNode = parentNodeList.get(i);
				String pnid = parentNode.getNodeid();
				parentNode.setLeaf(false);
				String maxCode ="0";
				if("声像系统".equals(xtType)){
					maxCode = dataNodeRepository.getSxMaxNodecodeByParentnodeid(pnid);
				}else{
					maxCode = dataNodeRepository.getMaxNodecodeByParentnodeid(pnid);
				}
				if (maxCode != null) {
					maxCode = maxCode.substring(maxCode.length() - 3);
				} else {
					maxCode = "0";
				}

				String nodeid="";
				if("声像系统".equals(xtType)){
					Tb_data_node_sx data_node = new Tb_data_node_sx();
					//data_node.setNodelevel(parentNode.getNodelevel() + 1);
					data_node.setNodecode(
							nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
					data_node.setNodename(organ_return.getOrganname());
					data_node.setNodetype(1);// 1：机构
					data_node.setParentnodeid(pnid);
					data_node.setRefid(organ_return.getOrganid());
					data_node.setOrders(orders);//统一顺序
					data_node.setLeaf(true);
					data_node.setClassid(parentNode.getClassid());
					data_node.setOrganid(organ_return.getOrganid());
					data_node.setClasslevel(parentNode.getClasslevel());
					data_node.setLuckstate("0");//默认为不能修改
					//data_node = dataNodeSxRepository.save(data_node);
					nodeid=data_node.getNodeid();
					saveNodeSxList.add(data_node);
				}else{
					Tb_data_node data_node = new Tb_data_node();
					data_node.setNodelevel(parentNode.getNodelevel() + 1);
					data_node.setNodecode(
							nodesettingService.joint(parentNode.getNodecode(), Integer.parseInt(maxCode) + 1 + ""));
					data_node.setNodename(organ_return.getOrganname());
					data_node.setNodetype(1);// 1：机构
					data_node.setParentnodeid(pnid);
					data_node.setRefid(organ_return.getOrganid());
					data_node.setOrders(orders);//统一顺序
					data_node.setLeaf(true);
					data_node.setClassid(parentNode.getClassid());
					data_node.setOrganid(organ_return.getOrganid());
					data_node.setClasslevel(parentNode.getClasslevel());
					data_node.setLuckstate("0");//默认为不能修改
					data_node = dataNodeRepository.save(data_node);
					nodeid=data_node.getNodeid();
					saveNodeList.add(data_node);
				}

				if (hasTemplateSet.contains(pnid)) {//有模板

					if("声像系统".equals(xtType)){
						List<Tb_data_template_sx> saveTemplateList = new ArrayList<>();
						List<Tb_data_template_sx> tempList = new ArrayList<>();
						tempList = sxTemplateRepository.findByNodeid(pnid);
						for (Tb_data_template_sx temp : tempList) {
							Tb_data_template_sx tdt = new Tb_data_template_sx();
							BeanUtils.copyProperties(temp, tdt);
							tdt.setTemplateid(null);
							tdt.setNodeid(nodeid);
							saveTemplateList.add(tdt);
						}
						sxTemplateRepository.save(saveTemplateList);
					}else{
						List<Tb_data_template> saveTemplateList = new ArrayList<>();
						List<Tb_data_template> tempList = new ArrayList<>();
						tempList = templateRepository.findByNodeid(pnid);
						for (Tb_data_template temp : tempList) {
							Tb_data_template tdt = new Tb_data_template();
							BeanUtils.copyProperties(temp, tdt);
							tdt.setTemplateid(null);
							tdt.setNodeid(nodeid);
							saveTemplateList.add(tdt);
						}
						templateRepository.save(saveTemplateList);
					}

					if (hasCodesetSet.contains(pnid)) {//有档号
						if("声像系统".equals(xtType)){
							List<Tb_codeset_sx> codesetList = new ArrayList<>();
							List<Tb_codeset_sx> codesetlist = new ArrayList<>();
							codesetlist = sxCodesetRepository.findByDatanodeidOrderByOrdernum(pnid);
							for (Tb_codeset_sx cs : codesetlist) {
								Tb_codeset_sx codeset = new Tb_codeset_sx();
								BeanUtils.copyProperties(cs, codeset);
								codeset.setCodeid(null);
								codeset.setDatanodeid(nodeid);
								codesetList.add(codeset);
							}
							sxCodesetRepository.save(codesetList);
						}else{
							List<Tb_codeset> codesetList = new ArrayList<>();
							List<Tb_codeset> codesetlist = new ArrayList<>();
							codesetlist = codesetRepository.findByDatanodeidOrderByOrdernum(pnid);
							for (Tb_codeset cs : codesetlist) {
								Tb_codeset codeset = new Tb_codeset();
								BeanUtils.copyProperties(cs, codeset);
								codeset.setCodeid(null);
								codeset.setDatanodeid(nodeid);
								codesetList.add(codeset);
							}
							codesetRepository.save(codesetList);
						}
					}
				}
			}

			// 节点授权：凡拥有该节点的父节点的用户，同时拥有该节点
			if("声像系统".equals(xtType)){
				List<Tb_user_data_node_sx> list = new ArrayList<>();
				for (Tb_data_node_sx dataNode : saveNodeSxList) {
					List<Tb_user> userOwnNode = new ArrayList<>();// 拥有父节点的人
					userOwnNode = userRepository.getSxUserFromUserNode(dataNode.getParentnodeid());// 拥有父节点的人
					Tb_user_data_node_sx userDataNode;
					for (Tb_user user : userOwnNode) {
						userDataNode = new Tb_user_data_node_sx();
						userDataNode.setNodeid(dataNode.getNodeid());
						userDataNode.setUserid(user.getUserid());
						list.add(userDataNode);
					}
				}
				sxUserDataNodeRepository.save(list);
			}else{
				List<Tb_user_data_node> list = new ArrayList<>();
				for (Tb_data_node dataNode : saveNodeList) {
					List<Tb_user> userOwnNode = new ArrayList<>();// 拥有父节点的人
					userOwnNode = userRepository.getUserFromUserNode(dataNode.getParentnodeid());// 拥有父节点的人
					Tb_user_data_node userDataNode;
					for (Tb_user user : userOwnNode) {
						userDataNode = new Tb_user_data_node();
						userDataNode.setNodeid(dataNode.getNodeid());
						userDataNode.setUserid(user.getUserid());
						list.add(userDataNode);
					}
				}
				userDataNodeRepository.save(list);
			}
		} else {// Organ-Class模式:尚未处理
			//return new ExtMsg(true, "增加成功", organ_return);
		}
		return new ExtMsg(true, "增加成功", tro);
	}

	public void importOrgan(List<Tb_right_organ> organReturnList, String parentid, Set<String> parentOrganidSet) {
		// 增加机构
		/*Map<String, Object> resMap = new ListHashMap<>();
		Integer orders = rightOrganRepository.findMaxOrdersByParentid(parentid);
		orders = ((orders == null || orders < 0) ? 0 : orders) + 1;
		for (int i = 0; i < organList.size(); i++) {
			Tb_right_organ right_organ = organList.get(i);
			right_organ.setParentid(parentid);
			right_organ.setServicesid("402789f55d54dc21015d54dccadd0000");
			right_organ.setSystemid("402789f55d54e087015d54e0cfca0000");
			right_organ.setSortsequence(orders + i);
			//记录日志
			logService.recordTextLog("机构管理","导入机构;##机构名："+right_organ.getOrganname()+";##机构描述："+right_organ.getDesciption());
		}

		List<Tb_right_organ> organReturnList = rightOrganRepository.save(organList);// 增加机构*/
		List<Tb_right_organ> organs=rightOrganRepository.findByOrganlevelNull();//查找机构层级为空的机构集合
		if(organs.size()>0){
			updateOrganlevel();// 更新机构等级001.001.002
		}

		List<Tb_funds> fundsList = new ArrayList<>();
		for (Tb_right_organ organ_return : organReturnList) {
			if ("unit".equals(organ_return.getOrgantype())) {
				Tb_funds funds = new Tb_funds();
				funds.setOrganid(organ_return.getOrganid());
				funds.setOrganname(organ_return.getOrganname());
				fundsList.add(funds);
			}
		}
		fundsRepository.save(fundsList);

		// 授权
		List<Tb_user_organ> userOrganList = new ArrayList<>();
		List<Tb_user> userList;
		if ("0".equals(parentid)) {// 最顶层机构，默认给最高级三员授权
			userList = userRepository.findByLoginnameIn(new String[]{"aqbm", "xitong"});
		} else {
			userList = userRepository.getUserFromUserOrgan(parentid);// 给拥有该节点的父节点的用户授权
		}
		for (Tb_right_organ organ_return : organReturnList) {
			for (Tb_user user : userList) {
				Tb_user_organ userOrgan = new Tb_user_organ();
				userOrgan.setUserid(user.getUserid());
				userOrgan.setOrganid(organ_return.getOrganid());
				userOrganList.add(userOrgan);
			}
		}
		userOrganRepository.save(userOrganList);

		// 添加相应节点

		List<Tb_data_node> data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			/*resMap.put("success", true);
			resMap.put("msg","导入成功");
			return resMap;*/
		}

		List<Tb_data_template> saveTemplateList = new ArrayList<>();
		List<Tb_codeset> codesetList = new ArrayList<>();
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			List<Tb_data_node> parentNodeList;
			if ("0".equals(parentid)) {// 最高级机构
				List<String> sunClsIdList=getClassLeaf();//叶子分类id集合
				String[] sunClsIdArr=sunClsIdList.toArray(new String[sunClsIdList.size()]);
				parentNodeList =dataNodeRepository.findByRefidIn(sunClsIdArr);//叶子分类数据节点
				//parentNodeList = dataNodeRepository.getParentOfFirstOrgan(parentid);//获取的是全部的顶级机构数据节点的父数据节点，可以考虑用所有的叶子分类数据节点
			} else {
				parentNodeList = dataNodeRepository.findByRefid(parentid);
			}

			String[] parentAry = new String[parentNodeList.size()];
			parentNodeList.stream().map(datanode -> datanode.getNodeid()).collect(Collectors.toList()).toArray(parentAry);

			Set<String> hasTemplateSet = templateRepository.getNodeidByNodeidIn(parentAry);//有模板的父节点
			hasTemplateSet = hasTemplateSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
			Set<String> hasCodesetSet = codesetRepository.getNodeidByNodeidIn(parentAry);//有档号的父节点
			hasCodesetSet = hasCodesetSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());

			for (int i = 0; i < parentNodeList.size(); i++) {//所有需要挂接层级的父机构节点
				Tb_data_node parentNode = parentNodeList.get(i);
				String pnid = parentNode.getNodeid();
				parentNode.setLeaf(false);
				String maxCode = dataNodeRepository.getMaxNodecodeByParentnodeid(pnid);
				if (maxCode != null) {
					maxCode = maxCode.substring(maxCode.length() - 3);
				} else {
					maxCode = "0";
				}

				List<Tb_data_template> tempList = templateRepository.findByNodeid(pnid);
				List<Tb_codeset> codesetlist = codesetRepository.findByDatanodeidOrderByOrdernum(pnid);
				List<Tb_data_node> saveNodeList = new ArrayList<>();
				Map<String,String> organNodeMap=new HashMap<>();//organid，nodeid  同一分类节点下的机构节点-数据节点
				Map<String,String> organNodecodeMap=new HashMap<>();//organid，nodecode  同一分类节点下的机构节点-数据节点
				for (int j = 0; j < organReturnList.size(); j++) {
					Tb_right_organ organ_return = organReturnList.get(j);
					Tb_data_node data_node = new Tb_data_node();
					data_node.setNodelevel(parentNode.getNodelevel() + 1);
					data_node.setNodename(organ_return.getOrganname());
					data_node.setNodetype(1);// 1：机构
					if(organNodeMap.containsKey(organ_return.getParentid())){//集合存在该机构的上级机构id，则表示为挂接机构的子机构层级
						data_node.setParentnodeid(organNodeMap.get(organ_return.getParentid()));
						data_node.setNodecode(nodesettingService.joint(organNodecodeMap.get(organ_return.getParentid()),  organ_return.getSortsequence() + ""));
					}else{//非子机构层级，设置为挂接的最上层节点
						data_node.setParentnodeid(pnid);
						data_node.setNodecode(nodesettingService.joint(parentNode.getNodecode(),  organ_return.getSortsequence() + ""));
					}
					data_node.setRefid(organ_return.getOrganid());
					//data_node.setOrders(orders + j);//统一顺序
					data_node.setOrders(organ_return.getSortsequence());//统一顺序
					if(parentOrganidSet.contains(organ_return.getOrganid())){//节点在父级机构id集合中
						data_node.setLeaf(false);//有子机构
					}else{
						data_node.setLeaf(true);
					}
					data_node.setClassid(parentNode.getClassid());
					data_node.setOrganid(organ_return.getOrganid());
					data_node.setClasslevel(parentNode.getClasslevel());
					data_node.setLuckstate("0");//默认为不能修改
					data_node = dataNodeRepository.save(data_node);
					organNodeMap.put(organ_return.getOrganid(),data_node.getNodeid());
					organNodecodeMap.put(organ_return.getOrganid(),data_node.getNodecode());
					saveNodeList.add(data_node);

					if (hasTemplateSet.contains(pnid)) {//有模板
						for (Tb_data_template temp : tempList) {
							Tb_data_template tdt = new Tb_data_template();
							BeanUtils.copyProperties(temp, tdt);
							tdt.setTemplateid(null);
							tdt.setNodeid(data_node.getNodeid());
							saveTemplateList.add(tdt);
						}
						if (hasCodesetSet.contains(pnid)) {//有档号
							for (Tb_codeset cs : codesetlist) {
								Tb_codeset codeset = new Tb_codeset();
								BeanUtils.copyProperties(cs, codeset);
								codeset.setCodeid(null);
								codeset.setDatanodeid(data_node.getNodeid());
								codesetList.add(codeset);
							}
						}
					}
				}

				// 每个分类节点下的新增机构节点授权：凡拥有该挂接节点的用户，同时拥有该新增节点权限
				List<Tb_user_data_node> list = new ArrayList<>();
				List<Tb_user> userOwnNode = userRepository.getUserFromUserNode(pnid);// 拥有挂接节点的人
				for (Tb_data_node dataNode : saveNodeList) {
					Tb_user_data_node userDataNode;
					for (Tb_user user : userOwnNode) {
						userDataNode = new Tb_user_data_node();
						userDataNode.setNodeid(dataNode.getNodeid());
						userDataNode.setUserid(user.getUserid());
						list.add(userDataNode);
					}
				}
				userDataNodeRepository.save(list);
			}
			templateRepository.save(saveTemplateList);
			codesetRepository.save(codesetList);

			/*// 节点授权：凡拥有该节点的父节点的用户，同时拥有该节点
			List<Tb_user_data_node> list = new ArrayList<>();
			for (Tb_data_node dataNode : saveNodeList) {
				List<Tb_user> userOwnNode = userRepository.getUserFromUserNode(dataNode.getParentnodeid());// 拥有父节点的人
				Tb_user_data_node userDataNode;
				for (Tb_user user : userOwnNode) {
					userDataNode = new Tb_user_data_node();
					userDataNode.setNodeid(dataNode.getNodeid());
					userDataNode.setUserid(user.getUserid());
					list.add(userDataNode);
				}
			}
			userDataNodeRepository.save(list);*/
		} else {// Organ-Class模式:尚未处理
			/*resMap.put("success", true);
			resMap.put("msg","导入成功");
			return resMap;*/
		}
		/*resMap.put("success", true);
		resMap.put("msg","导入成功");
		return resMap;*/
	}

	//获取叶子分类节点
	public List<String> getClassLeaf(){
		List<Tb_classification> classifications=classificationRepository.findAll();
		List<Tb_classification> sunClas;//子分类
		List<String> sunClsIdList=new ArrayList<>();//叶子分类id集合
		for(Tb_classification classification:classifications){
			sunClas=classificationRepository.findByCodelevel(classification.getCodelevel().trim()+".");
			if(sunClas.size()==0){//叶子分类
				sunClsIdList.add(classification.getClassid());
			}
		}

		return sunClsIdList;
	}

	//获取叶子分类节点 声像
	public List<String> getSxClassLeaf(){
		List<Tb_classification_sx> classifications=sxClassificationRepository.findAll();
		List<Tb_classification_sx> sunClas;//子分类
		List<String> sunClsIdList=new ArrayList<>();//叶子分类id集合
		for(Tb_classification_sx classification:classifications){
			sunClas=sxClassificationRepository.findByCodelevel(classification.getCodelevel().trim()+".");
			if(sunClas.size()==0){//叶子分类
				sunClsIdList.add(classification.getClassid());
			}
		}

		return sunClsIdList;
	}

	public void importSxOrgan(List<Tb_right_organ> organReturnList, String parentid, Set<String> parentOrganidSet) {

		// 增加机构
		List<Tb_right_organ_sx> organSxList=new ArrayList<>();
		for (int i = 0; i < organReturnList.size(); i++) {
			Tb_right_organ right_organ = organReturnList.get(i);
			Tb_right_organ_sx organSx=new Tb_right_organ_sx();
			BeanUtils.copyProperties(right_organ,organSx);
			organSxList.add(organSx);
			//记录日志
			//logService.recordTextLog("机构管理","导入机构;##机构名："+right_organ.getOrganname()+";##机构描述："+right_organ.getDesciption());
		}
		sxRightOrganRepository.save(organSxList);//保存声像机构

		//List<Tb_right_organ> organReturnList = rightOrganRepository.save(organList);


		List<Tb_right_organ_sx> organs=sxRightOrganRepository.findByOrganlevelNull();//查找机构层级为空的机构集合
		if(organs.size()>0){
			updateSxOrganlevel();// 更新机构等级001.001.002
		}

		List<Tb_funds_sx> fundsList = new ArrayList<>();
		for (Tb_right_organ_sx organ_return : organSxList) {
			if ("unit".equals(organ_return.getOrgantype())) {
				Tb_funds_sx funds = new Tb_funds_sx();
				funds.setOrganid(organ_return.getOrganid());
				funds.setOrganname(organ_return.getOrganname());
				fundsList.add(funds);
			}
		}
		sxFundsRepository.save(fundsList);

		// 授权
		List<Tb_user_organ_sx> userOrganList = new ArrayList<>();
		List<Tb_user_sx> userList;
		if ("0".equals(parentid)) {// 最顶层机构，默认给最高级三员授权
			userList = sxUserRepository.findByLoginnameIn(new String[]{"aqbm", "xitong"});
		} else {
			userList = sxUserRepository.getUserFromUserOrgan(parentid);// 给拥有该节点的父节点的用户授权
		}
		for (Tb_right_organ_sx organ_return : organSxList) {
			for (Tb_user_sx user : userList) {
				Tb_user_organ_sx userOrgan = new Tb_user_organ_sx();
				userOrgan.setUserid(user.getUserid());
				userOrgan.setOrganid(organ_return.getOrganid());
				userOrganList.add(userOrgan);
			}
		}
		sxUserOrganRepository.save(userOrganList);

		// 添加相应节点

		List<Tb_data_node_sx> data_nodeList =secondaryDataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			/*resMap.put("success", true);
			resMap.put("msg","导入成功");
			return resMap;*/
		}

		List<Tb_data_template_sx> saveTemplateList = new ArrayList<>();
		List<Tb_codeset_sx> codesetList = new ArrayList<>();
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			List<Tb_data_node_sx> parentNodeList;
			if ("0".equals(parentid)) {// 最高级机构
				List<String> sunClsIdList=getSxClassLeaf();//叶子分类id集合
				String[] sunClsIdArr=sunClsIdList.toArray(new String[sunClsIdList.size()]);
				parentNodeList =secondaryDataNodeRepository.findByRefidIn(sunClsIdArr);//叶子分类数据节点
				//parentNodeList = secondaryDataNodeRepository.getParentOfFirstOrgan(parentid);//获取的是全部的顶级机构数据节点的父数据节点，可以考虑用所有的叶子分类数据节点
			} else {
				parentNodeList = secondaryDataNodeRepository.findByRefid(parentid);
			}

			String[] parentAry = new String[parentNodeList.size()];
			parentNodeList.stream().map(datanode -> datanode.getNodeid()).collect(Collectors.toList()).toArray(parentAry);

			Set<String> hasTemplateSet = null;
			Set<String> hasCodesetSet = null;
			if (parentAry.length != 0){
				hasTemplateSet = sxTemplateRepository.getNodeidByNodeidIn(parentAry);//有模板的父节点
				hasTemplateSet = hasTemplateSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
				hasCodesetSet = sxCodesetRepository.getNodeidByNodeidIn(parentAry);//有档号的父节点
				hasCodesetSet = hasCodesetSet.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toSet());
			}

			for (int i = 0; i < parentNodeList.size(); i++) {//所有需要挂接层级的父机构节点
				Tb_data_node_sx parentNode = parentNodeList.get(i);
				String pnid = parentNode.getNodeid();
				parentNode.setLeaf(false);
				String maxCode = secondaryDataNodeRepository.getMaxNodecodeByParentnodeid(pnid);
				if (maxCode != null) {
					maxCode = maxCode.substring(maxCode.length() - 3);
				} else {
					maxCode = "0";
				}

				List<Tb_data_template_sx> tempList = sxTemplateRepository.findByNodeid(pnid);
				List<Tb_codeset_sx> codesetlist = sxCodesetRepository.findByDatanodeidOrderByOrdernum(pnid);
				List<Tb_data_node_sx> saveNodeList = new ArrayList<>();
				Map<String,String> organNodeMap=new HashMap<>();//organid，nodeid  同一分类节点下的机构节点-数据节点
				Map<String,String> organNodecodeMap=new HashMap<>();//organid，nodecode  同一分类节点下的机构节点-数据节点
				for (int j = 0; j < organSxList.size(); j++) {
					Tb_right_organ_sx organ_return = organSxList.get(j);
					Tb_data_node_sx data_node = new Tb_data_node_sx();
					data_node.setNodelevel(parentNode.getNodelevel() + 1);
					data_node.setNodename(organ_return.getOrganname());
					data_node.setNodetype(1);// 1：机构
					if(organNodeMap.containsKey(organ_return.getParentid())){//集合存在该机构的上级机构id，则表示为挂接机构的子机构层级
						data_node.setParentnodeid(organNodeMap.get(organ_return.getParentid()));
						data_node.setNodecode(nodesettingService.joint(organNodecodeMap.get(organ_return.getParentid()),  organ_return.getSortsequence() + ""));
					}else{//非子机构层级，设置为挂接的最上层节点
						data_node.setParentnodeid(pnid);
						data_node.setNodecode(nodesettingService.joint(parentNode.getNodecode(),  organ_return.getSortsequence() + ""));
					}
					data_node.setRefid(organ_return.getOrganid());
					//data_node.setOrders(orders + j);//统一顺序
					data_node.setOrders(organ_return.getSortsequence());//统一顺序
					if(parentOrganidSet.contains(organ_return.getOrganid())){//节点在父级机构id集合中
						data_node.setLeaf(false);//有子机构
					}else{
						data_node.setLeaf(true);
					}
					data_node.setClassid(parentNode.getClassid());
					data_node.setOrganid(organ_return.getOrganid());
					data_node.setClasslevel(parentNode.getClasslevel());
					data_node.setLuckstate("0");//默认为不能修改
					data_node = secondaryDataNodeRepository.save(data_node);
					organNodeMap.put(organ_return.getOrganid(),data_node.getNodeid());
					organNodecodeMap.put(organ_return.getOrganid(),data_node.getNodecode());
					saveNodeList.add(data_node);

					if (hasTemplateSet.contains(pnid)) {//有模板
						for (Tb_data_template_sx temp : tempList) {
							Tb_data_template_sx tdt = new Tb_data_template_sx();
							BeanUtils.copyProperties(temp, tdt);
							tdt.setTemplateid(null);
							tdt.setNodeid(data_node.getNodeid());
							saveTemplateList.add(tdt);
						}
						if (hasCodesetSet.contains(pnid)) {//有档号
							for (Tb_codeset_sx cs : codesetlist) {
								Tb_codeset_sx codeset = new Tb_codeset_sx();
								BeanUtils.copyProperties(cs, codeset);
								codeset.setCodeid(null);
								codeset.setDatanodeid(data_node.getNodeid());
								codesetList.add(codeset);
							}
						}
					}
				}

				// 每个分类节点下的新增机构节点授权：凡拥有该挂接节点的用户，同时拥有该新增节点权限
				List<Tb_user_data_node_sx> list = new ArrayList<>();
				List<Tb_user_sx> userOwnNode = sxUserRepository.getUserFromUserNode(pnid);// 拥有挂接节点的人
				for (Tb_data_node_sx dataNode : saveNodeList) {
					Tb_user_data_node_sx userDataNode;
					for (Tb_user_sx user : userOwnNode) {
						userDataNode = new Tb_user_data_node_sx();
						userDataNode.setNodeid(dataNode.getNodeid());
						userDataNode.setUserid(user.getUserid());
						list.add(userDataNode);
					}
				}
				sxUserDataNodeRepository.save(list);
			}
			sxTemplateRepository.save(saveTemplateList);
			sxCodesetRepository.save(codesetList);

		} else {// Organ-Class模式:尚未处理
			/*resMap.put("success", true);
			resMap.put("msg","导入成功");
			return resMap;*/
		}
		/*resMap.put("success", true);
		resMap.put("msg","导入成功");
		return resMap;*/
	}

	public Tb_right_organ updateOrgan(Tb_right_organ right_organ,String xtType) {
		right_organ=rightOrganRepository.save(right_organ);
		// 修改机构时，如果为部门信息修改成单位信息
		if ("unit".equals(right_organ.getOrgantype())) {
			Tb_funds fundsInfo = fundsRepository.findByOrganid(right_organ.getOrganid());
			if (fundsInfo == null) {// 产生一条全宗记录
				Tb_funds funds = new Tb_funds();
				funds.setOrganid(right_organ.getOrganid());
				funds.setOrganname(right_organ.getOrganname());
				fundsRepository.save(funds);
			} else {
				fundsRepository.modifyByOrganid(right_organ.getOrganname(), right_organ.getOrganid());
			}
		}
		// 更新关联的数据节点 档案
		List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefid(right_organ.getOrganid());
		for (Tb_data_node dataNode : dataNodeList) {
			dataNode.setNodename(right_organ.getOrganname());
			dataNodeRepository.save(dataNode);
		}

		/*//更新声像系统数据
		updateSxOrgan(right_organ);

		//设置返回信息
		Tb_right_organ tro=new Tb_right_organ();
		tro=right_organ;
		tro.setSystemname(iarchivesxSyncPath);//设置跳转地址*/
		return right_organ;
	}

	//更新声像机构
	@Transactional(value = "transactionManagerSecondary")
	public void updateSxOrgan(Tb_right_organ right_organ) {
		Tb_right_organ_sx organSx=new Tb_right_organ_sx();
		BeanUtils.copyProperties(right_organ,organSx);
		sxRightOrganRepository.save(organSx);

		// 修改机构时，如果为部门信息修改成单位信息
		if ("unit".equals(right_organ.getOrgantype())) {
			Tb_funds_sx fundsInfo = sxFundsRepository.findByOrganid(right_organ.getOrganid());
			if (fundsInfo == null) {// 产生一条全宗记录
				Tb_funds_sx funds = new Tb_funds_sx();
				funds.setOrganid(right_organ.getOrganid());
				funds.setOrganname(right_organ.getOrganname());
				sxFundsRepository.save(funds);
			} else {
				sxFundsRepository.modifyByOrganid(right_organ.getOrganname(), right_organ.getOrganid());
			}
		}

		// 更新关联的数据节点  声像
		List<Tb_data_node_sx> dataNodeSxList = secondaryDataNodeRepository.findByRefid(right_organ.getOrganid());
		for (Tb_data_node_sx dataNodeSx : dataNodeSxList) {
			dataNodeSx.setNodename(right_organ.getOrganname());
			secondaryDataNodeRepository.save(dataNodeSx);
		}
	}

	//删除数据节点
	public void deleteDatanode(String[] organidArr){
		dataNodeRepository.deleteByRefidIN(organidArr);
	}

	//删除数据节点声像
	public void deleteSxDatanode(String[] organidArr){
		secondaryDataNodeRepository.deleteByRefidIN(organidArr);
	}

	//启动线程删除机构节点关联数据
	/**
	 *
	 * @param organidArr  机构id
	 * @param nodeIds  数据节点id 档案
	 * @param nodeidSxArr 数据节点id  声像
	 *
	 */
	public void delOrganRef(String[] organidArr,String[] nodeIds, String[] nodeidSxArr) {
		Thread thread = new Thread(() -> {
			//删除档案机构关联数据
			delDaOrganRef(organidArr, nodeIds);
			//删除声像机构关联数据
			delSxOrganRef(organidArr, nodeidSxArr);
		});
		thread.start();
	}

	@Transactional
	public void delDaOrganRef(String[] organidArr, String[] nodeIds){
		for(String organid:organidArr){
			userOrganRepository.deleteByOrganid(organid);// 删除关联该机构的权限
			// 删除机构关联用户及其相关信息
			List<String> useridList = userRepository.findUseridByOrganid(organid);
			String[] ids = useridList.toArray(new String[useridList.size()]);
			if(ids.length>0){
				userFunctionRepository.deleteAllByUseridIn(ids);
				userRoleRepository.deleteAllByUseridIn(ids);
				userGroupRepository.deleteAllByUseridIn(ids);
				userDataNodeRepository.deleteAllByUseridIn(ids);
				userNodeRepository.deleteAllByUseridIn(ids);
				userOrganRepository.deleteAllByUseridIn(ids);
				personalizedRepository.deleteByUseridIn(ids);
				//userRepository.deleteAllByUseridIn(ids);
			}
		}

		// 删除所有节点相关数据  档案
		//String[] nodeIds = classificationService.getAllNodeArray(organidArr,"");
		// 删除所有节点相关数据
		if (nodeIds.length > 2000) {//超出2000则批量查询
			int quotient = nodeIds.length / 2000;
			for (int i = 0; i <= quotient; i++) {
				int dataLength = (i + 1) * 2000 > nodeIds.length ? nodeIds.length - i * 2000 : 2000;
				String[] nids = new String[dataLength];
				System.arraycopy(nodeIds, i * 2000, nids, 0, dataLength);
				classificationService.deleteAllByNodeids(nids);
			}
		}else {
			classificationService.deleteAllByNodeids(nodeIds);
		}

	}

	@Transactional(value = "transactionManagerSecondary")
	public void delSxOrganRef(String[] organidArr, String[] sxNodeIds){

		for(String organid:organidArr){
			// 直到找到没子机构的机构
			sxUserOrganRepository.deleteByOrganid(organid);// 删除关联该机构的权限

			// 删除用户及其相关信息
			List<String> useridList = sxUserRepository.findUseridByOrganid(organid);
			String[] ids = useridList.toArray(new String[useridList.size()]);
			if(ids.length>0){
				sxUserFunctionRepository.deleteAllByUseridIn(ids);
				sxUserRoleRepository.deleteAllByUseridIn(ids);
				sxUserGroupRepository.deleteAllByUseridIn(ids);
				sxUserDataNodeRepository.deleteAllByUseridIn(ids);
				sxUserNodeRepository.deleteAllByUseridIn(ids);
				sxUserOrganRepository.deleteAllByUseridIn(ids);
				sxPersonalizedRepository.deleteByUseridIn(ids);
				//sxUserRepository.deleteAllByUseridIn(ids);
			}
		}

		// 删除所有节点相关数据  声像
		//String[] sxNodeIds = classificationService.getAllNodeArray(ids,"声像系统");
		// 删除所有节点相关数据
		if(sxNodeIds.length==0){
			return;
		}
		if (sxNodeIds.length > 2000) {//超出2000则批量查询
			int quotient = sxNodeIds.length / 2000;
			for (int i = 0; i <= quotient; i++) {
				int dataLength = (i + 1) * 2000 > sxNodeIds.length ? sxNodeIds.length - i * 2000 : 2000;
				String[] nids = new String[dataLength];
				System.arraycopy(sxNodeIds, i * 2000, nids, 0, dataLength);
				classificationService.deleteAllSxByNodeids(nids);
			}
		}else{
			classificationService.deleteAllSxByNodeids(sxNodeIds);
		}

	}

	public void deleteOrgans(String[] ids,List<String> organidList) {
		for (String id : ids) {// 逐条执行删除操作
			deleteChildOrgan(id, organidList);// 删除
		}
		//删除相关数据节点中的相关机构节点
		//String[] organidArr=organidList.toArray(new String[organidList.size()]);
		//dataNodeRepository.deleteByRefidIN(organidArr);

		/*// 删除所有节点相关数据  档案
		String[] nodeIds = classificationService.getAllNodeArray(ids,"");
		// 删除所有节点相关数据
		if (nodeIds.length > 2000) {//超出2000则批量查询
			int quotient = nodeIds.length / 2000;
			for (int i = 0; i <= quotient; i++) {
				int dataLength = (i + 1) * 2000 > nodeIds.length ? nodeIds.length - i * 2000 : 2000;
				String[] nids = new String[dataLength];
				System.arraycopy(nodeIds, i * 2000, nids, 0, dataLength);
				classificationService.deleteAllByNodeids(nids);
			}
		}else {
			classificationService.deleteAllByNodeids(nodeIds);
		}*/
		//删除声像机构
		deleteSxOrgans(ids);
	}

	//删除声像机构
	@Transactional(value = "transactionManagerSecondary")
	public void deleteSxOrgans(String[] ids) {
		for (String id : ids) {// 逐条执行删除操作
			deleteSxChildOrgan(id);// 删除
		}

		//删除相关数据节点中的相关机构节点
		//secondaryDataNodeRepository.deleteByRefidIN(ids);

		/*// 删除所有节点相关数据  声像
		String[] sxNodeIds = classificationService.getAllNodeArray(ids,"声像系统");
		// 删除所有节点相关数据
		if (sxNodeIds.length > 2000) {//超出2000则批量查询
			int quotient = sxNodeIds.length / 2000;
			for (int i = 0; i <= quotient; i++) {
				int dataLength = (i + 1) * 2000 > sxNodeIds.length ? sxNodeIds.length - i * 2000 : 2000;
				String[] nids = new String[dataLength];
				System.arraycopy(sxNodeIds, i * 2000, nids, 0, dataLength);
				classificationService.deleteAllSxByNodeids(nids);
			}
		}else {
			classificationService.deleteAllSxByNodeids(sxNodeIds);
		}*/
	}

	@Transactional
	public void deleteChildOrgan(String id, List<String> organidList) {
		List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentidOrderBySortsequence(id);
		for (Tb_right_organ right_organ : rightOrganList) {
			deleteChildOrgan(right_organ.getOrganid(), organidList);
		}
		// 直到找到没子机构的机构
		//userOrganRepository.deleteByOrganid(id);// 删除关联该机构的权限

		// 删除用户及其相关信息
		List<String> useridList = userRepository.findUseridByOrganid(id);
		String[] ids = useridList.toArray(new String[useridList.size()]);
		/*if(ids.length>0){
			userFunctionRepository.deleteAllByUseridIn(ids);
			userRoleRepository.deleteAllByUseridIn(ids);
			userGroupRepository.deleteAllByUseridIn(ids);
			userDataNodeRepository.deleteAllByUseridIn(ids);
			userNodeRepository.deleteAllByUseridIn(ids);
			userOrganRepository.deleteAllByUseridIn(ids);
			personalizedRepository.deleteByUseridIn(ids);
		}*/
		if(ids.length > 0){
			userRepository.deleteAllByUseridIn(ids);
		}
		rightOrganRepository.deleteByOrganid(id);
		organidList.add(id);
	}

	//删除声像系统相关数据
	public void deleteSxChildOrgan(String id) {
		List<Tb_right_organ_sx> rightOrganList = sxRightOrganRepository.findByParentidOrderBySortsequence(id);
		for (Tb_right_organ_sx right_organ : rightOrganList) {
			deleteSxChildOrgan(right_organ.getOrganid());
		}
		// 直到找到没子机构的机构
		//sxUserOrganRepository.deleteByOrganid(id);// 删除关联该机构的权限

		// 删除用户及其相关信息4028812a74673d9b0174674135e739a9
		List<String> useridList = sxUserRepository.findUseridByOrganid(id);
		String[] ids = useridList.toArray(new String[useridList.size()]);
//		if(ids.length>0){
//			sxUserFunctionRepository.deleteAllByUseridIn(ids);
//			sxUserRoleRepository.deleteAllByUseridIn(ids);
//			sxUserGroupRepository.deleteAllByUseridIn(ids);
//			sxUserDataNodeRepository.deleteAllByUseridIn(ids);
//			sxUserNodeRepository.deleteAllByUseridIn(ids);
//			sxUserOrganRepository.deleteAllByUseridIn(ids);
//			sxPersonalizedRepository.deleteByUseridIn(ids);
//		}
		if(ids.length>0){
			sxUserRepository.deleteAllByUseridIn(ids);
		}
		sxRightOrganRepository.deleteByOrganid(id);
	}

	public Tb_right_organ findOrgan(String organid) {
		return rightOrganRepository.findByOrganid(organid);
	}

	public Page<Tb_right_organ> findBySearch(int page, int limit, String condition, String operator, String content,
			String id, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_user_organ> user_organList = userOrganRepository.findByUserid(userDetails.getUserid());
		String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_organList, "organid");


		Specification<Tb_right_organ> searchid = new Specification<Tb_right_organ>() {
			@Override
			public Predicate toPredicate(Root<Tb_right_organ> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate[] p = new Predicate[2];
				p[0] = criteriaBuilder.equal(root.get("parentid"), id);

				CriteriaBuilder.In ci = criteriaBuilder.in(root.get("organid"));
				if (organids.length == 0) {
					ci.value("");
				} else {
					for (String organ : organids) {
						ci.value(organ);
					}
				}
				p[1] = ci;
				return criteriaBuilder.and(p);
			}
		};
		Specifications specifications = Specifications.where(searchid);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return rightOrganRepository.findAll(specifications, pageRequest);
	}

	public Page<Tb_right_organ> findByOrganids(int page, int limit, String id, Sort sort) {
		List<String> organidList = new ArrayList<>();
		if (id != null && !id.equals("")) {
			for (int i = 0; i < id.split(",").length; i++) {
				organidList.add(id.split(",")[i]);
			}
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		return rightOrganRepository.findByOrganid(organidList, pageRequest);
	}

	public void findBySortquence(String[] organid, int currentcount, String operate) {
		List<Tb_right_organ> organlist = rightOrganRepository.findByOrganid(organid);
		Tb_right_organ uporgan = organlist.get(currentcount);
		Tb_right_organ downorgan = new Tb_right_organ();
		if (operate.equals("up")) {
			downorgan = organlist.get(currentcount - 1);
		} else if (operate.equals("down")) {
			downorgan = organlist.get(currentcount + 1);
		}
		int count = uporgan.getSortsequence();
		uporgan.setSortsequence(downorgan.getSortsequence());
		dataNodeRepository.modifyOrderByRefid(downorgan.getSortsequence(), uporgan.getOrganid());
		downorgan.setSortsequence(count);
		dataNodeRepository.modifyOrderByRefid(count, downorgan.getOrganid());
	}

	public void modifyOrganOrder(Tb_right_organ organ, int target, String overid) {
		int sub = target - rightOrganRepository.findByOrganid(overid).getSortsequence();// 0、1
		List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefid(organ.getOrganid());
		if (organ.getSortsequence() == null || organ.getSortsequence() < target) {
			// 后移。1.将目标位置包括后面的所有数据后移一个位置；
			rightOrganRepository.modifyOrganOrder(target, Integer.MAX_VALUE);
			for (Tb_data_node dataNode : dataNodeList) {
				dataNodeRepository.modifyOrganNodeOrderByParent(target + sub, Integer.MAX_VALUE, dataNode.getParentnodeid());
				dataNode.setSortsequence(target + sub);
			}
		} else {
			// 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
			rightOrganRepository.modifyOrganOrder(target, organ.getSortsequence());
			for (Tb_data_node dataNode : dataNodeList) {
				dataNodeRepository.modifyOrganNodeOrderByParent(target + sub, organ.getSortsequence(), dataNode.getParentnodeid());
				dataNode.setSortsequence(target + sub);
			}
		}
		// 2.将当前数据移到目标位置
		organ.setSortsequence(target);
		rightOrganRepository.save(organ);
	}

	/**
	 * 递归获取当前节点的所有叶子节点organid
	 *
	 * @param organid
	 *            选择节点的organid
	 * @param ifContainSelfNode
	 *            确定是否查询当前节点下的非叶子节点及当前非叶子节点
	 * @param organidList
	 *            递归时用于传递organids集合,调用此方法时最后一个参数需传入没有任何元素的空集合
	 */

	public List<String> getOrganidLoop(String organid, boolean ifContainSelfNode, List<String> organidList) {
		List<String> organids = new ArrayList<>();
		if (organidList.size() > 0) {// 方法被递归调用
			organids = organidList;// 将原集合的值赋给新建的集合
		}
		List<Tb_right_organ> data_nodeList = rightOrganRepository.findByParentidOrderBySortsequence(organid);// 查找子节点
		if (data_nodeList.size() == 0) {// 选择节点为叶子节点
			List<String> resultList = new ArrayList<>();
			resultList.add(organid);
			return resultList;
		}
		if (ifContainSelfNode) {
			organids.add(organid);// 添加所选非叶子节点及其包含的非叶子节点
		}
		for (Tb_right_organ right_organ : data_nodeList) {// 遍历选择节点的所有子节点
			List<Tb_right_organ> IsLeafOfOrganList = rightOrganRepository
					.findByParentidOrderBySortsequence(right_organ.getOrganid());
			if (!IsLeafOfOrganList.isEmpty()) {// 子节点为非叶子节点
				organids = getOrganidLoop(right_organ.getOrganid(), ifContainSelfNode, organids);
			} else {// 子节点为叶子节点
				organids.add(right_organ.getOrganid());
			}
		}
		return organids;
	}

	public List<String> getSxOrganidLoop(String organid, boolean ifContainSelfNode, List<String> organidList) {
		List<String> organids = new ArrayList<>();
		if (organidList.size() > 0) {// 方法被递归调用
			organids = organidList;// 将原集合的值赋给新建的集合
		}
		List<Tb_right_organ_sx> data_nodeList = sxRightOrganRepository.findByParentidOrderBySortsequence(organid);//
		// 查找子节点
		if (data_nodeList.size() == 0) {// 选择节点为叶子节点
			List<String> resultList = new ArrayList<>();
			resultList.add(organid);
			return resultList;
		}
		if (ifContainSelfNode) {
			organids.add(organid);// 添加所选非叶子节点及其包含的非叶子节点
		}
		for (Tb_right_organ_sx right_organ : data_nodeList) {// 遍历选择节点的所有子节点
			List<Tb_right_organ_sx> IsLeafOfOrganList = sxRightOrganRepository
					.findByParentidOrderBySortsequence(right_organ.getOrganid());
			if (!IsLeafOfOrganList.isEmpty()) {// 子节点为非叶子节点
				organids = getSxOrganidLoop(right_organ.getOrganid(), ifContainSelfNode, organids);
			} else {// 子节点为叶子节点
				organids.add(right_organ.getOrganid());
			}
		}
		return organids;
	}

	public Tb_funds addFunds(String organid, String organname) {
		Tb_funds funds = new Tb_funds();
		funds.setOrganid(organid);
		funds.setOrganname(organname);
		return fundsRepository.save(funds);
	}

	public String getCodeByUserid(String userid) {
		return rightOrganRepository.findCodeByUserid(userid);
	}

	public String findOrganByOrganid(String organid) {
		return rightOrganRepository.findOrganByOrganid(organid);
	}

	/**
	 * 增加机构 获取预览
	 *
	 * @param organName
	 * @param parentId
	 * @return
	 */
	public List<NodesettingTree> addOrganPreview(String organName, String parentId,String xtType) {
		List<Tb_data_node> data_nodeList = new ArrayList<>();
		if("声像系统".equals(xtType)){
			return addSxOrganPreview(organName, parentId, xtType);
		}else{
			data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
			if (data_nodeList.size() == 0) {// 初始化时有可能为空
				return null;
			}

			NodesettingTree[] newTree;
			if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
				List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
				NodesettingTree[] nodeTree = new NodesettingTree[oldNodeTreeList.size()];
				oldNodeTreeList.toArray(nodeTree);
				for (NodesettingTree nodetree : nodeTree) {
					nodetree.setExpanded(false);// 收起
				}
				List<Tb_data_node> parentNodeOfClass;
				if ("0".equals(parentId.trim())) {// 最高级分类
					parentId = String.format("%1$-36s", (String) parentId);
					parentNodeOfClass = dataNodeRepository.getParentOfFirstOrgan(parentId);

				} else {
					parentNodeOfClass = dataNodeRepository.findByRefid(parentId);
				}
				Set<String> parnetSet = new HashSet<>();
				for (Tb_data_node dataNode : parentNodeOfClass) {
					parnetSet.add(dataNode.getNodeid());
				}
				newTree = insertPreviewOrgan(nodeTree, organName, parnetSet);
			} else {// Organ-Class模式
				return null;
			}

			return Arrays.asList(newTree);
		}

	}

	/**
	 * 增加机构 获取预览 声像
	 *
	 * @param organName
	 * @param parentId
	 * @return
	 */
	public List<NodesettingTree> addSxOrganPreview(String organName, String parentId,String xtType) {
		List<Tb_data_node_sx> data_nodeList = new ArrayList<>();
		data_nodeList = secondaryDataNodeRepository.findSxByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return null;
		}

		NodesettingTree[] newTree;
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,xtType);
			NodesettingTree[] nodeTree = new NodesettingTree[oldNodeTreeList.size()];
			oldNodeTreeList.toArray(nodeTree);
			for (NodesettingTree nodetree : nodeTree) {
				nodetree.setExpanded(false);// 收起
			}
			List<Tb_data_node_sx> parentNodeOfClass;
			if ("0".equals(parentId.trim())) {// 最高级分类
				parentId = String.format("%1$-36s", (String) parentId);
				parentNodeOfClass = secondaryDataNodeRepository.getSxParentOfFirstOrgan(parentId);
			} else {
				parentNodeOfClass = secondaryDataNodeRepository.findSxByRefid(parentId);
			}
			Set<String> parnetSet = new HashSet<>();
			for (Tb_data_node_sx dataNode : parentNodeOfClass) {
				parnetSet.add(dataNode.getNodeid());
			}
			newTree = insertPreviewOrgan(nodeTree, organName, parnetSet);
		} else {// Organ-Class模式
			return null;
		}

		return Arrays.asList(newTree);
	}

	/**
	 * 插入机构
	 *
	 * @param nodeTree
	 * @param organName
	 * @param parentSet
	 * @return
	 */
	public NodesettingTree[] insertPreviewOrgan(NodesettingTree[] nodeTree, String organName, Set<String> parentSet) {
		for (NodesettingTree node : nodeTree) {
			if (parentSet.contains(node.getFnid())) {
				NodesettingTree organTree = new NodesettingTree();// 避免循环引用
				organTree.setFnid("");
				organTree.setText("<font color=green>" + organName + "</font>");
				organTree.setLeaf(true);
				organTree.setCls("file");
				organTree.setChildren(null);

				NodesettingTree[] newTree;
				if (node.getChildren() != null) {
					newTree = Arrays.copyOf(node.getChildren(), node.getChildren().length + 1);
					newTree[node.getChildren().length] = organTree;
				} else {
					newTree = new NodesettingTree[1];
					newTree[0] = organTree;
				}
				node.setChildren(newTree);
				node.setExpanded(true);
				node.setLeaf(false);
			} else if (node.getChildren() != null) {
				insertPreviewOrgan(node.getChildren(), organName, parentSet);
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

	// 更新机构等级001.001.002
	public void updateOrganlevel() {
		List<Tb_right_organ> firstList = rightOrganRepository.findFirstLevel();
		String organid;
		String organlevel;
		if (firstList.size() < 1) {
			return;
		}
		for (int i = 0; i < firstList.size(); i++) {
			organid = firstList.get(i).getOrganid();
			organlevel = String.format("%0" + 3 + "d", i + 1).trim();// 按3位在前边补0
			rightOrganRepository.updateOrganlevel(organlevel, organid);
			// 给organlevel递归赋值
			updateSubOrganlevel(organlevel, organid);
		}

	}

	public void updateSubOrganlevel(String organlevel, String organid) {
		List<Tb_right_organ> listSub = rightOrganRepository.findByParentid(organid);
		if (listSub.size() > 0) {
			for (int j = 0; j < listSub.size(); j++) {
				String subOrganid = listSub.get(j).getOrganid();
				String subOrganlevel = organlevel + "." + String.format("%0" + 3 + "d", j + 1).trim();
				rightOrganRepository.updateOrganlevel(subOrganlevel, subOrganid);
				updateSubOrganlevel(subOrganlevel, subOrganid);
			}
		} else {
			return;
		}
	}

	// 更新机构等级001.001.002
	public void updateSxOrganlevel() {
		List<Tb_right_organ_sx> firstList = sxRightOrganRepository.findFirstLevel();
		String organid;
		String organlevel;
		if (firstList.size() < 1) {
			return;
		}
		for (int i = 0; i < firstList.size(); i++) {
			organid = firstList.get(i).getOrganid();
			organlevel = String.format("%0" + 3 + "d", i + 1).trim();// 按3位在前边补0
			sxRightOrganRepository.updateOrganlevel(organlevel, organid);
			// 给organlevel递归赋值
			updateSxSubOrganlevel(organlevel, organid);
		}

	}

	public void updateSxSubOrganlevel(String organlevel, String organid) {
		List<Tb_right_organ_sx> listSub = sxRightOrganRepository.findByParentid(organid);
		if (listSub.size() > 0) {
			for (int j = 0; j < listSub.size(); j++) {
				String subOrganid = listSub.get(j).getOrganid();
				String subOrganlevel = organlevel + "." + String.format("%0" + 3 + "d", j + 1).trim();
				sxRightOrganRepository.updateOrganlevel(subOrganlevel, subOrganid);
				updateSxSubOrganlevel(subOrganlevel, subOrganid);
			}
		} else {
			return;
		}
	}

	public String findFullOrgan(String name, String organid) {
		Tb_right_organ organ = rightOrganRepository.findByOrganid(organid);// 首先查出当前的机构名称
		if (organ != null) {
			if ("".equals(name)) {
				name = organ.getOrganname();// 保存当前机构名称
			} else {
				name = organ.getOrganname() + "/" + name;
			}
			Tb_right_organ parentOrgan = rightOrganRepository.findByOrganid(organ.getParentid());// 查找出父级机构的信息
			if (parentOrgan != null) {
				return findFullOrgan(name, parentOrgan.getOrganid());
			}
		}
		return name;
	}

	public List<NodesettingTree> updateOrganPreview(String organName, String organId) {
		List<Tb_data_node> data_nodeList = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
		if (data_nodeList.size() == 0) {// 初始化时有可能为空
			return null;
		}

		NodesettingTree[] newTree;
		if (data_nodeList.get(0).getNodetype() == 2) {// Class-Organ模式
			List<NodesettingTree> oldNodeTreeList = nodesettingService.getNodeByParentId("", false,"");
			NodesettingTree[] nodeTree = new NodesettingTree[oldNodeTreeList.size()];
			oldNodeTreeList.toArray(nodeTree);
			for (NodesettingTree nodetree : nodeTree) {
				nodetree.setExpanded(false);// 收起
			}
			List<Tb_data_node> dataNodeList = dataNodeRepository.findByRefid(organId);// 更新关联的数据节点
			Set<String> nodeIdSet = new HashSet<>();
			for (Tb_data_node dataNode : dataNodeList) {
				nodeIdSet.add(dataNode.getNodeid());
			}
			newTree = classificationService.updateRenderPreview(nodeTree, nodeIdSet, organName);
		} else {// Organ-Class模式
			return null;
		}

		return Arrays.asList(newTree);
	}

	public void deleteAddOrgans(List<Tb_right_organ> organList) {//删除导入的新增机构节点
		for (Tb_right_organ organ : organList) {// 逐条执行删除操作
			deleteAddChildOrgan(organ.getOrganid());// 删除
		}
	}

	public void deleteAddChildOrgan(String id) {//删除导入的新增子机构节点
		List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentidOrderBySortsequence(id);
		for (Tb_right_organ right_organ : rightOrganList) {
			deleteAddChildOrgan(right_organ.getOrganid());
		}
		rightOrganRepository.deleteByOrganid(id);
	}


	public String login(Tb_user user, String loginPwd, HttpSession session){
		String username = user.getUsername();
		String password = user.getPassword();
		if (!checkLock(session, username)) {//检验账号失败次数和间隔时间
			return "2";
		}
		if (!(loginPwd!=null&&loginPwd.equals(password)|| MD5.MD5(loginPwd).equals(password))) {// 密码匹配验证
			//密码错误，新增登录失败记录
			addFailNum(session, username);
			return "1";
		}
		//清空登录失败记录
		cleanFailNum(session, username);
		return "0";
	}

	/**
	 * 校验用户登录失败次数
	 * @param session
	 * @param username
	 * @return
	 */
	public boolean checkLock(HttpSession session,String username) {
		Object o = session.getServletContext().getAttribute(username);
		if(o==null) {
			return true;
		}
		HashMap<String,Object> map  = (HashMap<String, Object>) o;
		int num  = (int) map.get("num");
		Date date = (Date) map.get("lastDate");
		long timeDifference = ((new Date().getTime()-date.getTime())/60/1000);
		if(num>=3&&timeDifference<3) {//设置登录失败次数和间隔时间
			return false;
		}
		return true;
	}
	/**
	 * 新增用户登录失败次数
	 * @param session
	 * @param username
	 */
	public void addFailNum(HttpSession session, String username) {
		Object o = session.getServletContext().getAttribute(username);
		HashMap<String,Object> map = null;
		int num= 0;
		if(o==null) {
			map = new HashMap<String,Object>();
		}else {
			map  = (HashMap<String, Object>) o;
			num  = (int) map.get("num");
			Date date = (Date) map.get("lastDate");
			long timeDifference = ((new Date().getTime()-date.getTime())/60/1000);
			if(timeDifference>=30) {//
				num=0;
			}
		}
		map.put("num", num+1);
		map.put("lastDate", new Date());
		session.getServletContext().setAttribute(username, map);
	}

	/**
	 * 清理用户登录失败的记录
	 * @param session
	 * @param username
	 */
	public void cleanFailNum(HttpSession session, String username) {
		session.getServletContext().removeAttribute(username);
	}
}