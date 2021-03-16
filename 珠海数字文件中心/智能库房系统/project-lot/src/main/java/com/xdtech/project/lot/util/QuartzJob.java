package com.xdtech.project.lot.util;

import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务Job
 */

public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext content) throws JobExecutionException {
        DeviceRepository deviceRepository =  SpringContextUtil.getBean(DeviceRepository.class);
//        String jobName = content.getJobDetail().getName();
        JobDataMap dataMap = content.getJobDetail().getJobDataMap();
        String deviceid = dataMap.getString("deviceid");
        String mode = dataMap.getString("mode");
        Device device = deviceRepository.findById(deviceid);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "★★★★★★★★★★★");
//        System.out.println("任务名字:" + jobName);
        System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆开始执行定时任务了☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");

        switch (device.getType().getTypeCode().toLowerCase()){
            //安防设备
            case "af":
                if(mode.equals("open")){
                    device.setStatus(2);
                    deviceRepository.save(device);

                }
                else{
                    device.setStatus(3);
                    deviceRepository.save(device);
                }
                break;
            default:
                 break;
        }

    }
}

