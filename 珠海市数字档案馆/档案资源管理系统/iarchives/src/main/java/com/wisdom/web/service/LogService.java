package com.wisdom.web.service;

import com.alibaba.fastjson.JSONObject;
import com.wisdom.secondaryDataSource.entity.Tb_log_msg_sx;
import com.wisdom.secondaryDataSource.repository.SxLogMsgRepository;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.ExcelUtil;
import com.wisdom.util.ExportUtil;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xd on 2017/10/10.
 * 日志管理service
 */
@Service
@Transactional
public class LogService {

    private final Logger logger = LoggerFactory.getLogger(LogService.class);
    
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    LogRepository logRepository;

    @Autowired
    LogMsgRepository logMsgRepository;

	@Autowired
	SxLogMsgRepository sxlogMsgRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserOrganRepository userOrganRepository;
    
    @Autowired
    UserService userService;

    @Autowired
	RoleRepository roleRepository;

	@Autowired
	LogAop logAop;

	@Autowired
	ClassifySearchService classifySearchService;

	@Value("${system.document.rootpath}")
	private String rootpath;//系统文件根目录

	@Value("${system.iarchivesx.serviceSxImpl}")
	private String serviceSxImpl;//声像系统共享服务接口

	@Value("${system.iarchivexw.serviceXwImpl}")
	private String serviceXwImpl;//声像系统共享服务接口

    public List<Tb_log> getLog() {
        List<Tb_log> mlogs = logRepository.findAllByLeaf();
        return mlogs;
    }
    
    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段o
     * @param operator  操作符
     * @param content   查询条件内容
     *  系统管理员
	 *		不能看到系统/安全保密/安全审计员日志(get)
	 *		父级 - 可以看到所有业务日志(get)
	 *		子级 - 只能看到当前和子级机构业务日志(get)
	 *	安全保密管理员
	 *		父级 - 可以看到所有系统管理员日志(get)
	 *		子级 - 只能看到当前机构和子级系统管理员日志(get)
	 *	安全审计管理员
	 *		父级 - 可以看到所有系统/安全保密/安全审计员日志(get)
	 *		子级 - 只能看到当前和子级机构系统/安全保密/安全审计员日志(get)
     * @return
     */
    public Page<Object> findBySearch(int page, int limit, String condition, String operator, String content, Sort sortobj,String flag) {
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        Sort sortTwo = new Sort(Sort.Direction.DESC, "startTime");
        PageRequest pageRequest = new PageRequest(page-1, limit, sortobj==null?sort:sortobj);
        PageRequest pageRequestTwo = new PageRequest(page-1, limit,sortobj==null?sortTwo:sortobj);
        Specifications specifications = null;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //登录用户为系统管理员(系统管理员都不能看到三员用户的日志信息)
        if (userDetails.getRealname().contains(Tb_log_msg.getSystemname())||userDetails.getLoginname().contains(Tb_log_msg.getSystem())) {
        	return getSystemInfo(userDetails, content, specifications, condition, operator,
        			pageRequest, pageRequestTwo,/* loginnames, realnames, childloginnames, childrealnames,*/flag,page, limit,sortobj);
        //登录用户为安全保密管理员
    	} else if (userDetails.getRealname().contains(Tb_log_msg.getAqbmname())||userDetails.getLoginname().contains(Tb_log_msg.getAqbm())) {
    		return getAqbmInfo(userDetails, content, specifications, condition, operator,
    				pageRequest, pageRequestTwo,/* loginnames, realnames, childloginnames, childrealnames,*/flag);
    	//登录用户为安全审计管理员
    	} else if (userDetails.getRealname().contains(Tb_log_msg.getAqsjname())||userDetails.getLoginname().contains(Tb_log_msg.getAqsj())) {
    		return getAqsjInfo(userDetails, content, specifications, condition, operator,
    				pageRequest, pageRequestTwo, /*loginnames, realnames, childloginnames, childrealnames,*/flag);
    	}
        return null;
    }

	/**
	 * 获取声像、新闻平台日志
	 * @param page  页号
	 * @param limit  每页大小
	 * @param condition  检索条件
	 * @param operator  like,=
	 * @param content  检索内容
	 * @param sortStr  排序字段
	 * @return
	 */
	public Page<Tb_log_msg> findSxBySearch(String flag,int page, int limit, String condition, String operator, String content, String sortStr) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid=userDetails.getUserid();
    	try{
    		//启用webService
			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			String serviceIp=serviceSxImpl;//声像系统共享地址
			if("新闻系统".equals(flag)){
				serviceIp=serviceXwImpl;//新闻系统共享地址
			}
			Client client = dcf.createClient(serviceIp);
			Object[] logsObj = client.invoke("getSharedData", 4,condition,operator,content,sortStr,userid,page,limit);//获取声像系统共享日志数据
			Sort sortobj = WebSort.getSortByJson(sortStr);
			Sort sort = new Sort(Sort.Direction.DESC, "startTime");
			PageRequest pageRequest = new PageRequest(page-1, limit, sortobj==null?sort:sortobj);
			return syncLogs(logsObj,pageRequest);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//获取日志信息
	public Page<Tb_log_msg>  syncLogs(Object[] logsObj,PageRequest pageRequest){
		JSONObject logJson = JSONObject.parseObject(logsObj[0].toString());//将返回的日志数据字符串转为JOSN对象
		SharedPage logSharedPage = JSONObject.toJavaObject(logJson,SharedPage.class);//将日志JSON对象
		List<Tb_log_msg> logList = new ArrayList<>();
		if(logSharedPage.getData()!=null&&logSharedPage.getData().size()>0){
			for(int i=0;i<logSharedPage.getData().size();i++){//转化json数据为日志实体类
				Map logMsgMap = (Map)logSharedPage.getData().get(i);
				Tb_log_msg logMsg = new Tb_log_msg();
				logMsg.setId((String) logMsgMap.get("lmid"));//日志id
				logMsg.setIp((String)logMsgMap.get("ip"));//IP
				logMsg.setRealname((String)logMsgMap.get("realname"));//姓名
				logMsg.setOrgan((String)logMsgMap.get("organ"));//机构
				logMsg.setModule((String)logMsgMap.get("module"));//模块
				logMsg.setStartTime((String)logMsgMap.get("startTime"));//开始时间
				logMsg.setEnd_time((String)logMsgMap.get("end_time"));//结束时间
				logMsg.setConsume_time((String)logMsgMap.get("consume_time"));//操作时间
				logMsg.setDesci((String)logMsgMap.get("desci"));//描述
				logMsg.setOperate_user((String)logMsgMap.get("operate_user"));//用户账号
				logList.add(logMsg);
			}
			return new PageImpl(logList, pageRequest, logSharedPage.getTotalCount());
		}
		return  null;
	}

	public List<Object> getLogMsgList(String flag, String condition, String operator, String content) {
		Specifications specifications = null;
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String organid = userRepository.findByUserid(userDetails.getUserid()).getOrganid();
		//查找到同一机构的所有名称
		List<String> loginnames = userOrganRepository.findLoginnameByOrganid(organid);//账号
		List<String> realnames = userOrganRepository.findRealnameByOrganid(organid);//真实姓名
		//查找到子级机构的所有名称
		List<Tb_user> childInfo = userService.getUnitUserInfo(organid);
		List<String> childloginnames = new ArrayList<>();
		List<String> childrealnames = new ArrayList<>();
		for (int i = 0; i < childInfo.size(); i++) {
			Tb_user user = childInfo.get(i);
			childloginnames.add(user.getLoginname());
			childrealnames.add(user.getRealname());
		}
		//登录用户为系统管理员(系统管理员都不能看到三员用户的日志信息)
		if (userDetails.getRealname().equals(Tb_log_msg.getSystemname())) {
			return getSystemInfo(userDetails, content, specifications, condition, operator, loginnames, realnames, childloginnames, childrealnames,flag);
			//登录用户为安全保密管理员
		} else if (userDetails.getRealname().equals(Tb_log_msg.getAqbmname())) {
			return getAqbmInfo(userDetails, content, specifications, condition, operator, loginnames, realnames, childloginnames, childrealnames,flag);
			//登录用户为安全审计管理员
		} else if (userDetails.getRealname().equals(Tb_log_msg.getAqsjname())) {
			return getAqsjInfo(userDetails, content, specifications, condition, operator, loginnames, realnames, childloginnames, childrealnames,flag);
		}
		return null;
	}
    
    private List<String> getXT(List<String> loginnames, List<String> realnames, SecurityUser userDetails) {
    	List<String> name = new ArrayList<>();
    	for (int i = 0; i < loginnames.size(); i++) {
			if (!loginnames.get(i).contains(Tb_log_msg.getAqbm()) && !realnames.get(i).contains(Tb_log_msg.getAqbmname())
					&&!loginnames.get(i).contains(Tb_log_msg.getAqsj()) && !realnames.get(i).contains(Tb_log_msg.getAqsjname())
					&&!loginnames.get(i).equals(userDetails.getLoginname())) {
				name.add(loginnames.get(i));
			}
		}
    	return name;
    }
    
    private Page<Object> getSystemInfo(SecurityUser userDetails, String content, Specifications specifications,
    		String condition, String operator, PageRequest pageRequest,PageRequest pageRequestTwo,/* List<String> loginnames,List<String> realnames,
    		List<String> childloginnames, List<String> childrealnames,*/String flag,int page, int limit,Sort sort) {
    	//如果是父级系统管理员
		if ("xitong".equals(userDetails.getLoginname())) {
			String[] realname = {"系统管理员","安全保密管理员","安全审计员"};
			if("声像系统".equals(flag)||"新闻系统".equals(flag)){
				List<String> notExist = logMsgRepository.findByDesci();
				List<String> names = userRepository.findLoginnameByRealnameNotIn(realname);
				names.addAll(notExist);
				if (content == null) {
					//初始化加载父级系统管理员业务日志(排除所有管理员日志 - 可以查看所有用户日志)
					return getFindConditionAll(flag, null, names, specifications, null, null, pageRequestTwo);
				}else{
					Page<Object> result = getFindAll(flag, content, names, specifications, condition, operator, pageRequestTwo);
					return result;
				}
			}else{//档案系统
				String findSql=classifySearchService.getSqlByConditionsto(condition,content,"",operator);//封装查询条件
				String sortSql="";
				if (sort != null && sort.iterator().hasNext()) {//排序（默认按类型和时间）
					Sort.Order order = sort.iterator().next();
					sortSql = " order by " + order.getProperty() + " " + order.getDirection();
				} else {
					sortSql = " order by start_time desc ";
				}
				String sql = "select * from tb_log_msg  where operate_user not in('xitong','aqbm','aqsj') "+findSql;
				String countSql="select count(1) from tb_log_msg  where operate_user not in('xitong','aqbm','aqsj') "+findSql;
				Query countQuery=entityManager.createNativeQuery(countSql);
				int count = Integer.parseInt(countQuery.getResultList().get(0) + "");
				Query query=entityManager.createNativeQuery(DBCompatible.getInstance().sqlPages(sql+sortSql,page-1,limit), Tb_log_msg.class);
				List<Tb_log_msg> logmsgList=query.getResultList();
				return new PageImpl(logmsgList,pageRequest,count);
			}
		}
		String organid = userRepository.findByUserid(userDetails.getUserid()).getOrganid();
		//查找到同一机构的所有名称
		List<String> loginnames = userOrganRepository.findLoginnameByOrganid(organid);//账号
		List<String> realnames = userOrganRepository.findRealnameByOrganid(organid);//真实姓名
		//查找到子级机构的所有名称
		List<Tb_user> childInfo = userService.getUnitUserInfo(organid);
		List<String> childloginnames = new ArrayList<>();
		List<String> childrealnames = new ArrayList<>();
		for (int i = 0; i < childInfo.size(); i++) {
			Tb_user user = childInfo.get(i);
			childloginnames.add(user.getLoginname());
			childrealnames.add(user.getRealname());
		}
		//如果是子级系统管理员
		List<String> name = getXT(loginnames, realnames, userDetails);//当前机构非管理员信息
		name.addAll(getXT(childloginnames, childrealnames, userDetails));//子级机构非管理员信息
		Page<Object> result = getFindAll(flag, content, name, specifications, condition, operator, pageRequestTwo);
		if (result != null) {
			return result;
		}
		// 初始化加载业务日志
		return getFindConditionAll(flag, null, name, specifications, null, null, pageRequestTwo);
    }

	private List<Object> getSystemInfo(SecurityUser userDetails, String content, Specifications specifications,
										   String condition, String operator, List<String> loginnames,List<String> realnames,
										   List<String> childloginnames, List<String> childrealnames,String flag) {
		List<String> notExist = logMsgRepository.findByDesci();
		//如果是父级系统管理员
		if (userDetails.getLoginname().equals(Tb_log_msg.getSystem())) {
			String[] realname = {"系统管理员","安全保密管理员","安全审计员"};
			List<String> names = userRepository.findLoginnameByRealnameNotIn(realname);
			names.addAll(notExist);
			List<Object> result = getFindAll(flag, content, names, specifications, condition, operator);
			if (result != null) {
				return result;
			}
			//初始化加载父级系统管理员业务日志(排除所有管理员日志 - 可以查看所有用户日志)
			return getFindConditionAll(flag,null, names, specifications, null, null);
		}
		//如果是子级系统管理员
		List<String> name = getXT(loginnames, realnames, userDetails);//当前机构非管理员信息
		name.addAll(getXT(childloginnames, childrealnames, userDetails));//子级机构非管理员信息
		List<Object> result = getFindAll(flag, content, name, specifications, condition, operator);
		if (result != null) {
			return result;
		}
		// 初始化加载业务日志
		return getFindConditionAll(flag,null, name, specifications, null, null);
	}
    
    private List<String> getAqbm(List<String> loginnames, List<String> realnames) {
    	List<String> name = new ArrayList<>();
    	for (int i = 0; i < loginnames.size(); i++) {
			if (loginnames.get(i).contains(Tb_log_msg.getSystem())||realnames.get(i).contains(Tb_log_msg.getSystemname())) {
				name.add(loginnames.get(i));
			}
		}
    	return name;
    }
    
    private Page<Object> getAqbmInfo(SecurityUser userDetails, String content, Specifications specifications, String condition,
    		String operator, PageRequest pageRequest, PageRequest pageRequestTwo, /*List<String> loginnames,List<String> realnames,
    		List<String> childloginnames, List<String> childrealnames,*/String flag) {
    	//如果是一级安全保密管理员
		if (userDetails.getLoginname().equals(Tb_log_msg.getAqbm())) {
			String[] realname = {"系统管理员"};
			List<String> name = userRepository.findLoginnameByRealnameIn(realname);
			Page<Object> result = getFindAll(flag, content, name, specifications, condition, operator, pageRequestTwo);
			if (result != null) {
				return result;
			}
			return getFindConditionAll(flag, null, name, specifications, null, null, pageRequestTwo);
		}

		String organid = userRepository.findByUserid(userDetails.getUserid()).getOrganid();
		//查找到同一机构的所有名称
		List<String> loginnames = userOrganRepository.findLoginnameByOrganid(organid);//账号
		List<String> realnames = userOrganRepository.findRealnameByOrganid(organid);//真实姓名
		//查找到子级机构的所有名称
		List<Tb_user> childInfo = userService.getUnitUserInfo(organid);
		List<String> childloginnames = new ArrayList<>();
		List<String> childrealnames = new ArrayList<>();
		for (int i = 0; i < childInfo.size(); i++) {
			Tb_user user = childInfo.get(i);
			childloginnames.add(user.getLoginname());
			childrealnames.add(user.getRealname());
		}
		
		//如果是子级安全保密管理员
		List<String> name = getAqbm(loginnames, realnames);
		name.addAll(getAqbm(childloginnames, childrealnames));
		Page<Object> result = getFindAll(flag, content, name, specifications, condition, operator, pageRequestTwo);
		if (result != null) {
			return result;
		}
		return getFindConditionAll(flag, null, name, specifications, null, null, pageRequestTwo);
    }

	private List<Object> getAqbmInfo(SecurityUser userDetails, String content, Specifications specifications, String
			condition,String operator,List<String> loginnames,List<String> realnames,
			List<String> childloginnames, List<String> childrealnames,String flag) {
		//如果是一级安全保密管理员
		if (userDetails.getLoginname().equals(Tb_log_msg.getAqbm())) {
			String[] realname = {"系统管理员"};
			List<String> name = userRepository.findLoginnameByRealnameIn(realname);
			List<Object> result = getFindAll(flag, content, name, specifications, condition, operator);
			if (result != null) {
				return result;
			}
			return getFindConditionAll(flag,null, name, specifications, null, null);
		}

		//如果是子级安全保密管理员
		List<String> name = getAqbm(loginnames, realnames);
		name.addAll(getAqbm(childloginnames, childrealnames));
		List<Object> result = getFindAll(flag, content, name, specifications, condition, operator);
		if (result != null) {
			return result;
		}
		return getFindConditionAll(flag,null, name, specifications, null, null);
	}
    
    private List<String> getAqsj(List<String> loginnames, List<String> realnames) {
    	List<String> name = new ArrayList<>();
    	for (int i = 0; i < loginnames.size(); i++) {
    		if(loginnames.get(i).contains(Tb_log_msg.getSystem()) || realnames.contains(Tb_log_msg.getSystemname())
					|| loginnames.get(i).contains(Tb_log_msg.getAqbm()) || realnames.contains(Tb_log_msg.getAqbmname())
					|| loginnames.get(i).contains(Tb_log_msg.getAqsj()) || realnames.contains(Tb_log_msg.getAqsjname())){
				name.add(loginnames.get(i));
			}

		}
    	return name;
    }
    
    private Page<Object> getAqsjInfo(SecurityUser userDetails, String content, Specifications specifications, String condition,
    		String operator, PageRequest pageRequest, PageRequest pageRequestTwo,/* List<String> loginnames,List<String> realnames,
    		List<String> childloginnames, List<String> childrealnames,*/String flag) {
    	//如果是一级安全审计员
		if (userDetails.getLoginname().equals(Tb_log_msg.getAqsj())) {
			String[] realname = {"系统管理员","安全保密管理员","安全审计员"};
			List<String> names = userRepository.findLoginnameByRealnameIn(realname);
			Page<Object> result = getFindAll(flag, content, names, specifications, condition, operator, pageRequestTwo);
			if (result != null) {
				return result;
			}
			return getFindConditionAll(flag, null, names, specifications, null, null, pageRequestTwo);
		}
		String organid = userRepository.findByUserid(userDetails.getUserid()).getOrganid();
		//查找到同一机构的所有名称
		List<String> loginnames = userOrganRepository.findLoginnameByOrganid(organid);//账号
		List<String> realnames = userOrganRepository.findRealnameByOrganid(organid);//真实姓名
		//查找到子级机构的所有名称
		List<Tb_user> childInfo = userService.getUnitUserInfo(organid);
		List<String> childloginnames = new ArrayList<>();
		List<String> childrealnames = new ArrayList<>();
		for (int i = 0; i < childInfo.size(); i++) {
			Tb_user user = childInfo.get(i);
			childloginnames.add(user.getLoginname());
			childrealnames.add(user.getRealname());
		}
		//如果是子级安全审计员
		List<String> name = getAqsj(loginnames, realnames);
		name.addAll(getAqsj(childloginnames, childrealnames));
		Page<Object> result = getFindAll(flag, content, name, specifications, condition, operator, pageRequestTwo);
		if (result != null) {
			return result;
		}
		return getFindConditionAll(flag, null, name, specifications, null, null, pageRequestTwo);
    }

	private List<Object> getAqsjInfo(SecurityUser userDetails, String content, Specifications specifications, String
			condition,String operator, List<String> loginnames,List<String> realnames,
			List<String> childloginnames, List<String> childrealnames,String flag) {
		//如果是一级安全审计员
		if (userDetails.getLoginname().equals(Tb_log_msg.getAqsj())) {
			String[] realname = {"系统管理员","安全保密管理员","安全审计员"};
			List<String> names = userRepository.findLoginnameByRealnameIn(realname);
			List<Object> result = getFindAll(flag, content, names, specifications, condition, operator);
			if (result != null) {
				return result;
			}
			return getFindConditionAll(flag,null, names, specifications, null, null);
		}
		//如果是子级安全审计员
		List<String> name = getAqsj(loginnames, realnames);
		name.addAll(getAqsj(childloginnames, childrealnames));
		List<Object> result = getFindAll(flag, content, name, specifications, condition, operator);
		if (result != null) {
			return result;
		}
		return getFindConditionAll(flag,null, name, specifications, null, null);
	}
    
    private Page<Object> getFindConditionAll(String flag, String content, List<String> name, Specifications specifications, String condition,
    		String operator, PageRequest pageRequestTwo) {
    	Specification<Tb_log_msg> searchLogID = new Specification<Tb_log_msg>() {
        	@Override
        	public Predicate toPredicate(Root<Tb_log_msg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        		if (name.size() > 1000) {
        			int num = 998;//一个组数据中的长度
            		int group = (name.size() / num) + 1;//分为x+1组
            		Predicate[] predicates = new Predicate[group];//创建容器存储
            		CriteriaBuilder.In in = cb.in(root.get("operate_user"));//查找到操作者属性
            		for (int q = 0; q < group; q++) {
                        if (q == group - 1) {//获取前面几组数的数据
                            for (int i = q * num; i < name.size(); i++) {
                                in.value(name.get(i));
                            }
                        } else {//获取最后一组数的数据
                            for (int i = q * num; i < q * num + num; i++) {
                                in.value(name.get(i));
                            }
                        }
                        predicates[q] = in;
                        in = cb.in(root.get("operate_user"));
                    }
                    return cb.or(predicates);
        		}
    			Path<String> namePath = root.get("operate_user");
        	    Predicate[] predicates = new Predicate[name.size()];
    	    	for (int i = 0; i < name.size(); i++) {
        	    	predicates[i] = cb.equal(namePath, name.get(i));
        	    }
        	    return cb.or(predicates);
        	}
        };
        specifications = Specifications.where(searchLogID);
        specifications = ClassifySearchService.addSearchCondition(specifications, condition, operator, content);
		if("声像系统".equals(flag)||"新闻系统".equals(flag)){
			//PageRequest pageRequest = new PageRequest(0, 50);
			return sxlogMsgRepository.findAll(specifications,pageRequestTwo);
		}else{
			return logMsgRepository.findAll(specifications, pageRequestTwo);
		}
    }

	private List<Object> getFindConditionAll(String flag, String content, List<String> name, Specifications specifications,
											  String condition,String operator) {
		Specification<Tb_log_msg> searchLogID = new Specification<Tb_log_msg>() {
			@Override
			public Predicate toPredicate(Root<Tb_log_msg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (name.size() > 1000) {
					int num = 998;
					int group = (name.size() / num) + 1;
					Predicate[] predicates = new Predicate[group];
					CriteriaBuilder.In in = cb.in(root.get("operate_user"));
					for (int q = 0; q < group; q++) {
						if (q == group - 1) {
							for (int i = q * num; i < name.size(); i++) {
								in.value(name.get(i));
							}
						} else {
							for (int i = q * num; i < q * num + num; i++) {
								in.value(name.get(i));
							}
						}
						predicates[q] = in;
						in = cb.in(root.get("operate_user"));
					}
					return cb.or(predicates);
				}
				Path<String> namePath = root.get("operate_user");
				Predicate[] predicates = new Predicate[name.size()];
				for (int i = 0; i < name.size(); i++) {
					predicates[i] = cb.equal(namePath, name.get(i));
				}
				return cb.or(predicates);
			}
		};
		specifications = Specifications.where(searchLogID);
		specifications = ClassifySearchService.addSearchCondition(specifications, condition, operator, content);
		if("声像系统".equals(flag)||"新闻系统".equals(flag)){
			return sxlogMsgRepository.findAll(specifications);
		}else{
			return logMsgRepository.findAll(specifications);
		}
	}

	public List<Object> getFindAllById(String flag, String[] ids) {
		Specifications specifications = null;
		Specification<Tb_log_msg> searchLogID = new Specification<Tb_log_msg>() {
			@Override
			public Predicate toPredicate(Root<Tb_log_msg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (ids.length > 1000) {
					int num = 998;
					int group = (ids.length / num) + 1;
					Predicate[] predicates = new Predicate[group];
					CriteriaBuilder.In in = cb.in(root.get("lmid"));
					for (int q = 0; q < group; q++) {
						if (q == group - 1) {
							for (int i = q * num; i < ids.length; i++) {
								in.value(ids[i]);
							}
						} else {
							for (int i = q * num; i < q * num + num; i++) {
								in.value(ids[i]);
							}
						}
						predicates[q] = in;
						in = cb.in(root.get("lmid"));
					}
					return cb.or(predicates);
				}
				Path<String> namePath = root.get("lmid");
				Predicate[] predicates = new Predicate[ids.length];
				for (int i = 0; i < ids.length; i++) {
					predicates[i] = cb.equal(namePath, ids[i]);
				}
				return cb.or(predicates);
			}
		};
		specifications = Specifications.where(searchLogID);
		if("声像系统".equals(flag)||"新闻系统".equals(flag)){
			return sxlogMsgRepository.findAll(specifications);
		}else{
			return logMsgRepository.findAll(specifications);
		}
	}

    private Page<Object> getFindAll(String flag, String content, List<String> name, Specifications specifications, String condition,
    		String operator, PageRequest pageRequestTwo) {
    	if (content != null) {
			return getFindConditionAll(flag, content, name, specifications, condition, operator, pageRequestTwo);
		}
    	return null;
    }

	private List<Object> getFindAll(String flag, String content, List<String> name, Specifications specifications, String
			condition,String operator) {
		if (content != null) {
			return getFindConditionAll(flag,content, name, specifications, condition, operator);
		}
		return null;
	}
    
    public ExtMsg deleteLogDetail(String[] ids){
        try{
            Integer i = logMsgRepository.deleteByLmidIn(ids);
            return new ExtMsg(true,"成功删除"+i+"条日志",null);
        }catch (Exception e){
            logger.error("删除日志出错",e);
            return new ExtMsg(false,"删除日志出错",null);
        }
    };

    public void saveLogDetail(List<Tb_log_msg> lists) {
        for (int i = 0; i < lists.size(); i++) {
            logMsgRepository.save(lists.get(i));
        }
    }

    public List<Tb_log_msg> getLogDetailByIDIn(String[] ids) {
        return logMsgRepository.findByLmidInOrderByStartTimeDesc(ids);
    }

    public void exportOtherFormat(String[] ids, HttpServletResponse res){
        List<Tb_log_msg> log_msgList=logMsgRepository.findByLmidInOrderByStartTimeDesc(ids);
        String[] names={"IP地址","操作用户","开始时间","结束时间","耗时","模块","操作描述"};
        String[] keys={"ip","operate_user","start_time","end_time","consume_time","module","desci"};
        List<Map<String,Object>> list=createExcelRecord(log_msgList);
        ExportUtil exportUtil=new ExportUtil("日志"+new Date().getTime(),res,list,keys,names);
        exportUtil.exportExcel();
    }
    private List<Map<String,Object>> createExcelRecord(List<Tb_log_msg> log_msgList){
        List<Map<String,Object>> list=new ArrayList<>();
        for(Tb_log_msg log_msg:log_msgList){
            Map<String,Object> map=new HashMap<>();
            map.put("ip",log_msg.getIp());
            map.put("operate_user",log_msg.getOperate_user());
            map.put("start_time",log_msg.getStartTime());
            map.put("end_time",log_msg.getEnd_time());
            map.put("consume_time",log_msg.getConsume_time());
            map.put("module",log_msg.getModule());
            map.put("desci",log_msg.getDesci());
            list.add(map);
        }
        return list;
    }
	public String exportLogMsg(String flag, List<Object> logMsgs, String fileName, String sheetName) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String dir = rootpath + "/log/" + userDetails.getUsername();
		File upDir = new File(dir);
		if (!upDir.exists()) {
			upDir.mkdirs();
		}
		String path = dir + "/" + fileName + ".xls";
		List<Tb_log_msg> logMsgList=new ArrayList<>();
		List<Tb_log_msg_sx> logMsgSxList=new ArrayList<>();
		if("声像系统".equals(flag)||"新闻系统".equals(flag)){
			for(Object o:logMsgs){
				logMsgSxList.add((Tb_log_msg_sx)o);
			}
			ExcelUtil<Tb_log_msg_sx> excelUtil = new ExcelUtil<Tb_log_msg_sx>(Tb_log_msg_sx.class);
			return excelUtil.getListToExcel(logMsgSxList, sheetName, path);
		}else{
			for(Object o:logMsgs){
				logMsgList.add((Tb_log_msg)o);
			}
			ExcelUtil<Tb_log_msg> excelUtil = new ExcelUtil<Tb_log_msg>(Tb_log_msg.class);
			return excelUtil.getListToExcel(logMsgList, sheetName, path);
		}
	}

	/**
	 * 该方法用于对条目对象的操作 适用于（文字描述  无条目操作类日志记录 导出模板）
	 *
	 * @param module     模块字符串
	 * @param desciStart 操作描述
	 * @param text       详细数据描述 如（节点名，）
	 */
	public void recordTextLog(String module, String desciStart, String text) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		SecurityUser securityUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		String ip = LogAop.getIpAddress();
		Thread thread = new Thread(() -> {
			List<Tb_log_msg> log_msgs = new ArrayList<>();
			Tb_log_msg logMsg = new Tb_log_msg();
//            String organ = organService.findFullOrgan("", securityUser.getOrganid());
			String organ = securityUser.getOrganid();
			List<String> roles = roleRepository.findByuserid(securityUser.getUserid());
			if (roles.size() != 0) {
				logMsg.setRolename(StringUtils.strip(roles.toString(), "[]"));
			}
			logMsg.setOrgan(organ);
			logMsg.setRealname(securityUser.getRealname());
			logMsg.setOperate_user(securityUser.getLoginname());
			logMsg.setIp(ip);
			logMsg.setModule(module);
			try {
				logMsg.setDesci("操作描述：" + desciStart + ";#数据信息：" + text+";");
			} catch (Exception e) {
				e.printStackTrace();
			}
			logMsg.setStartTime(startTime);
			logMsg.setEnd_time(LogAop.getCurrentSystemTime());
			logMsg.setConsume_time(System.currentTimeMillis() - startMillis + "ms");
			log_msgs.add(logMsg);
			logAop.generateManualLog(log_msgs);
		});
		thread.start();
	}

	public void recordTextLog(String module,String text){
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		SecurityUser securityUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		String ip = LogAop.getIpAddress();
		Thread thread = new Thread(() -> {
			List<Tb_log_msg> log_msgs = new ArrayList<>();
			Tb_log_msg logMsg = new Tb_log_msg();
//            String organ = organService.findFullOrgan("", securityUser.getOrganid());
			String organ = securityUser.getOrganid();
			List<String> roles = roleRepository.findByuserid(securityUser.getUserid());
			if (roles.size() != 0) {
				logMsg.setRolename(StringUtils.strip(roles.toString(), "[]"));
			}
			logMsg.setOrgan(organ);
			logMsg.setRealname(securityUser.getRealname());
			logMsg.setOperate_user(securityUser.getLoginname());
			logMsg.setIp(ip);
			logMsg.setModule(module);
			try {
				logMsg.setDesci("操作描述："+text+";");
			} catch (Exception e) {
				e.printStackTrace();
			}
			logMsg.setStartTime(startTime);
			logMsg.setEnd_time(LogAop.getCurrentSystemTime());
			logMsg.setConsume_time(System.currentTimeMillis() - startMillis + "ms");
			log_msgs.add(logMsg);
			logAop.generateManualLog(log_msgs);
		});
		thread.start();
	}

	public String getVisitNum(){
		return logMsgRepository.getVisitNum();
	}

	public Map<String, Integer> getVisitNumAvg(int month, String startDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		LinkedHashMap<String,Integer> dateMap=new LinkedHashMap();
		for(int i=0;i<month;i++){//初始化所有月份
			String d=subMonth(i,startDate,sdf);
			dateMap.put(d,0);
		}
		String endDate=subMonth(month,startDate,sdf);
		List<Object[]> list=logMsgRepository.getVisitNumAvg(startDate,endDate);
		//计算平均值
		Integer count=Integer.valueOf(logMsgRepository.getVisitNum(startDate,endDate))/month;
		if(list.size()>0) {
			for (Object[] objects : list) {
				if(objects[1].toString().length()==1){//补齐月份的0
					objects[1]="0"+objects[1];
				}
				dateMap.put(objects[0]+"-"+objects[1],Integer.valueOf(objects[2].toString()));//给月份赋值
			}
			dateMap.put("平均人次",count);
		}
		return dateMap;
	}

	/**
	 * 日期添加月份
	 * @param month 要添加的几个月份
	 * @param date 日期
	 * @return
	 */
	public String subMonth(int month,String date,SimpleDateFormat sdf){
		String reStr="";
		try{
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date dt = sdf.parse(date);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);
			rightNow.add(Calendar.MONTH, month);
			Date dt1 = rightNow.getTime();
			reStr = sdf.format(dt1);
		}catch (ParseException e) {
			e.printStackTrace();
		}
		return reStr;
	}
}