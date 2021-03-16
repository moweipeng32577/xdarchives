package com.wisdom.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.wisdom.web.security.MyFilterSecurityInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
//@EnableAutoConfiguration(exclude = MyFilterSecurityInterceptor.class)
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if("1".equals(systemLoginType)){//政务网
            registry.addViewController("/").setViewName("/governmentLogin");
            registry.addViewController("/login").setViewName("/governmentLogin");
        }else{
            registry.addViewController("/").setViewName("/login");
            registry.addViewController("/login").setViewName("/login");
        }
        registry.addViewController("/selfquery").setViewName("/selfquery");
        registry.addViewController("/SelfServiceQuery").setViewName("/SelfServiceQuery");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter);
    }

}
