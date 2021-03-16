package com.wisdom.web.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 卷内目录实体类
 * Created by Rong on 2018/6/2.
 */
@Entity
public class ReportData {

    @Id
    private String id;
    private String entryid;
    private String pagename;
    private String count;
    private String filepahenum;
    private String bz;
    private String f49;
    private String f50;
    private String biscopyed;
    private String czjd;

    public String getBiscopyed() {
        return biscopyed;
    }

    public void setBiscopyed(String biscopyed) {
        this.biscopyed = biscopyed;
    }

    public String getEntryid() {
        return entryid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }
    public String getFilepahenum() {
        return filepahenum;
    }

    public String getBz() {
        return bz;
    }

    public String getF49() {
        return f49;
    }

    public String getF50() {
        return f50;
    }

    public void setFilepahenum(String filepahenum) {
        this.filepahenum = filepahenum;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public void setF49(String f49) {
        this.f49 = f49;
    }

    public void setF50(String f50) {
        this.f50 = f50;
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCzjd() {
        return czjd;
    }

    public void setCzjd(String czjd) {
        this.czjd = czjd;
    }
}
