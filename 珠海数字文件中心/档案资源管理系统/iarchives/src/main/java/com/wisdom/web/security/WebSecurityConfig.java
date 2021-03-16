package com.wisdom.web.security;

import com.wisdom.util.MD5;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.xml.ws.Endpoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {


    /**
     *
     * 自助查询系统 登录界面验证方法
     *
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
    @Order(2)
    public static class OneSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

        @Autowired
        private MyAuthenticationProvider authenticationProvider;//自定义验证
        @Autowired
        private MyUserDetailsService userDetailsService;

        private MyFilterSecurityInterceptor mySecurityFilter = new MyFilterSecurityInterceptor();

        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {

            return super.authenticationManagerBean();

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
            auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {

                @Override
                public String encode(CharSequence rawPassword) {
                    return MD5.MD5((String) rawPassword);
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encodedPassword.equals(MD5.MD5((String) rawPassword));
                }
            });
        }

        /**
         * .antMatchers 未登陆，允许的请求
         * .addFilterBefore 自定义拦截器
         * .authorizeRequests 请求授权
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/selfquery/**")
                    .addFilterBefore(mySecurityFilter, FilterSecurityInterceptor.class)
                    .authorizeRequests()
                    .antMatchers("/download").permitAll()
                    .antMatchers("/netca/**").permitAll()//CA验证
                    .mvcMatchers("/shared/**").permitAll()
                    .mvcMatchers("/oaimport/**").permitAll()//OA导入数据任务的主动执行
                    .antMatchers("/webService/**").permitAll()
                    .antMatchers("/sharedService/**").permitAll()
                    .antMatchers("/adminResetPwd").permitAll()//管理员重置密码
                    .antMatchers("/getProductMsg").permitAll()//获取版本信息
                    .antMatchers("/doc/*").permitAll()//帮助信息
                    .antMatchers("/verificationCode/*").permitAll()//验证码
                    .antMatchers("/getplatformopen").permitAll()//利用平台控制
                    .antMatchers("/getSSOValue").permitAll()//利用平台控制
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginPage("/selfquery")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/selfquery?error=true")
                    .permitAll()
                    .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/selfquery")
                    .and()

                    .headers()
                    .frameOptions().sameOrigin().disable()//disable X-Frame-Options
                    .csrf().disable() //disable csrf
                    .sessionManagement().maximumSessions(1);

            // 禁止url缓存
            http.requestCache().requestCache(new NullRequestCache());

        }

        @Override
        public void configure(WebSecurity web) throws Exception {

            //静态文件不许授权处理
            web.ignoring().antMatchers("/js/**", "/img/**", "/css/**", "*.woff", "/ext/**", "/xsd/**");
        }
    }


    /**
     *
     * 数字档案馆 登录界面验证方法
     *
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
    @Order(3)
    public static class OtherSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

        @Autowired
        private MyAuthenticationProvider authenticationProvider;//自定义验证
        @Autowired
        private MyUserDetailsService userDetailsService;

        private MyFilterSecurityInterceptor mySecurityFilter = new MyFilterSecurityInterceptor();

        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {

            return super.authenticationManagerBean();

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
            auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {

                @Override
                public String encode(CharSequence rawPassword) {
                    return MD5.MD5((String) rawPassword);
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encodedPassword.equals(MD5.MD5((String) rawPassword));
                }
            });
        }

        /**
         * .antMatchers 未登陆，允许的请求
         * .addFilterBefore 自定义拦截器
         * .authorizeRequests 请求授权
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .addFilterBefore(mySecurityFilter, FilterSecurityInterceptor.class)
                    .authorizeRequests()
                    .antMatchers("/download").permitAll()
                    .antMatchers("/netca/**").permitAll()//CA验证
                    .mvcMatchers("/shared/**").permitAll()
                    .mvcMatchers("/oaimport/**").permitAll()//OA导入数据任务的主动执行
                    .antMatchers("/webService/**").permitAll()
                    .antMatchers("/sharedService/**").permitAll()
                    .antMatchers("/ureport/pdf").permitAll()//报表pdf下载
                    .antMatchers("/adminResetPwd").permitAll()//管理员重置密码
                    .antMatchers("/getProductMsg").permitAll()//获取版本信息
                    .antMatchers("/doc/*").permitAll()//帮助信息
                    .antMatchers("/verificationCode/*").permitAll()//验证码
                    .antMatchers("/getplatformopen").permitAll()//利用平台控制
                    .antMatchers("/getSSOValue").permitAll()//利用平台控制
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginPage("/login")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/login?error=true")
                    .permitAll()
                    .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/login")
                    .and()

                    .headers()
                    .frameOptions().sameOrigin().disable()//disable X-Frame-Options
                    .csrf().disable() //disable csrf
                    .sessionManagement().maximumSessions(1);
            // 禁止url缓存
            http.requestCache().requestCache(new NullRequestCache());

        }

        @Override
        public void configure(WebSecurity web) throws Exception {

            //静态文件不许授权处理
            web.ignoring().antMatchers("/js/**", "/img/**", "/css/**", "*.woff", "/ext/**", "/xsd/**");
        }
    }

    /**
     *
     * 档案管理馆 登录界面验证方法
     *
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
    @Order(1)
    public static class OneSecurityConfigurationAdapter1 extends WebSecurityConfigurerAdapter {

        protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

        @Autowired
        private MyAuthenticationProvider authenticationProvider;//自定义验证
        @Autowired
        private MyUserDetailsService userDetailsService;

        private MyFilterSecurityInterceptor mySecurityFilter = new MyFilterSecurityInterceptor();

        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {

            return super.authenticationManagerBean();

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
            auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {

                @Override
                public String encode(CharSequence rawPassword) {
                    return MD5.MD5((String) rawPassword);
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encodedPassword.equals(MD5.MD5((String) rawPassword));
                }
            });
        }

        /**
         * .antMatchers 未登陆，允许的请求
         * .addFilterBefore 自定义拦截器
         * .authorizeRequests 请求授权
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/SelfServiceQuery/**")
                    .addFilterBefore(mySecurityFilter, FilterSecurityInterceptor.class)
                    .authorizeRequests()
                    .antMatchers("/download").permitAll()
                    .mvcMatchers("/shared/**").permitAll()
                    .mvcMatchers("/oaimport/**").permitAll()//OA导入数据任务的主动执行
                    .antMatchers("/sso/**").permitAll()//OA导入数据任务的主动执行
                    .antMatchers("/webService/**").permitAll()
                    .antMatchers("/sharedService/**").permitAll()
                    .antMatchers("/adminResetPwd").permitAll()//管理员重置密码
                    .antMatchers("/getProductMsg").permitAll()//获取版本信息
                    .antMatchers("/doc/*").permitAll()//帮助信息
                    .antMatchers("/verificationCode/*").permitAll()//验证码
                    .antMatchers("/getplatformopen").permitAll()//利用平台控制
                    .antMatchers("/downloadUserGuide").permitAll()//用户操作手册
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginPage("/SelfServiceQuery")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/SelfServiceQuery?error=true")
                    .permitAll()
                    .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/SelfServiceQuery")
                    .and()

                    .headers()
                    .frameOptions().sameOrigin().disable()//disable X-Frame-Options
                    .csrf().disable() //disable csrf
                    .sessionManagement().maximumSessions(1);

            // 禁止url缓存
            http.requestCache().requestCache(new NullRequestCache());

        }

        @Override
        public void configure(WebSecurity web) throws Exception {

            //静态文件不许授权处理
            web.ignoring().antMatchers("/js/**", "/img/**", "/css/**", "*.woff", "/ext/**", "/xsd/**");
        }
    }
}