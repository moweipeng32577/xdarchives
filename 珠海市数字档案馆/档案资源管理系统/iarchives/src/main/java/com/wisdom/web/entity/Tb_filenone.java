package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 查无此档登记表
 */
@Entity
public class Tb_filenone {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String filenoneid;
  private String docid;
  private String filenum;
  private String recodetype;
  private String organname;
  private String personname;
  private String time;
  private String manname;
  private String mancard;
  private Integer mandie;
  private String womanname;
  private String womancard;
  private Integer womandie;
  private String starttime;
  private String endtime;
  private Integer printnumber;


  public String getFilenoneid() {
    return filenoneid;
  }

  public void setFilenoneid(String filenoneid) {
    this.filenoneid = filenoneid;
  }

  public String getDocid() {
    return docid;
  }

  public void setDocid(String docid) {
    this.docid = docid;
  }

  public String getFilenum() {
    return filenum;
  }

  public void setFilenum(String filenum) {
    this.filenum = filenum;
  }

  public String getRecodetype() {
    return recodetype;
  }

  public void setRecodetype(String recodetype) {
    this.recodetype = recodetype;
  }

  public String getPersonname() {
    return personname;
  }

  public void setPersonname(String personname) {
    this.personname = personname;
  }

  public String getOrganname() {
    return organname;
  }

  public void setOrganname(String organname) {
    this.organname = organname;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getManname() {
    return manname;
  }

  public void setManname(String manname) {
    this.manname = manname;
  }

  public String getMancard() {
    return mancard;
  }

  public void setMancard(String mancard) {
    this.mancard = mancard;
  }

  public Integer getMandie() {
    return mandie;
  }

  public void setMandie(Integer mandie) {
    this.mandie = mandie;
  }

  public String getWomanname() {
    return womanname;
  }

  public void setWomanname(String womanname) {
    this.womanname = womanname;
  }

  public String getWomancard() {
    return womancard;
  }

  public void setWomancard(String womancard) {
    this.womancard = womancard;
  }

  public Integer getWomandie() {
    return womandie;
  }

  public void setWomandie(Integer womandie) {
    this.womandie = womandie;
  }

  public String getStarttime() {
    return starttime;
  }

  public void setStarttime(String starttime) {
    this.starttime = starttime;
  }

  public String getEndtime() {
    return endtime;
  }

  public void setEndtime(String endtime) {
    this.endtime = endtime;
  }

  public Integer getPrintnumber() {
    return printnumber;
  }

  public void setPrintnumber(Integer printnumber) {
    this.printnumber = printnumber;
  }

}
