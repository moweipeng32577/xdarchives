package com.wisdom.web.entity;

/**
 * Created by tanly on 2018/1/25 0025.
 */
public class BackupFile {
    private String filename;
    private String filesize;
    private String filetime;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFiletime() {
        return filetime;
    }

    public void setFiletime(String filetime) {
        this.filetime = filetime;
    }

    public BackupFile(String filename, String filesize, String filetime) {
        this.filename = filename;
        this.filesize = filesize;
        this.filetime = filetime;
    }
}
