package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_user_function_sx {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String ufid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String fnid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;

  public String getUfid() {
    return ufid;
  }

  public void setUfid(String ufid) {
    this.ufid = ufid;
  }

  public String getFnid() {
    return fnid;
  }

  public void setFnid(String fnid) {
    this.fnid = fnid;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

}
