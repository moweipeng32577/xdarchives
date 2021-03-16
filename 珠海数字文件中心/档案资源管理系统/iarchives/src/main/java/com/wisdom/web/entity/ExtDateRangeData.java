package com.wisdom.web.entity;

/**
 * Created by RonJiang on 2017/12/21 0021.
 */
public class ExtDateRangeData {
    private String filedatestartday;
    private String filedateendday;

    public String getFiledatestartday() {
        return filedatestartday;
    }

    public void setFiledatestartday(String filedatestartday) {
        this.filedatestartday = filedatestartday;
    }

    public String getFiledateendday() {
        return filedateendday;
    }

    public void setFiledateendday(String filedateendday) {
        this.filedateendday = filedateendday;
    }

    @Override
    public String toString() {
        return "ExtDateRangeData{" +
                "filedatestartday='" + filedatestartday + '\'' +
                ", filedateendday='" + filedateendday + '\'' +
                '}';
    }
}
