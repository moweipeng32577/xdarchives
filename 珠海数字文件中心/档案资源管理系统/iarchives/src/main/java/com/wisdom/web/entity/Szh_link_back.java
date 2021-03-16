package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/7/24.
 */
@Entity
public class Szh_link_back {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;            //主键
    private String link;          //环节
    private String calloutid;       //调出条目id
    private String backname;      //退回人姓名
    private String backaccount;   //退回人账号
    private String backtime;      //退回时间
    private String depict;        //退回原因
    private String status;         //退回/备注登记


    public Szh_link_back(){}

    public Szh_link_back(String link,String calloutid,String backname,String backaccount,String backtime,String depict,String status){
        this.link = link;
        this.calloutid = calloutid;
        this.backname = backname;
        this.backaccount = backaccount;
        this.backtime = backtime;
        this.depict = depict;
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCalloutid() {
        return calloutid;
    }

    public void setCalloutid(String calloutid) {
        this.calloutid = calloutid;
    }

    public String getBackname() {
        return backname;
    }

    public void setBackname(String backname) {
        this.backname = backname;
    }

    public String getBackaccount() {
        return backaccount;
    }

    public void setBackaccount(String backaccount) {
        this.backaccount = backaccount;
    }

    public String getBacktime() {
        return backtime;
    }

    public void setBacktime(String backtime) {
        this.backtime = backtime;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
