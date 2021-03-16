package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_codeset_sx {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String codeid;
  @Column(columnDefinition = "integer")
  private long ordernum;
  @Column(columnDefinition = "varchar(20)")
  private String fieldcode;
  @Column(columnDefinition = "varchar(20)")
  private String fieldname;
  @Column(columnDefinition = "integer")
  private long fieldlength;
  @Column(columnDefinition = "varchar(10)")
  private String splitcode;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String datanodeid;

  public String getCodeid() {
    return codeid;
  }

  public void setCodeid(String codeid) {
    this.codeid = codeid;
  }

  public String getFieldcode() {
    return fieldcode;
  }

  public void setFieldcode(String fieldcode) {
    this.fieldcode = fieldcode;
  }

  public String getFieldname() {
    return fieldname;
  }

  public void setFieldname(String fieldname) {
    this.fieldname = fieldname;
  }

  public String getSplitcode() {
    return splitcode;
  }

  public void setSplitcode(String splitcode) {
    this.splitcode = splitcode;
  }

  public String getDatanodeid() {
    return datanodeid==null?null:datanodeid.trim();
  }

  public void setDatanodeid(String datanodeid) {
    this.datanodeid = datanodeid;
  }

  public long getOrdernum() {
    return ordernum;
  }

  public void setOrdernum(long ordernum) {
    this.ordernum = ordernum;
  }

  public long getFieldlength() {
    return fieldlength;
  }

  public void setFieldlength(long fieldlength) {
    this.fieldlength = fieldlength;
  }

  public boolean compareCodeset(Tb_codeset_sx codeset) {
    if (fieldname.equals(codeset.getFieldname()) && splitcode.equals(codeset.getSplitcode()) && fieldlength == codeset
            .getFieldlength() && ordernum == codeset.getOrdernum()) {
      return true;
    }
    return false;
  }
}
