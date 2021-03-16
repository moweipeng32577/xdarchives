package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_work {

  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String workid;
  @Column(columnDefinition = "varchar(200)")
  private String worktext;
  @Column(columnDefinition = "varchar(500)")
  private String workdesc;
  @Column(columnDefinition = "integer")
  private Integer sortsequence;
  @Column(columnDefinition = "varchar(10)")
  private String urgingstate;	//是否催办 1.可催办 2.不可
  @Column(columnDefinition = "varchar(10)")
  private String sendmsgstate;	//是否短信通知 1.是  0否

  public String getSendmsgstate() {
    return sendmsgstate;
  }

  public void setSendmsgstate(String sendmsgstate) {
    this.sendmsgstate = sendmsgstate;
  }

  public String getUrgingstate() {
    return urgingstate;
  }

  public void setUrgingstate(String urgingstate) {
    this.urgingstate = urgingstate;
  }

  public String getId() {
    return workid;
  }

  public void setId(String id) {
    this.workid = id;
  }

  public String getText() {
    return worktext;
  }

  public void setText(String text) {
    this.worktext = text;
  }

  public String getDesc() {
    return workdesc;
  }

  public void setDesc(String desc) {
    this.workdesc = desc;
  }

  public Integer getSortsequence() {
    return sortsequence;
  }

  public void setSortsequence(Integer sortsequence) {
    this.sortsequence = sortsequence;
  }
}
