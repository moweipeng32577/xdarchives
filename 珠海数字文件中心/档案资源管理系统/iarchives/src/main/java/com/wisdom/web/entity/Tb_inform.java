package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Tb_inform {

  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String informid;
  @Column(columnDefinition = "varchar(300)")
  private String title;
  //@Column(columnDefinition = "mediumtext")//mysql
  //@Column(columnDefinition = "clob")//oracle
  private String text;
  @Column(columnDefinition = "varchar(50)")
  private String postedman;
  //@Column(columnDefinition = "datetime")//mysql
  //@Column(columnDefinition = "date")//oracle
  private Date limitdate;
  //@Column(columnDefinition = "datetime")//mysql
  //@Column(columnDefinition = "date")//oracle
  private Date informdate;
  @Column(columnDefinition = "varchar(50)")
  private String posteduser;
  @Column(columnDefinition = "varchar(50)")
  private String postedusergroup;
  @Column(columnDefinition = "number(10)")
  private Integer stick;

  public String getPosteduser() {
    return posteduser;
  }

  public void setPosteduser(String posteduser) {
    this.posteduser = posteduser;
  }

  public String getPostedusergroup() {
    return postedusergroup;
  }

  public void setPostedusergroup(String postedusergroup) {
    this.postedusergroup = postedusergroup;
  }

  public String getId() {
    return informid;
  }

  public void setId(String id) {
    this.informid = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getPostedman() {
    return postedman;
  }

  public void setPostedman(String postedman) {
    this.postedman = postedman;
  }

  public Date getLimitdate() {
    return limitdate;
  }

  public void setLimitdate(Date limitdate) {
    this.limitdate = limitdate;
  }

  public Date getInformdate() {
    return informdate;
  }

  public void setInformdate(Date informdate) {
    this.informdate = informdate;
  }

  public Integer getStick() {
    return stick;
  }

  public void setStick(Integer stick) {
    this.stick = stick;
  }
}
