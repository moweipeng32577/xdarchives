package com.wisdom.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangmh on 2019/4/2.
 */
public class RuntimeUntil extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(RuntimeUntil.class);

    private static String dir;
    private static String type;

    public RuntimeUntil(String name, String dir,String type) {
        super(name);
        this.dir = dir;
        this.type = type;
    }

    public void run() {
        synchronized (type) {
            try {
                logger.info(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 图片压缩线程：" + this.getName() + "开始");
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(dir);
                if (process.waitFor() == 0) {
                    process.destroy();
                }
                logger.info(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " 图片压缩线程：" + this.getName() + "结束");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
