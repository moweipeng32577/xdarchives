package com.wisdom.util;

/**
 * Created by zengdw on 2019/08/09 
 */
public class DelThread extends Thread {

    private static String[] entries;
    private static String  type;
    public DelThread(String[] entries, String type) {
        this.entries = entries;
        this.type =  type;
    }

    public void run() {
        DelThreadAop.delElectronic(this.type, this.entries);
    }
}
