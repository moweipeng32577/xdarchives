package com.wisdom.web.security;

import com.wisdom.util.LogAop;
import com.wisdom.util.MD5;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.service.LoginService;
import com.wisdom.web.service.OrganService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

@Service("MyUserDetailsServiceImpl")
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	LogMsgRepository logMsgRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	OrganService organService;

	@Autowired
	LoginService loginService;

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		String cacheCode = session.getAttribute("verificationCode") != null
				? ((String) session.getAttribute("verificationCode")).toLowerCase() : "";
		String[] flStr = username.split("&");
		if("0".equals(flStr[0])&&"0".equals(flStr[1])&&"0".equals(flStr[2])&&"0".equals(flStr[3])) {
			throw new AccountExpiredException("数字证书还没有绑定相关用户!");
		}
		if(!"0".equals(flStr[2])){//验证码为0，声像跳转
			if(!cacheCode.equals(flStr[2])){
//				if("xd123".equals(flStr[2])){//增加万能验证码，无需验证
//				}else {
					throw new AccountExpiredException("验证码有误！&"+flStr[0]+"&"+flStr[3]);// UsernameNotFoundException
//				}
			}
		}
		Tb_user user = userRepository.findByLoginname(flStr[0]);
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), flStr[0], null, null, startTime,
				startTime, "0ms", "用户登录", "用户不存在！");// 创建日志记录对象
		if (user == null) {
			logMsgRepository.save(logMsg);
			throw new AccountExpiredException("用户名不存在！");// UsernameNotFoundException
		}
		else {
		    if("1".equals(systemLoginType) && ("11".equals(flStr[1]) || "1".equals(flStr[1]))&&(!("0".equals(flStr[2])&&"0".equals(flStr[3])))){//仅在政务网做过滤,证书登录不在此验证
                ExtMsg extMsg = loginService.isLDPermis(user,flStr[1]);
                if(extMsg.isSuccess()){
                    throw new AccountExpiredException(extMsg.getMsg());
                }
            }
			if(loginService.isPermiss(user,flStr[1])){
				throw new AccountExpiredException("用户没有该平台权限！");
			}
			String organ = organService.findFullOrgan("", user.getOrganid());
			logMsg.setOrgan(user.getRealname());
			logMsg.setOrgan(organ);
			logMsg.setRealname(user.getRealname());//设置Tb_log_msg用户名
			boolean loginStatus = true;//登录状态，用于判断是否能进行登录系统
			if("外来人员".equals(user.getOutuserstate())){
//				String infodate = "";
//				if (user.getInfodate().equals("一天")) {
//					infodate = "24";
//				} else if (user.getInfodate().equals("一周")) {
//					infodate = "168";
//				} else {
//					infodate = "720";
//				}
//				int starttime = Integer.parseInt(infodate);
//				Date outstartdata = reportGetDate(user.getOutuserstarttime(),"HOUR",starttime);
				if(new Date().after(user.getExdate())){
					logMsg.setDesci("当前临时账号已超过使用时间！");
					logMsgRepository.save(logMsg);
					loginStatus = false;
					user.setStatus(0L);
					userRepository.save(user);
					throw new LockedException("当前临时账号已超过使用时间,请联系系统管理员！");
				}
			}
			if (user.getStatus() == 0) {
				logMsg.setDesci("当前账号已被禁用！");
				logMsgRepository.save(logMsg);
				loginStatus = false;
				throw new LockedException("当前账号已被禁用,请联系系统管理员！");
			}
			if(!("0".equals(flStr[2])&&"0".equals(flStr[3]))) {//验证码和密码为0，数字证书登录,不验证密码
				if (!(flStr[3]!=null&&flStr[3].equals(user.getPassword())||MD5.MD5(flStr[3]).equals(user.getPassword()))) {
					loginStatus = false;
					throw new AccountExpiredException("用户密码有误！");
				}
			}
			if (loginStatus){//能进行登录时
				switch (flStr[1]){
					case "1"://档案管理系统
						logMsg.setDesci("登录档案资源管理系统");
						break;
					case "2"://声像系统
						logMsg.setDesci("登录声像档案管理系统");
						break;
					case "3"://数字化加工
						logMsg.setDesci("登录数字化加工");
						break;
					case "4"://基础安全平台
						logMsg.setDesci("登录基础安全平台");
						break;
					case "5"://目录中心
						logMsg.setDesci("登录目录中心系统");
						break;
					case "6"://新闻影像系统
						logMsg.setDesci("登录新闻影像采集归档管理系统");
						break;
					case "7"://编研管理系统
						logMsg.setDesci("登录编研管理系统");
						break;
					case "8"://库房管理系统
						logMsg.setDesci("登录库房管理系统");
						break;
					case "0"://利用平台
						logMsg.setDesci("登录利用平台");
						break;
					case "10"://综合事务管理系统
						logMsg.setDesci("登录综合事务管理系统");
						break;
					case "11"://立档单位归档系统”
						logMsg.setDesci("登录立档单位归档系统");
						break;
					case ""://利用平台
						logMsg.setDesci("登录利用平台");
						break;
				}
				logMsgRepository.save(logMsg);
			}

		}

		SecurityUser user1 = new SecurityUser();
		user1.setLoginname(user.getLoginname());
		user1.setLoginpassword(user.getLoginpassword());
		user1.setUserid(user.getUserid());
		user1.setRealname(user.getRealname());
		user1.setType(flStr[1]);
		user1.setUsertype(user.getUsertype());
		user1.setSex(user.getSex());
		user1.setUserid(user.getUserid());
		user1.setPlatformchange(user.getPlatformchange());
		user1.setLetternumber(user.getLetternumber());
		user1.setOutuserstate(user.getOutuserstate());
		user1.setOutuserstarttime(user.getOutuserstarttime());
		user1.setPhone(user.getPhone());
        user1.setReplaceOrganid(rightOrganRepository.findByOrganid(user.getOrganid()).getOrganid());
		user1.setLogout_time(user.getLogout_time());//密码修改时间
        String nickname=user.getNickname();
		if(nickname!=null&&nickname.length()>20){
			user1.setNickname(nickname);//数字证书id
		}
		user1.setOrganid(rightOrganRepository.findByOrganid(user.getOrganid()).getOrganname());
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_root");// root角色特权
		grantedAuthorities.add(grantedAuthority);
		return user1;
	}

	public Date reportGetDate(Date d,String type,int number){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		if(type.equals("HOUR")) calendar.add(Calendar.HOUR_OF_DAY,number);
		Date date = calendar.getTime();
		return date;
	}
}