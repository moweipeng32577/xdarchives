package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/7/24.
 */
@Entity
public class Szh_link_sign {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;         //主键
    private String userid;     //用户id
    private String calloutid;  //调出条目
    private String link;     //环节
    private String signtime;   //签收时间
    private String batchcode;  //批次号
    private String assemblyid;  //流水线id

    public String getAssemblyid() {
        return assemblyid;
    }

    public void setAssemblyid(String assemblyid) {
        this.assemblyid = assemblyid;
    }

    public Szh_link_sign(){}

    public Szh_link_sign(String userid,String calloutid,String link,String signtime,String batchcode,String assemblyid){
        this.userid = userid;
        this.calloutid = calloutid;
        this.link = link;
        this.signtime = signtime;
        this.batchcode = batchcode;
        this.assemblyid = assemblyid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCalloutid() {
        return calloutid;
    }

    public void setCalloutid(String calloutid) {
        this.calloutid = calloutid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSigntime() {
        return signtime;
    }

    public void setSigntime(String signtime) {
        this.signtime = signtime;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }
}
