package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_user_role")
public class Tb_user_role_sx {

  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String urid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String roleid;

  public String getUrid() {
    return urid;
  }

  public void setUrid(String urid) {
    this.urid = urid;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getRoleid() {
    return roleid;
  }

  public void setRoleid(String roleid) {
    this.roleid = roleid;
  }
}
