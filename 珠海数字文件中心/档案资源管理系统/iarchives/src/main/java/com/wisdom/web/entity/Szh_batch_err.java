package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 数字化质检模块(批次原文错误信息表)
 */
@Entity
public class Szh_batch_err {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;         //主键
    private String batchcode;  //批次号
    private String mediaid;    //批次原文id
    private String errtype;    //错误类型
    private String depict;     //描述
    private String filename;   //文件名
    private String captureentryid;//采集条目id
    private String status;     //状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getErrtype() {
        return errtype;
    }

    public void setErrtype(String errtype) {
        this.errtype = errtype;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCaptureentryid() {
        return captureentryid;
    }

    public void setCaptureentryid(String captureentryid) {
        this.captureentryid = captureentryid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
