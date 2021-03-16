package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2018/9/27.
 */
@Entity
public class Tb_textopen {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String textopenid;
    @Column(columnDefinition = "char(36)")
    private String eleid;
    @Column(columnDefinition = "varchar(100)")
    private String borrowcode;
    @Column(columnDefinition = "varchar(20)")
    private String state;

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getState() {

        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBorrowcode() {

        return borrowcode;
    }

    public void setBorrowcode(String borrowcode) {
        this.borrowcode = borrowcode;
    }

    public String getEleid() {

        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getTextopenid() {

        return textopenid;
    }

    public void setTextopenid(String textopenid) {
        this.textopenid = textopenid;
    }

    @Column(columnDefinition = "char(36)")
    private String entryid;
}
