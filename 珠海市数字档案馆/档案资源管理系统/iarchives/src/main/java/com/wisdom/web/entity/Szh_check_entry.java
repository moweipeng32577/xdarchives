package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2018/12/4.
 */
@Entity
public class Szh_check_entry {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;              //批次抽检用户处理id
    private String batchid;       //批次id
    private String checkuserid;        //质检人员id
    private String batchentryid;        //批次条目id
    private String batchdealid;        //批次处理id
    private String state;     //批次抽检状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getCheckuserid() {
        return checkuserid;
    }

    public void setCheckuserid(String checkuserid) {
        this.checkuserid = checkuserid;
    }

    public String getBatchentryid() {
        return batchentryid;
    }

    public void setBatchentryid(String batchentryid) {
        this.batchentryid = batchentryid;
    }

    public String getBatchdealid() {
        return batchdealid;
    }

    public void setBatchdealid(String batchdealid) {
        this.batchdealid = batchdealid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
