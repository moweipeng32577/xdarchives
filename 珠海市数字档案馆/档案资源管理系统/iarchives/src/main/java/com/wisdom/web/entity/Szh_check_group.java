package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2018/11/30.
 */
@Entity
public class Szh_check_group {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String checkgroupid;//质检组表id
    private String groupname;//质检组名
    private String desci;//组描述
    private String type;//类型

    public String getCheckgroupid() {
        return checkgroupid;
    }

    public void setCheckgroupid(String checkgroupid) {
        this.checkgroupid = checkgroupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getDesci() {
        return desci;
    }

    public void setDesci(String desci) {
        this.desci = desci;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
