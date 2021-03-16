package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 收藏记录、入库添加记录表
 */
@Entity
public class Tb_entry_bookmarks {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String bookmarkid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String userid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Column(columnDefinition = "varchar(20)")
  private String addstate;//收藏为0,入库为1，出库为2,目录收藏为3,馆库查询收藏4，目录中心声像收藏5, 编研管理系统声像收藏6

  @Column(columnDefinition = "nvarchar(30)")
  private String modify;

  public String getModify() {
    return modify;
  }

  public void setModify(String modify) {
    this.modify = modify;
  }

  public String getAddstate() {
    return addstate;
  }

  public void setAddstate(String addstate) {
    this.addstate = addstate;
  }

  public String getBookmarkid() {
    return bookmarkid;
  }

  public void setBookmarkid(String bookmarkid) {
    this.bookmarkid = bookmarkid;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getEntryid() {
    return entryid==null?null:entryid.trim();
  }

  public void setEntryid(String entryid) {
    this.entryid = entryid;
  }
}
