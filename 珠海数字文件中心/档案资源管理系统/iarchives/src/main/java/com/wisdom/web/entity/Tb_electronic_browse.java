package com.wisdom.web.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Tb_electronic_browse {
  @Id
  private String eleid;//电子文件主键ID(UUID)
  private String entryid;//条目ID(UUID)
  private String md5;//MD5码
  private String folder;//文件所属文件夹
  private String filename;//文件名称
  private String filetype;//文件类型
  private String filepath;//文件路径
  private String filesize;//文件大小
  private Long sequence;//顺序号

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

  public String getFolder() {
    return folder;
  }

  public void setFolder(String folder) {
    this.folder = folder;
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

  public Long getSequence() {
    return sequence;
  }

  public void setSequence(Long sequence) {
    this.sequence = sequence;
  }

  public Map<String, Object> getMap(){
    Map<String, Object> map = new HashMap<>();
    map.put("eleid", getEleid());
    map.put("entryid", getEntryid());
    map.put("filename",getFilename());
    map.put("filepath", getFilepath());
    map.put("filesize", getFilesize());
    map.put("filetype", getFiletype());
    return map;
  }
}
