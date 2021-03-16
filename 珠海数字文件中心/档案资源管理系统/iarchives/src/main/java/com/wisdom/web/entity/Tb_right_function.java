package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_right_function {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String fnid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String organid;
  @Column(columnDefinition = "varchar(100)")
  private String functionname;
  @Column(columnDefinition = "varchar(100)")
  private String functioncode;
  @Column(columnDefinition = "varchar(100)")
  private String organname;
  @Column(columnDefinition = "varchar(100)")
  private String desciption;
  @Column(columnDefinition = "varchar(50)")
  private String functiontype;
  @Column(columnDefinition = "varchar(1)")
  private String status;
  @Column(columnDefinition = "varchar(20)")
  private String tkey;
  @Column(columnDefinition = "varchar(50)")
  private String icon;
  @Column(columnDefinition = "varchar(200)")
  private String url;
  @Column(columnDefinition = "varchar(10)")
  private String haschilds;
  @Column(columnDefinition = "varchar(200)")
  private String isp;
  @Column(columnDefinition = "integer")
  private Integer sortsequence;

  public int getOrders() {
    return sortsequence;
  }

  public void setOrders(int orders) {
    this.sortsequence = orders;
  }

  public String getIsp() {
    return isp;
  }

  public void setIsp(String isp) {
    this.isp = isp;
  }

  public String getTkey() {
    return tkey;
  }

  public void setTkey(String tkey) {
    this.tkey = tkey;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHaschilds() {
    return haschilds;
  }

  public void setHaschilds(String haschilds) {
    this.haschilds = haschilds;
  }

  public String getFnid() {
    return fnid;
  }

  public void setFnid(String fnid) {
    this.fnid = fnid;
  }

  public String getOrganid() {
    return organid;
  }

  public void setOrganid(String organid) {
    this.organid = organid;
  }

  public String getName() {
    return functionname;
  }

  public void setName(String name) {
    this.functionname = name;
  }

  public String getCode() {
    return functioncode;
  }

  public void setCode(String code) {
    this.functioncode = code;
  }

  public String getOrganname() {
    return organname;
  }

  public void setOrganname(String organname) {
    this.organname = organname;
  }

  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

  public String getFunctiontype() {
    return functiontype;
  }

  public void setFunctiontype(String functiontype) {
    this.functiontype = functiontype;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
