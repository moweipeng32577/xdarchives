package com.xdtech.project.lot.util;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;


public class QuartzUtil {
    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "JOB_GROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "TRIGGER_GROUP_NAME";

    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param jobName 任务名
     * @param cls 任务
     * @param time 时间设置
     */
    @SuppressWarnings("rawtypes")
    public static void addJob(String mode,String deviceid,String jobName, Class cls, String time) {
        try {
            // 通过SchedulerFactory工厂构建Scheduler容器对象
            Scheduler sched = gSchedulerFactory.getScheduler();

            // 构建一个jobDetail 作业实例
            JobDetail jobDetail = JobBuilder.newJob(cls) // 构建一个新任务
                    .withIdentity(jobName, JOB_GROUP_NAME) // 给新任务起名和分组
                    .build(); // 绑定作业

            //可以传递参数
            jobDetail.getJobDataMap().put("deviceid", deviceid);
            jobDetail.getJobDataMap().put("mode", mode);

            // 触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)// 给触发器起名和分组
                    .startNow()// 立即执行
                    .withSchedule(
                            // 定义触发规则
                            CronScheduleBuilder.cronSchedule(time))
                    .build();// 绑定出发规则

            // 将job任务和trigger触发器添加到Scheduler容器中
            sched.scheduleJob(jobDetail, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @param time
     */
    @SuppressWarnings("rawtypes")
    public static void modifyJobTime(String mode,String deviceid,String jobName, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            // 获取指定任务的触发规则
            TriggerKey key = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
            CronTrigger cronTrigger = (CronTrigger) sched.getTrigger(key);
            if (cronTrigger == null) {
                return;
            }
            if (cronTrigger != null) {
                // 一、先在任务组中删除任务，再重新添加一个新的任务

               JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
               JobDetail jobDetail = sched.getJobDetail(jobKey);
               Class<? extends Job> jobClass = jobDetail.getJobClass(); // 移除指定任务
               removeJob(jobName); // 新增任务
               addJob(mode,deviceid,jobName, jobClass, time);


                // 二、重新构建一个触发器
                // 构建一个 CronSchedule 的触发器
                Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder
                        .withIdentity(jobName, TRIGGER_GROUP_NAME)// 给触发器起名和分组
                        .startNow()// 立即执行
                        .withSchedule(
                                // 定义触发规则
                                CronScheduleBuilder.cronSchedule(time))
                        .build();// 绑定出发规则
                // 调用 rescheduleJob 实现任务触发规则的重置
                sched.rescheduleJob(key, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     */
    public static void removeJob(String jobName) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);

            sched.pauseTrigger(triggerKey);// 暂停触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
            sched.deleteJob(jobKey);// 删除指定任务

            // 模拟数据处理
            Thread.sleep(3000);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
