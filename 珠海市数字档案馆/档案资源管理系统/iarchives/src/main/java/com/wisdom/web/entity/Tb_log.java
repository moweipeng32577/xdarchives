package com.wisdom.web.entity;


import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by xd on 2017/10/10.
 */
@Entity
public class Tb_log {
    @Id
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String logid;
    @Column(columnDefinition = "varchar(300)")
    private String logName;
    @Column(columnDefinition = "char(1)")
    private boolean leaf;


    public String getId() {
        return logid;
    }

    public void setId(String id) {
        this.logid = id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        return "logid:" + logid + "_logName:" + logName + "_leaf:" + leaf;
    }
}
