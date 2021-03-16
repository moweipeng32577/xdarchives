package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_feedback {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String feedbackid;
  @Column(columnDefinition = "varchar(255)")
  private String title;//标题
  @Column(columnDefinition = "varchar(50)")
  private String asktime;//提问时间
  @Column(columnDefinition = "varchar(50)")
  private String askman;//投件人
  @Column(columnDefinition = "varchar(255)")
  private String content;//提问内容
  @Column(columnDefinition = "varchar(50)")
  private String replytime;//回复时间
  @Column(columnDefinition = "varchar(50)")
  private String replyby;//回复人
  @Column(columnDefinition = "varchar(255)")
  private String replycontent;//回复内容
  @Column(columnDefinition = "varchar(10)")
  private String flag;//回复状态
  @Column(columnDefinition = "varchar(50)")
  private String appraise;//评分
  @Column(columnDefinition = "char(36)")
  private String borrowdocid;//单据id
  @Column(columnDefinition = "char(36)")
  private String submiterid;//提交人id
  @Column(columnDefinition = "varchar(500)")
  private String appraisetext;//评分内容

  public String getAppraisetext() {
    return appraisetext;
  }

  public void setAppraisetext(String appraisetext) {
    this.appraisetext = appraisetext;
  }

  public String getSubmiterid() {
    return submiterid;
  }

  public void setSubmiterid(String submiterid) {
    this.submiterid = submiterid;
  }

  public String getAppraise() {
    return appraise;
  }

  public void setAppraise(String appraise) {
    this.appraise = appraise;
  }

  public String getBorrowdocid() {
    return borrowdocid;
  }

  public void setBorrowdocid(String borrowdocid) {
    this.borrowdocid = borrowdocid;
  }

  public String getFeedbackid() {
    return feedbackid;
  }

  public void setFeedbackid(String feedbackid) {
    this.feedbackid = feedbackid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAsktime() {
    return asktime;
  }

  public void setAsktime(String asktime) {
    this.asktime = asktime;
  }

  public String getAskman() {
    return askman;
  }

  public void setAskman(String askman) {
    this.askman = askman;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getReplytime() {
    return replytime;
  }

  public void setReplytime(String replytime) {
    this.replytime = replytime;
  }

  public String getReplyby() {
    return replyby;
  }

  public void setReplyby(String replyby) {
    this.replyby = replyby;
  }

  public String getReplycontent() {
    return replycontent;
  }

  public void setReplycontent(String replycontent) {
    this.replycontent = replycontent;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }
}
