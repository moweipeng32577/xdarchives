package com.wisdom.web.security;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 不需要跳转Controller的页面
 */

public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
       /* registry.addViewController("/login").setViewName("/login");
        registry.addViewController("/index").setViewName("/index");
        registry.addViewController("/error1").setViewName("/error");*/
       // registry.addViewController("/zt").setViewName("/setting/zt");
        //registry.addViewController("/anim").setViewName("/setting/anim");

    }
}
