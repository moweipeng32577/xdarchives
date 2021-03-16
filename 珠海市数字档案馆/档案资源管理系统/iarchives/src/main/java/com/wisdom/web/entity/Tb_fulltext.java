package com.wisdom.web.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_fulltext {
  @Id
  @Field
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String textid;
  @Field
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String eleid;
  @Field
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Field
  //@Column(columnDefinition = "mediumtext")//mysql
  //@Column(columnDefinition = "clob")//oracle
  private String filetext;
  @Field
  @Column(columnDefinition = "varchar(150)")
  private String filename;
  @Field
  //@Column(columnDefinition = "datetime")//mysql
  //@Column(columnDefinition = "date")//oracle
  private String updatetime;

  public String getTextid() {
    return textid;
  }

  public void setTextid(String textid) {
    this.textid = textid;
  }

  public String getEleid() {
    return eleid;
  }

  public void setEleid(String eleid) {
    this.eleid = eleid;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }

  public String getFiletext() {
    return filetext;
  }

  public void setFiletext(String filetext) {
    this.filetext = filetext;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getUpdatetime() {
    return updatetime;
  }

  public void setUpdatetime(String updatetime) {
    this.updatetime = updatetime;
  }
}
