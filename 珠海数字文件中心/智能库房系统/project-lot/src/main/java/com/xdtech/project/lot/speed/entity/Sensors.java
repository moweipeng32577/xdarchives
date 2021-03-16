package com.xdtech.project.lot.speed.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 斯必得设备终端
 * Created by Rong on 2019-11-15.
 */
@Entity
public class Sensors {

    @Id
    @Column(name = "No")
    private Integer no;               //设备编号
    @Column(name = "IP")
    private String ip;                //IP地址
    @Column(name = "type")
    private String type;             //设备类型
    @Column(name = "displayname")
    private String displayName;     //设备名称

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}