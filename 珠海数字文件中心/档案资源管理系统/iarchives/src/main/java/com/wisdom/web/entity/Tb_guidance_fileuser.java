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
public class Tb_guidance_fileuser {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(50)")
    private String username;  //姓名
    @Column(columnDefinition = "varchar(10)")
    private String sex;  //性别
    @Column(columnDefinition = "varchar(30)")
    private String workno;  //上岗证号
    @Column(columnDefinition = "varchar(20)")
    private String officephone;  //办公室电话
    @Column(columnDefinition = "varchar(20)")
    private String mobilephone;  //联系电话
    @Column(columnDefinition = "varchar(10)")
    private String isfulltime;  //是否专职
    @Column(columnDefinition = "varchar(30)")
    private String workdate;  //上岗日期
    @Column(columnDefinition = "varchar(20)")
    private String aduitdate;  //最新年审日期
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;   //机构id
    @Column(columnDefinition = "varchar(50)")
    private String selectyear;  //年度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getOfficephone() {
        return officephone;
    }

    public void setOfficephone(String officephone) {
        this.officephone = officephone;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getIsfulltime() {
        return isfulltime;
    }

    public void setIsfulltime(String isfulltime) {
        this.isfulltime = isfulltime;
    }

    public String getWorkdate() {
        return workdate;
    }

    public void setWorkdate(String workdate) {
        this.workdate = workdate;
    }

    public String getAduitdate() {
        return aduitdate;
    }

    public void setAduitdate(String aduitdate) {
        this.aduitdate = aduitdate;
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
