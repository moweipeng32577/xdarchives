package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 接收管理-新增批次表
 * Created by Administrator on 2019/6/18.
 */
@Entity
public class Tb_acceptdoc_batch {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String batchid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String acceptdocid;//单据id
    @Column(columnDefinition = "varchar(20)")
    private String batchcode;//批次号
    @Column(columnDefinition = "varchar(20)")
    private String disinfector;//消毒员
    @Column(columnDefinition = "varchar(20)")
    private String disinfectiontime;//消毒时间
    @Column(columnDefinition = "varchar(255)")
    private String batchremark;//备注
    @Column(columnDefinition = "varchar(20)")
    private String state;//消毒状态
    @Column(columnDefinition = "varchar(100)")
    private String archivescope;

    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getAcceptdocid() {
        return acceptdocid;
    }

    public void setAcceptdocid(String acceptdocid) {
        this.acceptdocid = acceptdocid;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getDisinfector() {
        return disinfector;
    }

    public void setDisinfector(String disinfector) {
        this.disinfector = disinfector;
    }

    public String getDisinfectiontime() {
        return disinfectiontime;
    }

    public void setDisinfectiontime(String disinfectiontime) {
        this.disinfectiontime = disinfectiontime;
    }

    public String getBatchremark() {
        return batchremark;
    }

    public void setBatchremark(String batchremark) {
        this.batchremark = batchremark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getArchivescope() {
        return archivescope;
    }

    public void setArchivescope(String archivescope) {
        this.archivescope = archivescope;
    }
}
