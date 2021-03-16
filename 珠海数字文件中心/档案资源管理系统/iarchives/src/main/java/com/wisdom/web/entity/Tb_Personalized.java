package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 个性化实体
 */
@Entity
public class Tb_Personalized{
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String pid;
  @Column(columnDefinition = "varchar(30)")
  private String zts;
  @Column(columnDefinition = "varchar(50)")
  private String rgb;
  @Column(columnDefinition = "varchar(50)")
  private String anim;
  @Column(columnDefinition = "varchar(20)")
  private String ckb;
  @Column(columnDefinition = "varchar(50)")
  private String bg;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;
  @Column(columnDefinition = "varchar(50)")
  private String ztsize;
  @Column(columnDefinition = "varchar(300)")
  private String filepath;
  @Column(columnDefinition = "varchar(300)")
  private String title;

  public String getPath() {
    return filepath;
  }

  public void setPath(String path) {
    this.filepath = path;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getId() {
    return pid;
  }

  public void setId(String id) {
    this.pid = id;
  }

  public String getZts() {
    return zts;
  }

  public void setZts(String zts) {
    this.zts = zts;
  }

  public String getRgb() {
    return rgb;
  }

  public void setRgb(String rgb) {
    this.rgb = rgb;
  }

  public String getAnim() {
    return anim;
  }

  public void setAnim(String anim) {
    this.anim = anim;
  }

  public String getCkb() {
    return ckb;
  }

  public void setCkb(String ckb) {
    this.ckb = ckb;
  }

  public String getBg() {
    return bg;
  }

  public void setBg(String bg) {
    this.bg = bg;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getZtsize() {
    return ztsize;
  }

  public void setZtsize(String ztsize) {
    this.ztsize = ztsize;
  }
}
