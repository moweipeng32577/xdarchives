package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 * 数字证书信息表  Zengdw  20200613
 */
@Entity
public class Tb_ca {

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String cid;
  @Column(columnDefinition = "char(50)")
  private String caid;//证书ID
  @Column(columnDefinition = "varchar(2000)")
  private String certcode;//证书BASE64码
  @Column(columnDefinition = "varchar(20000)")
  private String signcode;//签章BASE64码

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public String getCaid() {
    return caid;
  }

  public void setCaid(String caid) {
    this.caid = caid;
  }

  public String getCertcode() {
    return certcode;
  }

  public void setCertcode(String certcode) {
    this.certcode = certcode;
  }

  public String getSigncode() {
    return signcode;
  }

  public void setSigncode(String signcode) {
    this.signcode = signcode;
  }
}
