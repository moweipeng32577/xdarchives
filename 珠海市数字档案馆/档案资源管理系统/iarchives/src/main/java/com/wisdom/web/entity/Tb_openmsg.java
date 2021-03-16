package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Tb_openmsg{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String msgid;
  @Column(columnDefinition = "varchar(36)")
  private String batchnum;
  @Column(columnDefinition = "varchar(30)")
  private String state;
  @Column(columnDefinition = "varchar(30)")
  private String result;
  @Column(columnDefinition = "varchar(30)")
  private String dealdate;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Column(columnDefinition = "varchar(100)")
  private String entryunit;  //档案所属单位
  @Column(columnDefinition = "varchar(100)")
  private String appraisedata;  //鉴定依据
  @Column(columnDefinition = "varchar(500)")
  private String appraisetext;  //初审鉴定意见
  @Column(columnDefinition = "varchar(500)")
  private String updatetitle;  //修改题名
  @Column(columnDefinition = "varchar(30)")
  private String firstresult;  //拟开放状态
  @Column(columnDefinition = "varchar(50)")
  private String firstappraiser;  //初审人
  @Column(columnDefinition = "varchar(50)")
  private String lastappraiser;  //复审人
  @Column(columnDefinition = "varchar(100)")
  private String lastappraisetext;  //复审鉴定意见
  @Column(columnDefinition = "varchar(30)")
  private String lastresult;  //复审开放状态
  @Column(columnDefinition = "varchar(30)")
  private String finalresult;  //最终开放状态

  public String getFinalresult() {
    return finalresult;
  }

  public void setFinalresult(String finalresult) {
    this.finalresult = finalresult;
  }

  public String getLastresult() {
    return lastresult;
  }

  public void setLastresult(String lastresult) {
    this.lastresult = lastresult;
  }

  public String getLastappraisetext() {
    return lastappraisetext;
  }

  public void setLastappraisetext(String lastappraisetext) {
    this.lastappraisetext = lastappraisetext;
  }

  public String getFirstresult() {
    return firstresult;
  }

  public void setFirstresult(String firstresult) {
    this.firstresult = firstresult;
  }

  public String getFirstappraiser() {
    return firstappraiser;
  }

  public void setFirstappraiser(String firstappraiser) {
    this.firstappraiser = firstappraiser;
  }

  public String getLastappraiser() {
    return lastappraiser;
  }

  public void setLastappraiser(String lastappraiser) {
    this.lastappraiser = lastappraiser;
  }

  public String getMsgid() {
    return msgid;
  }

  public void setMsgid(String msgid) {
    this.msgid = msgid;
  }

  public String getEntryunit() {
    return entryunit;
  }

  public void setEntryunit(String entryunit) {
    this.entryunit = entryunit;
  }

  public String getAppraisedata() {
    return appraisedata;
  }

  public void setAppraisedata(String appraisedata) {
    this.appraisedata = appraisedata;
  }

  public String getAppraisetext() {
    return appraisetext;
  }

  public void setAppraisetext(String appraisetext) {
    this.appraisetext = appraisetext;
  }

  public String getUpdatetitle() {
    return updatetitle;
  }

  public void setUpdatetitle(String updatetitle) {
    this.updatetitle = updatetitle;
  }

  public String getId() {
    return msgid;
  }

  public void setId(String id) {
    this.msgid = id;
  }

  public String getBatchnum() {
    return batchnum;
  }

  public void setBatchnum(String batchnum) {
    this.batchnum = batchnum;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getDealdate() {
    return dealdate;
  }

  public void setDealdate(String dealdate) {
    this.dealdate = dealdate;
  }
}
