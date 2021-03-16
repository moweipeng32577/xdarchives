package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/9/28.
 */
@Entity
public class Tb_guidance_organ {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(100)")
    private String organname;  //机构名称
    @Column(columnDefinition = "varchar(50)")
    private String classtype;  //级别
    @Column(columnDefinition = "varchar(10)")
    private String isindependent;  //是否独立
    @Column(columnDefinition = "varchar(100)")
    private String underdepartment;  //归口部门
    @Column(columnDefinition = "varchar(50)")
    private String username;  //部门负责人姓名
    @Column(columnDefinition = "varchar(100)")
    private String post;  //职务
    @Column(columnDefinition = "varchar(50)")
    private String politicstate;  //政治面貌
    @Column(columnDefinition = "varchar(20)")
    private String mobilephone;  //联系电话
    @Column(columnDefinition = "integer")
    private int fulltimenum;  //专职档案员数（单位：人）
    @Column(columnDefinition = "integer")
    private int parttimenum;  //兼职档案员数（单位：人）
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;   //机构id
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;  //年度

    public int getFulltimenum() {
        return fulltimenum;
    }

    public void setFulltimenum(int fulltimenum) {
        this.fulltimenum = fulltimenum;
    }

    public int getParttimenum() {
        return parttimenum;
    }

    public void setParttimenum(int parttimenum) {
        this.parttimenum = parttimenum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganname() {
        return organname;
    }

    public void setOrganname(String organname) {
        this.organname = organname;
    }

    public String getClasstype() {
        return classtype;
    }

    public void setClasstype(String classtype) {
        this.classtype = classtype;
    }

    public String getIsindependent() {
        return isindependent;
    }

    public void setIsindependent(String isindependent) {
        this.isindependent = isindependent;
    }

    public String getUnderdepartment() {
        return underdepartment;
    }

    public void setUnderdepartment(String underdepartment) {
        this.underdepartment = underdepartment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPoliticstate() {
        return politicstate;
    }

    public void setPoliticstate(String politicstate) {
        this.politicstate = politicstate;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getSelectyear() {
        return selectyear;
    }

    public void setSelectyear(String selectyear) {
        this.selectyear = selectyear;
    }
}
