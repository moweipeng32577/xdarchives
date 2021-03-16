package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/10/15.
 */
@Entity
public class Tb_yearlycheck_approvedoc {


    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(100)")
    private String approvecode;//审批号
    @Column(columnDefinition = "varchar(1000)")
    private String approve;//批示
    @Column(columnDefinition = "varchar(50)")
    private String submiter;//提交人
    @Column(columnDefinition = "varchar(30)")
    private String submittime;//提交时间
    @Column(columnDefinition = "varchar(30)")
    private String state;//单据状态
    @Column(columnDefinition = "varchar(50)")
    private String approvetext;//审批环节
    @Column(columnDefinition = "varchar(50)")
    private String approveman;//审批人
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String submiterid;  //提交人id
    @Column(columnDefinition = "varchar(500)")
    private String remark;//备注

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApprovecode() {
        return approvecode;
    }

    public void setApprovecode(String approvecode) {
        this.approvecode = approvecode;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public String getSubmiter() {
        return submiter;
    }

    public void setSubmiter(String submiter) {
        this.submiter = submiter;
    }

    public String getSubmittime() {
        return submittime;
    }

    public void setSubmittime(String submittime) {
        this.submittime = submittime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApprovetext() {
        return approvetext;
    }

    public void setApprovetext(String approvetext) {
        this.approvetext = approvetext;
    }

    public String getApproveman() {
        return approveman;
    }

    public void setApproveman(String approveman) {
        this.approveman = approveman;
    }

    public String getSubmiterid() {
        return submiterid;
    }

    public void setSubmiterid(String submiterid) {
        this.submiterid = submiterid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
