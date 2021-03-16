package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_orderset {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String orderid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String datanodeid;
  @Column(columnDefinition = "integer")
  private long ordernum;
  @Column(columnDefinition = "varchar(20)")
  private String fieldcode;
  @Column(columnDefinition = "varchar(20)")
  private String fieldname;
  @Column(columnDefinition = "char(1)")
  private String direction;

  public String getOrderid() {
    return orderid;
  }

  public void setOrderid(String orderid) {
    this.orderid = orderid;
  }

  public String getDatanodeid() {
    return datanodeid;
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

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }
}
