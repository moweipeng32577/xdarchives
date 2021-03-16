package com.wisdom.service.timejob.configuration;

import com.wisdom.service.timejob.service.TimeJobTask;
import com.wisdom.web.entity.Tb_time_job;
import com.wisdom.web.service.TimeJobService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;

/**
 * Created by yl on 2020/1/3.
 */
@Configuration
public class TimeJobListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    TimeJobTask timeJobTask;

    @Autowired
    TimeJobService timeJobService;

    /**
     * 初始启动quartz，查询定时任务数据库表，启动状态为1任务(0：未开启 1：开启)
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<Tb_time_job> timeJobs = timeJobService.findALLTimeJob();
        System.out.println("查找tb_time_job定时任务数量为：--------" + timeJobs.size());
        timeJobs.stream().forEach(timeJob -> {
            if ("1".equals(timeJob.getJobstate())) {
                System.out.println("添加定时任务--------" + timeJob.getJobname());
                try {
                    timeJobTask.startJob(timeJob);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始注入scheduler
     *
     * @return
     * @throws SchedulerException
     */
    @Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
        return schedulerFactoryBean.getScheduler();
    }

    @Bean
    public TimeJobFactory jobFactory() {
        return new TimeJobFactory();
    }

    @Bean(name ="schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(TimeJobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }
}
