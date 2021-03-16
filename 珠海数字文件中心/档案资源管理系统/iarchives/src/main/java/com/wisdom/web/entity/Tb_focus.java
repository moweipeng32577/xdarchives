package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_focus {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String focusid;
  @Column(columnDefinition = "varchar(200)")
  private String focuspath;
  @Column(columnDefinition = "varchar(500)")
  private String title;

  public Integer getSortsequence() {
    return sortsequence;
  }

  public void setSortsequence(Integer sortsequence) {
    this.sortsequence = sortsequence;
  }

  @Column(columnDefinition = "integer")
  private Integer sortsequence;

  public String getId() {
    return focusid;
  }

  public void setId(String id) {
    this.focusid = id;
  }

  public String getPath() {
    return focuspath;
  }

  public void setPath(String path) {
    this.focuspath = path;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
