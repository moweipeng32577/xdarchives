package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_ca_transfor {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String ctid;
  @Column(columnDefinition = "char(36)")
  private String docid;//移交ID
  @Column(columnDefinition = "varchar(50)")
  private String transforcaid;//移交证书id
  @Column(columnDefinition = "varchar(50)")
  private String editcaid;//审核证书id

  public String getCtid() {
    return ctid;
  }

  public void setCtid(String ctid) {
    this.ctid = ctid;
  }

  public String getDocid() {
    return docid;
  }

  public void setDocid(String docid) {
    this.docid = docid;
  }

  public String getTransforcaid() {
    return transforcaid;
  }

  public void setTransforcaid(String transforcaid) {
    this.transforcaid = transforcaid;
  }

  public String getEditcaid() {
    return editcaid;
  }

  public void setEditcaid(String editcaid) {
    this.editcaid = editcaid;
  }
}
