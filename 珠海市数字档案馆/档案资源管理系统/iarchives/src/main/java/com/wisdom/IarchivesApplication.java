package com.wisdom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
},scanBasePackages = {"com.wisdom", "com.xdtech"})
@EnableScheduling   //发现注解@Scheduled的任务并后台执行定时器
@EnableAsync
public class IarchivesApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.sources(IarchivesApplication.class);
		return super.configure(application);
	}

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		FilterRegistration.Dynamic openEntityManagerInViewFilter
				= servletContext.addFilter("openEntityManagerInViewFilter", OpenEntityManagerInViewFilter.class);
		openEntityManagerInViewFilter.setInitParameter("entityManagerFactoryBeanName","entityManagerFactory");
		openEntityManagerInViewFilter.addMappingForUrlPatterns(null, false, "/*");
		super.onStartup(servletContext);
	}
	public static void main(String[] args) {
		SpringApplication.run(IarchivesApplication.class, args);
	}
}
