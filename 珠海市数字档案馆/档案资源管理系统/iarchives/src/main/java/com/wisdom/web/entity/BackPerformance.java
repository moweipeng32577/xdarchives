package com.wisdom.web.entity;

/**
 * Created by Administrator on 2020/4/13.
 */
public class BackPerformance {

    private String title;  //统计名称
    private int submitcount;  //提交数
    private int successcount;  //成功数
    private int failcount;  //失败数

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSubmitcount() {
        return submitcount;
    }

    public void setSubmitcount(int submitcount) {
        this.submitcount = submitcount;
    }

    public int getSuccesscount() {
        return successcount;
    }

    public void setSuccesscount(int successcount) {
        this.successcount = successcount;
    }

    public int getFailcount() {
        return failcount;
    }

    public void setFailcount(int failcount) {
        this.failcount = failcount;
    }
}
