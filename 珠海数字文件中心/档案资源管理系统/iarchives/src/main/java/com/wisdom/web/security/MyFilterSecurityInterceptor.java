package com.wisdom.web.security;

import com.wisdom.util.LogAop;
import com.wisdom.util.SpringContextUtils;
import com.wisdom.web.entity.Tb_right_function;
import com.wisdom.web.repository.FunctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */
//@Component
public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor
        implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static List<String> allUrls = new ArrayList<>();
    private static FunctionRepository functionRepository;
    private static SlmRuntimeEasy slmRuntimeEasy;
    private static List<String> IPControls = Arrays.asList("127.0.0.1","0:0:0:0:0:0:0:1");
    private static Long nowTime;
    public static Long overdueTime;

    @PostConstruct
    public void init(){

    }

    public MyFilterSecurityInterceptor(){
        try{
            if(slmRuntimeEasy == null){//手动注入slmRuntimeEasy
                slmRuntimeEasy = (SlmRuntimeEasy) SpringContextUtils.getBean("slmRuntimeEasy");
            }
            nowTime = new Date().getTime();
            String keydata = slmRuntimeEasy.getUserData();
            if(!"".equals(keydata)){
                SlmRuntimeEasy.VERSION_CURRENT = keydata.split(",")[1];
                SlmRuntimeEasy.NET_LIMIT = Integer.parseInt(keydata.split(",")[2]);
                if(slmRuntimeEasy.hasOvertime()){
                    overdueTime = slmRuntimeEasy.getDeadTime().getTime();
                }else{
                    overdueTime = null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException{
        if(functionRepository==null){//手动注入functionRepository
            functionRepository = (FunctionRepository) SpringContextUtils.getBean("functionRepository");
        }
        Date nowDate = new Date();
        FilterInvocation fi = new FilterInvocation( request, response, chain );
        String url = fi.getRequestUrl().split("\\?")[0];//获取实际请求地址
        HttpServletResponse res = (HttpServletResponse) response;

        if(url.indexOf("/doc")==-1 && !slmRuntimeEasy.hasRemoteAccess() && !IPControls.contains(LogAop.getIpAddress())){
            res.sendRedirect("/doc/djprompt.html");
        }else if(url.indexOf("/doc")==-1&&overdueTime!=null&&nowTime>overdueTime&&slmRuntimeEasy.hasOvertime()){
            res.sendRedirect("/doc/trailPrompt.html");
        }else{
            if(allUrls.size()==0){//获取全部启用功能
                List<Tb_right_function> functions = functionRepository
                        .findByFunctiontypeInAndStatus(new String[]{"desktop","button"},"1");
                if(functions!=null){
                    for(Tb_right_function fn:functions){
                        if(fn.getUrl()!=null){//获取实际请求地址,存入全局集合
                            allUrls.add(fn.getUrl().split("\\?")[0]);
                        }
                    }
                }
            }

            if(allUrls.contains(url)){//判断是否包含在受控资源中
                if(url.equals("/simpleSearch/mainly")){//任何用户都可以使用 利用平台

                }
                else {
                    SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if (!userDetails.getResources().contains(url)) {//对用户资源与授权资源进行匹配
                        logger.error("用户:" + userDetails.getLoginname() + "正在尝试访问其权限范围外的资源(" + url + ")");
                        throw new AccessDeniedException("权限不足");
                    }
                }
            }
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }
    }

    public Class<? extends Object> getSecureObjectClass(){
        return FilterInvocation.class;
    }


    public void invoke( FilterInvocation fi ) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try{
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }finally{
            super.afterInvocation(token, null);
        }

    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource(){
        return null;
    }

    public void destroy(){}
    public void init( FilterConfig filterconfig ) throws ServletException{}
}
