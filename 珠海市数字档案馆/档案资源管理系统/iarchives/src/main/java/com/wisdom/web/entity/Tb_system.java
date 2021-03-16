package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_system {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String systemid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String servicesid;
  @Column(columnDefinition = "varchar(100)")
  private String systemname;
  @Column(columnDefinition = "varchar(100)")
  private String servicesname;
  @Column(columnDefinition = "varchar(500)")
  private String desciption;
  @Column(columnDefinition = "integer")
  private Long status;
  @Column(columnDefinition = "varchar(50)")
  private String systemtype;
  @Column(columnDefinition = "varchar(1000)")
  private String uri;

  public String getSystemid() {
    return systemid;
  }

  public void setSystemid(String systemid) {
    this.systemid = systemid;
  }

  public String getServicesid() {
    return servicesid;
  }

  public void setServicesid(String servicesid) {
    this.servicesid = servicesid;
  }

  public String getSystemname() {
    return systemname;
  }

  public void setSystemname(String systemname) {
    this.systemname = systemname;
  }

  public String getServicesname() {
    return servicesname;
  }

  public void setServicesname(String servicesname) {
    this.servicesname = servicesname;
  }

  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getSystemtype() {
    return systemtype;
  }

  public void setSystemtype(String systemtype) {
    this.systemtype = systemtype;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
