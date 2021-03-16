package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/2/21.
 */
@Entity
public class Tb_electronic_version {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;//电子文件保存主键ID(UUID)
    @Column(columnDefinition = "char(36)")
    private String eleid;//电子文件ID(UUID)
    @Column(columnDefinition = "varchar(50)")
    private String version;//版本
    @Column(columnDefinition = "varchar(50)")
    private String createtime;//创建时间
    @Column(columnDefinition = "varchar(20)")
    private String filesize;//电子文件大小
    @Column(columnDefinition = "varchar(50)")
    private String createname;//创建者账号
    @Column(columnDefinition = "varchar(500)")
    private String remark;//备注
    @Column(columnDefinition = "varchar(500)")
    private String filepath;//文件路径
    @Column(columnDefinition = "varchar(500)")
    private String filename;//文件名
    @Column(columnDefinition = "varchar(20)")
    private String filetype;//文件类型
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目id

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

}
