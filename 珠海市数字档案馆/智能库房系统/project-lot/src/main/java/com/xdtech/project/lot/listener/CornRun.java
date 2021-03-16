package com.xdtech.project.lot.listener;

import com.xdtech.project.lot.device.entity.DeviceWork;
import com.xdtech.project.lot.device.repository.DeviceWorkRepository;
import com.xdtech.project.lot.util.CornUtil;
import com.xdtech.project.lot.util.QuartzJob;
import com.xdtech.project.lot.util.QuartzUtil;
import com.xdtech.project.lot.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.text.ParseException;
import java.util.List;

/**
 * Created by wangmh on 2020/3/10.
 * 项目启动时----加载定时任务
 */
@WebListener
public class CornRun implements ServletContextListener  {

    private static final Logger logger = LoggerFactory.getLogger(CornRun.class);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        SpringContextUtil springContextUtil =new SpringContextUtil();
        springContextUtil.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext()));
       DeviceWorkRepository deviceWorkRepository = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext()).getBean(DeviceWorkRepository.class);
       List<DeviceWork> workList = deviceWorkRepository.findAll();
       logger.info("<<<<<<<<<<<<<<<<<<<<<加载定时任务>>>>>>>>>>>>>>>>>>>>>>>>");
       for(DeviceWork deviceWork : workList){
           logger.info("[JobName]："+ deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode()+"---------------添加定时任务!---------------"+ deviceWork.getWorkTime());
           try {
               String  cronTime = CornUtil.getCron(deviceWork.getPeriod(),deviceWork.getWorkTime());
               QuartzUtil.addJob(deviceWork.getMode(),deviceWork.getDevice().getId(),deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode(), QuartzJob.class, cronTime);
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}