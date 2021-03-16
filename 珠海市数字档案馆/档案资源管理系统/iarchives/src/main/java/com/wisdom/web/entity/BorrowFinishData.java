package com.wisdom.web.entity;

/**
 * Created by Administrator on 2018/11/28.
 */
public class BorrowFinishData {
    private String docid;
    private String type;//查档类型
    private String desci;//查档描述
    private String borrowdate;//查档时间
    private int borrowts;//申请查档天数
    private int borrowtyts;//查档同意天数
    private String state;//审批状态
    private String finishtime;//办理完结时间

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getDesci() {
        return desci;
    }

    public void setDesci(String desci) {
        this.desci = desci;
    }

    public String getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(String borrowdate) {
        this.borrowdate = borrowdate;
    }

    public int getBorrowts() {
        return borrowts;
    }

    public void setBorrowts(int borrowts) {
        this.borrowts = borrowts;
    }

    public int getBorrowtyts() {
        return borrowtyts;
    }

    public void setBorrowtyts(int borrowtyts) {
        this.borrowtyts = borrowtyts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

}
