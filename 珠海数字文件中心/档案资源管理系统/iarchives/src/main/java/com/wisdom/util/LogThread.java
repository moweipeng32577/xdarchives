package com.wisdom.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zengdw on 2019/08/09 
 */
public class LogThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(LogThread.class);
    private static String[] entries;
    private static String  nodeid;
    private static String  type;
    private static String  loginname;
    private static String  realname;
    private static String  ipAddress;

    public LogThread(String[] entries,String nodeid,String type,String loginname,String realname,String ipStr) {
        this.entries = entries;
        this.nodeid =  nodeid;
        this.type =  type;
        this.loginname =  loginname;
        this.realname =  realname;
        this.ipAddress =  ipStr;
    }

    public void run() {
        LogAop.staticGenerateManualLog(this.type, this.nodeid,this.entries,this.loginname,this.realname,this.ipAddress);
    }
}
