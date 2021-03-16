package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_template_desc {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String tdid;
  @Column(columnDefinition = "varchar(20)")
  private String fieldcode;
  @Column(columnDefinition = "varchar(500)")
  private String descs;

  public String getTdid() {
    return tdid;
  }

  public void setTdid(String tdid) {
    this.tdid = tdid;
  }

  public String getFieldcode() {
    return fieldcode;
  }

  public void setFieldcode(String fieldcode) {
    this.fieldcode = fieldcode;
  }

  public String getDescs() {
    return descs;
  }

  public void setDescs(String descs) {
    this.descs = descs;
  }
}
