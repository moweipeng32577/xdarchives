package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Leo on 2020/7/3 0003.
 */
@Entity
@Table(name = "tb_consult_statistics")
public class Tb_consult_statistics {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(30)")
    private String datetime;
    @Column(columnDefinition = "varchar(20)")
    private String company;   //单位
    @Column(columnDefinition = "varchar(20)")
    private String personal;  //个人
    @Column(columnDefinition = "varchar(20)")
    private String volume;    //卷
    @Column(columnDefinition = "varchar(20)")
    private String piece;     //件
    @Column(columnDefinition = "varchar(20)")
    private String tocopy;    //复印
    @Column(columnDefinition = "varchar(20)")
    private String prove;     //证明
    @Column(columnDefinition = "varchar(20)")
    private String type;     //类型
    @Column(columnDefinition = "int")
    private int orderby;     //类型

    public Tb_consult_statistics(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCompany() {
        return company==null?"0":company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPersonal() {
        return personal==null?"0":personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getVolume() {
        return volume==null?"0":volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPiece() {
        return piece==null?"0":piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getTocopy() {
        return tocopy==null?"0":tocopy;
    }

    public void setTocopy(String tocopy) {
        this.tocopy = tocopy;
    }

    public String getProve() {
        return prove==null?"0":prove;
    }

    public void setProve(String prove) {
        this.prove = prove;
    }

    public String getType() {
        return type==null?"0":type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrderby() {
        return orderby;
    }

    public void setOrderby(int orderby) {
        this.orderby = orderby;
    }
}
