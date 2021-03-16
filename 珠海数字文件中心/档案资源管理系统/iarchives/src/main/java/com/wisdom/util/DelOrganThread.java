package com.wisdom.util;

/**
 * Created by zengdw on 2019/08/09 
 */
public class DelOrganThread extends Thread {

    private static String[] organidArr;
    private static String[] nodeIds;
    private static String[] nodeidSxArr;

    public DelOrganThread(String[] organidArr, String[] nodeIds, String[] nodeidSxArr) {
        this.organidArr = organidArr;
        this.nodeIds = nodeIds;
        this.nodeidSxArr = nodeidSxArr;
    }

    public void run() {
        DelThreadAop.delOrganRef(this.organidArr, this.nodeIds, this.nodeidSxArr);
    }
}
