package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Tb_showroom {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String showroomid;
  @Column(columnDefinition = "varchar(255)")
  private String title;//展厅名称
  @Column(columnDefinition = "varchar(1000)")
  private String content;//展厅介绍
  @Column(columnDefinition = "varchar(255)")
  private String appendix;//展厅附件
  @Column(columnDefinition = "char(1)")
  private String flag;//展厅状态  1 表示已满  0 正常使用  2 维护中
  @Column(columnDefinition = "integer")
  private Integer audiences;//每日参观人数
  @Column(columnDefinition = "integer")
  private Integer sequence;//展厅序号
  @Transient
  private Integer yyAudiences;//预约人数

  public String getShowroomid() {
    return showroomid;
  }

  public void setShowroomid(String showroomid) {
    this.showroomid = showroomid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getAppendix() {
    return appendix;
  }

  public void setAppendix(String appendix) {
    this.appendix = appendix;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public Integer getAudiences() {
    return audiences;
  }

  public void setAudiences(Integer audiences) {
    this.audiences = audiences;
  }

  public Integer getSequence() {
    return sequence;
  }

  public void setSequence(Integer sequence) {
    this.sequence = sequence;
  }

  public Integer getYyAudiences() {
    return yyAudiences;
  }

  public void setYyAudiences(Integer yyAudiences) {
    this.yyAudiences = yyAudiences;
  }
}
