package com.xdtech.project.lot.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.repository.DeviceWorkRepository;
import com.xdtech.project.lot.device.service.DeviceService;
import com.xdtech.project.lot.mjj.DCPService;
import com.xdtech.project.lot.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import java.util.*;

/**
 * 连接设备上下文监听器
 * @author wujy  2019/09/29
 */
//@WebListener
public class WebContextListener extends HttpServlet implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(WebContextListener.class);

    private DeviceService deviceService;
    private DCPService dcpService;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        SpringContextUtil springContextUtil =new SpringContextUtil();
        springContextUtil.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
        deviceService = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DeviceService.class);
        dcpService = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DCPService.class);
        List<Device> devices = deviceService.findDevice("MJJ");
        List<Map<String,Object>> deviceList = new ArrayList<>();
        for (Device device : devices) {
            Map<String,Object> deviceProp = (Map<String, Object>) JSON.parse(device.getProp());
            Map<String,Object> deviceMap = (Map<String, Object>) JSON.toJSON(device);
            deviceMap.remove("prop");
            deviceMap.putAll(deviceProp);
            deviceList.add(deviceMap);
        }
        //循环一次启动每个密集架设备服务的独立线程。
        for(Map<String, Object> device:deviceList){
            dcpService.createOrUpateServiceDeviceInfo((String) device.get("id"),device);
        }

    }
    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
