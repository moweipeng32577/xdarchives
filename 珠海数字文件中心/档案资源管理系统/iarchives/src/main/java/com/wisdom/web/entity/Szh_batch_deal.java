package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2018/12/4.
 */
@Entity
public class Szh_batch_deal {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;              //批次处理id
    private String batchid;       //批次id
    private String checkgroupid;        //质检组id
    private String state;     //批次处理状态

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

    public String getCheckgroupid() {
        return checkgroupid;
    }

    public void setCheckgroupid(String checkgroupid) {
        this.checkgroupid = checkgroupid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
