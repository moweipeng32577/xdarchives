package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_appraisal_standard {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String appraisalstandardid;
  @Column(columnDefinition = "varchar(255)")
  private String appraisaltypevalue;//鉴定类型
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String appraisaltypeid;//鉴定类型id
  @Column(columnDefinition = "varchar(255)")
  private String appraisalstandardvalue;//鉴定标准值
  @Column(columnDefinition = "varchar(50)")
  private String appraisalretention;//保管期限
  @Column(columnDefinition = "varchar(255)")
  private String appraisaldesc;//描述
  @Column(columnDefinition = "varchar(255)")
  private String appraisalfieldf;
  @Column(columnDefinition = "varchar(255)")
  private String appraisalfields;

  public String getAppraisalstandardid() {
    return appraisalstandardid;
  }

  public void setAppraisalstandardid(String appraisalstandardid) {
    this.appraisalstandardid = appraisalstandardid;
  }

  public String getAppraisaltypevalue() {
    return appraisaltypevalue;
  }

  public void setAppraisaltypevalue(String appraisaltypevalue) {
    this.appraisaltypevalue = appraisaltypevalue;
  }

  public String getAppraisaltypeid() {
    return appraisaltypeid;
  }

  public void setAppraisaltypeid(String appraisaltypeid) {
    this.appraisaltypeid = appraisaltypeid;
  }

  public String getAppraisalstandardvalue() {
    return appraisalstandardvalue;
  }

  public void setAppraisalstandardvalue(String appraisalstandardvalue) {
    this.appraisalstandardvalue = appraisalstandardvalue;
  }

  public String getAppraisalretention() {
    return appraisalretention;
  }

  public void setAppraisalretention(String appraisalretention) {
    this.appraisalretention = appraisalretention;
  }

  public String getAppraisaldesc() {
    return appraisaldesc;
  }

  public void setAppraisaldesc(String appraisaldesc) {
    this.appraisaldesc = appraisaldesc;
  }

  public String getAppraisalfieldf() {
    return appraisalfieldf;
  }

  public void setAppraisalfieldf(String appraisalfieldf) {
    this.appraisalfieldf = appraisalfieldf;
  }

  public String getAppraisalfields() {
    return appraisalfields;
  }

  public void setAppraisalfields(String appraisalfields) {
    this.appraisalfields = appraisalfields;
  }
}
