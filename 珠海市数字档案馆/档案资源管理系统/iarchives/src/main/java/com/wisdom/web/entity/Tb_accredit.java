package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by SunK on 2020/5/7 0007.
 */
//授权元数据实体类
@Entity
public class Tb_accredit {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String aid;

    @Column(columnDefinition = "varchar(50)")
    private String shortname;

    @Column(columnDefinition = "varchar(255)")
    private String fullname;

    @Column(columnDefinition = "varchar(50)")
    private String atype;

    @Column(columnDefinition = "varchar(50)")
    private String publishtime;

    @Column(columnDefinition = "varchar(200)")
    private String text;

    @Column(columnDefinition = "varchar(50)")
    private String parentid;
    @Column(columnDefinition = "int")
    private Integer sortsequence;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(int sortsequence) {
        this.sortsequence = sortsequence;
    }
}
