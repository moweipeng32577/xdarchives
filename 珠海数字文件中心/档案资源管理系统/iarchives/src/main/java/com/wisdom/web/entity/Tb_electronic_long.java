package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanly on 2018/3/26 0026.
 */
@Entity
public class Tb_electronic_long {
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String eleid;//固化文件主键ID(UUID)
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目ID(UUID)
    @Column(columnDefinition = "char(32)")
    private String md5;//MD5码
    @Column(columnDefinition = "varchar(20)")
    private String filefolder;//文件所属文件夹
    @Column(columnDefinition = "varchar(200)")
    private String filename;//文件名称
    @Column(columnDefinition = "varchar(20)")
    private String filetype;//文件类型
    @Column(columnDefinition = "varchar(500)")
    private String filepath;//文件路径
    @Column(columnDefinition = "varchar(20)")
    private String filesize;//文件大小
    @Column(columnDefinition = "integer")
    private Integer sortsequence;//顺序号

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
        return filefolder;
    }

    public void setFolder(String folder) {
        this.filefolder = folder;
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

    public Integer getSequence() {
        return sortsequence;
    }

    public void setSequence(Integer sequence) {
        this.sortsequence = sequence;
    }

    public Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("eleid", getEleid());
        map.put("entryid", getEntryid());
        map.put("filename",getFilename());
        map.put("filepath", getFilepath());
        map.put("filesize", getFilesize());
        map.put("filetype", getFiletype());
        map.put("folder", getFolder());
        return map;
    }
}
