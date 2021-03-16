package com.wisdom.web.security;

import com.wisdom.util.LogAop;
import com.wisdom.util.MD5;
import com.wisdom.web.entity.Tb_log_msg;
import com.wisdom.web.entity.Tb_right_organ;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.LogMsgRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.LoginService;
import com.wisdom.web.service.OrganService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * 自定义验证方式
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private MyUserDetailsService userService;
	
	@Autowired
	OrganService organService;

	@Autowired
	LogMsgRepository logMsgRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	LoginService loginService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		String[] flStr = password.split("&");
		UserDetails user;
		if("DK".equals(flStr[flStr.length-1]) || "SR".equals(flStr[flStr.length-1])){//自助查询登录

			SecurityUser userTemp=new SecurityUser();
			userTemp.setType("a");
			userTemp.setLoginname(username);
			userTemp.setRealname(username);
			userTemp.setLoginpassword(MD5.MD5(password));
//			Tb_right_organ organ = organService.findByOrganname("外来人员部门");
			Tb_log_msg log_msg=new Tb_log_msg();
			log_msg.setIp(LogAop.getIpAddress());
			log_msg.setDesci("外来人员自主查询");
			log_msg.setOrgan("外来人员部门");
			log_msg.setRealname(password);
			log_msg.setModule("自主查询");
			log_msg.setOperate_user(username);
			log_msg.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			logMsgRepository.save(log_msg);
//			if (organ != null) {
//				userTemp.setOrganid(organ.getOrganid());
//			}
			user=userTemp;
			if (MD5.MD5(password).equals(user.getPassword())) {// 密码匹配验证
				Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				return new UsernamePasswordAuthenticationToken(user, password, authorities);
			}
		}
		Tb_user loginUser;
		if(flStr.length>1&&"wztca".equals(flStr[1])){//数字证书登录
			loginUser = userRepository.findByNickname(flStr[0]);
			if(loginUser==null){//证书用户不存在
				user = userService.loadUserByUsername("0" + '&' + "0" +'&'+"0"+'&'+"0");
				throw new BadCredentialsException("数字证书还没有绑定相关用户!");
			}else{
				String sysChoose="1";//默认管理平台
				if(username.indexOf("&")>-1){
					sysChoose=username.substring(username.indexOf("&")+1);//选择登录的系统
				}
				if("1".equals(sysChoose)){
					sysChoose=loginService.getLDPermis(loginUser);
				}
				user = userService.loadUserByUsername(loginUser.getLoginname() + '&' + sysChoose +'&'+"0"+'&'+"0");
				Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				return new UsernamePasswordAuthenticationToken(user, loginUser.getPassword(), authorities);
			}

		}else{//账号登录
			if(password.length()-password.replaceAll("&","").length()==1){//只有一个&，属于声像跳转
				password = flStr[0];
				user = userService.loadUserByUsername(username + '&' + flStr[1]+'&'+""+'&'+password);
			}else{
				password = flStr[0];
				user = userService.loadUserByUsername(username + '&' + flStr[1]+'&'+flStr[2]+'&'+password);
			}

			if (password!=null&&password.equals(user.getPassword())||MD5.MD5(password).equals(user.getPassword())) {// 密码匹配验证
				Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				return new UsernamePasswordAuthenticationToken(user, password, authorities);
			}

			loginUser = userRepository.findByLoginname(username);
			String startTime = LogAop.getCurrentSystemTime();// 开始时间
			String organ = organService.findFullOrgan("", loginUser.getOrganid());
			Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), username, loginUser.getRealname(), organ, startTime,
					startTime, "0ms", "用户登录", "密码有误！");
			logMsgRepository.save(logMsg);
			throw new BadCredentialsException("密码有误!");
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return true;
	}
}