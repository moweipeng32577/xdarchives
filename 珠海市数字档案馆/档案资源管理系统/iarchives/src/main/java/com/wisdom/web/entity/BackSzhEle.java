package com.wisdom.web.entity;

/**
 * Created by Administrator on 2019/9/20.
 */
public class BackSzhEle {

    private String id;   //文件id
    private String filename;  //文件名
    private String status;   //审核状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
