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
public class Tb_role_ele_function {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String usergroupid;
    @Column(columnDefinition = "integer")
    private String download;
    @Column(columnDefinition = "integer")
    private String downloadAll;
    @Column(columnDefinition = "integer")
    private String printt;
    @Column(columnDefinition = "integer")
    private String printBatch;
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
    @Column(columnDefinition = "varchar(50)")
    private String platform; // 管理平台or利用平台

    public Tb_role_ele_function(){

    }

    public Tb_role_ele_function(String usergroupid, String platform) {
        this.usergroupid = usergroupid;
        this.download = "0";
        this.downloadAll = "0";
        this.printt = "0";
        this.printBatch = "0";
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


    public String getUsergroupid() {
        return usergroupid;
    }

    public void setUsergroupid(String usergroupid) {
        this.usergroupid = usergroupid;
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

    public String getPrintBatch() {
        return printBatch;
    }

    public void setPrintBatch(String printBatch) {
        this.printBatch = printBatch;
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

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }


}
