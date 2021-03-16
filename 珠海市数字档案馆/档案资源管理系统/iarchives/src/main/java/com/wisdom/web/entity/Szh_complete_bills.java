package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 完成环节表
 */
@Entity
public class Szh_complete_bills {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;
  private String batchcode;
  private String nodeid;
  private String archivecode;
  private String filecode;
  private String tidy;
  private String scan;
  private String pictureprocess;
  private String audit;
  private String record;
  private String workstatus;
  private String lendstatus;
  private String checkstatus;
  private String scanstatus;
  private String entrypictureprocess;
  private Integer a0;
  private Integer a1;
  private Integer a2;
  private Integer a3;
  private Integer a4;
  private Integer za4;
  private Integer pages;
  private Integer copies;
  private Integer filecount;

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

  public String getNodeid() {
    return nodeid;
  }

  public void setNodeid(String nodeid) {
    this.nodeid = nodeid;
  }

  public String getArchivecode() {
    return archivecode;
  }

  public void setArchivecode(String archivecode) {
    this.archivecode = archivecode;
  }

  public String getFilecode() {
    return filecode;
  }

  public void setFilecode(String filecode) {
    this.filecode = filecode;
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

  public String getWorkstatus() {
    return workstatus;
  }

  public void setWorkstatus(String workstatus) {
    this.workstatus = workstatus;
  }

  public String getLendstatus() {
    return lendstatus;
  }

  public void setLendstatus(String lendstatus) {
    this.lendstatus = lendstatus;
  }

  public String getCheckstatus() {
    return checkstatus;
  }

  public void setCheckstatus(String checkstatus) {
    this.checkstatus = checkstatus;
  }

  public String getScanstatus() {
    return scanstatus;
  }

  public void setScanstatus(String scanstatus) {
    this.scanstatus = scanstatus;
  }

  public String getEntrypictureprocess() {
    return entrypictureprocess;
  }

  public void setEntrypictureprocess(String entrypictureprocess) {
    this.entrypictureprocess = entrypictureprocess;
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
}
