package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 流水线环节表
 */
@Entity
public class Szh_assembly_flows {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  private String id;         //主键
  private String nodename;   //节点名
  private String modelname;  //模块名
  private Integer sorting;   //排序

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNodename() {
    return nodename;
  }

  public void setNodename(String nodename) {
    this.nodename = nodename;
  }

  public String getModelname() {
    return modelname;
  }

  public void setModelname(String modelname) {
    this.modelname = modelname;
  }

  public Integer getSorting() {
    return sorting;
  }

  public void setSorting(Integer sorting) {
    this.sorting = sorting;
  }
}
