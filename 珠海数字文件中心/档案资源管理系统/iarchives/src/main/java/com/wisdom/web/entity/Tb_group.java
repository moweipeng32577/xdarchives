package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_group {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String groupid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String parentgroupid;
  @Column(columnDefinition = "varchar(100)")
  private String groupname;
  @Column(columnDefinition = "varchar(500)")
  private String desciption;
  @Column(columnDefinition = "integer")
  private Long status;
  @Column(columnDefinition = "varchar(50)")
  private String grouptype;
  public String getGroupid() {
    return groupid;
  }

  public void setGroupid(String groupid) {
    this.groupid = groupid;
  }

  public String getParentgroupid() {
    return parentgroupid;
  }

  public void setParentgroupid(String parentgroupid) {
    this.parentgroupid = parentgroupid;
  }

  public String getGroupname() {
    return groupname;
  }

  public void setGroupname(String groupname) {
    this.groupname = groupname;
  }

  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getGrouptype() {
    return grouptype;
  }

  public void setGrouptype(String grouptype) {
    this.grouptype = grouptype;
  }
}
