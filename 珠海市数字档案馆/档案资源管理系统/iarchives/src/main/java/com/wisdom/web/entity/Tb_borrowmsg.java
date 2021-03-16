package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_borrowmsg {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String msgid;
    @Column(columnDefinition = "varchar(100)")
    private String borrowcode;
    @Column(columnDefinition = "varchar(100)")
    private String borrowdate;//查档时间
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;
    @Column(columnDefinition = "varchar(100)")
    private String borrowman;
    @Column(columnDefinition = "varchar(100)")
    private String borrowmantel;
    @Column(columnDefinition = "varchar(100)")
    private String approver;//审批人
    @Column(columnDefinition = "integer")
    private int jyts;//查档天数
    @Column(columnDefinition = "varchar(100)")
    private String state;
    @Column(columnDefinition = "varchar(100)")
    private String jybackdate;//审批通过时间
    @Column(columnDefinition = "varchar(50)")
    private String lyqx;
    @Column(columnDefinition = "varchar(100)")
    private String backdate;//到期时间
    @Column(columnDefinition = "varchar(1000)")
    private String renewreason;//续借理由
    @Column(columnDefinition = "varchar(1000)")
    private String returntime;//归还时间
    @Column(columnDefinition = "varchar(50)")
    private String returnloginname;//归还人账号
    @Column(columnDefinition = "varchar(50)")
    private String returnware;    //归还人
    @Column(columnDefinition = "varchar(500)")
    private String description;   //备注
    @Column(columnDefinition = "varchar(10)")
    private String type;//查档类型

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

    public String getBorrowman() {
        return borrowman;
    }

    public void setBorrowman(String borrowman) {
        this.borrowman = borrowman;
    }

    public String getId() {
        return msgid;
    }

    public void setId(String id) {
        this.msgid = id;
    }

    public String getBorrowcode() {
        return borrowcode;
    }

    public void setBorrowcode(String borrowcode) {
        this.borrowcode = borrowcode;
    }

    public String getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(String borrowdate) {
        this.borrowdate = borrowdate;
    }

    public String getEntryid() {
        return entryid==null?null:entryid.trim();
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public int getJyts() {
        return jyts;
    }

    public void setJyts(int jyts) {
        this.jyts = jyts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJybackdate() {
        return jybackdate;
    }

    public void setJybackdate(String jybackdate) {
        this.jybackdate = jybackdate;
    }

    public String getLyqx() {
        return lyqx;
    }

    public void setLyqx(String lyqx) {
        this.lyqx = lyqx;
    }

    public String getBackdate() {
        return backdate;
    }

    public void setBackdate(String backdate) {
        this.backdate = backdate;
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
