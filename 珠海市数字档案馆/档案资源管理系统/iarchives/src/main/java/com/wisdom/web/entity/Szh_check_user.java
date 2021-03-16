package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2018/11/30.
 */
@Entity
public class Szh_check_user {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String checkuserid;//质检人员表id
    private String checkgroupid;//质检组表id
    private String userid;//用户id

    public String getCheckuserid() {
        return checkuserid;
    }

    public void setCheckuserid(String checkuserid) {
        this.checkuserid = checkuserid;
    }

    public String getCheckgroupid() {
        return checkgroupid;
    }

    public void setCheckgroupid(String checkgroupid) {
        this.checkgroupid = checkgroupid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
