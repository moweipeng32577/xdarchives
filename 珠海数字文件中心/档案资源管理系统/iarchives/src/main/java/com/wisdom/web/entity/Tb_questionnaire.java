package com.wisdom.web.entity;

import groovy.transform.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity

/**问卷信息*/
public class Tb_questionnaire {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String questionnaireID;//问卷ID

    @Column(columnDefinition = "varchar(200)")
    private String title;//标题

    @Column(columnDefinition = "char(36)")
    private String userID;//创建人

    @Column(columnDefinition = "varchar(20)")
    private String createtime;//创建时间

    @Column(columnDefinition = "varchar(20)")
    private String starttime;//开始时间

    @Column(columnDefinition = "varchar(20)")
    private String endtime;//结束时间

    @Column(columnDefinition = "varchar(20)")
    private String publishtime;//发布时间

    @Column(columnDefinition = "integer")
    private Integer publishstate;//发布状态

    @Column(columnDefinition = "integer")
    private Integer stick;//置顶等级

    @Column(columnDefinition = "integer")
    private Integer isanswer;//是否已有人答题

    public Tb_questionnaire() {
    }

    public Tb_questionnaire(String title, String userID, String createtime, String starttime, String endTime, String publishTime, Integer publishstate, Integer stick,Integer isanswer) {
        this.title = title;
        this.userID = userID;
        this.createtime = createtime;
        this.starttime = starttime;
        this.endtime = endtime;
        this.publishtime = publishtime;
        this.publishstate = publishstate;
        this.stick = stick;
        this.isanswer = isanswer;
    }

    public String getQuestionnaireID() {
        return questionnaireID;
    }

    public void setQuestionnaireID(String questionnaireID) {
        this.questionnaireID = questionnaireID;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
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

    public Integer getIsanswer() {
        return isanswer;
    }

    public void setIsanswer(Integer isanswer) {
        this.isanswer = isanswer;
    }

    @Override
    public String toString() {
        return "Tb_questionnaire{" +
                "questionnaireID='" + questionnaireID + '\'' +
                ", title='" + title + '\'' +
                ", userID='" + userID + '\'' +
                ", createtime='" + createtime + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", publishtime='" + publishtime + '\'' +
                ", publishstate=" + publishstate +
                ", stick=" + stick +
                ", isanswer=" + isanswer +
                '}';
    }
}
