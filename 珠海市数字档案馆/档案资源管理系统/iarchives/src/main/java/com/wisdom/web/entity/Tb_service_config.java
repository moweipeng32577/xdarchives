package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by SunK on 2020/5/12 0012.
 */
//业务元数据维护实体类
@Entity
public class Tb_service_config {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String cid;
    @Column(columnDefinition = "char(36)")
    private String parentid;
    @Column(columnDefinition = "varchar(255)")
    private String operation;//业务行为
    @Column(columnDefinition = "varchar(100)")
    private String mstatus;//业务状态
    @Column(columnDefinition = "varchar(255)")
    private String operationmsg;//行为描述
    @Column(columnDefinition = "varchar(255)")
    private String shortname;//授权标识 简称
    @Column(columnDefinition = "char(36)")
    private String aid;
    @Column(columnDefinition = "int")
    private Integer sortsequence;


    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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

    public String getOperationmsg() {
        return operationmsg;
    }

    public void setOperationmsg(String operationmsg) {
        this.operationmsg = operationmsg;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public Integer getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(Integer sortsequence) {
        this.sortsequence = sortsequence;
    }
}
