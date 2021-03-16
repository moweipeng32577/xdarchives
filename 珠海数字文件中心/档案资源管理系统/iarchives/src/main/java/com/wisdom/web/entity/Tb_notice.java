package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_notice {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String noticeID;//ID

    @Column(columnDefinition = "varchar(200)")
    private String title;//标题

    @Column(columnDefinition = "char(36)")
    private String userID;//创建人

    @Column(columnDefinition = "varchar(20)")
    private String organ;//机构

    @Column(columnDefinition = "varchar(5000)")
    private String content;//内容

    @Column(columnDefinition = "varchar(20)")
    private String publishtime;//发布时间

    @Column(columnDefinition = "integer")
    private Integer publishstate;//发布状态

    @Column(columnDefinition = "integer")
    private Integer stick;//置顶等级

    public Tb_notice() {
    }

    public Tb_notice(String title, String userID, String organ, String content, String publishtime, Integer publishstate, Integer stick) {
        this.title = title;
        this.userID = userID;
        this.organ = organ;
        this.content = content;
        this.publishtime = publishtime;
        this.publishstate = publishstate;
        this.stick = stick;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public Integer getPublishstate() {
        return publishstate;
    }

    public void setPublishstate(Integer publishstate) {
        this.publishstate = publishstate;
    }

    public Integer getStick() {
        return stick;
    }

    public void setStick(Integer stick) {
        this.stick = stick;
    }

    @Override
    public String toString() {
        return "Tb_notice{" +
                "noticeID='" + noticeID + '\'' +
                ", title='" + title + '\'' +
                ", userID='" + userID + '\'' +
                ", organ='" + organ + '\'' +
                ", content='" + content + '\'' +
                ", publishtime='" + publishtime + '\'' +
                ", publishstate=" + publishstate +
                ", stick=" + stick +
                '}';
    }
}
