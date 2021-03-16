package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 通知信息类
 * Created by Administrator on 2017/9/11.
 */
@Entity
public class Tb_Msg {
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String msgid;//信息id
    @Column(columnDefinition = "varchar(255)")
    private String msgtype;//信息类型
    @Column(columnDefinition = "varchar(255)")
    private String msgtypetext;//信息类型文本
    @Column(columnDefinition = "varchar(255)")
    private String msgtext;//信息内容
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String borrowmsgid;//查档详细条目id
    @Column(columnDefinition = "varchar(10)")
    private String urging;//是否为催办 1.自动 2.手动 3.手动+自动

    public String getUrging() {
        return urging;
    }

    public void setUrging(String urging) {
        this.urging = urging;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getMsgtypetext() {
        return msgtypetext;
    }

    public void setMsgtypetext(String msgtypetext) {
        this.msgtypetext = msgtypetext;
    }

    public String getMsgtext() {
        return msgtext;
    }

    public void setMsgtext(String msgtext) {
        this.msgtext = msgtext;
    }

    public String getBorrowmsgid() {
        return borrowmsgid;
    }

    public void setBorrowmsgid(String borrowmsgid) {
        this.borrowmsgid = borrowmsgid;
    }
}
