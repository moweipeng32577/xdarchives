package com.wisdom.service.timejob.service;

import com.wisdom.web.entity.Tb_time_job;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yl on 2020/1/3.
 * 动态定时任务service
 */
@Service
public class TimeJobTask {
    private static final Logger log = LoggerFactory.getLogger(TimeJobTask.class);

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 启动定时任务
     *
     * @param timeJob
     * @return
     */
    public void startJob(Tb_time_job timeJob) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Class clazz = Class.forName(timeJob.getJobclass());
        JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(timeJob.getJobname()).build();
        // 触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(timeJob.getId(), Scheduler.DEFAULT_GROUP);
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(timeJob.getCronexpression())).build();
        //检查是否存在该任务
        if (!scheduler.checkExists(triggerKey)) {
            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown()) {
                scheduler.start();
                log.info("---任务[" + timeJob.getJobname() + "]启动成功-------");
            } else {
                log.info("---任务[" + timeJob.getJobname() + "]已经启动-------");
            }
        } else {
            //若存在该任务，则重新恢复这个任务
            scheduler.rescheduleJob(triggerKey, trigger);
            log.info("---任务[" + timeJob.getJobname() + "]恢复成功-------");
        }
    }

    /**
     * 更新定时任务
     *
     * @param timeJob
     * @return
     */
    public void updateJob(Tb_time_job timeJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        // 触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(timeJob.getId(), Scheduler.DEFAULT_GROUP);
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(timeJob.getCronexpression())).build();
        //重置对应的job
        Date Date = scheduler.rescheduleJob(triggerKey, trigger);
        log.info("---任务[" + timeJob.getJobname() + "]更新成功-------" + Date);
    }

    /**
     * 暂停定时任务
     *
     * @param timeJob
     * @return
     * @throws SchedulerException
     */
    public void pauseJob(Tb_time_job timeJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        // 触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(timeJob.getId(), Scheduler.DEFAULT_GROUP);
        scheduler.pauseTrigger(triggerKey);
        //遍历定时任务列表，根据当前任务匹配任务名称，匹配对了就终止当前任务
        List<JobExecutionContext> currentlyExecuting = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext jobExecutionContext : currentlyExecuting) {
            if(jobExecutionContext.getJobDetail().getKey().getName().equals(timeJob.getJobname())){
                scheduler.interrupt(jobExecutionContext.getJobDetail().getKey());
            }
        }
    }

    /**
     * 恢复定时任务
     *
     * @param timeJob
     * @return
     * @throws SchedulerException
     */
    public void resumeJob(Tb_time_job timeJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        // 触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(timeJob.getId(), Scheduler.DEFAULT_GROUP);
        scheduler.resumeTrigger(triggerKey);
    }
}
