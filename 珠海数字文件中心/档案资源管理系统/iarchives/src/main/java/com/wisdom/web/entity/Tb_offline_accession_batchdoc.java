package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 离线接收（批次表）
 */
@Entity
public class Tb_offline_accession_batchdoc {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;         //主键
    private String filename;  //文件名称
    private String authenticity;    //真实性
    private String integrity;  //完整性
    private String usability;    //可用性
    private String safety;  //安全性
    private String checkstatus;    //检查结果
    private String isaccess; //是否接入
    private String batchid;    //批次ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAuthenticity() {
        return authenticity;
    }

    public void setAuthenticity(String authenticity) {
        this.authenticity = authenticity;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public String getUsability() {
        return usability;
    }

    public void setUsability(String usability) {
        this.usability = usability;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getIsaccess() {
        return isaccess;
    }

    public void setIsaccess(String isaccess) {
        this.isaccess = isaccess;
    }


    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }


    public Tb_offline_accession_batchdoc(String filename, String authenticity, String integrity, String usability, String safety, String checkstatus, String batchid) {
        this.filename = filename;
        this.authenticity = authenticity;
        this.integrity = integrity;
        this.usability = usability;
        this.safety = safety;
        this.checkstatus = checkstatus;
        this.batchid = batchid;
    }

    public Tb_offline_accession_batchdoc(){

    }
}
