package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Szh_archives_callout {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;                //主键
  private String batchcode;         //批次号
  private String batchname;         //批次名
  private String assemblycode;      //流水线号
  private String assembly;          //流水线
  private Integer ajcopies;         //借出份数
  private Integer pages;            //借出页数
  private String searchivescode;    //起始档号
  private String spillagecode;      //漏号
  private String lender;            //借档人
  private String lendtime;          //借出时间
  private Integer lendcopies;       //借出份数
  private Integer lendpages;        //借出页数
  private String lendadmin;         //借出管理员
  private String lendsuperior;      //借出监理
  private String lendexplain;       //借出说明
  private Integer returncopies;     //归还份数
  private Integer returnpages;      //归还页数
  private String timetype;          //时间类型
  private String returntime;        //归还时间
  private String returncrew;        //归还人员
  private String returnsuperior;    //归还监理
  private String returnexplain;     //归还说明
  private String returnstatus;      //归还状态
  private String batchstatus;       //批次状态
  private String connectstatus;     //移交状态

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBatchcode() {
    return batchcode;
  }

  public void setBatchcode(String batchcode) {
    this.batchcode = batchcode;
  }

  public String getBatchname() {
    return batchname;
  }

  public void setBatchname(String batchname) {
    this.batchname = batchname;
  }

  public String getAssemblycode() {
    return assemblycode;
  }

  public void setAssemblycode(String assemblycode) {
    this.assemblycode = assemblycode;
  }

  public String getAssembly() {
    return assembly;
  }

  public void setAssembly(String assembly) {
    this.assembly = assembly;
  }

  public Integer getAjcopies() {
    return ajcopies;
  }

  public void setAjcopies(Integer ajcopies) {
    this.ajcopies = ajcopies;
  }

  public Integer getPages() {
    return pages;
  }

  public void setPages(Integer pages) {
    this.pages = pages;
  }

  public String getSearchivescode() {
    return searchivescode;
  }

  public void setSearchivescode(String searchivescode) {
    this.searchivescode = searchivescode;
  }

  public String getSpillagecode() {
    return spillagecode;
  }

  public void setSpillagecode(String spillagecode) {
    this.spillagecode = spillagecode;
  }

  public String getLender() {
    return lender;
  }

  public void setLender(String lender) {
    this.lender = lender;
  }

  public String getLendtime() {
    return lendtime;
  }

  public void setLendtime(String lendtime) {
    this.lendtime = lendtime;
  }

  public Integer getLendcopies() {
    return lendcopies;
  }

  public void setLendcopies(Integer lendcopies) {
    this.lendcopies = lendcopies;
  }

  public Integer getLendpages() {
    return lendpages;
  }

  public void setLendpages(Integer lendpages) {
    this.lendpages = lendpages;
  }

  public String getLendadmin() {
    return lendadmin;
  }

  public void setLendadmin(String lendadmin) {
    this.lendadmin = lendadmin;
  }

  public String getLendsuperior() {
    return lendsuperior;
  }

  public void setLendsuperior(String lendsuperior) {
    this.lendsuperior = lendsuperior;
  }

  public String getLendexplain() {
    return lendexplain;
  }

  public void setLendexplain(String lendexplain) {
    this.lendexplain = lendexplain;
  }

  public Integer getReturncopies() {
    return returncopies;
  }

  public void setReturncopies(Integer returncopies) {
    this.returncopies = returncopies;
  }

  public Integer getReturnpages() {
    return returnpages;
  }

  public void setReturnpages(Integer returnpages) {
    this.returnpages = returnpages;
  }

  public String getTimetype() {
    return timetype;
  }

  public void setTimetype(String timetype) {
    this.timetype = timetype;
  }

  public String getReturntime() {
    return returntime;
  }

  public void setReturntime(String returntime) {
    this.returntime = returntime;
  }

  public String getReturncrew() {
    return returncrew;
  }

  public void setReturncrew(String returncrew) {
    this.returncrew = returncrew;
  }

  public String getReturnsuperior() {
    return returnsuperior;
  }

  public void setReturnsuperior(String returnsuperior) {
    this.returnsuperior = returnsuperior;
  }

  public String getReturnexplain() {
    return returnexplain;
  }

  public void setReturnexplain(String returnexplain) {
    this.returnexplain = returnexplain;
  }

  public String getReturnstatus() {
    return returnstatus;
  }

  public void setReturnstatus(String returnstatus) {
    this.returnstatus = returnstatus;
  }

  public String getBatchstatus() {
    return batchstatus;
  }

  public void setBatchstatus(String batchstatus) {
    this.batchstatus = batchstatus;
  }

  public String getConnectstatus() {
    return connectstatus;
  }

  public void setConnectstatus(String connectstatus) {
    this.connectstatus = connectstatus;
  }
}
