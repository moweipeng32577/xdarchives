package com.wisdom.configuration;

import com.wisdom.util.PlanListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.EventListener;

/**
 * Created by tanly on 2017/11/23 0023.
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {
    @Bean
    public ServletListenerRegistrationBean<EventListener> RunConvertlistener(){
        ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<>();
        registrationBean.setListener(new PlanListener());
        return registrationBean;
    }

}
