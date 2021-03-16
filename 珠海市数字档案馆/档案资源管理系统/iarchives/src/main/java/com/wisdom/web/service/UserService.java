package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_user_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_user_function_sx;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.util.GainField;
import com.wisdom.util.MD5;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;

import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.security.SlmRuntimeEasy;
import com.xdtech.project.lot.device.entity.*;
import com.xdtech.project.lot.device.repository.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/8/16.
 */
@Service
@Transactional
public class UserService {

	@Value("${system.iarchivesx.syncpath}")
	private String iarchivesxSyncPath;//数据同步请求地址

	@Autowired
	OrganService organService;

	@Autowired
	IconRepository iconRepository;

	@Autowired
	PersonalizedRepository personalizedRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	UserGroupRepository userGroupRepository;

	@Autowired
	UserNodeRepository userNodeRepository;

	@Autowired
	FunctionRepository functionRepository;

	@Autowired
	UserFunctionRepository userFunctionRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	UserDataNodeRepository userDataNodeRepository;

	@Autowired
	UserDataNodeSxRepository userDataNodeSxRepository;

	@Autowired
	RoleDataNodeRepository roleDataNodeRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	InFormUserRepository inFormUserRepository;

	@Autowired
	InFormRepository inFormRepository;

	@Autowired
	UserOrganRepository userOrganRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	FundsRepository fundsRepository;

	@Autowired
	SlmRuntimeEasy slmRuntimeEasy;

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	UserFunctionSxRepository userFunctionSxRepository;

	@Autowired
	UserNodeTempRepository userNodeTempRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	UserDevicePremissRepository userDevicePremissRepository;

	@Autowired
	UserDeviceRepository userDeviceRepository;

	@Autowired
	UserAreaRepository userAreaRepository;

	@Autowired
	DeviceAreaRepository deviceAreaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
    EleFunctionRepository eleFunctionRepository;

    @Autowired
    RoleEleFunctionRepository roleEleFunctionRepository;

	@Autowired
	CaRepository caRepository;

    @Autowired
	SecondaryUserDateNodeRepository secondaryUserDateNodeRepository;

    @Autowired
	SecondaryRoleDataNodeRepository secondaryRoleDataNodeRepository;
	@Autowired
	SxUserRepository sxUserRepository;
	@Autowired
	SxUserDataNodeRepository sxUserDataNodeRepository;
	@Autowired
	SxUserOrganRepository sxUserOrganRepository;
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
	@Autowired
	SxFunctionRepository sxFunctionRepository;
	@Autowired
	SxRoleRepository sxRoleRepository;

	@Autowired
	UserFillSortRepository userFillSortRepository;

	@Autowired
	SxRightOrganRepository sxRightOrganRepository;
	/**
	 * 保存桌面快捷方式
	 *
	 * @param icons
	 * @return
	 */
	public String saveicon(List<Tb_Icon> icons,String sysType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		iconRepository.deleteByUseridAndSystype(userDetails.getUserid(),sysType);
		String url="";
		for (int i = 0; i < icons.size(); i++) {
			icons.get(i).setUserid(userDetails.getUserid());
			icons.get(i).setSystype(sysType);
			url=icons.get(i).getUrl();
			if(url!=null&&url.contains("?v=")){// /thematicProd/main?v=12021528&v=12170338
				url=url.substring(0,url.indexOf("?v="));
			}
			if(url!=null&&url.contains("&v=")){// /thematicProd/main?isp=k21&v=12170338
				url=url.substring(0,url.indexOf("&v="));
			}
			icons.get(i).setUrl(url);
			iconRepository.save(icons.get(i));
		}

		return "{flag:1}";
	}

	/**
	 * 删除桌面快捷方式
	 *
	 * @param sortsequence
	 *            唯一标识
	 * @param flag
	 *            是否为最后一个
	 * @return 成功状态
	 */
	public String delicon(String sortsequence, String flag,String sysType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!"0".equals(flag)) {
			iconRepository.deleteByCodeAndUseridAndSystype(flag, userDetails.getUserid(),sysType);
		} else {
			iconRepository.deleteBySortsequenceAndUseridAndSystype(Integer.parseInt(sortsequence), userDetails.getUserid(),sysType);
		}
		return "{flag:1}";
	}

	public String upPersonalized(Tb_Personalized personalized) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int i = personalizedRepository.deleteByUserid(userDetails.getUserid());
		personalized.setUserid(userDetails.getUserid());
		personalized = personalizedRepository.save(personalized);
		return "1";
	}

	public List<Tb_user> getUnitUserInfo(String organid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_user_organ> user_organList = userOrganRepository.findByUserid(userDetails.getUserid());
		String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_organList, "organid");
		List<String> qxlists = java.util.Arrays.asList(organids);
		List<String> organidList = organService.getOrganidLoop(organid, true, new ArrayList<String>());

		if (qxlists.size() > 1) {
			organidList.retainAll(qxlists);// 取交集
		}
		String[] organidArr = new String[organidList.size()];
		organidList.toArray(organidArr);
		organids = ArrayUtils.addAll(organids, organidArr);

		Specification<Tb_user> specification = getUsers(organids);
		Specifications sp = Specifications.where(specification);
		return userRepository.findAll(sp);
	}

	public Page<Tb_user> getUnitUser(String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, int page,
			int limit, Sort sort,String xtType) {
		String[] organIDs = new String[] {};
		if (ifSearchLeafNode) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			List<Tb_user_organ> user_organList = userOrganRepository.findByUserid(userDetails.getUserid());
			String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(user_organList, "organid");
			List<String> qxlists = java.util.Arrays.asList(organids);
			List<String> organidList = organService.getOrganidLoop(organID, ifContainSelfNode, new ArrayList<String>());

			if (qxlists.size() > 1) {
				organidList.retainAll(qxlists);// 取交集
			}
			String[] organidArr = new String[organidList.size()];
			organidList.toArray(organidArr);
			organIDs = ArrayUtils.addAll(organIDs, organidArr);
		} else {
			organIDs = new String[] { organID };
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction.ASC,"sortsequence") : sort);
		Specification<Tb_user> specification = getUsers(organIDs);
		Specifications sp = Specifications.where(specification);
		Tb_right_organ organ = rightOrganRepository.findByOrganid(organID);
		if(organ==null||!"外来人员部门".equals(organ.getOrganname())){
			sp = sp.and(new SpecificationUtil("outuserstate","isNull",""));
		}
		return userRepository.findAll(sp, pageRequest);
	}

	public Page<Tb_user_sx> getSxUnitUser(String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, int page,
									 int limit, Sort sort,String xtType) {
		String[] organIDs = new String[] {};
		if (ifSearchLeafNode) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			List<Tb_user_organ_sx> user_organList = sxUserOrganRepository.findByUserid(userDetails.getUserid());
			String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(user_organList, "organid");
			List<String> qxlists = java.util.Arrays.asList(organids);
			List<String> organidList = organService.getSxOrganidLoop(organID, ifContainSelfNode, new ArrayList<String>());

			if (qxlists.size() > 1) {
				organidList.retainAll(qxlists);// 取交集
			}
			String[] organidArr = new String[organidList.size()];
			organidList.toArray(organidArr);
			organIDs = ArrayUtils.addAll(organIDs, organidArr);
		} else {
			organIDs = new String[] { organID };
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		if(organIDs.length<1){
			organIDs = new String[] { "" };
		}
		Specification<Tb_user_sx> specification = getSxUsers(organIDs);
		Specifications sp = Specifications.where(specification);
		return sxUserRepository.findAll(sp, pageRequest);
	}

	public static Specification<Tb_user> getUsers(String[] organIDs) {
		Specification<Tb_user> specification = new Specification<Tb_user>() {
			@Override
			public Predicate toPredicate(Root<Tb_user> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("organ").get("organid"));
				for (String organid : organIDs) {
					in.value(organid);
				}
				return criteriaBuilder.or(in);
			}
		};
		return specification;
	}
	public static Specification<Tb_user_sx> getSxUsers(String[] organIDs) {
		Specification<Tb_user_sx> specification = new Specification<Tb_user_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_user_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("organ").get("organid"));
				for (String organid : organIDs) {
					in.value(organid);
				}
				return criteriaBuilder.or(in);
			}
		};
		return specification;
	}

	
	public String getUserfullname(String organid, String fullname) {
		Tb_right_organ organ = rightOrganRepository.findByOrganid(organid);
		if (organ != null) {
			if ("".equals(fullname)) {
				fullname = organ.getOrganname();
			} else {
				fullname = organ.getOrganname() + "/" + fullname;
			}
			if (!"0".equals(organ.getParentid()) && organ.getParentid() != null) {
				return getUserfullname(organ.getParentid(), fullname);
			}
		}
		return fullname;
	}

	public Tb_user addUser(Tb_user user) {
		return userRepository.save(user);
	}

	//同步档案用户到声像
	public void syncUserToSx() {
		//sxUserRepository.deleteAll();
		List<Tb_user> daList= userRepository.findAll();
		for(Tb_user user:daList){
			try{
				addSxUser(user);
			}catch(Exception e) {
				System.out.println(user.getLoginname());
				e.printStackTrace();
			}
		}
	}


	//增加声像用户
	public void addSxUser(Tb_user user) {
		Tb_user_sx userSx=new Tb_user_sx();
		BeanUtils.copyProperties(user,userSx);
		sxUserRepository.save(userSx);
	}

	public Tb_user findByLoginname(String loginname) {
		return userRepository.findByLoginname(loginname);
	}

	public Integer userDel(String[] ids) {
		userFunctionRepository.deleteAllByUseridIn(ids);
		userRoleRepository.deleteAllByUseridIn(ids);
		userGroupRepository.deleteAllByUseridIn(ids);
		userDataNodeRepository.deleteAllByUseridIn(ids);
		userNodeRepository.deleteAllByUseridIn(ids);
		userOrganRepository.deleteAllByUseridIn(ids);
		personalizedRepository.deleteByUseridIn(ids);
		Integer delCount = userRepository.deleteAllByUseridIn(ids);
		return delCount;
	}

	//删除声像用户
	@Transactional(value = "transactionManagerSecondary")
	public void userSxDel(String[] ids) {
		sxUserFunctionRepository.deleteAllByUseridIn(ids);
		sxUserRoleRepository.deleteAllByUseridIn(ids);
		sxUserGroupRepository.deleteAllByUseridIn(ids);
		sxUserDataNodeRepository.deleteAllByUseridIn(ids);
		sxUserNodeRepository.deleteAllByUseridIn(ids);
		sxUserOrganRepository.deleteAllByUseridIn(ids);
		sxPersonalizedRepository.deleteByUseridIn(ids);
		Integer delCount = sxUserRepository.deleteAllByUseridIn(ids);
	}

	/**
	 * 修改用户
	 *
	 * @param user
	 * @return
	 */
	public Integer userEditSubmit(Tb_user user) {
		int i = userRepository.updateNameById(user.getLoginname(), user.getRealname(), user.getPhone(),
				user.getAddress(), user.getSex(), user.getUsertype(),user.getOrganusertype(),user.getDuty(), user.getUserid());
		return i;
	}

	/**
	 * 绑定用户
	 *
	 * @param user
	 * @return
	 */
	public Integer userBindSubmit(Tb_user user, String cacode, String signcode) {
		Tb_user userExist = userRepository.findByNickname(user.getNickname());
		if(userExist!=null){//如果该数字证书之前已绑定一个用户，清空之前的用户绑定
			userRepository.updateNicknameById("", userExist.getUserid());
		}
		int i = userRepository.updateNicknameById(user.getNickname(), user.getUserid());
		//更新key信息
		updateCa(user.getNickname(),cacode,signcode);
		return i;
	}

	//更新key信息
	private void updateCa(String caid, String cacode, String signcode){
		Tb_ca ca=new Tb_ca();
		List<Tb_ca> caList=caRepository.findByCaid(caid);
		if(caList.size()>0){
			ca=caList.get(0);
		}else{
			ca.setCaid(caid);//证书ID
		}
		ca.setCertcode(cacode);
		ca.setSigncode(signcode);
		caRepository.save(ca);
	}

	/**
	 * 修改声像用户
	 *
	 * @param user
	 * @return
	 */
	public void userSxEditSubmit(Tb_user user) {
		sxUserRepository.updateNameById(user.getLoginname(), user.getRealname(), user.getPhone(),
				user.getAddress(), user.getSex(), user.getUsertype(), user.getUserid());
	}

	/**
	 * @param page
	 *            第几页
	 * @param limit
	 *            一页获取多少行
	 * @param condition
	 *            字段
	 * @param operator
	 *            操作符
	 * @param content
	 *            查询条件内容
	 * @param organID
	 *            单位节点ID
	 * @param ifSearchLeafNode
	 *            是否检索所选节点的子节点（若选择节点为非叶子节点且此参数为false，则无检索结果）
	 * @param ifContainSelfNode
	 *            是否查询出当前非叶子节点及其包含的所有非叶子节点数据
	 * @return
	 */
	public Page<Tb_user> findBySearch(int page, int limit, String condition, String operator, String content,
			String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, Sort sort,String xtType) {
		String[] organIDs = new String[] {};
		if (ifSearchLeafNode) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			List<Tb_user_organ> user_organList = userOrganRepository.findByUserid(userDetails.getUserid());
			String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(user_organList, "organid");
			List<String> qxlists = java.util.Arrays.asList(organids);

			List<String> organidList = organService.getOrganidLoop(organID, ifContainSelfNode, new ArrayList<String>());

			organidList.retainAll(qxlists);// 取交集
			String[] organidArr = new String[organidList.size()];
			organidList.toArray(organidArr);
			organIDs = ArrayUtils.addAll(organIDs, organidArr);
		} else {
			organIDs = new String[] { organID };
		}
		Specification<Tb_user> searchOrganidCondition = getSearchOrganidCondition(organIDs);
		Tb_right_organ organ = rightOrganRepository.findByOrganid(organID);
		Specifications specifications = Specifications.where(searchOrganidCondition);
		if(organ==null||!"外来人员部门".equals(organ.getOrganname())){
			specifications = specifications.and(new SpecificationUtil("outuserstate","isNull",""));
		}
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		return userRepository.findAll(specifications, pageRequest);
	}

	public Page<Tb_user_sx> findSxBySearch(int page, int limit, String condition, String operator, String content,
									  String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, Sort sort,String xtType) {
		String[] organIDs = new String[] {};
		if (ifSearchLeafNode) {
			SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			List<Tb_user_organ_sx> user_organList = sxUserOrganRepository.findByUserid(userDetails.getUserid());
			String[] organids = GainField.getFieldValues(user_organList, "organid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(user_organList, "organid");
			List<String> qxlists = java.util.Arrays.asList(organids);

			List<String> organidList = organService.getSxOrganidLoop(organID, ifContainSelfNode, new ArrayList<String>());

			organidList.retainAll(qxlists);// 取交集
			String[] organidArr = new String[organidList.size()];
			organidList.toArray(organidArr);
			organIDs = ArrayUtils.addAll(organIDs, organidArr);
		} else {
			organIDs = new String[] { organID };
		}
		Specification<Tb_user_sx> searchOrganidCondition = getSxSearchOrganidCondition(organIDs);
		Specifications specifications = Specifications.where(searchOrganidCondition);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("sortsequence") : sort);
		return sxUserRepository.findAll(specifications, pageRequest);
	}

	/**
	 * 获取全部功能权限
	 *
	 * @param fnid
	 * @param userId
	 * @return
	 */
	public List<ExtTree> getAllGn(String fnid, String userId,String xtType) {
		if ("09454816393442aba7e95f16a826353f".equals(fnid.trim())
				|| "8adae20e224b43bf80c362ca9b59fc73".equals(fnid.trim())) {// 过滤掉
																			// 安全维护\系统设置
																			// 的子功能
			return null;
		}

		String isp = "1";
		if (!"1".equals(fnid)) {// 判断是否为根节点
			Tb_right_function function;
			function= functionRepository.findByFnid(fnid);
			if (function != null) {
				isp = function.getTkey();
			}
		}
		List<Tb_right_function> list = new ArrayList<>();
		if(slmRuntimeEasy.hasPlatform()){
			list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
		}else{
			list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
		}

		List<Tb_right_function> functions = new ArrayList<>();// 批量设置
		List<Tb_role> roles = roleRepository.findBygroups(userId);
		if (userId.split(",").length == 1) {
			String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
			functions = functionRepository.findByfunctions(roleids, userId);//获取用户功能权限
		}
		String[] funids = GainField.getFieldValues(functions, "fnid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(functions, "fnid");
		List<String> funclists = java.util.Arrays.asList(funids);
		ExtMsg result = getRoleGn(userId);

		//获取已设置用户组的相关权限，字体灰色显示
		List<Tb_right_function> resultfunctions = (List<Tb_right_function>) result.getData();
		String[] resultfunids = GainField.getFieldValues(resultfunctions, "fnid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(resultfunctions, "fnid");
		List<String> resultfunclists = java.util.Arrays.asList(resultfunids);
		List<Tb_right_function> allGnList = functionRepository.findByStatusOrderBySortsequence("1");
		return getCheckGnChildren(resultfunclists, funclists, list, allGnList);
	}

	/**
	 * 功能授权：返回一个完整的树ExtTree
	 *
	 * @param groupFunctions 用户组功能id
	 * @param functions 首层功能
	 * @param allGnList 全部功能
	 * @param userFunctions 用户和角色功能id
	 * @return
	 */
	private List<ExtTree> getCheckGnChildren(List<String> groupFunctions,List<String> userFunctions, List<Tb_right_function> functions, List<Tb_right_function> allGnList) {
		List<ExtTree> gnTrees = new ArrayList<>();

		for (int i = 0; i < functions.size(); i++) {
			Tb_right_function function = functions.get(i);
			if ("09454816393442aba7e95f16a826353f".equals(function.getFnid().trim())
					|| "8adae20e224b43bf80c362ca9b59fc73".equals(function.getFnid().trim())) {// 过滤掉
				// 安全维护\系统设置
				// 功能
				continue;
			}
			ExtTree tree = new ExtTree();
			tree = new ExtTree();
			if (!"true".equals(function.getHaschilds())) {
				tree.setLeaf(true);
			}

			if (userFunctions.contains(function.getFnid())) {// 比较是否包含功能权限,是则勾选
				tree.setChecked(true);
			}

			if ( groupFunctions !=  null &&  groupFunctions.size() >0 ){
				if(groupFunctions.contains(function.getFnid()) && !"true".equals(function.getHaschilds())){
					//比较是否是用户组设置的权限，是则灰色显示
					tree.setText("<span style = 'color:gray;editable:false'>"+function.getName()+"</span>");
				} else {
					tree.setText(function.getName());
				}
			} else {
				tree.setText(function.getName());
			}
			tree.setFnid(function.getFnid());
			//List<Tb_user_organ_parents> childUserOrgans = findTopOrganOfPcid(useRightOrgan.getOrganid(), parents);// 判断是否有子节点
			if ("true".equals(function.getHaschilds())) {
				tree.setCls("folder");
				tree.setLeaf(false);
				String isp = "1";
				if (!"1".equals(function.getFnid())) {// 判断是否为根节点
					isp = function.getTkey();
				}
				List<Tb_right_function> list = new ArrayList<>();
				/*if(slmRuntimeEasy.hasPlatform()){
					list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
				}else{
					list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
				}*/
				for(Tb_right_function subFun:allGnList){
					if(isp.equals(subFun.getIsp())){
						list.add(subFun);
					}
				}

				List<ExtTree> extTrees = getCheckGnChildren(groupFunctions, userFunctions, list, allGnList);
				ExtTree[] gnTreeList = new ExtTree[extTrees.size()];
				for(int j=0;j<extTrees.size();j++){
					gnTreeList[j]=extTrees.get(j);
				}
				tree.setChildren(gnTreeList);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			gnTrees.add(tree);
		}
		return gnTrees;
	}

	/**
	 * 获取全部功能权限  声像
	 *
	 * @param fnid
	 * @param userId
	 * @return
	 */
	public List<ExtTree> getAllSxGn(String fnid, String userId,String xtType) {
		if ("09454816393442aba7e95f16a826353f".equals(fnid.trim())
				|| "8adae20e224b43bf80c362ca9b59fc73".equals(fnid.trim())) {// 过滤掉
			// 安全维护\系统设置
			// 的子功能
			return null;
		}

		String isp = "1";
		if (!"1".equals(fnid)) {// 判断是否为根节点
			Tb_right_function_sx function;
			function = sxFunctionRepository.findByFnid(fnid);
			if (function != null) {
				isp = function.getTkey();
			}
		}
		List<Tb_right_function_sx> list = new ArrayList<>();
		if (slmRuntimeEasy.hasPlatform()) {
			list = sxFunctionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
		} else {
			list = sxFunctionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
		}

		List<Tb_right_function_sx> functions = new ArrayList<>();// 批量设置
		List<Tb_role_sx> roles = sxRoleRepository.findBygroups(userId);
		if (userId.split(",").length == 1) {
			String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[]{""} : GainField.getFieldValues(roles, "roleid");
			functions = sxFunctionRepository.findByfunctions(roleids, userId);//获取用户功能权限
		}
		String[] funids = GainField.getFieldValues(functions, "fnid").length == 0 ? new String[]{""}
				: GainField.getFieldValues(functions, "fnid");
		List<String> funclists = java.util.Arrays.asList(funids);
		ExtMsg result = getSxRoleGn(userId);

		//获取已设置用户组的相关权限，字体灰色显示
		List<Tb_right_function_sx> resultfunctions = (List<Tb_right_function_sx>) result.getData();
		String[] resultfunids = GainField.getFieldValues(resultfunctions, "fnid").length == 0 ? new String[]{""}
				: GainField.getFieldValues(resultfunctions, "fnid");
		List<String> resultfunclists = java.util.Arrays.asList(resultfunids);
		List<Tb_right_function_sx> allGnList = sxFunctionRepository.findByStatusOrderBySortsequence("1");
		return getCheckGnChildrenSx(resultfunclists, funclists, list, allGnList);
	}

	private List<ExtTree> getCheckGnChildrenSx(List<String> resultfunclists,List<String> funclists,
											  List<Tb_right_function_sx> list, List<Tb_right_function_sx> allGnList) {
		List<ExtTree> extTrees = new ArrayList<>();
		ExtTree tree;
		for (Tb_right_function_sx function : list) {// 生成树节点结合
			if ("09454816393442aba7e95f16a826353f".equals(function.getFnid().trim())
					|| "8adae20e224b43bf80c362ca9b59fc73".equals(function.getFnid().trim())) {// 过滤掉
				// 安全维护\系统设置
				// 功能
				continue;
			}
			tree = new ExtTree();
			if (!"true".equals(function.getHaschilds())) {
				tree.setLeaf(true);
			}

			if (funclists.contains(function.getFnid())) {// 比较是否包含功能权限,是则勾选
				tree.setChecked(true);
			}

			if ( resultfunclists !=  null &&  resultfunclists.size() >0 ){
					if(resultfunclists.contains(function.getFnid()) && !"true".equals(function.getHaschilds())){
						//比较是否是用户组设置的权限，是则灰色显示
						tree.setText("<span style = 'color:gray;editable:false'>"+function.getName()+"</span>");
					} else {
						tree.setText(function.getName());
					}
			} else {
				tree.setText(function.getName());
			}
			tree.setFnid(function.getFnid());
			if ("true".equals(function.getHaschilds())) {
				tree.setCls("folder");
				tree.setLeaf(false);
				String isp = "1";
				if (!"1".equals(function.getFnid())) {// 判断是否为根节点
					isp = function.getTkey();
				}
				List<Tb_right_function_sx> listSx = new ArrayList<>();
				/*if(slmRuntimeEasy.hasPlatform()){
					list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
				}else{
					list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
				}*/
				for(Tb_right_function_sx subFun:allGnList){
					if(isp.equals(subFun.getIsp())){
						listSx.add(subFun);
					}
				}

				List<ExtTree> extTreeSx = getCheckGnChildrenSx(resultfunclists, funclists, listSx, allGnList);
				ExtTree[] gnTreeList = new ExtTree[extTreeSx.size()];
				for(int j=0;j<extTreeSx.size();j++){
					gnTreeList[j]=extTreeSx.get(j);
				}
				tree.setChildren(gnTreeList);
			} else {
				tree.setCls("file");
				tree.setLeaf(true);
			}
			extTrees.add(tree);
		}
		return extTrees;
	}


	/**
	 * 获取已设置用户组相关功能权限
	 * @param userId
	 * @return
	 */
	public ExtMsg getRoleGn(String userId){
		List<Tb_right_function> functions = new ArrayList<>();// 批量设置
		List<Tb_role> roles = roleRepository.findBygroups(userId);
		Boolean flag = false;
		if(roles!=null && roles.size() > 0){
			String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
			functions = functionRepository.findByfunctions(roleids, "");
			flag = true;
		}
		return new ExtMsg(flag,"",functions);
	}

	/**
	 * 获取已设置用户组相关功能权限
	 * @param userId
	 * @return
	 */
	public ExtMsg getSxRoleGn(String userId){
		List<Tb_right_function_sx> functions = new ArrayList<>();// 批量设置
		List<Tb_role_sx> roles = sxRoleRepository.findBygroups(userId);
		Boolean flag = false;
		if(roles!=null && roles.size() > 0){
			String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
			functions = sxFunctionRepository.findByfunctions(roleids, "");
			flag = true;
		}
		return new ExtMsg(flag,"",functions);
	}

	/**
	 * 设置功能权限
	 *
	 * @param fnid
	 * @param userId
	 * @return
	 */
	public List UserSetGnSubmit(String[] fnid, String userId,String xtType) {
		String[] userArray = userId.split(",");
		userFunctionRepository.deleteAllByUseridIn(userArray);// 根据id删除已有的功能权限

		if (fnid == null) {
			List<Tb_user_function> l = new ArrayList<>();
			l.add(new Tb_user_function());
			return l;
		}

		List<Tb_user_function> ufs = new ArrayList<>();
		for (String user : userArray) {
			for (String fid : fnid) {
				Tb_user_function tuf = new Tb_user_function();
				tuf.setFnid(fid);
				tuf.setUserid(user);
				ufs.add(tuf);
			}
		}
		return userFunctionRepository.save(ufs);
	}

	/**
	 * 设置功能权限  声像
	 *
	 * @param fnid
	 * @param userId
	 * @return
	 */
	@Transactional(value = "transactionManagerSecondary")
	public List UserSetSxGnSubmit(String[] fnid, String userId,String xtType) {
		String[] userArray = userId.split(",");
		sxUserFunctionRepository.deleteAllByUseridIn(userArray);// 根据id删除已有的功能权限

		if (fnid == null) {
			List<Tb_user_function> l = new ArrayList<>();
			l.add(new Tb_user_function());
			return l;
		}

		List<Tb_user_function_sx> ufs = new ArrayList<>();
		for (String user : userArray) {
			for (String fid : fnid) {
				Tb_user_function_sx tuf = new Tb_user_function_sx();
				tuf.setFnid(fid);
				tuf.setUserid(user);
				ufs.add(tuf);
			}
		}
		return sxUserFunctionRepository.save(ufs);
	}

	/**
	 * 设置数据权限
	 *
	 * @param fnid
	 *            权限id
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List UserSetSjSubmit(String[] fnid, String userId,String xtType) {
		if("声像系统".equals(xtType)){
			return UserSxSetSjSubmit(fnid, userId, xtType);
		}else{
			String[] userArray = userId.split(",");
			if(userArray.length==1){
				userDataNodeRepository.deleteAllByUserid(userId);// 根据id清除数据权限
			}else{
				userDataNodeRepository.deleteAllByUseridIn(userArray);// 根据id清除数据权限
			}
			if (fnid == null) {
				List<Tb_user_data_node> l = new ArrayList<>();
				l.add(new Tb_user_data_node());
				//return l;
			}
			List<Tb_user_data_node> ufs = new ArrayList<>();
			for (String user : userArray) {
				for (String fid : fnid) {
					Tb_user_data_node udn = new Tb_user_data_node();
					udn.setUserid(user);
					udn.setNodeid(fid);
					ufs.add(udn);
				}
			}
			batchInsert(ufs);
			return ufs;
		}
	}

	//批量插入
	public void batchInsert(List list) {
		for (int i = 0; i < list.size(); i++) {
			entityManager.persist(list.get(i));
			if (i % 100 == 0) {//一次一百条插入
				entityManager.flush();
				entityManager.clear();
			}
		}
	}

	public List UserSxSetSjSubmit(String[] fnid, String userId,String xtType) {
		String[] userArray = userId.split(",");
		sxUserDataNodeRepository.deleteAllByUseridIn(userArray);// 根据id清除数据权限
		if (fnid == null) {
			List<Tb_user_data_node> l = new ArrayList<>();
			l.add(new Tb_user_data_node());
			return l;
		}

		List<Tb_user_data_node_sx> ufs = new ArrayList<>();
		for (String user : userArray) {
			for (String fid : fnid) {
				Tb_user_data_node_sx udn = new Tb_user_data_node_sx();
				udn.setUserid(user);
				udn.setNodeid(fid);
				ufs.add(udn);
			}
		}
		return sxUserDataNodeRepository.save(ufs);
	}

	/**
	 * 设置机构权限
	 *
	 * @param fnid
	 *            权限id
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<Tb_user_organ> userSetOrganSubmit(String[] fnid, String userId) {
		String[] userArray = userId.split(",");
		userOrganRepository.deleteAllByUseridIn(userArray);// 根据id清除数据权限
		if (fnid == null) {
			List<Tb_user_organ> user_organList = new ArrayList<>();
			user_organList.add(new Tb_user_organ());
			return user_organList;
		}

		List<Tb_user_organ> ufs = new ArrayList<>();
		for (String user : userArray) {
			for (String fid : fnid) {
				Tb_user_organ udn = new Tb_user_organ();
				udn.setUserid(user);
				udn.setOrganid(fid);
				ufs.add(udn);
			}
		}

		return userOrganRepository.save(ufs);
	}

	public List<String> findDataAuths(String userid) {
		List<String> authList = userDataNodeRepository.findByUserid(userid);
		authList.addAll(roleDataNodeRepository.findRoleDataAuth(userid));
		return authList.stream().map(auth -> auth.trim()).collect(Collectors.toList());
	}

	public List<String> findSecondaryDataAuths(String userid) {
		List<String> authList = secondaryUserDateNodeRepository.findByUserid(userid);
		authList.addAll(secondaryRoleDataNodeRepository.findRoleDataAuth(userid));
		return authList.stream().map(auth -> auth.trim()).collect(Collectors.toList());
	}

	public String getAuthNodeids(String userid){
		String nodeidStr=" and nodeid in(" +
				" select trdn.nodeid from tb_role_data_node trdn " +
				" inner join tb_user_role tur on trdn.roleid=tur.roleid  " +
				" where tur.userid='"+userid+"'  " +
				" union  " +
				" select tudn.nodeid from tb_user_data_node tudn  " +
				" where tudn.userid='"+userid+"') ";
		return nodeidStr;
	}

	//用exists
	public String getAuthNode(String userid){
		String nodeidStr=" and exists (select t.nodeid from (" +
				"select trdn.nodeid from tb_role_data_node trdn inner join tb_user_role tur on trdn.roleid=tur.roleid  " +
				" where tur.userid='"+userid+"'  "+
				" union  " +
				"select tudn.nodeid from tb_user_data_node tudn  where tudn.userid='"+userid+"')t " +
				"where t.nodeid=sid.nodeid ) ";
		return nodeidStr;
	}

	public List<ExtNcTree> findAllOrgan() {
		List<ExtNcTree> list = new ArrayList<>();
		List<Tb_right_organ> organs = rightOrganRepository.findAll();
		if (organs != null) {
			for (Tb_right_organ organ : organs) {
				ExtNcTree extTree = new ExtNcTree();
				extTree.setCls("folder");
				extTree.setText(organ.getOrganname());
				extTree.setLeaf(true);
				list.add(extTree);
			}
		}
		return list;
	}

	public String editPwd(String oldpwd, String pwd) throws Exception {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String msg = "修改失败";
		Tb_user user = userRepository.findByUserid(userDetails.getUserid());
		Tb_user_sx user_sx = sxUserRepository.findByUserid(userDetails.getUserid());
		if (user != null) {
			if (MD5.MD5(oldpwd).equals(user.getLoginpassword())) {// 判断原密码是否输入正确
				user.setLoginpassword(MD5.MD5(pwd));
				msg = "修改成功";
				if(user_sx !=null){
					int count = sxUserRepository.updateLoginPasswordByUserId(MD5.MD5(pwd),userDetails.getUserid());
					if(count>0){
						msg = "修改成功，需重新登录才可以切换系统！";
					}
				}
			} else {
				msg = "原密码匹配失败";
			}
		}

		return msg;
	}

	/**
	 * 获取未过期推送的公告
	 *
	 * @param userDetails
	 *            安全对象
	 * @return
	 */
	public List<Tb_inform> findInforms(SecurityUser userDetails) {
		List<Tb_role> roles = userDetails.getRoles();
		String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(roles, "roleid");// 获取用户组id
		String[] userroleids = Arrays.copyOf(roleids, roleids.length + 1);// 添加用户id
		userroleids[userroleids.length - 1] = userDetails.getUserid();
		List<Tb_inform_user> inform_users = inFormUserRepository.findByUserroleidInAndStateIsNull(userroleids);
		String[] informids = GainField.getFieldValues(inform_users, "informid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(inform_users, "informid");
//		List<Sort.Order> sorts = new ArrayList<>();
//		sorts.add(new Sort.Order(Sort.Direction.ASC,"stick"));//置顶
//		sorts.add(new Sort.Order(Sort.Direction.DESC, "informdate"));// 发布时间降序
//		PageRequest pageRequest = new PageRequest(0, 100, new Sort(sorts));
		//加上公车预约以及场地预约取消的通知
		List<Tb_inform> page = inFormRepository.getInForms(informids);
//		List<Tb_inform> list = new ArrayList<>();
//		if (page != null) {
//			list = page.getContent();
//		}
		return page;
	}

	/**
	 * 获取个性头像用户id
	 *
	 * @return
	 */
	public String getUserId() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getUserid();
	}

	public Tb_inform getInform(String msgid) {
		return inFormRepository.findByInformidInOrderByInformdate(new String[] { msgid }).get(0);
	}

	/**
	 * 根据Key值获取其时间
	 *
	 * @param key
	 *            用户输入的key值
	 * @return
	 */
	public String getTimebyKey(String key) {
		String month1 = key.substring(1, 2);
		String month2 = key.substring(6, 7);
		String day1 = key.substring(12, 13);
		String day2 = key.substring(17, 18);
		String hour1 = key.substring(3, 4);
		String hour2 = key.substring(13, 14);
		String mintue1 = key.substring(5, 6);
		String mintue2 = key.substring(15, 16);
		StringBuilder time = new StringBuilder();
        time.append(month1).append(month2).append("-").append(day1).append(day2).append(" ").append(hour1).append(hour2).append(":").append(mintue1).append(mintue2);
		String keytime = time.toString();
		return keytime;
	}

	/**
	 * 根据Key值获取其固定串
	 *
	 * @param key
	 *            用户输入的key值
	 * @return
	 */
	public String getFixbyKey(String key) {
		String fix1 = key.substring(0, 1);
		String fix2 = key.substring(2, 3);
		String fix3 = key.substring(7, 8);
		String fix4 = key.substring(8, 9);
		String fix5 = key.substring(10, 11);
		String fix6 = key.substring(11, 12);
		String fix7 = key.substring(16, 17);
		String fix8 = key.substring(18, 19);
		StringBuilder Fix = new StringBuilder();
		Fix.append(fix1).append(fix2).append("-").append(fix3).append(fix4).append("-").append(fix5).append(fix6).append("-").append(fix7).append(fix8);
		String keyFix = Fix.toString();
		return keyFix;
	}

	/**
	 * 获取当前系统时间
	 *
	 * @return
	 */
	public String sysTime(){
		SimpleDateFormat bDay = new SimpleDateFormat("MM-dd HH:mm");//定义日期格式
		String sysTime = bDay.format(new Date());//获取时间的String值
		return sysTime;
	}

	public String resetUserPW(String username, String key) throws Exception {
		if(key.length()!=19){
			return "请输入正确的key值!";
		}

		if (!"xitong".equals(username)) { // 判断是否为管理员
			return "普通用户请联系管理员重置密码!";
		}

		String keyFix = getFixbyKey(key);//根据Key获取固定串
		if (!keyFix.equals("26-56-07-56")) {// 验证固定串是否正确
			return "请输入正确的key值!";
		}

		String keyTime = getTimebyKey(key); // 根据Key获取时间
		String sysTime = sysTime();//获取系统时间
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
	    try {
	    	    df.setLenient(false);//日期转换严格模式
	        	Date keytime = (Date) df.parse(keyTime);//将String类转化为Date类
		        Date systime = (Date)df.parse(sysTime);//将String类转化为Date类
		    	Long temp =keytime.getTime() - systime.getTime();//相差毫秒数
			    double hours =(double)temp / 1000 / 3600;//相差小时数
		    	double abshours = Math.abs(hours);//绝对值

			    if(abshours >= 1) {//判断时间是否在上下1小时内
				      return "Key值已超有效期!";
			    }
		}catch (Exception e){  //从key值提取日期格式不正确
			return "请输入正确的key值!";
		}

		Tb_user user = userRepository.findByLoginname(username);// 获取用户
		user.setLoginpassword(MD5.MD5("555")); // 重置密码
		return "密码已重置为:555";
	}

	public Integer resetUserPW(String[] userIds, String password) {
		int count = 0;
		for (String userId : userIds) {
			Tb_user tb_user = userRepository.findByUserid(userId);
			Tb_user_sx user_sx = sxUserRepository.findByUserid(userId);
			if (tb_user != null) {
				tb_user.setLoginpassword(MD5.MD5(password));
				if(user_sx != null){
					count = sxUserRepository.updateLoginPasswordByUserId(MD5.MD5(password),userId);
				}
				count++;
			}
		}
		return count;
	}

	public Tb_user findUser(String userid) {
		return userRepository.findByUserid(userid);
	}

	public void modifyUserOrder(Tb_user user, int target) {
		if (user.getSortsequence() == null || user.getSortsequence() < target) {
			// 后移。1.将目标位置包括后面的所有数据后移一个位置；
			userRepository.modifyUserOrder(target, Integer.MAX_VALUE);
		} else {
			// 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
			userRepository.modifyUserOrder(target, user.getSortsequence());
		}
		// 2.将当前数据移到目标位置
		user.setSortsequence(target);
		userRepository.save(user);
	}

	public Tb_user findByRealname(String realname) {
		return userRepository.findByRealname(realname);
	}

	public static Specification<Tb_user> getSearchOrganidCondition(String[] organIDs) {
		Specification<Tb_user> searchOrganID = null;
		if (organIDs != null && organIDs.length > 0) {
			searchOrganID = new Specification<Tb_user>() {
				@Override
				public Predicate toPredicate(Root<Tb_user> root, CriteriaQuery<?> criteriaQuery,
						CriteriaBuilder criteriaBuilder) {
					Predicate[] predicates = new Predicate[organIDs.length];
					for (int i = 0; i < organIDs.length; i++) {
						predicates[i] = criteriaBuilder.equal(root.get("organ").get("organid"), organIDs[i]);
					}
					return criteriaBuilder.or(predicates);
				}
			};
		}
		return searchOrganID;
	}

	public static Specification<Tb_user_sx> getSxSearchOrganidCondition(String[] organIDs) {
		Specification<Tb_user_sx> searchOrganID = null;
		if (organIDs != null && organIDs.length > 0) {
			searchOrganID = new Specification<Tb_user_sx>() {
				@Override
				public Predicate toPredicate(Root<Tb_user_sx> root, CriteriaQuery<?> criteriaQuery,
											 CriteriaBuilder criteriaBuilder) {
					Predicate[] predicates = new Predicate[organIDs.length];
					for (int i = 0; i < organIDs.length; i++) {
						predicates[i] = criteriaBuilder.equal(root.get("organ").get("organid"), organIDs[i]);
					}
					return criteriaBuilder.or(predicates);
				}
			};
		}
		return searchOrganID;
	}

	public ExtMsg endiableUser(String userid) {
		Tb_user user = userRepository.findByUserid(userid);
		Long status = user.getStatus();
		if (status == 1) {
			user.setStatus(0L);
			Tb_user userMsg=new Tb_user();
			userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
			userMsg.setUserid(userid);
			return new ExtMsg(true, "禁用用户成功", userMsg);
		} else {
			if("外来人员".equals(user.getOutuserstate())){
				user.setOutuserstarttime(new Date());
	            if (user.getInfodate() != null) {
	            	Date date = new Date();
					Calendar rightNow = Calendar.getInstance();
		            rightNow.setTime(date);
		            
		            if (user.getInfodate().equals("一天")) {
		            	rightNow.add(Calendar.DAY_OF_MONTH, 1);
		            } else if (user.getInfodate().equals("一周")) {
		            	rightNow.add(Calendar.WEEK_OF_MONTH, 1);
		            } else {// 一月
		            	rightNow.add(Calendar.MONTH, 1);
		            }
		            Date dt1 = rightNow.getTime();
		            user.setExdate(dt1);
	            }
			}
			user.setStatus(1L);
			Tb_user userMsg=new Tb_user();
			userMsg.setUserid(userid);
			userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
			return new ExtMsg(true, "启动用户成功", userMsg);
		}
	}

	public ExtMsg setNewTime(String userid,String expiryDate) {
		Tb_user user = userRepository.findByUserid(userid);
			if("外来人员".equals(user.getOutuserstate())){
				user.setOutuserstarttime(new Date());

					Date date = new Date();
					Calendar rightNow = Calendar.getInstance();
					rightNow.setTime(date);

					if (expiryDate.equals("一天")) {
						rightNow.add(Calendar.DAY_OF_MONTH, 1);
					} else if (expiryDate.equals("一周")) {
						rightNow.add(Calendar.WEEK_OF_MONTH, 1);
					} else {// 一月
						rightNow.add(Calendar.MONTH, 1);
					}
					Date dt1 = rightNow.getTime();
					user.setExdate(dt1);
					user.setInfodate(expiryDate);
					user.setStatus(1L);
			}
			return new ExtMsg(true, "启动用户成功", user);
	}




	public String[] getFilteredUseridByOrgan(String[] userids) {
		// 1.查找当前用户单位所属的所有用户，包括其他部门
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid = userDetails.getUserid();
		// 1.1查找当前用户所属单位，需要网上找，当前机构可能为部门
		String organid = userRepository.findOrganidByUserid(userid);
		Tb_right_organ organ = rightOrganRepository.findOne(organid);
		while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
			organ = rightOrganRepository.findOne(organ.getParentid());
		}
		// 1.2查找当前机构下的所有用户
		List<String> organidList = organService.getOrganidLoop(organ.getOrganid(), true, new ArrayList<>());
		organidList.add(organ.getOrganid());
		String[] organs = new String[organidList.size()];
		Specification<Tb_user> specification = getUsers(organidList.toArray(organs));
		List<Tb_user> useridList = userRepository.findAll(Specifications.where(specification));
		// 2.获取同单位下当前环节的用户
		List<String> resultList = new ArrayList<>();
		for (int i = 0; i < userids.length; i++) {
			for (int j = 0; j < useridList.size(); j++) {
				if (userids[i].equals(useridList.get(j).getUserid())) {
					resultList.add(userids[i]);
					break;
				}
			}
		}
		String[] resultArr = new String[resultList.size()];
		resultList.toArray(resultArr);
		return resultArr;
	}

    public String[] getFilteredUseridByOrgans(String[] userids,String organids) {
        String[] organid = organids.split(",");
        List<Tb_right_organ> organlist = rightOrganRepository.findByOrganid(organid);
        //查找当前条目所属单位，需要往上找，当前机构可能为部门
        List<String> list = new ArrayList<>();
        for(int i=0;i<organlist.size();i++){
            Tb_right_organ organ = organlist.get(i);
            while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
                organ = rightOrganRepository.findOne(organ.getParentid());
            }
            list.add(organ.getOrganid());
        }
        // 1.2查找当前机构下的所有用户
        List<String> organidList = organService.getOrganidLoop(list.get(0), true, new ArrayList<>());
        organidList.add(list.get(0));
        String[] organs = new String[organidList.size()];
        Specification<Tb_user> specification = getUsers(organidList.toArray(organs));
        List<Tb_user> useridList = userRepository.findAll(Specifications.where(specification));
        // 2.获取同单位下当前环节的用户
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < userids.length; i++) {
            for (int j = 0; j < useridList.size(); j++) {
                if (userids[i].equals(useridList.get(j).getUserid())) {
                    resultList.add(userids[i]);
                    break;
                }
            }
        }
        String[] resultArr = new String[resultList.size()];
        resultList.toArray(resultArr);
        return resultArr;
    }

	// 移动用户
	public ExtMsg changeOrgan(String[] userIds, String refid) {
		for (String userId : userIds) {
			userRepository.changeOrgan(userId, refid);
		}
		Tb_user userMsg=new Tb_user();
		userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
		userMsg.setUserid(String.join(",",userIds));
		userMsg.setOrganid(refid);
		return new ExtMsg(true, "", userMsg);
	}

	/**
	 * 验证机构是否已有三员
	 * 
	 * @param organId
	 * @return
	 */
	public ExtMsg addAdminValidation(String organId) {
		Set<String> realNameSet = userRepository.getRealNameByOrganid(organId);
		boolean isfind = false;
		for(String name:realNameSet){
			if(name.indexOf("安全保密管理员")>-1||name.indexOf("系统管理员")>-1||name.indexOf("安全审计员")>-1){
				isfind=true;
				break;
			}
		}
		return new ExtMsg(isfind, "", null);
	}

	/**
	 * 新增三员
	 * 
	 * @param secretAdmin
	 * @param systemAdmin
	 * @param auditor
	 * @param organId
	 * @return
	 */
	public ExtMsg addAmin(String secretAdmin, String systemAdmin, String auditor, String xitongname, String aqbmname,
			String aqsjname, String organId) {
		// ---------START---------添加三个用户
		List<Tb_user> userAddList = new ArrayList<>();
		Tb_user secretAdminUser = new Tb_user();
		secretAdminUser.setLoginname(secretAdmin);
		secretAdminUser.setRealname(aqbmname);
		secretAdminUser.setCreatetime(new Date());
		secretAdminUser.setLoginpassword(MD5.MD5("555"));
		secretAdminUser.setOrganid(organId);
		secretAdminUser.setStatus(1L);
		secretAdminUser.setUsertype(1 + "");
		Integer orders = userRepository.findOrdersByOrganid(organId);
		orders = ((orders == null || orders < 0) ? 0 : orders) + 1;
		secretAdminUser.setSortsequence(orders);
		userAddList.add(secretAdminUser);

		Tb_user systemAdminUser = new Tb_user();
		BeanUtils.copyProperties(secretAdminUser, systemAdminUser);
		systemAdminUser.setLoginname(systemAdmin);
		systemAdminUser.setRealname(xitongname);
		systemAdminUser.setSortsequence(orders + 1);
		userAddList.add(systemAdminUser);

		Tb_user auditorUser = new Tb_user();
		BeanUtils.copyProperties(secretAdminUser, auditorUser);
		auditorUser.setLoginname(auditor);
		auditorUser.setRealname(aqsjname);
		auditorUser.setSortsequence(orders + 2);
		userAddList.add(auditorUser);

		List<Tb_user> syUsers = userRepository.save(userAddList);
		// ---------END---------添加三个用户

		// ---------START---------添加三员用户默认快捷方式
		List<Tb_Icon> icons = new ArrayList();
		List<Tb_Icon> syIcons = iconRepository.findByUseridInOrderBySortsequence(new String[] { "1", "2", "3" });
		String userId = "";
		String userids="";//用于同步到其他系统
		for (Tb_user user : syUsers) {
			for (Tb_Icon icon : syIcons) {
				if ("系统管理员".equals(user.getRealname()) && "1".equals(icon.getUserid())) {
					userId = user.getUserid();
					userids+=userId+",";
				} else if ("安全保密管理员".equals(user.getRealname()) && "2".equals(icon.getUserid())) {
					userId = user.getUserid();
					userids+=userId+",";
				} else if ("安全审计员".equals(user.getRealname()) && "3".equals(icon.getUserid())) {
					userId = user.getUserid();
					userids+=userId+",";
				}

				if (!"".equals(userId)) {
					icons.add(new Tb_Icon(icon.getId(), icon.getPid(), icon.getUrl(), icon.getCode(), icon.getTkey(),
							icon.getIcon(), icon.getText(), user.getUserid(), icon.getOrders()));
					userId = "";
				}
			}
		}
		if(!"".equals(userids)){
			userids=userids.substring(0,userids.length()-1);
		}
		iconRepository.save(icons);
		// ---------END---------添加三员用户默认快捷方式

		// ---------START---------设置三个用户为三员角色
		List<Tb_user_role> userRoleList = new ArrayList<>();
		List<Tb_role> roleList = roleRepository.findByRolenameIn(new String[] { "安全保密管理员", "系统管理员", "安全审计员" });
		String secretRoleid = "", systemRoleid = "", auditorRoleid = "";
		for (Tb_role role : roleList) {
			if (role.getRolename().equals("安全保密管理员")) {
				secretRoleid = role.getRoleid();
			} else if (role.getRolename().equals("系统管理员")) {
				systemRoleid = role.getRoleid();
			} else {
				auditorRoleid = role.getRoleid();
			}
		}

		Tb_user_role secretUserRole = new Tb_user_role();
		secretUserRole.setRoleid(secretRoleid);
		secretUserRole.setUserid(secretAdminUser.getUserid());
		userRoleList.add(secretUserRole);

		Tb_user_role systemUserRole = new Tb_user_role();
		systemUserRole.setRoleid(systemRoleid);
		systemUserRole.setUserid(systemAdminUser.getUserid());
		userRoleList.add(systemUserRole);

		Tb_user_role auditorUserRole = new Tb_user_role();
		auditorUserRole.setRoleid(auditorRoleid);
		auditorUserRole.setUserid(auditorUser.getUserid());
		userRoleList.add(auditorUserRole);

		userRoleRepository.save(userRoleList);
		// ---------END---------设置三个用户为三员角色

		// ---------START---------为"安全保密管理员"、"系统管理员"设置机构权限
		List<Tb_user_organ> userOrganList = new ArrayList<>();
		SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_user_organ> myUserOrgan = userOrganRepository.findByUserid(user.getUserid());// 自身拥有机构权限
		Set<String> organIds = new HashSet<>();
		for (Tb_user_organ userOrgan : myUserOrgan) {
			organIds.add(userOrgan.getOrganid());
		}
		List<Tb_right_organ> rightOrganList = new ArrayList<>();
		rightOrganList = filterOrgan(getAllOrgan(organId, rightOrganList), organIds);

		rightOrganList.add(rightOrganRepository.findByOrganid(organId));// 加上父节点
		for (Tb_right_organ rightOrgan : rightOrganList) {
			Tb_user_organ secretUserOrgan = new Tb_user_organ();
			secretUserOrgan.setUserid(secretAdminUser.getUserid());
			secretUserOrgan.setOrganid(rightOrgan.getOrganid());
			userOrganList.add(secretUserOrgan);

			Tb_user_organ systemUserOrgan = new Tb_user_organ();
			systemUserOrgan.setUserid(systemAdminUser.getUserid());
			systemUserOrgan.setOrganid(rightOrgan.getOrganid());
			userOrganList.add(systemUserOrgan);
		}
		userOrganRepository.save(userOrganList);
		// ---------END---------为"安全保密管理员"、"系统管理员"设置机构权限

		// ---------START---------为"安全保密管理员"、"系统管理员"设置数据节点权限
		Set<String> organIdSet = new HashSet<>();
		for (Tb_right_organ right_organ : rightOrganList) {
			organIdSet.add(right_organ.getOrganid());
		}
		List<Tb_user_data_node> userDataNodeList = new ArrayList<>();
		List<Tb_data_node> nodeList = dataNodeRepository.findAll();
		for (Tb_data_node dataNode : nodeList) {
			if (dataNode.getNodetype() == 2 || organIdSet.contains(dataNode.getRefid())) {// 所有分类+部分机构
				Tb_user_data_node secretUserDataNode = new Tb_user_data_node();
				secretUserDataNode.setUserid(secretAdminUser.getUserid());
				secretUserDataNode.setNodeid(dataNode.getNodeid());
				userDataNodeList.add(secretUserDataNode);

				Tb_user_data_node systemUserDataNode = new Tb_user_data_node();
				systemUserDataNode.setUserid(systemAdminUser.getUserid());
				systemUserDataNode.setNodeid(dataNode.getNodeid());
				userDataNodeList.add(systemUserDataNode);
			}
		}
		userDataNodeRepository.save(userDataNodeList);
		// ---------END---------为"安全保密管理员"、"系统管理员"设置数据节点权限

		Tb_user userMsg=new Tb_user();
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String adminUserid = userDetails.getUserid();
		userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
		userMsg.setUserid(userids);
		userMsg.setOrganid(organId);
		userMsg.setRemark(adminUserid);
		return new ExtMsg(true, "添加三员成功", userMsg);
	}

	public List<Tb_right_organ> getAllOrgan(String organId, List<Tb_right_organ> returnList) {
		List<Tb_right_organ> rightOrganList = rightOrganRepository.findByParentid(organId);
		for (Tb_right_organ rightOrgan : rightOrganList) {
			returnList.add(rightOrgan);
			getAllOrgan(rightOrgan.getOrganid(), returnList);
		}
		return returnList;
	}

	public List<Tb_right_organ> filterOrgan(List<Tb_right_organ> rightOrganList, Set<String> organIds) {
		List<Tb_right_organ> returnList = new ArrayList<>();
		for (Tb_right_organ rightOrgan : rightOrganList) {
			if (organIds.contains(rightOrgan.getOrganid())) {
				returnList.add(rightOrgan);
			}
		}
		return returnList;
	}

	public boolean deleteAdmin(String organId) {
		List<Tb_user> userList = userRepository.getByOrganidAndRealname(organId, "安全保密管理员", "系统管理员", "安全审计员");
		String loginName = userList.get(0).getLoginname();
		if (loginName.equals("aqbm") || loginName.equals("xitong") || loginName.equals("aqsj")) {
			return false;
		}
		String[] adminIdArray = GainField.getFieldValues(userList, "userid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(userList, "userid");
		userRoleRepository.deleteAllByUseridIn(adminIdArray);
		userOrganRepository.deleteAllByUseridIn(adminIdArray);
		userDataNodeRepository.deleteAllByUseridIn(adminIdArray);
		userGroupRepository.deleteAllByUseridIn(adminIdArray);
		userFunctionRepository.deleteAllByUseridIn(adminIdArray);
		userNodeRepository.deleteAllByUseridIn(adminIdArray);
		personalizedRepository.deleteByUseridIn(adminIdArray);
		userRepository.deleteAllByUseridIn(adminIdArray);
		return true;
	}

	public void authorizeByRole(String roleid, boolean isIncluded) {
		userDataNodeRepository.deleteByRoleId(roleid);//删除原权限数据

		List<Tb_user_data_node> saveUdn = new ArrayList<>();//保存的节点权限
		List<Tb_user_role> userRoleList = userRoleRepository.findByRoleid(roleid);
		List<Tb_data_node> dn = dataNodeRepository.findAll();
		if (isIncluded) {
			for (Tb_user_role ur : userRoleList) {
				Set<String> showSet = new HashSet<>();
				String organid = userRepository.findByUserid(ur.getUserid()).getOrganid();
				showSet.add(organid);
				List<Tb_right_organ> childOrganList = rightOrganRepository.findByParentid(organid);
				while (childOrganList.size() != 0) {
					String[] childids = GainField.getFieldValues(childOrganList, "organid").length == 0 ? new String[]{""} : GainField.getFieldValues(childOrganList, "organid");
					showSet.addAll(new HashSet<>(Arrays.asList(childids)));
					childOrganList = rightOrganRepository.findByParentidIn(childids);
				}

				for (Tb_data_node node : dn) {
					if (node.getNodetype() == 2 || showSet.contains(node.getRefid())) {
						Tb_user_data_node udn = new Tb_user_data_node();
						udn.setNodeid(node.getNodeid());
						udn.setUserid(ur.getUserid());
						saveUdn.add(udn);
					}
				}
			}
		} else {
			for (Tb_user_role ur : userRoleList) {
				String organid = userRepository.findByUserid(ur.getUserid()).getOrganid();
				for (Tb_data_node node : dn) {
					if (node.getNodetype() == 2 || node.getRefid().equals(organid)) {
						Tb_user_data_node udn = new Tb_user_data_node();
						udn.setNodeid(node.getNodeid());
						udn.setUserid(ur.getUserid());
						saveUdn.add(udn);
					}
				}
			}
		}

		userDataNodeRepository.save(saveUdn);
	}

	public void authorizeByNode(String nodeid, boolean isIncluded) {
		userDataNodeRepository.deleteByWorkNodeId(nodeid);//删除原权限数据

		List<Tb_user_data_node> saveUdn = new ArrayList<>();//保存的节点权限
		List<Tb_user_node> userNodeList = userNodeRepository.findByNodeid(nodeid);
		List<Tb_data_node> dn = dataNodeRepository.findAll();
		if (isIncluded) {
			for (Tb_user_node un : userNodeList) {
				Set<String> showSet = new HashSet<>();
				String organid = userRepository.findByUserid(un.getUserid()).getOrganid();
				showSet.add(organid);
				List<Tb_right_organ> childOrganList = rightOrganRepository.findByParentid(organid);
				while (childOrganList.size() != 0) {
					String[] childids = GainField.getFieldValues(childOrganList, "organid").length == 0 ? new String[]{""} : GainField.getFieldValues(childOrganList, "organid");
					showSet.addAll(new HashSet<>(Arrays.asList(childids)));
					childOrganList = rightOrganRepository.findByParentidIn(childids);
				}

				for (Tb_data_node node : dn) {
					if (node.getNodetype() == 2 || showSet.contains(node.getRefid())) {
						Tb_user_data_node udn = new Tb_user_data_node();
						udn.setNodeid(node.getNodeid());
						udn.setUserid(un.getUserid());
						saveUdn.add(udn);
					}
				}
			}
		} else {
			for (Tb_user_node un : userNodeList) {
				String organid = userRepository.findByUserid(un.getUserid()).getOrganid();
				for (Tb_data_node node : dn) {
					if (node.getNodetype() == 2 || node.getRefid().equals(organid)) {
						Tb_user_data_node udn = new Tb_user_data_node();
						udn.setNodeid(node.getNodeid());
						udn.setUserid(un.getUserid());
						saveUdn.add(udn);
					}
				}
			}
		}

		userDataNodeRepository.save(saveUdn);
	}

	public Page<Tb_user> findByUserids(int page, int limit, String id, Sort sort){
		List<String> organidList = new ArrayList<>();
		if (id != null && !id.equals("")) {
			for (int i = 0; i < id.split(",").length; i++) {
				organidList.add(id.split(",")[i]);
			}
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort==null?new Sort("sortsequence"):sort);
		return  userRepository.findByUserid(organidList,pageRequest);
	}

	public void findBySortquence(String[] userid, int currentcount,String operate){
		List<Tb_user> userlist = userRepository.findByUserid(userid);
		if(operate.equals("up")) {
			Tb_user upuser = userlist.get(currentcount);
			Tb_user downuser = userlist.get(currentcount - 1);
			int count = upuser.getSortsequence();
			upuser.setSortsequence(downuser.getSortsequence());
			downuser.setSortsequence(count);
		}else if(operate.equals("down")){
			Tb_user upuser = userlist.get(currentcount);
			Tb_user downuser = userlist.get(currentcount + 1);
			int count = upuser.getSortsequence();
			upuser.setSortsequence(downuser.getSortsequence());
			downuser.setSortsequence(count);
		}
	}

	/**
	 * 初始化系统机构,全宗,密码
	 * loginService类jpa实体数据更新数据库问题,移至此处
	 * @param organname  机构名
	 * @param fundsname  全宗名
	 * @param fundscode  全宗号
	 * @param xtpwd      系统用户密码
	 * @param bmpwd      安全保密用户密码
	 * @param sjpwd      安全审计用户密码
	 * @param dapwd      默认用户密码
	 * @param consistent 多用户密码一致
	 * @return           修改状态
	 */
	public String initSysData(String organname,String fundsname,String fundscode,
							  String xtpwd,String bmpwd,String sjpwd,String dapwd,String shpwd,boolean consistent){
		List<Tb_user> users = userRepository.findByLoginnameIn(new String[]{"xitong","aqbm","aqsj","dauser","shuser"});
		Tb_right_organ organ = rightOrganRepository.findByOrgannameAndIsinit("全宗单位","0");
		Tb_funds funds = fundsRepository.findByFundsnameAndIsinit("全宗单位","0");
		String msg = "修改失败";
		try{
			if(consistent){
				for(Tb_user user:users){
					user.setLoginpassword(MD5.MD5(xtpwd));
				}
			}else{
				for (Tb_user user:users){
					switch (user.getLoginname()){
						case "xitong":
							user.setLoginpassword(MD5.MD5(xtpwd));
							break;
						case "aqbm":
							user.setLoginpassword(MD5.MD5(bmpwd));
							break;
						case "aqsj":
							user.setLoginpassword(MD5.MD5(sjpwd));
							break;
						case "dauser":
							user.setLoginpassword(MD5.MD5(dapwd));
							break;
						case "shuser":
							user.setLoginpassword(MD5.MD5(shpwd));
							break;
						default:
							break;
					}

				}
			}

			if(organ!=null){
				List<Tb_data_node> nodes = dataNodeRepository.findByOrganid(organ.getOrganid());
				for(Tb_data_node node:nodes){
					node.setNodename(organname);
				}
				boolean state = false;
				if(!"".equals(organname)){
					organ.setOrganname(organname);
					state = true;
				}

				if(state){
					organ.setIsinit("1");
				}
			}

			if(funds!=null){
				boolean state = false;
				if(!"".equals(fundsname)){
					funds.setFundsname(fundsname);
					state = true;
				}
				if(!"".equals(fundscode)){
					funds.setFunds(fundscode);
					state = true;
				}
				if(state){
					funds.setOrganname(organname);
					funds.setIsinit("1");
				}
			}
			msg = "修改成功";
		}catch (Exception e){
			e.printStackTrace();
		}
		return msg;
	}

	public void PlatformChange(String userid,String changtype){
		Tb_user  user = userRepository.findByUserid(userid);
		user.setPlatformchange(changtype);
	}

	public Tb_borrowdoc getBorrowInform(String docid){
		Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(docid);
		return borrowdoc;
	}

	public String getUserOrgan(String username){
		String organid = "";
		try{
			Tb_user user = userRepository.findByLoginname(username);
			if (user!=null){
				organid = user.getOrganid();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return organid;
	}

	/**
	 * 修改外来人员用户
	 *
	 * @param user
	 * @return
	 */
	public Integer userOutEditSubmit(Tb_user user) {
		Date date = new Date();
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		if (user.getInfodate().equals("一天")) {
			rightNow.add(Calendar.DAY_OF_MONTH, 1);
		} else if (user.getInfodate().equals("一周")) {
			rightNow.add(Calendar.WEEK_OF_MONTH, 1);
		} else {// 一月
			rightNow.add(Calendar.MONTH, 1);
		}
		int i = userRepository.updateOutNameById(user.getLoginname(), user.getRealname(), user.getPhone(),
				user.getAddress(), user.getSex(), user.getUsertype(),user.getLetternumber(),user.getRemark(),
				user.getUserid(),user.getBirthday(),user.getEthnic(),user.getInfodate(),rightNow.getTime());
		return i;
	}

	/**
	 * 根据机构获取用户（过滤掉三员）和源用户
	 *
	 * @param organid
	 * @return
	 */
	public List<ExtNcTree> getCopyUser(String organid, String sourceId,String username) {
		List<ExtNcTree> list = new ArrayList<>();
		List<Tb_user> users;
		Specification<Tb_user> specification = null;
		Specification<Tb_user> specificationUserName = null;
		if(username!=null&&!"".equals(username)){
			specificationUserName = Specifications.where(new SpecificationUtil("realname","like",username));
		}
		if (organid == null || "0".equals(organid.trim())) {
			specification = Specifications.where(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName);
			users = userRepository.findAll(specification);
		} else {
			Tb_right_organ organ = rightOrganRepository.findByOrganid(organid);
			List<String> organidList = organService.getOrganidLoop(organid, true, new ArrayList<String>());
			organidList.add(organid);
			String[] organs = new String[organidList.size()];
			specification = getUsers(organidList.toArray(organs));
			if(!"外来人员部门".equals(organ.getOrganname())){
				users = userRepository.findAll(Specifications.where(specification).and(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName));
			}else{
				users = userRepository.findAll(Specifications.where(specification).and(specificationUserName));
			}
		}
		List<String> userList = new ArrayList<String>();
		if (users.size() > 0) {
			for (int i = 0; i < users.size(); i++) {
				Tb_user user = users.get(i);
				String name = user.getRealname();
				if (name.contains("安全保密管理员") || name.contains("系统管理员") || name.contains("安全审计员") || (sourceId != null && user.getUserid().trim().equals(sourceId.trim()))) {
					continue;
				}
				userList.add(user.getRealname() + " and " + user.getUserid()); //从用 "-" 连接 改为用"and"，因为子三员realname也用"-"链接
			}
		}
		String[] strings = new String[userList.size()];
		String[] arrStrings = userList.toArray(strings);
		// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
		Arrays.sort(arrStrings, comparator);
		for (int i = 0; i < arrStrings.length; i++) {
			ExtNcTree tree = new ExtNcTree();
			String[] teStrings = arrStrings[i].split(" and "); //从用 "-" 连接 改为用"and"，因为子三员realname也用"-"链接
			tree.setFnid(teStrings[1]);
			tree.setText(teStrings[0]);
			list.add(tree);
		}
		return list;
	}


	/**
	 * 根据机构获取用户（过滤掉三员）和源用户
	 *
	 * @param organid
	 * @return
	 */
	public List<ExtNcTree> getSxCopyUser(String organid, String sourceId,String username) {
		List<ExtNcTree> list = new ArrayList<>();
		List<Tb_user_sx> users;
		Specification<Tb_user_sx> specification = null;
		Specification<Tb_user_sx> specificationUserName = null;
		if(username!=null&&!"".equals(username)){
			specificationUserName = Specifications.where(new SpecificationUtil("realname","like",username));
		}
		if (organid == null || "0".equals(organid.trim())) {
			specification = Specifications.where(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName);
			users = sxUserRepository.findAll(specification);
		} else {
			Tb_right_organ_sx organ = sxRightOrganRepository.findByOrganid(organid);
			List<String> organidList = organService.getSxOrganidLoop(organid, true, new ArrayList<String>());
			organidList.add(organid);
			String[] organs = new String[organidList.size()];
			specification = getSxUsers(organidList.toArray(organs));
			if(!"外来人员部门".equals(organ.getOrganname())){
				users = sxUserRepository.findAll(Specifications.where(specification).and(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName));
			}else{
				users = sxUserRepository.findAll(Specifications.where(specification).and(specificationUserName));
			}
		}
		List<String> userList = new ArrayList<String>();
		if (users.size() > 0) {
			for (int i = 0; i < users.size(); i++) {
				Tb_user_sx user = users.get(i);
				String name = user.getRealname();
				if (name.equals("安全保密管理员") || name.equals("系统管理员") || name.equals("安全审计员") || (sourceId != null && user.getUserid().trim().equals(sourceId.trim()))) {
					continue;
				}
				userList.add(user.getRealname() + "-" + user.getUserid());
			}
		}
		String[] strings = new String[userList.size()];
		String[] arrStrings = userList.toArray(strings);
		// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
		Arrays.sort(arrStrings, comparator);
		for (int i = 0; i < arrStrings.length; i++) {
			ExtNcTree tree = new ExtNcTree();
			String[] teStrings = arrStrings[i].split("-");
			tree.setFnid(teStrings[1]);
			tree.setText(teStrings[0]);
			list.add(tree);
		}
		return list;
	}

	/**
	 * 执行 复制授权
	 *
	 * @param sourceId
	 * @param copys
	 * @param dataCheck
	 * @param organCheck
	 * @param fnCheck
	 * @return
	 */
	public ExtMsg copyUser(String sourceId, String[] copys, boolean dataCheck, boolean organCheck, boolean fnCheck,
						   boolean roleCheck, boolean nodeCheck, boolean fileCheck) {
		if (dataCheck) {//数据
			List<String> udnSourceList = userDataNodeRepository.findByUserid(sourceId);
			List<Tb_user_data_node> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String nodeId : udnSourceList) {
					Tb_user_data_node newUdn = new Tb_user_data_node();
					newUdn.setNodeid(nodeId);
					newUdn.setUserid(copys[i]);
					saveList.add(newUdn);
				}
			}
			userDataNodeRepository.deleteAllByUseridIn(copys);//删除原有
			userDataNodeRepository.save(saveList);
		}
		if (organCheck) {//机构
			List<Tb_user_organ> uoSourceList = userOrganRepository.findByUserid(sourceId);
			List<Tb_user_organ> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (Tb_user_organ uo : uoSourceList) {
					Tb_user_organ newUo = new Tb_user_organ();
					newUo.setOrganid(uo.getOrganid());
					newUo.setUserid(copys[i]);
					saveList.add(newUo);
				}
			}
			userOrganRepository.deleteAllByUseridIn(copys);
			userOrganRepository.save(saveList);
		}
		if (fnCheck) {//功能
			List<String> ufSourceList = userFunctionRepository.findByUserid(sourceId);
			List<Tb_user_function> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String fnid : ufSourceList) {
					Tb_user_function newUf = new Tb_user_function();
					newUf.setFnid(fnid);
					newUf.setUserid(copys[i]);
					saveList.add(newUf);
				}
			}
			userFunctionRepository.deleteAllByUseridIn(copys);
			userFunctionRepository.save(saveList);
		}
		if (roleCheck) {//角色
			List<String> urSourceList = userRoleRepository.findByUserid(sourceId);
			List<Tb_user_role> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String roleid : urSourceList) {
					Tb_user_role newUr = new Tb_user_role();
					newUr.setRoleid(roleid);
					newUr.setUserid(copys[i]);
					saveList.add(newUr);
				}
			}
			userRoleRepository.deleteAllByUseridIn(copys);
			userRoleRepository.save(saveList);
		}
		if (nodeCheck) {//工作流
			List<String> sourceList = userNodeRepository.findByUserid(sourceId);
			List<Tb_user_node> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String nodeid : sourceList) {
					Tb_user_node newUn = new Tb_user_node();
					newUn.setNodeid(nodeid);
					newUn.setUserid(copys[i]);
					saveList.add(newUn);
				}
			}
			userNodeRepository.deleteAllByUseridIn(copys);
			userNodeRepository.save(saveList);
		}
		if(fileCheck){
			List<Tb_ele_function> eleFunction = eleFunctionRepository.findByUserid(sourceId);
			List<Tb_ele_function> eleList = new ArrayList<>();
			for (Tb_ele_function ele : eleFunction) {
				for(int i = 0; i<copys.length; i++){
					Tb_ele_function eleUser = new Tb_ele_function();
					if (ele.getPlatform().equals("利用平台")) {
						eleUser.setDownload(ele.getDownload());
						eleUser.setDownloadAll(ele.getDownloadAll());
						eleUser.setPrint(ele.getPrint());
						eleUser.setPrintBatch(ele.getPrintBatch());
						eleUser.setUserid(copys[i]);
						eleUser.setPlatform("利用平台");
					}
					else if (ele.getPlatform().equals("管理平台")) {
						eleUser.setDownload(ele.getDownload());
						eleUser.setDownloadAll(ele.getDownloadAll());
						eleUser.setPrint(ele.getPrint());
						eleUser.setPrintBatch(ele.getPrintBatch());
						eleUser.setUpload(ele.getUpload());
						eleUser.setUp(ele.getUp());
						eleUser.setDown(ele.getDown());
						eleUser.setDel(ele.getDel());
						eleUser.setLookhistory(ele.getLookhistory());
						eleUser.setUserid(copys[i]);
						eleUser.setPlatform("管理平台");
					}
					eleList.add(eleUser);
				}
			}
			eleFunctionRepository.deleteAllByUseridIn(copys);
			eleFunctionRepository.save(eleList);
		}


		return new ExtMsg(true, "复制成功", null);
	}

	public ExtMsg copySxUser(String sourceId, String[] copys, boolean dataCheck, boolean organCheck, boolean fnCheck,
						   boolean roleCheck, boolean nodeCheck, boolean fileCheck) {
		if (dataCheck) {//数据
			List<String> udnSourceList = sxUserDataNodeRepository.findSxByUserid(sourceId);
			List<Tb_user_data_node_sx> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String nodeId : udnSourceList) {
					Tb_user_data_node_sx newUdn = new Tb_user_data_node_sx();
					newUdn.setNodeid(nodeId);
					newUdn.setUserid(copys[i]);
					saveList.add(newUdn);
				}
			}
			sxUserDataNodeRepository.deleteAllByUseridIn(copys);//删除原有
			sxUserDataNodeRepository.save(saveList);
		}
		if (organCheck) {//机构
			List<Tb_user_organ_sx> uoSourceList = sxUserOrganRepository.findByUserid(sourceId);
			List<Tb_user_organ_sx> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (Tb_user_organ_sx uo : uoSourceList) {
					Tb_user_organ_sx newUo = new Tb_user_organ_sx();
					newUo.setOrganid(uo.getOrganid());
					newUo.setUserid(copys[i]);
					saveList.add(newUo);
				}
			}
			sxUserOrganRepository.deleteAllByUseridIn(copys);
			sxUserOrganRepository.save(saveList);
		}
		if (fnCheck) {//功能
			List<String> ufSourceList = sxUserFunctionRepository.findByUserid(sourceId);
			List<Tb_user_function_sx> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String fnid : ufSourceList) {
					Tb_user_function_sx newUf = new Tb_user_function_sx();
					newUf.setFnid(fnid);
					newUf.setUserid(copys[i]);
					saveList.add(newUf);
				}
			}
			sxUserFunctionRepository.deleteAllByUseridIn(copys);
			sxUserFunctionRepository.save(saveList);
		}
		if (roleCheck) {//角色
			List<String> urSourceList = sxUserRoleRepository.findByUserid(sourceId);
			List<Tb_user_role_sx> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String roleid : urSourceList) {
					Tb_user_role_sx newUr = new Tb_user_role_sx();
					newUr.setRoleid(roleid);
					newUr.setUserid(copys[i]);
					saveList.add(newUr);
				}
			}
			sxUserRoleRepository.deleteAllByUseridIn(copys);
			sxUserRoleRepository.save(saveList);
		}
		if (nodeCheck) {//工作流
			List<String> sourceList = sxUserNodeRepository.findByUserid(sourceId);
			List<Tb_user_node_sx> saveList = new ArrayList<>();
			for (int i = 0; i < copys.length; i++) {
				for (String nodeid : sourceList) {
					Tb_user_node_sx newUn = new Tb_user_node_sx();
					newUn.setNodeid(nodeid);
					newUn.setUserid(copys[i]);
					saveList.add(newUn);
				}
			}
			sxUserNodeRepository.deleteAllByUseridIn(copys);
			sxUserNodeRepository.save(saveList);
		}
		if(fileCheck){
			List<Tb_ele_function> eleFunction = eleFunctionRepository.findByUserid(sourceId);
			List<Tb_ele_function> eleList = new ArrayList<>();
			for (Tb_ele_function ele : eleFunction) {
				for(int i = 0; i<copys.length; i++){
					Tb_ele_function eleUser = new Tb_ele_function();
					if (ele.getPlatform().equals("利用平台")) {
						eleUser.setDownload(ele.getDownload());
						eleUser.setDownloadAll(ele.getDownloadAll());
						eleUser.setPrint(ele.getPrint());
						eleUser.setPrintBatch(ele.getPrintBatch());
						eleUser.setUserid(copys[i]);
						eleUser.setPlatform("利用平台");
					}
					else if (ele.getPlatform().equals("管理平台")) {
						eleUser.setDownload(ele.getDownload());
						eleUser.setDownloadAll(ele.getDownloadAll());
						eleUser.setPrint(ele.getPrint());
						eleUser.setPrintBatch(ele.getPrintBatch());
						eleUser.setUpload(ele.getUpload());
						eleUser.setUp(ele.getUp());
						eleUser.setDown(ele.getDown());
						eleUser.setDel(ele.getDel());
						eleUser.setLookhistory(ele.getLookhistory());
						eleUser.setUserid(copys[i]);
						eleUser.setPlatform("管理平台");
					}
					eleList.add(eleUser);
				}
			}
			eleFunctionRepository.deleteAllByUseridIn(copys);
			eleFunctionRepository.save(eleList);
		}


		return new ExtMsg(true, "复制成功", null);
	}


	public ExtMsg copyCheck(String sourceId, String[] copys) {
		List<Tb_user> userList = new ArrayList<>();
		List<String[]> subAry = new InformService().subArray(copys, 1000);//处理ORACLE1000参数问题
		for (String[] ary : subAry) {
			userList.addAll(userRepository.getApprovingUsers(sourceId, ary));//获取仍有待审批流程，但会被除去相应工作流权限的用户
		}
		String userStr = "";
		if (userList.size() > 0) {
			int nameLimit = 8;//显示名字的个数限制
			int userCount = userList.size() > nameLimit ? nameLimit : userList.size();
			for (int i = 0; i < userCount; i++) {
				Tb_user user = userList.get(i);
				userStr += user.getRealname();
				if (i < userCount - 1) {
					userStr += ", ";
				} else if (userCount != userList.size()) {
					userStr += ", ......";
				}
			}
		}
		return new ExtMsg(userList.size() < 1, userStr, null);
	}

	public ExtMsg copySxCheck(String sourceId, String[] copys) {
		List<Tb_user_sx> userList = new ArrayList<>();
		List<String[]> subAry = new InformService().subArray(copys, 1000);//处理ORACLE1000参数问题
		for (String[] ary : subAry) {
			userList.addAll(sxUserRepository.getApprovingUsers(sourceId, ary));//获取仍有待审批流程，但会被除去相应工作流权限的用户
		}
		String userStr = "";
		if (userList.size() > 0) {
			int nameLimit = 8;//显示名字的个数限制
			int userCount = userList.size() > nameLimit ? nameLimit : userList.size();
			for (int i = 0; i < userCount; i++) {
				Tb_user_sx user = userList.get(i);
				userStr += user.getRealname();
				if (i < userCount - 1) {
					userStr += ", ";
				} else if (userCount != userList.size()) {
					userStr += ", ......";
				}
			}
		}
		return new ExtMsg(userList.size() < 1, userStr, null);
	}

	public ExtMsg countMax(String sourceId){
		int count = 0;
		if(!"".equals(sourceId)&&sourceId!=null){
			count = userDataNodeRepository.findCountByUserId(sourceId);
		}
		return new ExtMsg(true,"count",count);
	}

	public Map<String, Object> importUser(List<Tb_user> userlist,String parentid){
		Map<String, Object> resMap = new ListHashMap<>();
		//增加用户
		String userids="";
		for (int i = 0; i < userlist.size(); i++) {
			Tb_user tb_user = userlist.get(i);
			if("安全保密管理员".equals(tb_user.getRealname()) || "系统管理员".equals(tb_user.getRealname())|| "安全审计员".equals(tb_user.getRealname())){
				resMap.put("success", false);
				resMap.put("msg","请勿添加与三员用户");
				return resMap;
			}
			else if("xitong".equals(tb_user.getLoginname()) || "aqbm".equals(tb_user.getLoginname()) || "aqsj".equals(tb_user.getLoginname()) ){
				resMap.put("success", false);
				resMap.put("msg","请勿添加与三员用户");
				return resMap;
			}
			else if("".equals(tb_user.getLoginname()) || "".equals(tb_user.getRealname())){
				resMap.put("success", false);
				resMap.put("msg","登录名或者用户姓名没有填写");
				return resMap;
			}
			else if (tb_user.getLoginname().length()<3) {
				resMap.put("success", false);
				resMap.put("msg","登录名长度不允许少于3位");
				return resMap;
			}
			else if (tb_user.getLoginname().length()>30) {
				resMap.put("success", false);
				resMap.put("msg","登录名长度不允许超过30位");
				return resMap;
			}
			else if (tb_user.getRealname().length()>10) {
				resMap.put("success", false);
				resMap.put("msg","用户姓名字长度不允许超过10位");
				return resMap;
			}
			Integer orders = userRepository.findOrdersByOrganid(parentid);
			orders = ((orders==null || orders<0)?0:orders)+1;//若同级机构用户的orders最大值为空或负数，则转化为0，再+1
			if(userRepository.findCountByName(tb_user.getLoginname())==0){
				tb_user.setCreatetime(new Date());
				tb_user.setLoginpassword(MD5.MD5("555"));
				tb_user.setOrganid(parentid);
				tb_user.setStatus(1L);
				tb_user.setSortsequence(orders + i);
				tb_user.setUsertype("否".equals(tb_user.getUsertype()) ? "0" : "1");
				Tb_user user1 = addUser(tb_user);
				userids+=user1.getUserid()+",";
			}else {
				resMap.put("success", false);
				resMap.put("msg","请勿导入相同的账号");
				return resMap;
			}
		}
		if(!"".equals(userids)){
			userids=userids.substring(0,userids.length()-1);
			resMap.put("loginIp",iarchivesxSyncPath);
		}
		resMap.put("success", true);
		resMap.put("msg",userids);
		return resMap;
	}

	public void updateNodeTemp(String userid,String nodeid,String type) {
		if (userid != null && !"".equals("userid")) {
			String[] id = userid.split("-");
			if("top".equals(type)){  //调至顶端
				List<Tb_user_node_temp> node_temps = userNodeTempRepository.findByUseridInAndNodeidAndUniquetagOrderBySortsquence(id,nodeid,BatchModifyService.getUniquetag());
				userNodeTempRepository.modifyUserNodeTempOrderUp(node_temps.size(),1,node_temps.get(0).getSortsquence()-1); //更新目标之前的序号
				for(int i=0;i<node_temps.size();i++){
					node_temps.get(i).setSortsquence(i+1);
				}
			}else if("bottom".equals(type)){  //调至底部
				List<Tb_user_node_temp> node_tempAll = userNodeTempRepository.findByNodeidAndUniquetagAll(nodeid,BatchModifyService.getUniquetag());
				List<Tb_user_node_temp> node_temps = userNodeTempRepository.findUserByNodeidAndUniquetagDesc(id,nodeid,BatchModifyService.getUniquetag());
				userNodeTempRepository.modifyUserNodeTempOrderDown(node_temps.size(),node_temps.get(0).getSortsquence()+1,node_tempAll.size()); //更新目标之后的序号
				for(int i=0;i<node_temps.size();i++){
					node_temps.get(i).setSortsquence(node_tempAll.size()-i);
				}
			}else{  //上调或下调
				for(int i=0;i<id.length;i++){ //调整排序
					//调序目标
					Tb_user_node_temp node_tempTa = userNodeTempRepository.findByUseridAndNodeidAndUniquetag(id[i],nodeid,BatchModifyService.getUniquetag());
					int count = 0;
					if("up".equals(type)){ //上调
						count = node_tempTa.getSortsquence()-1;
					}else if("down".equals(type)){ //下调
						count = node_tempTa.getSortsquence()+1;
					}
					//被调序目标
					Tb_user_node_temp node_tempUd = userNodeTempRepository.findByNodeidAndUniquetagAndSortsquence(nodeid,BatchModifyService.getUniquetag(),count);
					int temp = node_tempTa.getSortsquence();
					node_tempTa.setSortsquence(node_tempUd.getSortsquence());
					node_tempUd.setSortsquence(temp);
				}
			}
		}
	}

	//获取用户
	public Tb_user getUser(String userid){
		return userRepository.findByUserid(userid);
	}

	/**
	 * 自制EXT tree格式表(权限接入)
	 * @param type
	 * @return
	 */
	public List<ExtTree> deviceJoinList(String type,String[] userids) {
		String hql = "";
		if (StringUtils.isNotBlank(type)) {
			hql += "SELECT new Device(de.id,de.name,de.type,de.typeName,de.enabled) FROM Device de WHERE de.typeName = " + "'" + type +"' ORDER BY de.typeName";
		}else{
			hql += "SELECT new Device(max(de.id),max(de.name),de.type,max(de.typeName)) FROM Device de"+""+" GROUP BY de.type ORDER BY de.type";
		}
		Query query = entityManager.createQuery(hql);
		List<Device> resultList = query.getResultList();
		List<ExtTree> trees = new ArrayList<>();
		List<Tb_user_devicepremiss> permissionsList = new ArrayList<>();
		boolean flagtype = false;
		if(userids!=null&&userids.length==1){
			permissionsList = userDevicePremissRepository.findByUseridIn(userids);
			flagtype = true;
		}

		for (Device device : resultList) {
			ExtTree tree = new ExtTree();
			tree.setDeviceid(device.getId());
			tree.setCls("folder");
			tree.setType(device.getType().getTypeName());
			//如果type不为null
			if(StringUtils.isNotBlank(type)){
				//设置用户是否只有一个
				if(flagtype){
					boolean flag = false;
					//判断设备是否有接入权限
					for(int i=0;i<permissionsList.size();i++){
						if(device.getId().trim().equals(permissionsList.get(i).getDeviceid().trim())){
							flag = true;
							break;
						}
					}
					if(flag){
						tree.setChecked(true);
					}
				}else{
					tree.setChecked(false);
				}
				tree.setLeaf(true); //叶子
				tree.setExpanded(false);//展开
				tree.setText(device.getName());
			}else{
				tree.setChecked(false);
				tree.setLeaf(false);//非叶子
				tree.setExpanded(false);//展开
				tree.setText(device.getTypeName());
			}

			trees.add(tree);
		}
		return trees;
	}

	/**
	 * 保存设备接入权限
	 * @param deviceList
	 * @return
	 */
	public List<Tb_user_devicepremiss> saveDeviceJoinAuthority(String[] deviceList,String[] userids) {
		userDevicePremissRepository.deleteByUseridIn(userids);  //删除设置接入权限
		List<Tb_user_devicepremiss> lotDevicePermissionsList = new ArrayList<>();
		if(deviceList == null){//没有选中设备
			lotDevicePermissionsList.add(new Tb_user_devicepremiss());
			return lotDevicePermissionsList;
		}
		for(int i=0;i<deviceList.length;i++){
			for(String userid : userids){
				Tb_user_devicepremiss lotDevicePermissions = new Tb_user_devicepremiss();
				lotDevicePermissions.setDeviceid(deviceList[i]);
				lotDevicePermissions.setUserid(userid);
				lotDevicePermissionsList.add(lotDevicePermissions);
			}
		}
		return userDevicePremissRepository.save(lotDevicePermissionsList);
	}

	/**
	 * 判断当前用户是否拥有设备接入权限
	 * @param
	 * @return
	 */
	public boolean isHasDevicePremissions(String deviceid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_user_devicepremiss device_permissions= userDevicePremissRepository.findByUseridAndAndDeviceid( userDetails.getUserid(),deviceid);  //删除设置接入权限
		if(device_permissions != null){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 自制EXT tree格式表
	 * @param type
	 * @return
	 */
	public List<ExtTree> deviceExtList(String type, String[] userIds) {
		String hql = "";
		if (StringUtils.isNotBlank(type)) {
			hql += "SELECT new Device(de.id,de.name,de.type,de.typeName) FROM Device de WHERE de.typeName = " + "'" + type +"' ORDER BY de.typeName";
		}else{
			hql += "SELECT new Device(max(de.id),max(de.name),de.type,max(de.typeName)) FROM Device de"+""+" GROUP BY de.type ORDER BY de.type";
		}
		Query query = entityManager.createQuery(hql);
		List<Device> resultList = query.getResultList();
		List<Tb_user_device> userDevices = new ArrayList<>();
		if(userIds!=null && userIds.length == 1){
			userDevices = userDeviceRepository.findByUserid(userIds[0]);
		}
		List<ExtTree> trees = new ArrayList<>();
		for (Device device : resultList) {
			ExtTree tree = new ExtTree();
			tree.setChecked(false);
			for (Tb_user_device userDevice : userDevices) {
				if(device.getId().trim().equals(userDevice.getDeviceid().trim())){
					tree.setChecked(true);
					continue;
				}
			}
			if(StringUtils.isNotBlank(type)){
				tree.setLeaf(true);
				tree.setExpanded(false);
			}else{
				tree.setLeaf(false);
				tree.setExpanded(false);
			}
			tree.setDeviceid(device.getId());
			tree.setCls("folder");
			tree.setType(device.getType().getTypeName());
			if(StringUtils.isNotBlank(type)){
				tree.setText(device.getName());
			}else{
				tree.setText(device.getTypeName());
			}
			trees.add(tree);
		}
		return trees;
	}

	/**
	 * 保存设备权限
	 * @param deviceList
	 * @param userIds
	 * @return
	 */
	public List<Tb_user_device> saveDeviceAuthority(String[] deviceList, String userIds) {
		String[] userArr = userIds.split(",");
		userDeviceRepository.deleteAllByUseridIn(userArr);
		if(deviceList == null){//没有选中设备
			List<Tb_user_device> userDevices = new ArrayList<>();
			userDevices.add(new Tb_user_device());
			return userDevices;
		}
		List<Tb_user_device> userDevices = new ArrayList<>();
		for (String userId : userArr) {
			for (String deviceId : deviceList) {
				Tb_user_device userDevice= new Tb_user_device();
				userDevice.setDeviceid(deviceId);
				userDevice.setUserid(userId);
				userDevices.add(userDevice);
			}
		}
		return userDeviceRepository.save(userDevices);
	}

	/**
	 * 按区域查找节点
	 * @return
	 */
	public List<ExtTree> findAreaList(String[] userId) {
		List<DeviceArea> deviceAreas = deviceAreaRepository.findAll();
		List<Tb_user_area> userAreas = new LinkedList<>();
		if(userId != null && userId.length == 1){
			userAreas = userAreaRepository.findByUserid(userId[0]);
		}
		List<ExtTree> trees = new LinkedList<>();
		for (DeviceArea deviceArea : deviceAreas) {
			ExtTree tree = new ExtTree();
			tree.setChecked(false);
			for (Tb_user_area userArea : userAreas) {
				if (deviceArea.getId().equals(userArea.getAreaid())) {
					tree.setChecked(true);
				}
			}
			tree.setAreaid(deviceArea.getId());
			tree.setLeaf(true);
			tree.setText(deviceArea.getName());
			tree.setExpanded(false);
			trees.add(tree);
		}
		return trees;
	}

	/**
	 * 保存区域权限
	 * @param areaList
	 * @param userId
	 * @return
	 */
	public List<Tb_user_area> saveAreaAuthority(String[] areaList, String userId) {
		String[] userArr = userId.split(",");
		userAreaRepository.deleteAllByUseridIn(userArr);
		if(areaList == null){
			List<Tb_user_area> userAreas = new LinkedList<>();
			userAreas.add(new Tb_user_area());
			return userAreas;
		}
		List<Tb_user_area> userAreas = new LinkedList<>();
		for (String user : userArr) {
			for (String area : areaList) {
				Tb_user_area userArea = new Tb_user_area();
				userArea.setUserid(user);
				userArea.setAreaid(area);
				userAreas.add(userArea);
			}
		}
		return  userAreaRepository.save(userAreas);
	}

	/**
	 * 获取个人的公告
	 *
	 * @param userDetails
	 *            安全对象
	 * @return
	 */
	public List<Tb_inform> findMyInforms(SecurityUser userDetails) {
		List<Tb_role> roles = userDetails.getRoles();
		String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(roles, "roleid");// 获取用户组id
		String[] userroleids = Arrays.copyOf(roleids, roleids.length + 1);// 添加用户id
		userroleids[userroleids.length - 1] = userDetails.getUserid();
		List<Tb_inform_user> inform_users = inFormUserRepository.findByUserroleidIn(userroleids);
		String[] informids = GainField.getFieldValues(inform_users, "informid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(inform_users, "informid");
		//加上公车预约以及场地预约取消的通知
		List<Tb_inform> page = inFormRepository.getInForms(informids);
		return page;
	}

    /**
     * 文件权限授权
     * @param userid
     * @return
     */
    public List<ExtTree> getWjList(String userid) {
        List<Tb_ele_function> eleFunction = eleFunctionRepository.findByUserid(userid);
        if(eleFunction==null||eleFunction.size() == 0){
            Tb_ele_function ele1= new Tb_ele_function(userid,"管理平台");
            Tb_ele_function ele2= new Tb_ele_function(userid,"利用平台");
            eleFunctionRepository.save(ele1);
            eleFunctionRepository.save(ele2);
            return getEleTree(userid);
        }
        else{
            return getEleTree(userid);
        }
    }

    /**
     * 获取文件权限树
     * @param userid
     * @return
     */
    public List<ExtTree> getEleTree(String userid){
        List<Tb_ele_function> eleFunction = eleFunctionRepository.findByUserid(userid);
        ExtTree[] trees = new ExtTree[eleFunction.size()];
        for (int i =0;i<eleFunction.size();i++) {
            ExtTree tree = new ExtTree();
            tree.setCls("folder");
            tree.setRoottype("root");
            tree.setText(eleFunction.get(i).getPlatform());
            tree.setExpanded(true);

            ExtTree[] childTree;
            if(eleFunction.get(i).getPlatform().equals("管理平台")){
                childTree=new ExtTree[6];
            }
            else{
                childTree=new ExtTree[4];
            }
            for(int j = 0;j<childTree.length;j++){
                ExtTree ctree = new ExtTree();
                switch (j){
                    case 0:	ctree.setText("下载");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getDownload().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 1:	ctree.setText("全部下载"); ctree.setLeaf(true);
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getDownloadAll().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 2:	ctree.setText("打印"); ctree.setLeaf(true);
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getPrint().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 3:	ctree.setText("批量打印");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getPrintBatch().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 4:
                        if(eleFunction.get(i).getPlatform().equals("管理平台")) {
                            ctree.setText("删除");
                            ctree.setLeaf(true);
                            ctree.setFnid("管理平台");
                            ctree.setChecked(eleFunction.get(i).getDel().equals("1"));
                        }
                        childTree[j] = ctree;
                        break;
                    case 5:
                        if(eleFunction.get(i).getPlatform().equals("管理平台")) {
                            ctree.setText("管理按钮");
                            ctree.setExpanded(true);
                            ctree.setLeaf(false);
                            ExtTree[] childcTree = new ExtTree[4];
                            boolean flag = false;
                            for (int k = 0; k < childcTree.length; k++) {
                                ExtTree cctree = new ExtTree();
                                switch (k) {
                                    case 0:
                                        cctree.setText("上传");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getUpload().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 1:
                                        cctree.setText("上移");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getUp().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 2:
                                        cctree.setText("下移");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getDown().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 3:
                                        cctree.setText("查看历史版本");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getLookhistory().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                }
                            }

                            ctree.setChecked(flag);
                            ctree.setChildren(childcTree);
                        }
                        childTree[j] = ctree;
                        break;
                    default:
                        break;
                }
            }
            if(childTree[0].isChecked() && childTree[1].isChecked() && childTree[2].isChecked()){
                tree.setChecked(true);
            }
            tree.setChildren(childTree);
            trees[i] = tree;
        }
        return Arrays.asList(trees);
    }

    /**
     * 获取文件权限(用户文件权限+用户组文件权限)
     * @param userid
     * @return
     */
    public List getWJQXbtn(String sysType,String userid){
        List btnFunctionList = new ArrayList();

        //获取该用户所在用户组的文件权限
        List<Tb_role_ele_function> roleFunction = null;
        List<String> roleid = userRoleRepository.findByUserid(userid);
        int size = roleid.size();
        if(size!=0){
            String[] roleids = new String[roleid.size()];
            roleid.toArray(roleids);
            roleFunction=roleEleFunctionRepository.findByPlatformAndUsergroupid(sysType.equals("0")?"利用平台":"管理平台",roleids);
            if(roleFunction.size() != 0) {
                for (Tb_role_ele_function role_ele_function : roleFunction) {
                    btnFunctionList.add(role_ele_function.getDownload().equals("1") ? "下载" : "");
                    btnFunctionList.add(role_ele_function.getDownloadAll().equals("1") ? "全部下载" : "");
                    btnFunctionList.add(role_ele_function.getPrint().equals("1") ? "打印" : "");
                    btnFunctionList.add(role_ele_function.getPrintBatch().equals("1") ? "批量打印" : "");
                    btnFunctionList.add(role_ele_function.getUpload().equals("1") ? "上传" : "");
                    btnFunctionList.add(role_ele_function.getUp().equals("1") ? "上移" : "");
                    btnFunctionList.add(role_ele_function.getDel().equals("1") ? "删除" : "");
                    btnFunctionList.add(role_ele_function.getDown().equals("1") ? "下移" : "");
                    btnFunctionList.add(role_ele_function.getLookhistory().equals("1") ? "查看历史版本" : "");
                }
            }
        }

        //获取该用户的文件权限
        Tb_ele_function eleFunction = eleFunctionRepository.findByPlatformAndUserid(sysType.equals("0")?"利用平台":"管理平台",userid);
        if(eleFunction != null) {
            btnFunctionList.add(eleFunction.getDownload().equals("1") ? "下载" : "");
            btnFunctionList.add(eleFunction.getDownloadAll().equals("1") ? "全部下载" : "");
            btnFunctionList.add(eleFunction.getPrint().equals("1") ? "打印" : "");
            btnFunctionList.add(eleFunction.getPrintBatch().equals("1") ? "批量打印" : "");
            btnFunctionList.add(eleFunction.getUpload().equals("1") ? "上传" : "");
            btnFunctionList.add(eleFunction.getUp().equals("1") ? "上移" : "");
            btnFunctionList.add(eleFunction.getDel().equals("1") ? "删除" : "");
            btnFunctionList.add(eleFunction.getDown().equals("1") ? "下移" : "");
            btnFunctionList.add(eleFunction.getLookhistory().equals("1") ? "查看历史版本" : "");
        }

        //去重
        Set set = new  HashSet();
        List newList = new  ArrayList();
        set.addAll(btnFunctionList);
        newList.addAll(set);
        return  newList;
    }

    /**
     * 设置文件权限
     * @param userid
     * @param lylist 利用平台
     * @param gllist 管理平台
     * @return
     */
    public void setWJQXbtn(String[] lylist,String[] gllist,String userid) {
        List<Tb_ele_function> eleFunction = eleFunctionRepository.findByUserid(userid);
        for (Tb_ele_function ele : eleFunction) {
            if (ele.getPlatform().equals("利用平台")) {
                ele.setDownload(ArrayUtils.contains(lylist,"下载")?"1":"0");
                ele.setDownloadAll(ArrayUtils.contains(lylist,"全部下载")?"1":"0");
                ele.setPrint(ArrayUtils.contains(lylist,"打印")?"1":"0");
                ele.setPrintBatch(ArrayUtils.contains(lylist,"批量打印")?"1":"0");
            }
            else if (ele.getPlatform().equals("管理平台")) {
                ele.setDownload(ArrayUtils.contains(gllist,"下载")?"1":"0");
                ele.setDownloadAll(ArrayUtils.contains(gllist,"全部下载")?"1":"0");
                ele.setPrint(ArrayUtils.contains(gllist,"打印")?"1":"0");
                ele.setPrintBatch(ArrayUtils.contains(gllist,"批量打印")?"1":"0");
                ele.setUpload(ArrayUtils.contains(gllist,"上传")?"1":"0");
                ele.setUp(ArrayUtils.contains(gllist,"上移")?"1":"0");
                ele.setDown(ArrayUtils.contains(gllist,"下移")?"1":"0");
                ele.setDel(ArrayUtils.contains(gllist,"删除")?"1":"0");
                ele.setLookhistory(ArrayUtils.contains(gllist,"查看历史版本")?"1":"0");
            }
        }
    }

	/**
	 * 获取归档排序用户
	 * @return
	 */
	public List<Tb_user> getFillSortUser(){
		return userRepository.findUsersByFillSortid();
	}

	/**
	 * 设置归档排序用户
	 * @return
	 */
	public List setFillSortUser(String[] userids){
		userFillSortRepository.deleteAll();  //删除之前数据
		List<Tb_user_fillsort> userFillsorts = new ArrayList<>();
		if(userids!=null){
			for(int i=0;i<userids.length;i++){
				Tb_user_fillsort userFillsort = new Tb_user_fillsort();
				userFillsort.setUserid(userids[i]);
				userFillsorts.add(userFillsort);
			}
		}
		return userFillSortRepository.save(userFillsorts);
	}

	public Page<Tb_user> findBySearchOutUsers(int page, int limit, String condition, String operator, String content,Sort sort) {
		Specification<Tb_user> searchOutuserstate = searchOutuserState();
		Specifications specifications = Specifications.where(searchOutuserstate);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .DESC,"sortsequence", "createtime") : sort);
		return userRepository.findAll(specifications, pageRequest);
	}

	public static Specification<Tb_user> searchOutuserState() {
		Specification<Tb_user> specification = new Specification<Tb_user>() {
			@Override
			public Predicate toPredicate(Root<Tb_user> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate[] predicates = new Predicate[2];
				predicates[0] = criteriaBuilder.notEqual(root.get("outuserstate"), "");
				predicates[1] = criteriaBuilder.isNotNull(root.get("outuserstate"));
				return criteriaBuilder.and(predicates);
			}
		};
		return specification;
	}
}
