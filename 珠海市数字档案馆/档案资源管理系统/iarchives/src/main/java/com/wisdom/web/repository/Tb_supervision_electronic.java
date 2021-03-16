package com.wisdom.web.repository;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/10/13.
 */
@Entity
public class Tb_supervision_electronic {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String eleid;//电子文件主键ID(UUID)
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(32)")
    private String md5;//MD5码
    @Column(columnDefinition = "varchar(20)")
    private String filefolder;//文件所属文件夹
    @Column(columnDefinition = "varchar(400)")
    private String filename;//文件名称
    @Column(columnDefinition = "varchar(20)")
    private String filetype;//文件类型
    @Column(columnDefinition = "varchar(800)")
    private String filepath;//文件路径
    @Column(columnDefinition = "varchar(20)")
    private String filesize;//文件大小
    @Column(columnDefinition = "integer")
    private Integer sortsequence;//顺序号
    @Column(columnDefinition = "varchar(20)")
    private String savetype;//文件大小
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;//机构id
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;//年度

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFilefolder() {
        return filefolder;
    }

    public void setFilefolder(String filefolder) {
        this.filefolder = filefolder;
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

    public Integer getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(Integer sortsequence) {
        this.sortsequence = sortsequence;
    }

    public String getSavetype() {
        return savetype;
    }

    public void setSavetype(String savetype) {
        this.savetype = savetype;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getSelectyear() {
        return selectyear;
    }

    public void setSelectyear(String selectyear) {
        this.selectyear = selectyear;
    }

    public Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("eleid", getEleid());
        map.put("filename",getFilename());
        map.put("filepath", getFilepath());
        map.put("filesize", getFilesize());
        map.put("filetype", getFiletype());
        map.put("folder", getFilefolder());
        map.put("pages",getSortsequence());
        return map;
    }
}
