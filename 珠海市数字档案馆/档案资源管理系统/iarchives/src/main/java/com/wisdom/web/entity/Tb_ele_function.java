package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/8/13.
 */
@Entity
public class Tb_ele_function {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String userid;
    @Column(columnDefinition = "integer")
    private String download;
    @Column(columnDefinition = "integer")
    private String downloadAll;
    @Column(columnDefinition = "integer")
    private String printt;
    @Column(columnDefinition = "varchar(50)")
    private String platform; // 管理平台or利用平台
    @Column(columnDefinition = "integer")
    private String printbatch;
    @Column(columnDefinition = "integer")
    private String upload;
    @Column(columnDefinition = "integer")
    private String del;
    @Column(columnDefinition = "integer")
    private String up;
    @Column(columnDefinition = "integer")
    private String down;
    @Column(columnDefinition = "integer")
    private String lookhistory;
    public Tb_ele_function(){

    }

    public String getPrintBatch() {
        return printbatch;
    }

    public void setPrintBatch(String printbatch) {
        this.printbatch = printbatch;
    }

    public Tb_ele_function(String userid, String platform) {
        this.userid = userid;
        this.download = "0";
        this.downloadAll = "0";
        this.printt = "0";
        this.printbatch = "0";
        this.upload = "0";
        this.del = "0";
        this.up = "0";
        this.down = "0";
        this.lookhistory = "0";
        this.platform = platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownloadAll() {
        return downloadAll;
    }

    public void setDownloadAll(String downloadAll) {
        this.downloadAll = downloadAll;
    }

    public String getPrint() {
        return printt;
    }

    public void setPrint(String print) {
        this.printt = print;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getLookhistory() {
        return lookhistory;
    }

    public void setLookhistory(String lookhistory) {
        this.lookhistory = lookhistory;
    }
}
