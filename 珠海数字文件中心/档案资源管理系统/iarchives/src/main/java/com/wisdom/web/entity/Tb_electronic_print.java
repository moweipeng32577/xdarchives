package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/5/18.
 */
@Entity
public class Tb_electronic_print {
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;//主键
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目ID(UUID)
    @Column(columnDefinition = "char(36)")
    private String eleid;//电子文件id
    @Column(columnDefinition = "varchar(100)")
    private String borrowcode;
    @Column(columnDefinition = "varchar(30)")
    private String printstate;//打印类型
    @Column(columnDefinition = "varchar(300)")
    private String scopepage;//打印页数范围
    @Column(columnDefinition = "varchar(30)")
    private String filename;//电子文件名
    @Column(columnDefinition = "integer")
    private int copies;//打印份数
    @Column(columnDefinition = "varchar(30)")
    private String state;//权限

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScopepage() {
        return scopepage;
    }

    public void setScopepage(String scopepage) {
        this.scopepage = scopepage;
    }
    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getBorrowcode() {
        return borrowcode;
    }

    public void setBorrowcode(String borrowcode) {
        this.borrowcode = borrowcode;
    }

    public String getPrintstate() {
        return printstate;
    }

    public void setPrintstate(String printstate) {
        this.printstate = printstate;
    }
}
