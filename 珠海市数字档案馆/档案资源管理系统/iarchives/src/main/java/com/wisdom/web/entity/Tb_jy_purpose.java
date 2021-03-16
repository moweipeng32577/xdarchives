package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_jy_purpose {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String purposeid;
  @Column(columnDefinition = "varchar(100)")
  private String text;
  @Column(columnDefinition = "integer")
  private Integer sortsequence;

  public String getId() {
    return purposeid;
  }

  public void setId(String id) {
    this.purposeid = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getOrders() {
    return sortsequence;
  }

  public void setOrders(int orders) {
    this.sortsequence = orders;
  }
}
