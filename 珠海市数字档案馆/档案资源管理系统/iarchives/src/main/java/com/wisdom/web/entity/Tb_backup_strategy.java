package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_backup_strategy {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String strategyid;
  @Column(columnDefinition = "varchar(100)")
  private String backupfrequency;
  @Column(columnDefinition = "varchar(100)")
  private String backuptime;
  @Column(columnDefinition = "varchar(100)")
  private String backuptype;
  @Column(columnDefinition = "varchar(100)")
  private String backupcontent;
  @Column(columnDefinition = "varchar(100)")
  private String cron;

  public String getId() {
    return strategyid;
  }

  public void setId(String id) {
    this.strategyid = id;
  }

  public String getBackupfrequency() {
    return backupfrequency;
  }

  public void setBackupfrequency(String backupfrequency) {
    this.backupfrequency = backupfrequency;
  }

  public String getBackuptime() {
    return backuptime;
  }

  public void setBackuptime(String backuptime) {
    this.backuptime = backuptime;
  }

  public String getBackuptype() {
    return backuptype;
  }

  public void setBackuptype(String backuptype) {
    this.backuptype = backuptype;
  }

  public String getBackupcontent() {
    return backupcontent;
  }

  public void setBackupcontent(String backupcontent) {
    this.backupcontent = backupcontent;
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }
}
