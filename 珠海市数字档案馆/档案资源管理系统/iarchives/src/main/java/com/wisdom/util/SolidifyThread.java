package com.wisdom.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tanly on 2018/4/18 0018.
 */
public class SolidifyThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(SolidifyThread.class);
    private static String[] entries;
    private static String type;
    private static String nameChange;//是否将电子原文按档号重命名, true需要， "" 不需要

    public SolidifyThread(String[] entries, String type, String nameChange) {
        this.entries = entries;
        this.nameChange = nameChange;
        this.type = type;
    }

    public void run() {
        if("true".equals(nameChange)){//需要重命名
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 原文重命名 开始");
            NameChange.changeFileName(this.entries,this.type);
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 原文重命名 结束");
        }
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " SolidifyThread 开始");
        Solidify.checkOffice();//确定使用哪种office进行固化
        if ("management".equals(this.type)) {
            Solidify.convertToPdfOfManagement(this.entries);
        } else {
            Solidify.convertToPdfOfCapture(this.entries);
        }
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " SolidifyThread 结束");
    }
}
