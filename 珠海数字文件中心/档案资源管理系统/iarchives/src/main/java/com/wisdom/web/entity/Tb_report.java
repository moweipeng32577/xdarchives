package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表实体
 */
@Entity
public class Tb_report {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String reportid;//报表ID
  @Column(columnDefinition = "varchar(300)")
  private String reportname;//报表名称
  @Column(columnDefinition = "varchar(300)")
  private String filename;//报表文件名
  @Column(columnDefinition = "integer")
  private Long reportformat;
  @Column(columnDefinition = "varchar(300)")
  private String archivestype;//档案类型
  @Column(columnDefinition = "varchar(50)")
  private String modul;//模板名称
  @Column(columnDefinition = "varchar(300)")
  private String reportxsdfile;
  @Column(columnDefinition = "varchar(300)")
  private String reportdesignfile;
  @Column(columnDefinition = "varchar(300)")
  private String printfieldnamelist;//打印描述字段名称
  @Column(columnDefinition = "varchar(300)")
  private String printfieldcodelist;//打印描述字段编码
  @Column(columnDefinition = "varchar(100)")
  private String reporttype;//报表访问类型
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String nodeid;//节点ID(GUID)
  @Column(columnDefinition = "varchar(100)")
  private String nodename;//数据节点名称
  @Column(columnDefinition = "varchar(100)")
  private String tables;//打印表名
  @Column(columnDefinition = "varchar(100)")
  private String mainkey;//打印主键名
  @Column(columnDefinition = "integer")
  private Long reporttitlelen;
  @Column(columnDefinition = "varchar(300)")
  private String orderfieldname;//排序字段名称
  @Column(columnDefinition = "varchar(300)")
  private String orderfieldcode;//排序字段编码
  @Column(columnDefinition = "varchar(10)")
  private String ischangefont;
  @Column(columnDefinition = "varchar(10)")
  private String printmethod;
  @Column(columnDefinition = "varchar(300)")
  private String reportparamvalue;//报表参数值
  @Column(columnDefinition = "integer")
  private Long reportparamcounts;//报表参数个数

  public String getReportid() {
    return reportid;
  }

  public void setReportid(String reportid) {
    this.reportid = reportid;
  }

  public String getReportname() {
    return reportname;
  }

  public void setReportname(String reportname) {
    this.reportname = reportname;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Long getReportformat() {
    return reportformat;
  }

  public void setReportformat(Long reportformat) {
    this.reportformat = reportformat;
  }

  public String getArchivestype() {
    return archivestype;
  }

  public void setArchivestype(String archivestype) {
    this.archivestype = archivestype;
  }

  public String getModul() {
    return modul;
  }

  public void setModul(String modul) {
    this.modul = modul;
  }

  public String getReportxsdfile() {
    return reportxsdfile;
  }

  public void setReportxsdfile(String reportxsdfile) {
    this.reportxsdfile = reportxsdfile;
  }

  public String getReportdesignfile() {
    return reportdesignfile;
  }

  public void setReportdesignfile(String reportdesignfile) {
    this.reportdesignfile = reportdesignfile;
  }

  public String getPrintfieldnamelist() {
    return printfieldnamelist;
  }

  public void setPrintfieldnamelist(String printfieldnamelist) {
    this.printfieldnamelist = printfieldnamelist;
  }

  public String getPrintfieldcodelist() {
    return printfieldcodelist;
  }

  public void setPrintfieldcodelist(String printfieldcodelist) {
    this.printfieldcodelist = printfieldcodelist;
  }

  public String getReporttype() {
    return reporttype;
  }

  public void setReporttype(String reporttype) {
    this.reporttype = reporttype;
  }

  public String getNodeid() {
    return nodeid;
  }

  public void setNodeid(String nodeid) {
    this.nodeid = nodeid;
  }

  public String getNodename() {
    return nodename;
  }

  public void setNodename(String nodename) {
    this.nodename = nodename;
  }

  public String getTables() {
    return tables;
  }

  public void setTables(String tables) {
    this.tables = tables;
  }

  public String getMainkey() {
    return mainkey;
  }

  public void setMainkey(String mainkey) {
    this.mainkey = mainkey;
  }

  public Long getReporttitlelen() {
    return reporttitlelen;
  }

  public void setReporttitlelen(Long reporttitlelen) {
    this.reporttitlelen = reporttitlelen;
  }

  public String getOrderfieldname() {
    return orderfieldname;
  }

  public void setOrderfieldname(String orderfieldname) {
    this.orderfieldname = orderfieldname;
  }

  public String getOrderfieldcode() {
    return orderfieldcode;
  }

  public void setOrderfieldcode(String orderfieldcode) {
    this.orderfieldcode = orderfieldcode;
  }

  public String getIschangefont() {
    return ischangefont;
  }

  public void setIschangefont(String ischangefont) {
    this.ischangefont = ischangefont;
  }

  public String getPrintmethod() {
    return printmethod;
  }

  public void setPrintmethod(String printmethod) {
    this.printmethod = printmethod;
  }

  public String getReportparamvalue() {
    return reportparamvalue;
  }

  public void setReportparamvalue(String reportparamvalue) {
    this.reportparamvalue = reportparamvalue;
  }

  public Long getReportparamcounts() {
    return reportparamcounts;
  }

  public void setReportparamcounts(Long reportparamcounts) {
    this.reportparamcounts = reportparamcounts;
  }

  @Override
  public String toString() {
    return "Tb_report{" +
            "reportid='" + reportid + '\'' +
            ", reportname='" + reportname + '\'' +
            ", filename='" + filename + '\'' +
            ", reportformat=" + reportformat +
            ", archivestype='" + archivestype + '\'' +
            ", modul='" + modul + '\'' +
            ", reportxsdfile='" + reportxsdfile + '\'' +
            ", reportdesignfile='" + reportdesignfile + '\'' +
            ", printfieldnamelist='" + printfieldnamelist + '\'' +
            ", printfieldcodelist='" + printfieldcodelist + '\'' +
            ", reporttype='" + reporttype + '\'' +
            ", nodeid='" + nodeid + '\'' +
            ", nodename='" + nodename + '\'' +
            ", tables='" + tables + '\'' +
            ", mainkey='" + mainkey + '\'' +
            ", reporttitlelen=" + reporttitlelen +
            ", orderfieldname='" + orderfieldname + '\'' +
            ", orderfieldcode='" + orderfieldcode + '\'' +
            ", ischangefont='" + ischangefont + '\'' +
            ", printmethod='" + printmethod + '\'' +
            ", reportparamvalue='" + reportparamvalue + '\'' +
            ", reportparamcounts=" + reportparamcounts +
            '}';
  }

  public Map<String, Object> getMap(){
    Map<String, Object> map = new HashMap<>();
    map.put("reportid", getReportid());
    map.put("filename",getFilename());
    return map;
  }

}
