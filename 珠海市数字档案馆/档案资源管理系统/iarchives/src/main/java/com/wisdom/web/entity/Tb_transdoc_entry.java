package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_transdoc_entry {

  public static final String STATUS_TRANSFOR = "待移交";
  public static final String STATUS_AUDIT = "待审核";
  public static final String STATUS_MOVE = "已入库";

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String teid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String docid;
  @Column(columnDefinition = "varchar(10)")
  private String status;
  @Column(columnDefinition = "char(36)")
  private String parententryid;

  public String getParententryid() {
    return parententryid;
  }

  public void setParententryid(String parententryid) {
    this.parententryid = parententryid;
  }

  public String getId() {
    return teid;
  }

  public void setId(String id) {
    this.teid = id;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }

  public String getDocid() {
    return docid;
  }

  public void setDocid(String docid) {
    this.docid = docid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
