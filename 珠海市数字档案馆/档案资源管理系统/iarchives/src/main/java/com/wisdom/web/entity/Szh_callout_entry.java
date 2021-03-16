package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Szh_callout_entry {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;                //主键
  private String batchcode;         //批次号
  private String archivecode;       //案卷号/档号
  private String workstate;         //工作状态
  private String lendstate;         //借出状态
  private String checkstate;        //检查状态
  private String scanstate;         //扫描状态
  private String picturestate;      //图片状态
  private String businesssigner;    //业务签收人
  private String businesssigncode;  //业务签收工号
  private String signtime;          //签收时间
  private String entrysigner;       //实体签收人
  private String entrysigncode;     //实体签收代号
  private String entrysigntime;     //实体签收时间
  private String entrysignorgan;    //实体签收单位
  private String entrysigntype;    //实体签收状态
  private Integer a0;               //A0页数
  private Integer a1;               //A1页数
  private Integer a2;               //A2页数
  private Integer a3;               //A3页数
  private Integer a4;               //A4页数
  private Integer za4;              //折算A4页数
  private String tidy;              //整理
  private String scan;              //扫描
  private String pictureprocess;    //图片处理
  private String definition;        //属性定义
  private String audit;             //审核
  private String record;            //著录
  private String bind;             //装订
  private Integer pages;            //页数
  private Integer copies;           //份数
  private Integer filecount;        //文件数
  private String returnman;           //归还人
  private String returntime;        //归还时间
  private String returnloginname;        //归还人代号
  private String nodeid;          //节点ID

  public String getNodeid() {
    return nodeid;
  }

  public void setNodeid(String nodeid) {
    this.nodeid = nodeid;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String getEntrysigntype() {
    return entrysigntype;
  }

  public void setEntrysigntype(String entrysigntype) {
    this.entrysigntype = entrysigntype;
  }

  public String getReturnloginname() {
    return returnloginname;
  }

  public void setReturnloginname(String returnloginname) {
    this.returnloginname = returnloginname;
  }

  public String getReturnman() {
    return returnman;
  }

  public void setReturnman(String returnman) {
    this.returnman = returnman;
  }

  public String getReturntime() {
    return returntime;
  }

  public void setReturntime(String returntime) {
    this.returntime = returntime;
  }

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

  public String getArchivecode() {
    return archivecode;
  }

  public void setArchivecode(String archivecode) {
    this.archivecode = archivecode;
  }

  public String getWorkstate() {
    return workstate;
  }

  public void setWorkstate(String workstate) {
    this.workstate = workstate;
  }

  public String getLendstate() {
    return lendstate;
  }

  public void setLendstate(String lendstate) {
    this.lendstate = lendstate;
  }

  public String getCheckstate() {
    return checkstate;
  }

  public void setCheckstate(String checkstate) {
    this.checkstate = checkstate;
  }

  public String getScanstate() {
    return scanstate;
  }

  public void setScanstate(String scanstate) {
    this.scanstate = scanstate;
  }

  public String getPicturestate() {
    return picturestate;
  }

  public void setPicturestate(String picturestate) {
    this.picturestate = picturestate;
  }

  public String getBusinesssigner() {
    return businesssigner;
  }

  public void setBusinesssigner(String businesssigner) {
    this.businesssigner = businesssigner;
  }

  public String getBusinesssigncode() {
    return businesssigncode;
  }

  public void setBusinesssigncode(String businesssigncode) {
    this.businesssigncode = businesssigncode;
  }

  public String getSigntime() {
    return signtime;
  }

  public void setSigntime(String signtime) {
    this.signtime = signtime;
  }

  public String getEntrysigner() {
    return entrysigner;
  }

  public void setEntrysigner(String entrysigner) {
    this.entrysigner = entrysigner;
  }

  public String getEntrysigncode() {
    return entrysigncode;
  }

  public void setEntrysigncode(String entrysigncode) {
    this.entrysigncode = entrysigncode;
  }

  public String getEntrysigntime() {
    return entrysigntime;
  }

  public void setEntrysigntime(String entrysigntime) {
    this.entrysigntime = entrysigntime;
  }

  public String getEntrysignorgan() {
    return entrysignorgan;
  }

  public void setEntrysignorgan(String entrysignorgan) {
    this.entrysignorgan = entrysignorgan;
  }

  public Integer getA0() {
    return a0;
  }

  public void setA0(Integer a0) {
    this.a0 = a0;
  }

  public Integer getA1() {
    return a1;
  }

  public void setA1(Integer a1) {
    this.a1 = a1;
  }

  public Integer getA2() {
    return a2;
  }

  public void setA2(Integer a2) {
    this.a2 = a2;
  }

  public Integer getA3() {
    return a3;
  }

  public void setA3(Integer a3) {
    this.a3 = a3;
  }

  public Integer getA4() {
    return a4;
  }

  public void setA4(Integer a4) {
    this.a4 = a4;
  }

  public Integer getZa4() {
    return za4;
  }

  public void setZa4(Integer za4) {
    this.za4 = za4;
  }

  public String getTidy() {
    return tidy;
  }

  public void setTidy(String tidy) {
    this.tidy = tidy;
  }

  public String getScan() {
    return scan;
  }

  public void setScan(String scan) {
    this.scan = scan;
  }

  public String getPictureprocess() {
    return pictureprocess;
  }

  public void setPictureprocess(String pictureprocess) {
    this.pictureprocess = pictureprocess;
  }

  public String getAudit() {
    return audit;
  }

  public void setAudit(String audit) {
    this.audit = audit;
  }

  public String getRecord() {
    return record;
  }

  public void setRecord(String record) {
    this.record = record;
  }

  public Integer getPages() {
    return pages;
  }

  public void setPages(Integer pages) {
    this.pages = pages;
  }

  public Integer getCopies() {
    return copies;
  }

  public void setCopies(Integer copies) {
    this.copies = copies;
  }

  public Integer getFilecount() {
    return filecount;
  }

  public void setFilecount(Integer filecount) {
    this.filecount = filecount;
  }

  public String getBind() {
    return bind;
  }

  public void setBind(String bind) {
    this.bind = bind;
  }
}
