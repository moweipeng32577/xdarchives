package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2017/11/1.
 * 专题详情
 */
@Entity
public class Tb_thematic_detail_make {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String thematicdetilid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String thematicid;
    @Column(columnDefinition = "varchar(1000)")
    private String title;
    @Column(columnDefinition = "varchar(20)")
    private String filedate;
    @Column(columnDefinition = "varchar(30)")
    private String responsibleperson;
    @Column(columnDefinition = "varchar(20)")
    private String filecode;
    @Column(columnDefinition = "varchar(100)")
    private String subheadings;
    @Column(columnDefinition = "varchar(500)")
    private String mediatext;

    public Tb_thematic_detail_make() {
    }

    public Tb_thematic_detail_make(String thematicid, String title, String date, String responsibleperson, String filecode, String subheadings, String mediatext) {
        this.thematicid = thematicid;
        this.title = title;
        this.filedate = date;
        this.responsibleperson = responsibleperson;
        this.filecode = filecode;
        this.subheadings = subheadings;
        this.mediatext = mediatext;
    }

    public String getMediatext() {
        return mediatext;
    }

    public void setMediatext(String mediatext) {
        this.mediatext = mediatext;
    }

    public String getThematicdetilid() {
        return thematicdetilid;
    }

    public void setThematicdetilid(String thematicdetilid) {
        this.thematicdetilid = thematicdetilid;
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

    public String getFiledate() {
        return filedate;
    }

    public void setFiledate(String filedate) {
        this.filedate = filedate;
    }

    public String getResponsibleperson() {
        return responsibleperson;
    }

    public void setResponsibleperson(String responsibleperson) {
        this.responsibleperson = responsibleperson;
    }

    public String getFilecode() {
        return filecode;
    }

    public void setFilecode(String filecode) {
        this.filecode = filecode;
    }

    public String getSubheadings() {
        return subheadings;
    }

    public void setSubheadings(String subheadings) {
        this.subheadings = subheadings;
    }

    @Override
    public String toString() {
        return "Tb_thematic_detail{" +
                "thematicdetilid='" + thematicdetilid + '\'' +
                ", thematicid='" + thematicid + '\'' +
                ", title='" + title + '\'' +
                ", filedate='" + filedate + '\'' +
                ", responsibleperson='" + responsibleperson + '\'' +
                ", filecode='" + filecode + '\'' +
                ", subheadings='" + subheadings + '\'' +
                '}';
    }
}
