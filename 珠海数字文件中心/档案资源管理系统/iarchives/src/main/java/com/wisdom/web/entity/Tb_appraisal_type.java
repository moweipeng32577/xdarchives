package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_appraisal_type {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String appraisaltypeid;
  @Column(columnDefinition = "varchar(36)")
  private String parentappraisaltypeid;
  @Column(columnDefinition = "varchar(255)")
  private String appraisaltypevalue;

  public String getAppraisaltypeid() {
    return appraisaltypeid;
  }

  public void setAppraisaltypeid(String appraisaltypeid) {
    this.appraisaltypeid = appraisaltypeid;
  }

  public String getParentappraisaltypeid() {
    return parentappraisaltypeid;
  }

  public void setParentappraisaltypeid(String parentappraisaltypeid) {
    this.parentappraisaltypeid = parentappraisaltypeid;
  }

  public String getAppraisaltypevalue() {
    return appraisaltypevalue;
  }

  public void setAppraisaltypevalue(String appraisaltypevalue) {
    this.appraisaltypevalue = appraisaltypevalue;
  }
}
