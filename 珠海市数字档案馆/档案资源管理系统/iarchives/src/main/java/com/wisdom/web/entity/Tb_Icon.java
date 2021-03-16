package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 桌面图标表
 */
@Entity
public class Tb_Icon {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String uuid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String iconid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String pid;
    @Column(columnDefinition = "varchar(200)")
    private String url;
    @Column(columnDefinition = "varchar(100)")
    private String code;
    @Column(columnDefinition = "varchar(50)")
    private String tkey;
    @Column(columnDefinition = "varchar(100)")
    private String icon;
    @Column(columnDefinition = "varchar(200)")
    private String text;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String userid;
    @Column(columnDefinition = "integer")
    private Integer sortsequence;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(2)")
    private String systype;

    public Tb_Icon(){}

    public Tb_Icon(String iconid,String pid,String url,String code,String tkey,String icon,String text,String userid,Integer sortsequence){
        this.iconid = iconid;
        this.pid = pid;
        this.url = url;
        this.code = code;
        this.tkey = tkey;
        this.icon = icon;
        this.text = text;
        this.userid = userid;
        this.sortsequence = sortsequence;
    }

    public String getUid() {
        return uuid;
    }

    public void setUid(String uid) {
        this.uuid = uid;
    }

    public String getId() {
        return iconid;
    }

    public void setId(String id) {
        this.iconid = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getOrders() {
        return sortsequence;
    }

    public void setOrders(Integer orders) {
        this.sortsequence = orders;
    }

    public String getSystype() {
        return systype;
    }

    public void setSystype(String systype) {
        this.systype = systype;
    }
}
