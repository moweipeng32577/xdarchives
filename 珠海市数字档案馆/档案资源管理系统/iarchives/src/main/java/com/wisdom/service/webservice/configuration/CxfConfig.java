package com.wisdom.service.webservice.configuration;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.webservice.service.SharedWebService;
import com.wisdom.service.webservice.serviceImpl.SharedWebServiceImpl;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;



/**
 * webService服务配置类
 * Created by wjh
 */
@Configuration
public class CxfConfig {
    @Bean
    public ServletRegistrationBean webServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/sharedService/*");
    }
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }
    @Bean
    public SharedWebService userWebService() {
        return new SharedWebServiceImpl();
    }
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), userWebService());
        endpoint.publish("/xd");
        return endpoint;
    }

    public static void main(String[] args) throws  Exception{
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://localhost:8080/sharedService/xd?wsdl");
        Object[] objects = client.invoke("getSharedData", 3,null,null,null,1,2);
        System.out.println(objects[0]);
    }
}