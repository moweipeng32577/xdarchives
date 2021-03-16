package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2017/12/5.
 * 销毁单据审批
 */
@Entity
public class Tb_bill_approval {

    public static final String STATE_WAIT_HANDLE = "待处理";
    public static final String STATE_FINISH_AUDIT = "已送审";
    public static final String STATE_FINISHED = "已完成";
    public static final String STATE_SEND_BACK = "退回";
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String destroybillid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String taskid;
    @Column(columnDefinition = "varchar(100)")
    private String code;
    @Column(columnDefinition = "varchar(1000)")
    private String billid;
    @Column(columnDefinition = "varchar(100)")
    private String userid;
    @Column(columnDefinition = "varchar(100)")
    private String username;
    @Column(columnDefinition = "varchar(100)")
    private String submitusername;
    @Column(columnDefinition = "varchar(100)")
    private String submitdate;
    @Column(columnDefinition = "varchar(50)")
    private String state;
    @Column(columnDefinition = "varchar(1000)")
    private String approve;
    @Column(columnDefinition = "char(36)")
    private String submitterid;// 申请人id

    public String getSubmitterid() {
        return submitterid;
    }

    public void setSubmitterid(String submitterid) {
        this.submitterid = submitterid;
    }

    public String getDestroybillid() {
        return destroybillid;
    }

    public void setDestroybillid(String destroybillid) {
        this.destroybillid = destroybillid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubmitusername() {
        return submitusername;
    }

    public void setSubmitusername(String submitusername) {
        this.submitusername = submitusername;
    }

    public String getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(String submitdate) {
        this.submitdate = submitdate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    @Override
    public String toString() {
        return "Tb_bill_approval{" +
                "destroybillid='" + destroybillid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", code='" + code + '\'' +
                ", billid='" + billid + '\'' +
                ", userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", submitusername='" + submitusername + '\'' +
                ", submitdate='" + submitdate + '\'' +
                ", state='" + state + '\'' +
                ", approve='" + approve + '\'' +
                '}';
    }
}
