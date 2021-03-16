package com.xdtech.project.lot.configuration;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by Rong on 2018/11/16.
 */
@ImportResource("classpath:ureport-console-context.xml")
@EnableAutoConfiguration
@Configuration
public class UReportConfig {

    @Bean
    public ServletRegistrationBean ureportServlet(){
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }

}
