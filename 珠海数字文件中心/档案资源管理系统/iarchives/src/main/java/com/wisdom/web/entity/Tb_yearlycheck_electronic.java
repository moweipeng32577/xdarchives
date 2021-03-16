package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/10/14.
 */
@Entity
public class Tb_yearlycheck_electronic {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String eleid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String reportid;//年检报表id
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

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
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
}
