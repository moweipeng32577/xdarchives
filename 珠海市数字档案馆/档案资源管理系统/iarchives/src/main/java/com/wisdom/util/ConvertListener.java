package com.wisdom.util;

import com.wisdom.web.controller.FullSearchController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by tanly on 2017/11/22 0022.
 */
public class ConvertListener extends TimerTask {
    private static String isRunning = ConfigValue.getValue("const.listener.isRunning");
    private static String hours;

    public ConvertListener(String hours) {
        this.hours = hours;
    }

    public void run() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (("true").equals(isRunning)) {//convert服务是否启动
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " 进入 ConvertListener......");
            try {
                //判断当前的时间点是否处于固化时间段
                if (this.hours.contains(hour + "")) {
                    System.out.println("执行转换ing。。。");
                    FullSearchController.convert2pdf();
                }
            } catch (Exception e) {
                System.out.println("错误："+e.getMessage() + "\t转换服务已停止");
                isRunning = "false";
            }
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " 结束 ConvertListener......");
        }

    }
}
