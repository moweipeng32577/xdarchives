package com.wisdom.secondaryDataSource.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by tanly on 2018/4/12 0012.
 */
@Entity
@Table(name = "t_csgappdoc_info")
public class AppDoc {
    @Id
    private String id;
    private String fileid;
    private String title;
    private String ext;
    private String url;
    private String isdeleted;
    private String prefix;//文件类型：附件、稿纸
    private String archiveEleFlag;//1:已接收；0：未接收

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(String isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getArchiveEleFlag() {
        return archiveEleFlag;
    }

    public void setArchiveEleFlag(String archiveEleFlag) {
        this.archiveEleFlag = archiveEleFlag;
    }
}
