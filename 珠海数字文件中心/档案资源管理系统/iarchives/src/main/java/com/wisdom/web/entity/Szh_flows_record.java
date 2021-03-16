package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Szh_flows_record {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;
  private String batchcode;
  private String calloutid;
  private String nodename;
  private String operator;
  private String operatetime;
  private String archivecode;
  private String status;

  public Szh_flows_record(){}

  public Szh_flows_record(String archivecode,String batchcode,String calloutid,String nodename,String operator,String operatetime,String status){
    this.archivecode = archivecode;
    this.calloutid = calloutid;
    this.nodename = nodename;
    this.operator = operator;
    this.operatetime = operatetime;
    this.batchcode = batchcode;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBatchcode() {
    return batchcode;
  }

  public void setBatchcode(String batchcode) {
    this.batchcode = batchcode;
  }

  public String getCalloutid() {
    return calloutid;
  }

  public void setCalloutid(String calloutid) {
    this.calloutid = calloutid;
  }

  public String getNodename() {
    return nodename;
  }

  public void setNodename(String nodename) {
    this.nodename = nodename;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getOperatetime() {
    return operatetime;
  }

  public void setOperatetime(String operatetime) {
    this.operatetime = operatetime;
  }

  public String getArchivecode() {
    return archivecode;
  }

  public void setArchivecode(String archivecode) {
    this.archivecode = archivecode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
