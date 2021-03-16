package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Tb_oa_record {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    public String id;
    @Column(columnDefinition = "varchar(255)")
    public String filename;//文件名
    @Column(columnDefinition = "varchar(255)")
    public String filesize;//文件大小
    @Column(columnDefinition = "varchar(255)")
    public String filepath;//文件路径
    @Column(columnDefinition = "varchar(255)")
    public String receivestate;//接收状态
    @Column(columnDefinition = "varchar(255)")
    public String filestate;//文件状态
    @Column(columnDefinition = "varchar(255)")
    public String date;//接收日期
    @Column(columnDefinition = "varchar(255)")
    public String entryid;//导入后生成的条目id
    @Column(columnDefinition = "varchar(255)")
    public String title;//导入后生成的条目id
    @Column(columnDefinition = "varchar(255)")
    public String code;//单位编码

    @Transient
    private String checkstatus;//检测状态
    @Transient
    private String authenticity;//准确性
    @Transient
    private String integrity;//完整性
    @Transient
    private String usability;//可用性
    @Transient
    private String safety;//安全性

    public Tb_oa_record(){}

    public Tb_oa_record(String filename, String filesize, String filepath, String receivestate, String filestate, String date, String entryid) {
        this.filename = filename;
        this.filesize = filesize;
        this.filepath = filepath;
        this.receivestate = receivestate;
        this.filestate = filestate;
        this.date = date;
        this.entryid = entryid;
    }

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

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getReceivestate() {
        return receivestate;
    }

    public void setReceivestate(String receivestate) {
        this.receivestate = receivestate;
    }

    public String getFilestate() {
        return filestate;
    }

    public void setFilestate(String filestate) {
        this.filestate = filestate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
