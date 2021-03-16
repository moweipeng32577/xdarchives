package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 档案迁移
 * Created by Leo on 2020/8/12 0012.
 */
@Entity
public class Tb_archives_migrate {
    public static final String STATE_INSERT = "新增";
    public static final String STATE_TRANSFER_ING = "迁移中";
    public static final String STATE_TRANSFER_COMPLETE  = "迁移完成";

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String migid;
    @Column(columnDefinition = "varchar(300)")
    private String migratedesc;     //迁移说明
    @Column(columnDefinition = "varchar(30)")
    private String migratedate;     //迁移时间
    @Column(columnDefinition = "varchar(30)")
    private String migrateuser;     //迁移人
    @Column(columnDefinition = "varchar(10)")
    private String migratecount;    //迁移数量
    @Column(columnDefinition = "varchar(50)")
    private String migratestate;    //状态
    @Column(columnDefinition = "varchar(300)")
    private String remarks;         //备注

    public static String getStateInsert() {
        return STATE_INSERT;
    }

    public static String getStateTransferIng() {
        return STATE_TRANSFER_ING;
    }

    public static String getStateTransferComplete() {
        return STATE_TRANSFER_COMPLETE;
    }

    public String getMigid() {
        return migid;
    }

    public void setMigid(String migid) {
        this.migid = migid;
    }

    public String getMigratedesc() {
        return migratedesc;
    }

    public void setMigratedesc(String migratedesc) {
        this.migratedesc = migratedesc;
    }

    public String getMigratedate() {
        return migratedate;
    }

    public void setMigratedate(String migratedate) {
        this.migratedate = migratedate;
    }

    public String getMigrateuser() {
        return migrateuser;
    }

    public void setMigrateuser(String migrateuser) {
        this.migrateuser = migrateuser;
    }

    public String getMigratecount() {
        return migratecount;
    }

    public void setMigratecount(String migratecount) {
        this.migratecount = migratecount;
    }

    public String getMigratestate() {
        return migratestate;
    }

    public void setMigratestate(String migratestate) {
        this.migratestate = migratestate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
