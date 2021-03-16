package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 数字化质检模块(批次条目表)
 */
@Entity
public class Szh_batch_entry {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;              //主键
    private String batchcode;       //批次号
    private String filecode;        //案卷号
    private String archivecode;     //档号
    private Integer pagenum;        //页数
    private String captureentryid;  //采集条目id
    private String nodeid;          //节点id
    private String ischeck;         //是否抽检
    private String status;          //质检状态
    private String type;            //类型
    private String checker;         //抽检人

    public Szh_batch_entry(){}

    public Szh_batch_entry(String batchcode,String filecode,String archivecode,Integer pagenum,String captureentryid,String nodeid,String ischeck,String status,String type){
        this.batchcode = batchcode;
        this.filecode = filecode;
        this.archivecode = archivecode;
        this.pagenum = pagenum;
        this.captureentryid = captureentryid;
        this.nodeid = nodeid;
        this.ischeck = ischeck;
        this.status = status;
        this.type = type;
    }

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

    public String getFilecode() {
        return filecode;
    }

    public void setFilecode(String filecode) {
        this.filecode = filecode;
    }

    public String getArchivecode() {
        return archivecode;
    }

    public void setArchivecode(String archivecode) {
        this.archivecode = archivecode;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    public String getCaptureentryid() {
        return captureentryid;
    }

    public void setCaptureentryid(String captureentryid) {
        this.captureentryid = captureentryid;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }
}
