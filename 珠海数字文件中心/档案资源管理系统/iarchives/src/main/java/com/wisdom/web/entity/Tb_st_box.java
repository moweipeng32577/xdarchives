package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_st_box {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String stid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;
  @Column(columnDefinition = "varchar(20)")
  private String borrowtype;
  @Column(columnDefinition = "varchar(20)")
  private String settype;  //设置利用类型

  public String getSettype() {
    return settype;
  }

  public void setSettype(String settype) {
    this.settype = settype;
  }

  public String getBorrowtype() {
    return borrowtype;
  }

  public void setBorrowtype(String borrowtype) {
    this.borrowtype = borrowtype;
  }

  public String getId() {
    return stid;
  }

  public void setId(String id) {
    this.stid = id;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }
}
