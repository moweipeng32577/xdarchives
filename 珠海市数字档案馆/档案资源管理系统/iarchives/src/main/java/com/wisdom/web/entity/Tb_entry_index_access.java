package com.wisdom.web.entity;

import com.wisdom.util.ExcelAttribute;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_entry_index_access {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String nodeid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String eleid;
  @ExcelAttribute(name = "题名")
  @Column(columnDefinition = "varchar(1000)")
  private String title;
  @ExcelAttribute(name = "全宗号")
  @Column(columnDefinition = "varchar(20)")
  private String funds;
  @ExcelAttribute(name = "属类号")
  @Column(columnDefinition = "varchar(30)")
  private String catalog;
  @ExcelAttribute(name = "文号")
  @Column(columnDefinition = "varchar(200)")
  private String filenumber;
  @ExcelAttribute(name = "保管期限")
  @Column(columnDefinition = "varchar(15)")
  private String entryretention;
  @ExcelAttribute(name = "成文日期")
  @Column(columnDefinition = "varchar(20)")
  private String filedate;
  @ExcelAttribute(name = "归档年度")
  @Column(columnDefinition = "varchar(8)")
  private String filingyear;
  @Column(columnDefinition = "varchar(100)")
  private String archivecode;
  @Column(columnDefinition = "varchar(10)")
  private String filecode;//案卷号
  @Column(columnDefinition = "varchar(10)")
  private String innerfile;//卷内顺序号
  @Column(columnDefinition = "varchar(100)")
  private String organ;//机构/问题
  @Column(columnDefinition = "varchar(10)")
  private String recordcode;//件号
  @Column(columnDefinition = "varchar(10)")
  private String entrysecurity;//密级
  @Column(columnDefinition = "varchar(10)")
  private String pages;//页数
  @Column(columnDefinition = "varchar(15)")
  private String pageno;//页号
  @Column(columnDefinition = "varchar(100)")
  private String responsible;//责任者
  @Column(columnDefinition = "varchar(20)")
  private String serial;//文件流水号
  @Column(columnDefinition = "varchar(20)")
  private String flagopen;//开放状态
  @Column(columnDefinition = "varchar(100)")
  private String entrystorage;//存储位置
  @Column(columnDefinition = "varchar(30)")
  private String descriptiondate;//著录时间
  @Column(columnDefinition = "varchar(30)")
  private String descriptionuser;//著录用户
  @Column(columnDefinition = "varchar(20)")
  private String keyword;//主题词
  @Column(columnDefinition = "varchar(30)")
  private String opendate;//开放时间

  public String getOpendate() {
    return opendate;
  }

  public void setOpendate(String opendate) {
    this.opendate = opendate;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }

  public String getNodeid() {
    return nodeid;
  }

  public void setNodeid(String nodeid) {
    this.nodeid = nodeid;
  }

  public String getEleid() {
    return eleid;
  }

  public void setEleid(String eleid) {
    this.eleid = eleid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getFilenumber() {
    return filenumber;
  }

  public void setFilenumber(String filenumber) {
    this.filenumber = filenumber;
  }

  public String getArchivecode() {
    return archivecode;
  }

  public void setArchivecode(String archivecode) {
    this.archivecode = archivecode;
  }

  public String getFunds() {
    return funds;
  }

  public void setFunds(String funds) {
    this.funds = funds;
  }

  public String getCatalog() {
    return catalog;
  }

  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }

  public String getFilecode() {
    return filecode;
  }

  public void setFilecode(String file) {
    this.filecode = file;
  }

  public String getInnerfile() {
    return innerfile;
  }

  public void setInnerfile(String innerfile) {
    this.innerfile = innerfile;
  }

  public String getFilingyear() {
    return filingyear;
  }

  public void setFilingyear(String filingyear) {
    this.filingyear = filingyear;
  }

  public String getEntryretention() {
    return entryretention;
  }

  public void setEntryretention(String retention) {
    this.entryretention = retention;
  }

  public String getOrgan() {
    return organ;
  }

  public void setOrgan(String organ) {
    this.organ = organ;
  }

  public String getRecordcode() {
    return recordcode;
  }

  public void setRecordcode(String recordcode) {
    this.recordcode = recordcode;
  }

  public String getEntrysecurity() {
    return entrysecurity;
  }

  public void setEntrysecurity(String security) {
    this.entrysecurity = security;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public String getPageno() {
    return pageno;
  }

  public void setPageno(String pageno) {
    this.pageno = pageno;
  }

  public String getFiledate() { return filedate; }

  public void setFiledate(String filedate) {
    this.filedate = filedate;
  }

  public String getResponsible() {
    return responsible;
  }

  public void setResponsible(String responsible) {
    this.responsible = responsible;
  }

  public String getSerial() {
    return serial;
  }

  public void setSerial(String serial) {
    this.serial = serial;
  }

  public String getFlagopen() {
    return flagopen;
  }

  public void setFlagopen(String open) {
    this.flagopen = open;
  }

  public String getEntrystorage() {
    return entrystorage;
  }

  public void setEntrystorage(String storage) {
    this.entrystorage = storage;
  }

  public String getDescriptiondate() {
    return descriptiondate;
  }

  public void setDescriptiondate(String descriptiondate) {
    this.descriptiondate = descriptiondate;
  }

  public String getDescriptionuser() {
    return descriptionuser;
  }

  public void setDescriptionuser(String descriptionuser) {
    this.descriptionuser = descriptionuser;
  }

  public String getKeyword() {
	return keyword;
  }

  public void setKeyword(String keyword) {
	this.keyword = keyword;
  }
}