package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_right_organ")
//@XmlRootElement(name="Tb_right_organ")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Tb_right_organ_sx {

  public static String ORGAN_TYPE_UNIT = "unit";               //机构类型-单位
  public static String ORGAN_TYPE_DEPARTMENT = "department";  //机构类型-部门

  @Id
  /*@GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")*/
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36) comment '主键'")
  private String organid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36) comment '服务主键'")
  private String servicesid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36) comment '系统主键'")
  private String systemid;
  @Column(columnDefinition = "varchar(100) comment '服务名称'")
  private String servicesname;
  @Column(columnDefinition = "varchar(100) comment '系统名称'")
  private String systemname;
  @Column(columnDefinition = "varchar(100) comment '机构名称'")
  private String organname;
  @Column(columnDefinition = "varchar(100) comment '描述'")
  private String desciption;
  @Column(columnDefinition = "varchar(50) comment '机构类型'")
  private String organtype;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "varchar(36) comment '父机构主键'")
  private String parentid;
  @Column(columnDefinition = "varchar(36) comment '机构层级'")
  private String organlevel;
  @Column(columnDefinition = "char(1) comment '使用状态'")
  private String usestatus;
  @Column(columnDefinition = "integer comment '排序号'")
  private Integer sortsequence;
  @Column(columnDefinition = "varchar(100) comment '引用其他系统ID'")
  private String refid;
  @Column(columnDefinition = "varchar(50) comment '引用编码'")
  private String code;
  @Column(columnDefinition = "char(1) comment '是否初始化'")
  private String isinit;
//  public Integer getOrders() {
//    return sortsequence;
//  }
//
//  public void setOrders(Integer orders) {
//    this.sortsequence = orders;
//  }
public String getOrganlevel() {
  return organlevel;
}

  public void setOrganlevel(String organlevel) {
    this.organlevel = organlevel;
  }

  public String getOrganid() {
    return organid==null?null:organid.trim();
  }

  public void setOrganid(String organid) {
    this.organid = organid;
  }

  public String getServicesid() {
    return servicesid;
  }

  public void setServicesid(String servicesid) {
    this.servicesid = servicesid;
  }

  public String getSystemid() {
    return systemid;
  }

  public void setSystemid(String systemid) {
    this.systemid = systemid;
  }

  public String getServicesname() {
    return servicesname;
  }

  public void setServicesname(String servicesname) {
    this.servicesname = servicesname;
  }

  public String getSystemname() {
    return systemname;
  }

  public void setSystemname(String systemname) {
    this.systemname = systemname;
  }

  public String getOrganname() {
    return organname;
  }

  public void setOrganname(String organname) {
    this.organname = organname;
  }

  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

  public String getOrgantype() {
    return organtype;
  }

  public void setOrgantype(String organtype) {
    this.organtype = organtype;
  }

  public String getParentid() {
    return parentid==null?null:parentid.trim();
  }

  public void setParentid(String parentid) {
    this.parentid = parentid;
  }

  public String getUsestatus() {
    return usestatus;
  }

  public void setUsestatus(String usestatus) {
    this.usestatus = usestatus;
  }

  public String getRefid() {
    return refid;
  }

  /*public Integer getOrganlevel() {
    return organlevel;
  }

  public void setOrganlevel(Integer organlevel) {
    this.organlevel = organlevel;
  }
*/
  public Integer getSortsequence() {
    return sortsequence;
  }

  public void setSortsequence(Integer sortsequence) {
    this.sortsequence = sortsequence;
  }

  public void setRefid(String refid) {
    this.refid = refid;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getIsinit() {
    return isinit;
  }

  public void setIsinit(String isinit) {
    this.isinit = isinit;
  }
}
