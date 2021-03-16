package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 调档批次条目临时采集表对应表
 */
@Entity
public class Szh_callout_capture {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;
  private String calloutid;
  private String entryid;

  public Szh_callout_capture(){}

  public Szh_callout_capture(String calloutid,String entryid){
    this.calloutid = calloutid;
    this.entryid = entryid;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCalloutid() {
    return calloutid;
  }

  public void setCalloutid(String calloutid) {
    this.calloutid = calloutid;
  }

  public String getEntryid() {
    return entryid;
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }
}
