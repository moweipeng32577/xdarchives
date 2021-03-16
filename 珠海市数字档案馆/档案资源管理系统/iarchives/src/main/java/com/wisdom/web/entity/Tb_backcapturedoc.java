package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/10/30.
 */
@Entity
public class Tb_backcapturedoc {


    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "char(36)")
    private String nodeid;   //退回节点id
    @Column(columnDefinition = "varchar(300)")
    private String backreason;  //退回原因
    @Column(columnDefinition = "varchar(50)")
    private String backcount;  //退回数量
    @Column(columnDefinition = "varchar(30)")
    private String backtime;  //退回时间
    @Column(columnDefinition = "varchar(50)")
    private String backorgan;  //退回机构
    @Column(columnDefinition = "varchar(30)")
    private String backer;  //退回人

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getBackreason() {
        return backreason;
    }

    public void setBackreason(String backreason) {
        this.backreason = backreason;
    }

    public String getBackcount() {
        return backcount;
    }

    public void setBackcount(String backcount) {
        this.backcount = backcount;
    }

    public String getBacktime() {
        return backtime;
    }

    public void setBacktime(String backtime) {
        this.backtime = backtime;
    }

    public String getBackorgan() {
        return backorgan;
    }

    public void setBackorgan(String backorgan) {
        this.backorgan = backorgan;
    }

    public String getBacker() {
        return backer;
    }

    public void setBacker(String backer) {
        this.backer = backer;
    }
}
