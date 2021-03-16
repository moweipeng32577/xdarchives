package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FileUtil;
import com.wisdom.util.*;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAnnotation;
import com.wisdom.util.netca.CertAuthClient;
import com.wisdom.util.netca.NetcaPKI;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.FunctionRepository;
import com.wisdom.web.repository.IconRepository;
import com.wisdom.web.repository.SystemConfigRepository;
import com.wisdom.web.security.MyFilterSecurityInterceptor;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.security.SlmRuntimeEasy;
import com.wisdom.web.service.LogService;
import com.wisdom.web.service.LoginService;
import com.wisdom.web.service.UnifyService;
import com.wisdom.web.service.UserService;
import net.netca.pki.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 登录处理控制器
 */
@Controller
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.iarchivesx.path}")
	private String iarchivesxPath;// 系统文件根目录

	@Value("${system.iarchivexwyx.path}")
	private String iarchivexwyxPath;// 新闻影像系统文件根目录

	@Value("${system.iarchiveElectronicSy.path}")
	private String iarchiveElectronicSyPath;// 电子文档系统文件根目录

	@Value("${system.loginType}")
	private String systemLoginType;//登录系统设置  政务网1  局域网0

	@Value("${CA.WebServiceURL}")
	private String WEBSERVICEURL; //CA网关地址
	@Value("${CA.sMethodOneServerUrl}")
	private String SMETHODONESERVERURL; //CA默认网关服务器地址
	@Value("${CA.sDefaultServerCert}")
	private String SDEFAULTSERVERCERT; //AC默认网关服务器接口

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	IconRepository iconRepository;

	@Autowired
	FunctionRepository functionRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	UserService userService;

	@Autowired
	private UnifyService unifyService;

	@Autowired
	SlmRuntimeEasy slmRuntimeEasy;

	@Autowired
	LogService logService;

	@Autowired
	SystemConfigRepository systemConfigRepository;


	private static List<String> syUsers = Arrays.asList("系统管理员", "安全保密管理员", "安全审计员");

	private String timeStamp;//静态资源时间标记

	@RequestMapping("/index1")
	public String index1(Model model) {
		return "/index1";
	}

	@RequestMapping("/indexsl")
	public String indexsl(Model model){
		return "/indexsl";
	}

	/**
	 * 用户登录页面跳转
	 *
	 * @param model
	 *            模型绑定对象
	 * @param sysType
	 *            系统类型(主要用于管理,利用平台切换)
	 * @return 平台页面
	 */
//	@LogAnnotation(module = "用户登录", startDesc = "登录系统")
	@RequestMapping("/index")
	public String index(Model model, String sysType) {
		boolean isChangeFunds = loginService.initFunds();
		if (!isChangeFunds) {
			return "/setting/initialize";
		}
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        if(sysType!=null){
            userDetails.setType(sysType);
        }
        Tb_Personalized personalizeds = loginService.index(userDetails);// 获取用户角色,资源等信息
		String personalized = JSON.toJSONString(personalizeds); // 个性化信息
		model.addAttribute("personalized", personalized); // 绑定个性化信息
		model.addAttribute("userid", userDetails.getUserid()); // 绑定用户id
		model.addAttribute("realname", userDetails.getRealname()); // 绑定用户真实姓名
		model.addAttribute("loginname", userDetails.getLoginname()); // 绑定用户登录账号
		model.addAttribute("imgsrc", "/electronic/outputUserimg");// 绑定用户头像
		model.addAttribute("sysType", sysType != null ? sysType : userDetails.getType());// 绑定系统类型
		model.addAttribute("platformopen", slmRuntimeEasy.hasPlatform());//绑定利用平台开关
		model.addAttribute("single", slmRuntimeEasy.isSingle());//绑定个性化开关
		model.addAttribute("usertype", userDetails.getUsertype());//用户权限类型

		//强制更新初始密码
//		if(userDetails.getLoginpassword().equals(MD5.MD5("555"))){
//			model.addAttribute("changePwd","true");
//		}else{
			//判断是否需要更新密码
			if(userDetails.getLogout_time()!=null){
				Date pwdDate=userDetails.getLogout_time();
				Tb_system_config systemConfig =systemConfigRepository.findByConfigcodeAndParentconfigidIsNull("密码更新间隔天数");
				int days=0;
				if(systemConfig!=null){
					try{
						days=Integer.parseInt(systemConfig.getValue());//密码更新间隔天数
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(days>0){//有正确设置间隔天数
					if((new Date().getTime() - pwdDate.getTime()) / (24 * 60 * 60 * 1000)>days){//判断间隔天数
						model.addAttribute("changePwd","new");//强制定期修改密码
					}
				}
			}
//		}

		String versionText = slmRuntimeEasy.getVersionName();//获取系统版本
		if (syUsers.contains(userDetails.getRealname())) {//三元用户手册
			boolean b = loginService.isPathExist("/static/doc/"+versionText+"/管理员操作手册.pdf");
			if(b) {
				model.addAttribute("doc", "/doc/" + versionText + "/管理员操作手册.pdf");// 绑定三元用户使用手册
			}else {//不存在是给定默认
				model.addAttribute("doc", "/doc/管理员操作手册.pdf");// 绑定三元用户使用手册
			}
		} else {// 普通用户使用手册
			boolean b = loginService.isPathExist("static/doc/"+versionText+"/用户操作手册.pdf");
			if(b) {
				model.addAttribute("doc", "/doc/" + versionText + "/用户操作手册.pdf");
			}else {//不存在是给定默认
				model.addAttribute("doc", "/doc/用户操作手册.pdf");
			}
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		if(SlmRuntimeEasy.NET_LIMIT > 0 && SlmRuntimeEasy.ONLINE + 1 > SlmRuntimeEasy.NET_LIMIT) {
			return "redirect:/doc/limitprompt.html";
		}
		session.setAttribute("username", userDetails.getRealname()); // 绑定用户名

		//具有管理平台权限
		if("1".equals(userDetails.getUsertype())) {


				// 跳转声像系统
				if ("2".equals(sysType) ||"2".equals(userDetails.getType())) {
					model.addAttribute("username", userDetails.getLoginname());
					model.addAttribute("pwd",userDetails.getLoginpassword());
					model.addAttribute("iarchivesxPath", iarchivesxPath);
					logger.info("***************************"+userDetails.getUsername()+"登录到声像系统***************************");
					return "/jump";
				}

				// 跳转新闻影像系统
				if ("6".equals(sysType) || "6".equals(userDetails.getType())) {
					model.addAttribute("username", userDetails.getLoginname());
					model.addAttribute("pwd",userDetails.getLoginpassword());
					model.addAttribute("iarchivesxPath", iarchivexwyxPath);
					logger.info("***************************"+userDetails.getUsername()+"登录到新闻影像***************************");

					return "/jump";
				}

				// 编研管理系统，目录中心
            if (sysType !=null? ("5".equals(sysType)|| "7".equals(sysType)|| "12".equals(sysType)|| "13".equals(sysType)||"14".equals(sysType)):
                    ("5".equals(userDetails.getType())||"7".equals(userDetails.getType())||"12".equals(userDetails.getType())||"13".equals(userDetails.getType())|| "14".equals(userDetails.getType()))) {
					List<Tb_right_function> funs = unifyService.getList(sysType !=null? sysType:userDetails.getType(), userDetails.getRealname(), userDetails.getUsername());
					model.addAttribute("functions",funs);
					model.addAttribute("systemLoginType", systemLoginType);//系统类型
					if("5".equals(sysType !=null? sysType:userDetails.getType())){
						logger.info("***************************"+userDetails.getUsername()+"登录到目录系统编制管理系统***************************");
                    }else if("7".equals(sysType !=null? sysType:userDetails.getType())){
						logger.info("***************************"+userDetails.getUsername()+"登录到编制管理系统***************************");
					}else if("12".equals(sysType !=null? sysType:userDetails.getType())){
						logger.info("***************************"+userDetails.getUsername()+"登录到电子档案元数据登记系统***************************");
					}else if("13".equals(sysType !=null? sysType:userDetails.getType())){
						logger.info("***************************"+userDetails.getUsername()+"登录到安全离线数据交换系统***************************");
					}else{
						logger.info("***************************"+userDetails.getUsername()+"登录到业务指导系统***************************");

					}
					return "/compilationAndCatalogue";
				}

				// 跳转自助查询
				if ("9".equals(sysType)||"9".equals(userDetails.getType())) {
					return "/indexcx";
				}

			// 综合事务管理系统
			if ("10".equals(sysType)||"10".equals(userDetails.getType())) {
				List<Tb_right_function> funs = unifyService.getList(sysType !=null? sysType:userDetails.getType(), userDetails.getRealname(), userDetails.getUsername());
				model.addAttribute("functions",funs);
				logger.info("***************************"+userDetails.getUsername()+"登录到综合事务管理系统***************************");
				return "/intergatedServices";
			}

				// 跳转利用平台
				if("0".equals(sysType)|| ("0".equals(userDetails.getType()) && sysType == null))
				{
					model.addAttribute("visitNum", logService.getVisitNum());//获取访问人次
					model.addAttribute("systemLoginType", systemLoginType);//系统类型
					if(timeStamp==null){
						timeStamp = new SimpleDateFormat("MMddhhmm").format(new Date());//获取时间
					}
					model.addAttribute("timeStamp", timeStamp);//日期标记
					logger.info("***************************"+userDetails.getUsername()+"登录到利用平台***************************");
					return "/indexly";
				}
				if("8".equals(sysType)||("8".equals(userDetails.getType())&&sysType==null)){
					logger.info("***************************"+userDetails.getUsername()+"登录到库房管理系统***************************");

				}
			// 电子文档系统
			if ("11".equals(sysType) || "11".equals(userDetails.getType())) {
				logger.info("***************************"+userDetails.getUsername()+"登录到立档单位归档系统***************************");
			}
				if("3".equals(sysType)||("3".equals(userDetails.getType())&&sysType==null)){
					logger.info("***************************"+userDetails.getUsername()+"登录到数字化加工系统***************************");
				}
				if("4".equals(sysType)||("4".equals(userDetails.getType())&&sysType==null)){
				logger.info("***************************"+userDetails.getUsername()+"登录到基础平台系统***************************");
				}
				if("1".equals(sysType)||("1".equals(userDetails.getType())&&sysType==null)){
				logger.info("***************************"+userDetails.getUsername()+"登录到档案资源管理***************************");
				}

		}

		//只有利用平台权限
		else if("0".equals(userDetails.getUsertype())){
			//跳转利用平台
			if ( sysType!=null && ! "0".equals(sysType)) {
				model.addAttribute("isLy", "true");
				logger.info("***************************"+userDetails.getUsername()+"登录到利用平台***************************");

			}

			// 跳转自助查询
			if ("9".equals(sysType)||"9".equals(userDetails.getType())) {
				return "/indexcx";
			}
			if(timeStamp==null){
				timeStamp = new SimpleDateFormat("MMddhhmm").format(new Date());//获取时间
			}
			model.addAttribute("timeStamp", timeStamp);//日期标记
			return "/indexly";
		}else if("a".equals(userDetails.getType())){
			return "/indexcx";
		}


		return "/index";
	}


	/**
	 * 退出系统
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @return 登录页面
	 */
	@RequestMapping(value = "/logoutt", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login";
	}

	/**
	 * 自助查询系统-退出系统
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @return 登录页面
	 */
	@RequestMapping(value = "/outlogoutt", method = RequestMethod.GET)
	public String outlogoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/selfquery";
	}

	@RequestMapping(value = "/selfquerylogoutt", method = RequestMethod.GET)
	public String selfquerylogoutt(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/SelfServiceQuery";
	}


	@RequestMapping("/message")
	public String message(Model model) {
		return "/message";
	}

	@RequestMapping("/zt")
	public String zt() {
		return "/setting/zt";
	}

	@RequestMapping("/anim")
	public String anim() {
		return "/setting/anim";
	}

	@RequestMapping("/checkInit")
	@ResponseBody
	public ExtMsg checkInit() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean initOrgan = loginService.initOrgan();
		boolean initFunds = loginService.initFunds();
		boolean initUserPwd = loginService.initUserPwd(userDetails.getLoginname());
		return new ExtMsg(true, "", initOrgan + "," + initFunds + "," + initUserPwd);
	}

	@RequestMapping("/initialize")
	public String userMsg(Model model, String init) {
		model.addAttribute("init", init);
		return "/setting/initialize";
	}

	@RequestMapping("/initdata")
	@ResponseBody
	public ExtMsg initdata(String organname, String fundsname, String fundscode, String xtpwd,
						   String bmpwd, String sjpwd, String dapwd,String shpwd, boolean consistent) {
		String msg = userService.initSysData(organname, fundsname, fundscode, xtpwd, bmpwd, sjpwd, dapwd,shpwd, consistent);
		return new ExtMsg(true, msg, null);
	}

	@RequestMapping("/getlist")
	@ResponseBody
	public List getlist() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_role> roles = userDetails.getRoles();
		String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(roles, "roleid");
		List<Tb_right_function> functions = new ArrayList<>();
		if(slmRuntimeEasy.hasPlatform()){
			functions = functionRepository.findByfunctionsdesktop(roleids, "desktop",
					userDetails.getUserid());
		}else{
			functions = functionRepository.findByfunctionsdesktopfalse(roleids, "desktop",
					userDetails.getUserid());
		}
		List<Tb_right_function> fns = new ArrayList<>();
		List<String> filter=new ArrayList<String>();//过滤功能权限的 functioncode集合
		filter.add("digital");//数字化系统
		filter.add("t20");//目录中心
		filter.add("t19");//新闻影像管理
		filter.add("t10");//编研管理系统
		filter.add("t17");//库房系统
		filter.add("filesystem");//档案资源管理系统
		filter.add("t110");//综合事务管理系统
		filter.add("t111");//电子文档系统
        filter.add("t21");//业务指导系统

		for(Tb_right_function function:functions){//过滤 其它系统
			if(!filter.contains(function.getCode().trim())){
				//局域网屏蔽问卷管理
				if("问卷管理".equals(function.getName())&&"0".equals(systemLoginType)){
					continue;
				}else if("jhxt".equals(function.getTkey())||"ysjxt".equals(function.getTkey())){
					continue;
				}
				else {
					fns.add(function);
				}
			}
		}
		//return fns;
		List<Tb_right_function> editFunctions = new ArrayList<>();
		String url="";
		if(timeStamp==null){
			timeStamp = new SimpleDateFormat("MMddhhmm").format(new Date());//获取时间
		}
		for(Tb_right_function function:fns){
			url=function.getUrl();
			if(url==null||url.indexOf("/")==-1){
				editFunctions.add(function);
				continue;
			}
			Tb_right_function editFunction=new Tb_right_function();
			BeanUtils.copyProperties(function,editFunction);
			if(url.contains("=")){//  /dataopen/main?isp=k64
				url = url + "&v="+timeStamp;
			}else{//  /checkGroup/main
				url = url + "?v="+timeStamp;
			}
			editFunction.setUrl(url);
			editFunctions.add(editFunction);
		}
		return editFunctions;
	}

	@RequestMapping("/indexswitch")
	public String indexswitch(Model model, String sysType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
		Tb_Personalized personalizeds = loginService.index(userDetails);// 获取用户角色,资源等信息
		String personalized = JSON.toJSONString(personalizeds); // 个性化信息
		model.addAttribute("personalized", personalized); // 绑定个性化信息
		model.addAttribute("userid", userDetails.getUserid()); // 绑定用户id
		model.addAttribute("realname", userDetails.getRealname()); // 绑定用户真实姓名
		model.addAttribute("loginname", userDetails.getLoginname()); // 绑定用户登录账号
		model.addAttribute("imgsrc", "/electronic/outputUserimg");// 绑定用户头像
		model.addAttribute("sysType", sysType != null ? sysType : userDetails.getType());// 绑定系统类型
		model.addAttribute("platformopen", slmRuntimeEasy.hasPlatform());//绑定利用平台开关
		model.addAttribute("single", slmRuntimeEasy.isSingle());//绑定个性化开关
		model.addAttribute("usertype", userDetails.getUsertype());//用户权限类型
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		if(SlmRuntimeEasy.NET_LIMIT > 0 && SlmRuntimeEasy.ONLINE + 1 > SlmRuntimeEasy.NET_LIMIT) {
			return "redirect:/doc/limitprompt.html";
		}
		session.setAttribute("username", userDetails.getRealname()); // 绑定用户名
//		if(sysType!=null||!slmRuntimeEasy.hasPlatform()){
//			if(!slmRuntimeEasy.hasPlatform()&&sysType!=null){
//				return "/error";
//			}
//			if(slmRuntimeEasy.hasPlatform()&&sysType!=null&&"0".equals(userDetails.getUsertype())){
//				return "/error";
//			}
//		}
		if("1".equals(systemLoginType)) {//政务网
			return "/governmentWitch";
		}else{
			return "/indexswitch";
		}
	}

	@RequestMapping("/geticon")
	@ResponseBody
	public List geticon(String sysType) {
		List<Tb_Icon> list = new ArrayList<Tb_Icon>();
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		list = iconRepository.findByUseridAndSystypeOrderBySortsequence(userDetails.getUserid(),sysType);
		List<String> syList = Arrays.asList("系统管理员");
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setUserid(null);
		}
		if(syList.contains(userDetails.getRealname())){
			return list;
		}
		// 根据userid获取当前用户的功能权限和当前用户角色的功能权限并集
		List<String> functionnames = new ArrayList<>();
		if(slmRuntimeEasy.hasPlatform()){
			functionnames = functionRepository.findUserRoleFunctionForUserId(userDetails.getUserid());
		}else{
			functionnames = functionRepository.findUserRoleFunctionForUserIdfalse(userDetails.getUserid());
		}
		// 使用迭代器移出没权限的桌面图标
		Iterator<Tb_Icon> iter = list.iterator();
		while (iter.hasNext()) {
			Tb_Icon tb_icon = iter.next();
			if (!functionnames.contains(tb_icon.getText())) {
				iter.remove();
			}
		}
		//return list;
		List<Tb_Icon> editList = new ArrayList<>();
		String url="";
		if(timeStamp==null){
			timeStamp = new SimpleDateFormat("MMddhhmm").format(new Date());//获取时间
		}
		for(Tb_Icon icon:list){
			url=icon.getUrl();
			if(url==null||url.indexOf("/")==-1){
				editList.add(icon);
				continue;
			}

			Tb_Icon editIcon=new Tb_Icon();
			BeanUtils.copyProperties(icon,editIcon);
			if(url.contains("=")){//  /dataopen/main?isp=k64
				url = url + "&v="+timeStamp;
			}else{//  /checkGroup/main
				url = url + "?v="+timeStamp;
			}
			editIcon.setUrl(url);
			editList.add(editIcon);
		}
		return  editList;
	}

	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response,String type) throws Exception {
		String fullFileName = request.getServletContext()
				.getRealPath("WEB-INF/classes/static/plugins/GoogleChromeframe.msi");
		if("ca".equals(type)){
			fullFileName = request.getServletContext()
					.getRealPath("WEB-INF/classes/static/plugins/caClient.zip");
		}
		File msi = new File(fullFileName);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + new String(msi.getName().getBytes("gbk"), "iso8859-1") + "\"");
		// response.setHeader("Content-Disposition","attachment;
		// filename=1.pdf");
		response.setContentType("application/msi");
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
		FileInputStream inputStream = new FileInputStream(msi);
		out = response.getOutputStream();

		int b = 0;
		byte[] buffer = new byte[1024];
		while ((b = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, b);
		}
		inputStream.close();
		out.flush();
		out.close();
	}

	/**
	 * 根据key值重置系统管理员密码
	 *
	 * @param username
	 *            用户名
	 * @param key
	 *            key值
	 * @return 修改状态信息
	 */
	@RequestMapping("/adminResetPwd")
	@ResponseBody
	public String adminResetPwd(String username, String key) {
		try {
			return userService.resetUserPW(username,key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "操作失败!";
	}

	/**
	 * 获取产品信息
	 *
	 * @return 返回产品信息
	 */
	@RequestMapping("/getProductMsg")
	@ResponseBody
	public ExtMsg getProductMsg() {
		String versionText = slmRuntimeEasy.getVersionName();// 版本
		String versionDate = "永久"; // 到期时间
		String versionCode = SlmRuntimeEasy.VERSION_CURRENT;
		//过滤简化功能
		String[] funs = new String[]{"个性化","批量处理-批量修改","批量处理-批量替换","批量处理-批量增加","统计项更新","页数矫正",
				"编辑新案卷","调序","电子文件检索","查档实体档案统计","回收管理","数据转移","档号对齐","批量修改","导入导出-导出xml",
				"导入导出-导出xml和原文","高级检索"};
		if(slmRuntimeEasy.isSingle()){
			loginService.updataFunctionStatus("0",funs);
		}else{
			loginService.updataFunctionStatus("1",funs);
		}
		//数据交换功能
		if(slmRuntimeEasy.hasExchange()){
			loginService.updataFunctionStatusWithChilds("1","数据交换");
		} else {
			loginService.updataFunctionStatusWithChilds("0","数据交换");
		}
		//数据采集、审核功能
		if(!slmRuntimeEasy.hasCapture()){
			loginService.updataFunctionStatus("0","数据采集");
			loginService.updataFunctionStatus("0","数据审核");
		}else {
			loginService.updataFunctionStatus("1","数据采集");
			//根据配置信息的system.audit.opened是否需要数据审核模块
			loginService.controlAudit();
		}
		//编演管理功能
		if(slmRuntimeEasy.hasCompilation()){
			loginService.updataFunctionStatusWithChilds("1","编研管理");
		}else{
			loginService.updataFunctionStatusWithChilds("0","编研管理");
		}
		//全宗卷管理功能
		if(slmRuntimeEasy.hasFondsArchive()){
			loginService.updataFunctionStatusWithChilds("1", "全宗卷管理");
		}else{
			loginService.updataFunctionStatusWithChilds("0", "全宗卷管理");
		}
		//全文检索功能
		/*if(slmRuntimeEasy.hasFulltext()){
			loginService.updataFunctionStatus("1","全文检索");
		}else{
			loginService.updataFunctionStatus("0","全文检索");
		}*/
		versionDate = slmRuntimeEasy.hasOvertime()
				? new SimpleDateFormat("yyyy年MM月dd日").format(new Date(MyFilterSecurityInterceptor.overdueTime))
				: versionDate;
		return new ExtMsg(true, "", new String[] { versionText, versionDate });
	}

	@RequestMapping("/verificationCode/getVerificationCode")
	public void getVerificationCode(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置不缓存图片
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "No-cache");
		response.setDateHeader("Expires", 0);
		// 指定生成的响应图片,一定不能缺少这句话,否则错误.
		response.setContentType("image/jpeg");
		int width = 86, height = 22; // 指定生成验证码的宽度和高度
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 创建BufferedImage对象,其作用相当于一图片
		Graphics g = image.getGraphics(); // 创建Graphics对象,其作用相当于画笔
		Graphics2D g2d = (Graphics2D) g; // 创建Grapchics2D对象
		Random random = new Random();
		Font mfont = new Font("微软雅黑", Font.CENTER_BASELINE, 17); // 定义字体样式
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height); // 绘制背景
		g.setFont(mfont); // 设置字体
		g.setColor(getRandColor(180, 200));

		// 绘制100条颜色和位置全部为随机产生的线条,该线条为2f
		for (int i = 0; i < 100; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int x1 = random.nextInt(6) + 1;
			int y1 = random.nextInt(12) + 1;
			BasicStroke bs = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL); // 定制线条样式
			Line2D line = new Line2D.Double(x, y, x + x1, y + y1);
			g2d.setStroke(bs);
			g2d.draw(line); // 绘制直线
		}
		// 输出由英文，数字，和中文随机组成的验证文字，具体的组合方式根据生成随机数确定。
		String sRand = "";
		String ctmp = "";
		int itmp = 0;
		// 制定输出的验证码为四位
		for (int i = 0; i < 4; i++) {
			switch (random.nextInt(3)) {
				case 10: // 生成A-Z的字母
					itmp = random.nextInt(26) + 65;
					ctmp = String.valueOf((char) itmp);
					break;
				// case 2: //生成汉字
				// String[]
				// rBase={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
				// //生成第一位区码
				// int r1=random.nextInt(3)+11;
				// String str_r1=rBase[r1];
				// //生成第二位区码
				// int r2;
				// if(r1==13){
				// r2=random.nextInt(7);
				// }else{
				// r2=random.nextInt(16);
				// }
				// String str_r2=rBase[r2];
				// //生成第一位位码
				// int r3=random.nextInt(6)+10;
				// String str_r3=rBase[r3];
				// //生成第二位位码
				// int r4;
				// if(r3==10){
				// r4=random.nextInt(15)+1;
				// }else if(r3==15){
				// r4=random.nextInt(15);
				// }else{
				// r4=random.nextInt(16);
				// }
				// String str_r4=rBase[r4];
				// //将生成的机内码转换为汉字
				// byte[] bytes=new byte[2];
				// //将生成的区码保存到字节数组的第一个元素中
				// String str_12=str_r1+str_r2;
				// int tempLow=Integer.parseInt(str_12, 16);
				// bytes[0]=(byte) tempLow;
				// //将生成的位码保存到字节数组的第二个元素中
				// String str_34=str_r3+str_r4;
				// int tempHigh=Integer.parseInt(str_34, 16);
				// bytes[1]=(byte)tempHigh;
				// /**
				// * 汉字显示
				// */
				// // ctmp=new String(bytes);
				// ctmp = new String(bytes,"gb2312");
				// break;
				default:
					itmp = random.nextInt(10) + 48;
					ctmp = String.valueOf((char) itmp);
					break;
			}
			sRand += ctmp;
			Color color = new Color(20 + random.nextInt(110), 20 + random.nextInt(110), random.nextInt(110));
			g.setColor(color);
			// 将生成的随机数进行随机缩放并旋转制定角度 PS.建议不要对文字进行缩放与旋转,因为这样图片可能不正常显示
			/* 将文字旋转制定角度 */
			Graphics2D g2d_word = (Graphics2D) g;
			AffineTransform trans = new AffineTransform();
			trans.rotate((45) * 3.14 / 180, 15 * i + 8, 7);
			/* 缩放文字 */
			float scaleSize = random.nextFloat() + 0.8f;
			if (scaleSize > 1f)
				scaleSize = 1f;
			trans.scale(scaleSize, scaleSize);
			g2d_word.setTransform(trans);
			g.drawString(ctmp, 15 * i + 12, 14);
		}
		HttpSession session = request.getSession();
		session.removeAttribute("verificationCode");
		session.setAttribute("verificationCode", sRand);
		g.dispose(); // 释放g所占用的系统资源
		ImageIO.write(image, "JPEG", response.getOutputStream()); // 输出图片
	}

	@RequestMapping("/verificationCode/checkVerificationCode")
	@ResponseBody
	public ExtMsg checkVerificationCode(String code, HttpServletRequest request, HttpServletResponse response) {
		ExtMsg msg = new ExtMsg(false, "", null);
		try {
			HttpSession session = request.getSession();
			String cacheCode = session.getAttribute("verificationCode") != null
					? ((String) session.getAttribute("verificationCode")).toLowerCase() : "";
			if ((code.toLowerCase()).equals(cacheCode)) {
				msg.setSuccess(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	/* 该方法主要作用是获得随机生成的颜色 */
	public Color getRandColor(int s, int e) {
		Random random = new Random();
		if (s > 255)
			s = 255;
		if (e > 255)
			e = 255;
		int r, g, b;
		r = s + random.nextInt(e - s); // 随机生成RGB颜色中的r值
		g = s + random.nextInt(e - s); // 随机生成RGB颜色中的g值
		b = s + random.nextInt(e - s); // 随机生成RGB颜色中的b值
		return new Color(r, g, b);
	}
	@RequestMapping("/getplatformopen")
	@ResponseBody
	public ExtMsg getPlatformopen() {
		String platform = String.valueOf(slmRuntimeEasy.hasPlatform());
		return new ExtMsg(true, "", platform);
	}

	//判断用户是否有登录平台的权限
	@RequestMapping("/isPermiss")
	@ResponseBody
	public ExtMsg isPermiss(String loginname,String logintype) {
		boolean flag;
		Tb_user user=userService.findByLoginname(loginname);
        if("1".equals(systemLoginType) && ("11".equals(logintype) || "1".equals(logintype))){//仅在政务网做过滤
            ExtMsg extMsg = loginService.isLDPermis(user,logintype);
            if(extMsg.isSuccess()){//馆用户无立档系统权限、立档用户无管理系统权限
                return new ExtMsg(extMsg.isSuccess(),extMsg.getMsg(),null);
            }
        }
		flag=loginService.isPermiss(user,logintype);
		return new ExtMsg(flag,null,null);
	}

	/**
	 * 获取登录的系统地址
	 * @param sysType
	 * @return
	 */
	@RequestMapping("/getSysFormUrl")
	@ResponseBody
	public String getSysFormUrl(String sysType) {
		if("2".equals(sysType))
			return this.iarchivesxPath;
		if("6".equals(sysType))
			return this.iarchivexwyxPath;
//		if("11".equals(sysType))
//			return this.iarchiveElectronicSyPath;
		return null;
	}

	/**
	 * 获取网关认证随机数
	 * @param number
	 * @return
	 */
	@RequestMapping(value="/netca/getRandomByServer", method = RequestMethod.POST)
	@ResponseBody
	public String getRandomByServer(String number) {
		String random = "";
		try {
			if(number!=null){
				int length = Integer.valueOf(number);
				if(length==0){
					random = NetcaPKI.getRandom(16);
				}else{
					random = NetcaPKI.getRandom(length);
				}
			}else{
				random = NetcaPKI.getRandom(16);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return random;
	}

	/**
	 * 获取网关认证随机数  signValue: signValue, source: g_ticket, extendVerify
	 * @param signValue  客户端签名值
	 * @param source  随机数
	 * @param extendVerify  网关验证
	 * @return
	 */
	@RequestMapping("/netca/getCertLoginVerify")
	@ResponseBody
	public String getCertLoginVerify(String signValue,String source,String extendVerify) throws ServletException, IOException {
		try {

			//步骤四：验证签名的有效性
			String cert = NetcaPKI.verifySignedData(source, signValue).pemEncode();
			//获取签名证书
			String certValue = cert;
			Certificate certificate = null;
			if(certValue!=null&&!(certValue.trim().equals(""))){		//需要网关验证证书 or OCSP验证
				certificate = new Certificate(certValue);
				if(certificate==null){
					//responsePrint(response,"构造证书失败，请查看传入的证书是否有效");
					//return;
					System.out.println("构造证书失败，请查看传入的证书是否有效");
					return null;
				}

				//验证签名值
				if(certificate.pemEncode().equals(cert)){
					System.out.println("步骤四：验证签名值成功");
				}else{
					//responsePrint(response,"验证签名值失败");
					//return;
					System.out.println("验证签名值失败");
					return null;
				}

				//步骤五：网关验证证书的有效性
				if("verifyCert".equals(extendVerify.trim())){
					String verifytime = null;
					int ku = 0xc0;
					HashMap<String, String> map = CertAuthClient.verifyCertEx(certificate, verifytime, ku, SDEFAULTSERVERCERT, SMETHODONESERVERURL);
					//HashMap<String, String> map = CertAuthClient.verifyCertByWebService(certificate, verifytime, ku, WEBSERVICEURL);
					System.out.println("1.signature="+map.get("signature"));
					System.out.println("2.certName="+map.get("certName"));
					System.out.println("3.digest="+map.get("digest"));
					System.out.println("4.version="+map.get("version"));
					System.out.println("5.verifytime="+map.get("verifytime"));
					System.out.println("6.status="+map.get("status"));

					if(map.get("status").equals("0")){
						System.out.println("步骤五：网关验证证书成功");
						return "网关验证证书成功";
					}else{
						System.out.println("步骤五：网关验证未通过，登录失败");
						System.out.println("步骤五：证书状态："+CertAuthClient.parseCertCode(Integer.valueOf(map.get("status"))));
						//return;
						return null;
					}
				}

			}

			/*certificate = new Certificate(cert);
			//步骤六：获取证书的微缩图
			String certHashValue = NetcaPKI.getX509CertificateInfo(certificate, 1);
			System.out.println(certHashValue);
			System.out.println("<br><span style='color:red'>证书微缩图为：</span>" + certHashValue);

			//步骤七：获取客户服务号  //证书唯一标识
			String UsrCertID = NetcaPKI.getX509CertificateInfo(certificate, NetcaPKI.NETCAPKI_CERT_INFO_USERCERTNO);// NetcaPKI.getX509CertificateInfo(oCert, 9);
			System.out.println("证书绑定值为："+UsrCertID);
			if(UsrCertID!=null&&!UsrCertID.trim().equals("")){
				System.out.println("<br><span style='color:red'>客户绑定值为：</span>" + UsrCertID);
			}
			if(certValue!=null&&!(certValue.trim().equals(""))){
				//showMessage.append(extendContent);
			}

			System.out.println("<br>登录成功<hr>");*/

			//System.out.println("showMessage.toString():"+showMessage.toString());
			/*String content = showMessage.toString();
			responsePrint(response, content);*/

		} catch (Exception e) {
			e.printStackTrace();
			//responsePrint(response,e.getMessage());
		} finally{
			/*if(certificate!=null){
				certificate.free();
			}*/
		}
		return "";
	}



	@RequestMapping(value = "/getSSOValue")
	@ResponseBody
	public ExtMsg getSSOValue(String userid){

		if(null!=userid&&!"".equals(userid)){
			Tb_user user = userService.findUser(userid);
			if(null!=user) {
				Map<String,String> r = new HashMap<>();
				r.put("user",user.getLoginname());
				r.put("pwd", user.getPassword());
				return new ExtMsg(false, "", r);
			}
		}
		return new ExtMsg(false,null,null);
	}

}