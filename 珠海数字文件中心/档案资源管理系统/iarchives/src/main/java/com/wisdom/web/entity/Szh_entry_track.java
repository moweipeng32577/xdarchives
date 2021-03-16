package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2019/1/25.
 * 实体流向追踪
 */
@Entity
public class Szh_entry_track {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;
    private String entryid;//批次条目id
    private String batchcode;//批次号
    private String archivecode;//案卷号/档号
    private String entrysigner;//实体签收人
    private String nodename;//节点名
    private String entrysigntime;//实体签收时间
    private String status;//实体状态（签收、退还）
    private String depict;//描述

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getArchivecode() {
        return archivecode;
    }

    public void setArchivecode(String archivecode) {
        this.archivecode = archivecode;
    }

    public String getEntrysigner() {
        return entrysigner;
    }

    public void setEntrysigner(String entrysigner) {
        this.entrysigner = entrysigner;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getEntrysigntime() {
        return entrysigntime;
    }

    public void setEntrysigntime(String entrysigntime) {
        this.entrysigntime = entrysigntime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }
}
