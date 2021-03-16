package com.wisdom.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tanly on 2018/4/18 0018.
 */
public class SolidifyByEleidThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(SolidifyByEleidThread.class);

    private static List electronicList;
    private static List<String> failList;
    private static String type;

    public SolidifyByEleidThread(List electronicList, List<String> eleList, String type) {
        this.electronicList = electronicList;
        this.failList = eleList;
        this.type = type;
    }

    public void run() {
        logger.info(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 固化线程：SolidifyByEleidThread 开始");
        Solidify.checkOffice();//确定使用哪种office进行固化
        if ("management".equals(this.type)) {
            Solidify.convertToPdfOfManagement(this.electronicList, this.failList);
        } else {
            Solidify.convertToPdfOfCapture(this.electronicList, this.failList);
        }
        logger.info(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 固化线程：SolidifyByEleidThread 结束");
    }
}