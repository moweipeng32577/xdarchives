package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2017/11/1.
 * 专题制作
 */
@Entity
public class Tb_thematic {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String thematicid;
    @Column(columnDefinition = "varchar(1000)")
    private String title;
    @Column(columnDefinition = "varchar(1000)")
    private String thematiccontent;
    @Column(columnDefinition = "varchar(100)")
    private String filepath;
    @Column(columnDefinition = "decimal(20,0)")
    private Long filesize;
    @Column(columnDefinition = "varchar(30)")
    private String publishstate;
    @Column(columnDefinition = "varchar(100)")
    private String backgroundpath;
    @Column(columnDefinition = "varchar(30)")
    private String publishtime;
    @Column(columnDefinition = "varchar(255)")
    private String thematictypes;
    @Column(columnDefinition = "varchar(100)")
    private String approvetext;
    @Column(columnDefinition = "varchar(30)")
    private String createtime;
    @Column(columnDefinition = "varchar(30)")
    private String submitedtime;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getSubmitedtime() {
        return submitedtime;
    }

    public void setSubmitedtime(String submitedtime) {
        this.submitedtime = submitedtime;
    }

    public String getApprovetext() {
        return approvetext;
    }

    public void setApprovetext(String approvetext) {
        this.approvetext = approvetext;
    }

    public String getThematictypes() {
        return thematictypes;
    }

    public void setThematictypes(String thematictypes) {
        this.thematictypes = thematictypes;
    }

    public String getThematicid() {
        return thematicid;
    }

    public void setThematicid(String thematicid) {
        this.thematicid = thematicid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThematiccontent() {
        return thematiccontent;
    }

    public void setThematiccontent(String thematiccontent) {
        this.thematiccontent = thematiccontent;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public String getPublishstate() {
        return publishstate;
    }

    public void setPublishstate(String publishstate) {
        this.publishstate = publishstate;
    }

    public String getBackgroundpath() {
        return backgroundpath;
    }

    public void setBackgroundpath(String backgroundpath) {
        this.backgroundpath = backgroundpath;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    @Override
    public String toString() {
        return "Tb_thematic{" +
                "thematicid='" + thematicid + '\'' +
                ", title='" + title + '\'' +
                ", thematiccontent='" + thematiccontent + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filesize=" + filesize +
                ", publishstate='" + publishstate + '\'' +
                ", backgroundpath='" + backgroundpath + '\'' +
                ", publishtime='" + publishtime + '\'' +
                ", thematictypes='" + thematictypes + '\'' +
                '}';
    }
}
