package com.wisdom.web.entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_node {

  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String nodeid;
  @Column(columnDefinition = "varchar(200)")
  private String text;
  @Column(columnDefinition = "varchar(200)")
  private String nextid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String workid;
  @Column(columnDefinition = "varchar(200)")
  private String nexttext;
  @Column(columnDefinition = "integer")
  private Integer sortsequence;
  @Column(columnDefinition = "varchar(500)")
  private String desci;
  @Column(columnDefinition = "varchar(30)")
  private String approvescope; //审批范围

  public String getApprovescope() {
    return approvescope;
  }

  public void setApprovescope(String approvescope) {
    this.approvescope = approvescope;
  }

  public String getId() {
    return nodeid;
  }

  public void setId(String id) {
    this.nodeid = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getNextid() {
    return nextid;
  }

  public void setNextid(String nextid) {
    this.nextid = nextid;
  }

  public String getWorkid() {
    return workid;
  }

  public void setWorkid(String workid) {
    this.workid = workid;
  }

  public String getNexttext() {
    return nexttext;
  }

  public void setNexttext(String nexttext) {
    this.nexttext = nexttext;
  }

  public int getOrders() {
    return sortsequence;
  }

  public void setOrders(int orders) {
    this.sortsequence = orders;
  }

  public String getDesci() {
    return desci;
  }

  public void setDesci(String desci) {
    this.desci = desci;
  }
}
