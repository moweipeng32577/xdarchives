package com.wisdom.secondaryDataSource.entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by tanly on 2018/4/21 0023.
 */
@Entity
@Table(name = "tb_user_organ")
public class Tb_user_organ_sx {

  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String uoid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String organid;

  public String getId() {
    return uoid;
  }

  public void setId(String id) {
    this.uoid = id;
  }

  public String getUserid() {
    return userid==null?null:userid.trim();
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getOrganid() {
    return organid==null?null:organid.trim();
  }

  public void setOrganid(String organid) {
    this.organid = organid;
  }
}
