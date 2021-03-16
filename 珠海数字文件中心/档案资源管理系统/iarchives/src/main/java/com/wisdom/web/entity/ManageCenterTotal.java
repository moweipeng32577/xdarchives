package com.wisdom.web.entity;

/**
 * Created by Administrator on 2020/7/21.
 */
public class ManageCenterTotal {


    private String year;  //年度
    private long elefile;  //电子文件
    private long elearchive;  //电子档案
    private long transfernum;  //移交数量
    private String unit;  //单位名称
    private String organid;  //机构id
    private long unfillingnum; //unfillingnum
    private long fillingnum; //归档未移交数据
    private long receiveday;  //当天接收合计
    private long receivemonth;  //当月接收合计
    private String lastreceivetime;  //最后接收时间
    private long fillingday;  //当日归档数据
    private long fillingmonth;  //当月归档数据
    private String lastfillingtime;  //最后归档时间

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getElefile() {
        return elefile;
    }

    public void setElefile(long elefile) {
        this.elefile = elefile;
    }

    public long getElearchive() {
        return elearchive;
    }

    public void setElearchive(long elearchive) {
        this.elearchive = elearchive;
    }

    public long getTransfernum() {
        return transfernum;
    }

    public void setTransfernum(long transfernum) {
        this.transfernum = transfernum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public long getUnfillingnum() {
        return unfillingnum;
    }

    public void setUnfillingnum(long unfillingnum) {
        this.unfillingnum = unfillingnum;
    }

    public long getFillingnum() {
        return fillingnum;
    }

    public void setFillingnum(long fillingnum) {
        this.fillingnum = fillingnum;
    }

    public long getReceiveday() {
        return receiveday;
    }

    public void setReceiveday(long receiveday) {
        this.receiveday = receiveday;
    }

    public long getReceivemonth() {
        return receivemonth;
    }

    public void setReceivemonth(long receivemonth) {
        this.receivemonth = receivemonth;
    }

    public String getLastreceivetime() {
        return lastreceivetime;
    }

    public void setLastreceivetime(String lastreceivetime) {
        this.lastreceivetime = lastreceivetime;
    }

    public long getFillingday() {
        return fillingday;
    }

    public void setFillingday(long fillingday) {
        this.fillingday = fillingday;
    }

    public long getFillingmonth() {
        return fillingmonth;
    }

    public void setFillingmonth(long fillingmonth) {
        this.fillingmonth = fillingmonth;
    }

    public String getLastfillingtime() {
        return lastfillingtime;
    }

    public void setLastfillingtime(String lastfillingtime) {
        this.lastfillingtime = lastfillingtime;
    }
}
