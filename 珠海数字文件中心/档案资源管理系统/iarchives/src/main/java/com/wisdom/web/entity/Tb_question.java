package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_question {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String questionID;//问题ID

    @Column(columnDefinition = "char(36)")
    private String questionnaireID;//问卷ID

    @Column(columnDefinition = "varchar(200)")
    private String content;//内容描述

    @Column(columnDefinition = "integer")
    private Integer type;//类型

    @Column(columnDefinition = "varchar(200)")
    private String optional;//可选项

    @Column(columnDefinition = "integer")
    private Integer isnecessary;//是否必答

    @Column(columnDefinition = "integer")
    private Integer sort;//序号

    public Tb_question() {
    }

    public Tb_question(String questionnaireID, String content, Integer type, String optional, Integer isnecessary, Integer sort) {
        this.questionnaireID = questionnaireID;
        this.content = content;
        this.type = type;
        this.optional = optional;
        this.isnecessary = isnecessary;
        this.sort = sort;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestionnaireID() {
        return questionnaireID;
    }

    public void setQuestionnaireID(String questionnaireID) {
        this.questionnaireID = questionnaireID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public Integer getIsnecessary() {
        return isnecessary;
    }

    public void setIsnecessary(Integer isnecessary) {
        this.isnecessary = isnecessary;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Tb_question{" +
                "questionID='" + questionID + '\'' +
                ", questionnaireID='" + questionnaireID + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", optional='" + optional + '\'' +
                ", isnecessary=" + isnecessary +
                ", sort=" + sort +
                '}';
    }

}
