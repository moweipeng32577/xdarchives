package com.wisdom.web.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 数字化质检模块(批次条目原文表)
 */
@Entity
public class Szh_batch_media {
    @Id
    private String id;        //主键
    private String medianame; //文件名
    private String batchcode; //批次号
    private String entryid;   //条目id
    private String eleid;     //原文id
    private String mediapath; //文件路径
    private String mediatype; //原文类型
    private String status;    //状态

    public Szh_batch_media(){}

    public Szh_batch_media(String id,String medianame,String batchcode,String entryid,String eleid,String mediapath,String mediatype,String status){
        this.id = id;
        this.medianame = medianame;
        this.batchcode = batchcode;
        this.entryid = entryid;
        this.eleid = eleid;
        this.mediapath = mediapath;
        this.mediatype = mediatype;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedianame() {
        return medianame;
    }

    public void setMedianame(String medianame) {
        this.medianame = medianame;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getMediapath() {
        return mediapath;
    }

    public void setMediapath(String mediapath) {
        this.mediapath = mediapath;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
