package com.wisdom.web.entity;

/**
 * Created by Administrator on 2017/11/18 0018.
 */
public class Tb_entry_index_borrow extends Tb_entry_index{

    private String id;
    private String lyqx;
    private String borrowcode;
    private String borrowdate;//查档时间
    private String borrowman;
    private String borrrowid;
    private String borrowmantel;
    private String jybackdate;//审批通过时间
    private String backdate;//到期时间
    private int jyts;
    private String renewreason;//续借理由
    private String approver;//审批人
    private String returntime;//归还时间
    private String returnloginname;//归还人账号
    private String returnware;    //归还人
    private String description;   //备注
    private String type;//查档类型
    private String state; //归还状态
    private String msgid;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReturnloginname() {
        return returnloginname;
    }

    public void setReturnloginname(String returnloginname) {
        this.returnloginname = returnloginname;
    }

    public int getJyts() {
        return jyts;
    }

    public void setJyts(int jyts) {
        this.jyts = jyts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBorrrowid() {
        return borrrowid;
    }

    public void setBorrrowid(String borrrowid) {
        this.borrrowid = borrrowid;
    }

    public String getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(String borrowdate) {
        this.borrowdate = borrowdate;
    }

    public String getBorrowman() {
        return borrowman;
    }

    public void setBorrowman(String borrowman) {
        this.borrowman = borrowman;
    }

    public String getLyqx() {
        return lyqx;
    }

    public void setLyqx(String lyqx) {
        this.lyqx = lyqx;
    }

    public String getJybackdate() {
        return jybackdate;
    }

    public void setJybackdate(String jybackdate) {
        this.jybackdate = jybackdate;
    }

    public String getBackdate() {
        return backdate;
    }

    public void setBackdate(String backdate) {
        this.backdate = backdate;
    }

    public String getBorrowcode() {
        return borrowcode;
    }

    public void setBorrowcode(String borrowcode) {
        this.borrowcode = borrowcode;
    }

    public String getRenewreason() {
        return renewreason;
    }

    public void setRenewreason(String renewreason) {
        this.renewreason = renewreason;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getBorrowmantel() {
        return borrowmantel;
    }

    public void setBorrowmantel(String borrowmantel) {
        this.borrowmantel = borrowmantel;
    }

    public String getReturntime() {
        return returntime;
    }

    public void setReturntime(String returntime) {
        this.returntime = returntime;
    }

    public String getReturnware() {
        return returnware;
    }

    public void setReturnware(String returnware) {
        this.returnware = returnware;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
