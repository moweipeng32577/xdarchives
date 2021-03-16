package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_showroom_date_person {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String sdpid;
  @Column(columnDefinition = "char(36)")
  private String showroomid;//展厅ID
  @Column(columnDefinition = "varchar(30)")
  private String visitingdate;//预约参观日期
  @Column(columnDefinition = "integer")
  private Integer audiences;//当天已预约参观人数

  public String getSdpid() {
    return sdpid;
  }

  public void setSdpid(String sdpid) {
    this.sdpid = sdpid;
  }

  public String getShowroomid() {
    return showroomid;
  }

  public void setShowroomid(String showroomid) {
    this.showroomid = showroomid;
  }

  public String getVisitingdate() {
    return visitingdate;
  }

  public void setVisitingdate(String visitingdate) {
    this.visitingdate = visitingdate;
  }

  public Integer getAudiences() {
    return audiences;
  }

  public void setAudiences(Integer audiences) {
    this.audiences = audiences;
  }
}
