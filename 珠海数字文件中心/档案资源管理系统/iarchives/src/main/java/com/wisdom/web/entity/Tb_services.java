package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_services {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String servicesid;
  @Column(columnDefinition = "varchar(100)")
  private String servicesname;
  @Column(columnDefinition = "varchar(500)")
  private String desciption;
  @Column(columnDefinition = "integer")
  private Long status;
  @Column(columnDefinition = "varchar(50)")
  private String servicestype;

  public String getServicesid() {
    return servicesid;
  }

  public void setServicesid(String servicesid) {
    this.servicesid = servicesid;
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

  public String getServicestype() {
    return servicestype;
  }

  public void setServicestype(String servicestype) {
    this.servicestype = servicestype;
  }
}
