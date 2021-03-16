package com.wisdom.util;

import com.wisdom.web.entity.Tb_log_msg;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.LogMsgRepository;
import com.wisdom.web.repository.RoleRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.OrganService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * aop 记录日志
 * 
 * @author wjh
 */
@Aspect
@Component
public class LogAop {

	private static final Logger logger = LoggerFactory.getLogger(LogAop.class);

	public static LogAop logAop;

	@Autowired
    LogMsgRepository logMsgRepository;

	@Autowired
    UserRepository userRepository;

	@Autowired
    OrganService organService;

	@Autowired
    RoleRepository roleRepository;

	@PostConstruct
	public void init() {
		logAop = this;
		logAop.logMsgRepository = this.logMsgRepository;
		logAop.userRepository = this.userRepository;
		logAop.organService = this.organService;
		logAop.roleRepository = this.roleRepository;
	}

	/**
	 * 定义切入点
	 */
	@Pointcut("@annotation(com.wisdom.util.LogAnnotation)")
	public void controllerMethodPointcut() {
	}

	/**
	 * aop具体实现
	 * 
	 * @param pjp
	 * @return
	 */
	@Around("controllerMethodPointcut()") // 环绕
	public Object Interceptor(ProceedingJoinPoint pjp) {
		try {
			long beginTime = System.currentTimeMillis();// 开始时间毫秒数
			String startTime = getCurrentSystemTime();//获取当前事件

			MethodSignature signature = (MethodSignature) pjp.getSignature();
			Method method = signature.getMethod(); // 获取被拦截的方法
			String methodName = method.getName(); // 获取被拦截的方法名
			LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

			Object result = null;
			try {
				result = pjp.proceed();//继续执行切点
			} catch (Throwable throwable) {
			//	throwable.printStackTrace();
				logger.error(throwable.getMessage());//切点方法发生错误是抛上来捕获并写入日志
			}

			long endLong = System.currentTimeMillis();
			String endTime = getCurrentSystemTime();
			// 保存所有请求参数
			Object[] objs = pjp.getArgs();
			String ip = getIpAddress();//获取IP地址
			String msg = "";
			boolean b = true;
			SecurityUser user =(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局);
			String organ = organService.findFullOrgan("", user.getOrganid());//获取用户所属的机构
			List<String> roles = roleRepository.findByuserid(user.getUserid());
			String rolename = "";
			if(roles.size()!=0){
				/*String[] strings = new String[roles.size()];
				rolename = strings.toString();*/
				rolename = StringUtils.strip(roles.toString(),"[]");
			}
			if (objs != null && objs.length > 0) {
				String[] sites = "".equals(logAnnotation.sites()) ? new String[0] : logAnnotation.sites().split(",");
				String[] typeFields = "".equals(logAnnotation.fields()) ? new String[0]
						: logAnnotation.fields().split("&");// 考虑多参数的属性,以&分隔如(name,sex&title) 字段名称
				String[] connects = "".equals(logAnnotation.connect()) ? new String[0]
						: logAnnotation.connect().split(",");
				int k = 0;
				for (int i = 0; i < sites.length; i++) {
					String connect = "";
					if ("java.lang.String[]".equals(objs[Integer.parseInt(sites[i]) - 1].getClass().getTypeName())) {// 判断字符串数组类型(主要对id数组类型写日志)
						String[] strs = (String[]) objs[Integer.parseInt(sites[i]) - 1];
						for (int l = 0; l < strs.length; l++) {
							logMsgRepository.save(new Tb_log_msg(ip, getCurrentOperateuser(), user.getRealname(), organ,
									startTime, endTime, endLong - beginTime + "ms", logAnnotation.module(),
									logAnnotation.startDesc() + strs[l] + logAnnotation.endDesc()));
							logger.info("***************************" + logAnnotation.startDesc() + strs[l]
									+ logAnnotation.endDesc() + "******************************");
						}
						b = false;
						continue;
					}
					if (objs[Integer.parseInt(sites[i]) - 1].getClass().isPrimitive()
							|| "java.lang.String".equals(objs[Integer.parseInt(sites[i]) - 1].getClass().getName())) {// 判断原始数据类型与字符串类型描述拼接
						if (k < connects.length) {// 多值之间拼接字符
							connect = connects[k];
							k++;
						}
						msg += objs[Integer.parseInt(sites[i]) - 1].toString() + connect;
						continue;
					}
					Class c = Class.forName(objs[Integer.parseInt(sites[i]) - 1].getClass().getName());
					for (int j = 0; j < typeFields[i].split(",").length; j++) {// 获取引用类型的字段字段值拼接
						if (k < connects.length) {// 多值之间拼接字符
							connect = connects[k];
							k++;
						}
						String methedName = getFunc(typeFields[i].split(",")[j]);//拿到getxxx()方法
						Method method1 = c.getMethod(methedName, new Class[0]);
						Object obj = method1.invoke(objs[Integer.parseInt(sites[i]) - 1]);//method.invoke(xxx) 等于 xxx.method();
						msg += Objects.toString(obj) + connect;
					}
				}
			}
			if (b) {
				logMsgRepository.save(new Tb_log_msg(ip, getCurrentOperateuser(), user.getRealname(), organ, startTime,
						endTime, endLong - beginTime + "ms", logAnnotation.module(),
						logAnnotation.startDesc() + msg + logAnnotation.endDesc()));
				logger.info("***************************" + logAnnotation.startDesc() + msg + logAnnotation.endDesc()
						+ "***************************");
			}
			return result;
		} catch (Exception e) {
			logger.error("日志写入出错", e);
		}
		return null;
	}

	public String getFunc(String str) {
		return "get" + str.toLowerCase().substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 手工生成并保存日志
	 * 
	 * @param module
	 *            模块名
	 * @param desci
	 *            操作描述
	 */
	public void generateManualLog(String startTime, String endTime, long duration, String module, String desci) {
		Tb_user user = userRepository.findByLoginname(getCurrentOperateuser());
		String organ = organService.findFullOrgan("", user.getOrganid());
		List<String> roles = roleRepository.findByuserid(user.getUserid());
		Tb_log_msg logMsg = new Tb_log_msg();
		if(roles.size()!=0){
			logMsg.setRolename(StringUtils.strip(roles.toString(),"[]"));
		}
		logMsg.setOperate_user(getCurrentOperateuser());
		logMsg.setRealname(getCurrentOperateuserRealname());
		logMsg.setOrgan(organ);
		logMsg.setIp(getIpAddress());
		logMsg.setModule(module);
		logMsg.setDesci(desci);
		logMsg.setStartTime(startTime);
		logMsg.setEnd_time(endTime);
		logMsg.setConsume_time(duration + "ms");
		logMsgRepository.save(logMsg);
	}

	//打印日志，用于多线程的打印
	public void generateManualLog2(String startTime, String endTime, long duration, String module, String desci,
								   String Operateuser,String OperateuserRealname,String IP) {
		Tb_user user = userRepository.findByLoginname(Operateuser);
		String organ = organService.findFullOrgan("", user.getOrganid());
		List<String> roles = roleRepository.findByuserid(user.getUserid());
		Tb_log_msg logMsg = new Tb_log_msg();
		if(roles.size()!=0){
			logMsg.setRolename(StringUtils.strip(roles.toString(),"[]"));
		}
		logMsg.setOperate_user(Operateuser);
		logMsg.setRealname(OperateuserRealname);
		logMsg.setOrgan(organ);
		logMsg.setIp(IP);
		logMsg.setModule(module);
		logMsg.setDesci(desci);
		logMsg.setStartTime(startTime);
		logMsg.setEnd_time(endTime);
		logMsg.setConsume_time(duration + "ms");
		logMsgRepository.save(logMsg);
	}


	/**
	 * 手工生成并保存日志
	 *
	 * @param module
	 *            模块名
	 * @param nodeid
	 *            目标节点
	 * @param entries
	 *            条目id数组
	 * @param loginname
	 *            登录账户
	 * @param realname
	 *            用户名字
	 * @param ipAddress
	 *            用户ip
	 */
	public static void staticGenerateManualLog( String module, String nodeid, String[] entries,String loginname,String realname,String ipAddress) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		String endTime= LogAop.getCurrentSystemTime();
		long duration=System.currentTimeMillis() - startMillis;
		Tb_user user = logAop.userRepository.findByLoginname(loginname);
		String organ = logAop.organService.findFullOrgan("", user.getOrganid());
		List<String> roles = logAop.roleRepository.findByuserid(user.getUserid());
		List<Tb_log_msg> logMsgs=new ArrayList<>();
		for (String entryid : entries) {
			Tb_log_msg logMsg = new Tb_log_msg();
			String desc="归档操作，条目id为：" + entryid + ",目标节点id为："+nodeid;
			if(roles.size()!=0){
				logMsg.setRolename(StringUtils.strip(roles.toString(),"[]"));
			}
			logMsg.setOperate_user(loginname);
			logMsg.setRealname(realname);
			logMsg.setOrgan(organ);
			logMsg.setIp(ipAddress);
			logMsg.setModule(module);
			logMsg.setDesci(desc);
			logMsg.setStartTime(startTime);
			logMsg.setEnd_time(endTime);
			logMsg.setConsume_time(duration + "ms");
			logMsgs.add(logMsg);
		}
		logAop.logMsgRepository.save(logMsgs);
	}

	/**
	 * 手工生成并保存日志
	 * 
	 * @param logs
	 *            日志集合
	 */
	// String startTime,String endTime,long duration,String module,String
	// desci,String username,String ip
	public void generateManualLog(List<Tb_log_msg> logs) {
		logMsgRepository.save(logs);
	}

	/**
	 * 获取访问者真实ip
	 * 
	 * @return ip
	 */
	public static String getIpAddress() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getCurrentSystemTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getCurrentOperateuser() {
		return ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoginname();
	}

	public static String getCurrentOperateuserRealname() {
		return ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRealname();
	}

}