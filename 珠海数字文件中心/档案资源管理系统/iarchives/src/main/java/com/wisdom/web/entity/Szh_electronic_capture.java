package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Szh_electronic_capture extends ElectronicBase {
  @Id
  @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
  @GeneratedValue(generator="idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String eleid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String entryid;
  @Column(columnDefinition = "char(32)")
  private String md5;
  @Column(columnDefinition = "varchar(20)")
  private String filefolder;
  @Column(columnDefinition = "varchar(200)")
  private String filename;
  @Column(columnDefinition = "varchar(20)")
  private String filetype;
  @Column(columnDefinition = "varchar(500)")
  private String filepath;
  @Column(columnDefinition = "varchar(20)")
  private String filesize;
  @Column(columnDefinition = "integer")
  private Integer sortsequence;

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

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFiletype() {
    return filetype;
  }

  public void setFiletype(String filetype) {
    this.filetype = filetype;
  }

  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public String getFilesize() {
    return filesize;
  }

  public void setFilesize(String filesize) {
    this.filesize = filesize;
  }

  public String getFolder() {
    return filefolder;
  }

  public void setFolder(String folder) {
    this.filefolder = folder;
  }

  public String getFilefolder() {
    return filefolder;
  }

  public void setFilefolder(String filefolder) {
    this.filefolder = filefolder;
  }

  public Integer getSortsequence() {
    return sortsequence;
  }

  public void setSortsequence(Integer sortsequence) {
    this.sortsequence = sortsequence;
  }

  public Map<String, Object> getMap(){
    Map<String, Object> map = new HashMap<>();
    map.put("eleid", getEleid());
    map.put("entryid", getEntryid());
    map.put("filename",getFilename());
    map.put("filepath", getFilepath());
    map.put("filesize", getFilesize());
    map.put("filetype", getFiletype());
    map.put("folder", getFilefolder());
    return map;
  }

}
