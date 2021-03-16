package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by SunK on 2020/5/9 0009.
 */
@Entity
public class Tb_service_metadata {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String sid;
    @Column(columnDefinition = "varchar(255)")
    private String operation;//业务行为
    @Column(columnDefinition = "varchar(100)")
    private String mstatus;//业务状态
    @Column(columnDefinition = "varchar(50)")
    private String servicetime;//业务时间
    @Column(columnDefinition = "varchar(255)")
    private String operationmsg;//行为描述
    @Column(columnDefinition = "char(36)")
    private String aid;//授权元数据id
    @Column(columnDefinition = "char(36)")
    private String userid;//用户id
    @Column(columnDefinition = "char(36)")
    private String entryids;//条目id

    @Transient
    private String loginname;
    @Transient
    private String realname;
    @Transient
    private String duty;//职务
    @Transient
    private String organusertype;//机构人员类型


    public Tb_service_metadata(){};
    public Tb_service_metadata(String operation, String mtype,
                               String servicetiome, String operationmsg,
                               String maid, String userid, String entryid) {
        this.operation = operation;
        this.mstatus = mtype;
        this.servicetime = servicetiome;
        this.operationmsg = operationmsg;
        this.aid = maid;
        this.userid = userid;
        this.entryids = entryid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMstatus() {
        return mstatus;
    }

    public void setMstatus(String mstatus) {
        this.mstatus = mstatus;
    }

    public String getServicetime() {
        return servicetime;
    }

    public void setServicetime(String servicetime) {
        this.servicetime = servicetime;
    }

    public String getOperationmsg() {
        return operationmsg;
    }

    public void setOperationmsg(String operationmsg) {
        this.operationmsg = operationmsg;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getEntryids() {
        return entryids;
    }

    public void setEntryids(String entryids) {
        this.entryids = entryids;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getOrganusertype() {
        return organusertype;
    }

    public void setOrganusertype(String organusertype) {
        this.organusertype = organusertype;
    }
}
