package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by wjh
 * 档案元数据日志表
 */
@Entity
public class Tb_metadata_log {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;//主键
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "varchar(500)")
    private String depict;//操作描述
    @Column(columnDefinition = "varchar(50)")
    private String operateuser;//操作人
    @Column(columnDefinition = "varchar(50)")
    private String operateusername;//操作人名
    @Column(columnDefinition = "varchar(20)")
    private String operatetime;//操作时间
    @Column(columnDefinition = "varchar(20)")
    private String ip;//操作IP
    @Column(columnDefinition = "varchar(50)")
    private String type;//操作类型
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目id

    public Tb_metadata_log(){}

    public Tb_metadata_log(String depict, String type, String entryid){
        this.depict = depict;
        this.type = type;
        this.entryid = entryid;
    }

    public Tb_metadata_log(String depict, String operateuser, String operateusername, String operatetime, String ip, String type){
        this.depict = depict;
        this.operateuser =operateuser;
        this.operateusername = operateusername;
        this.operatetime = operatetime;
        this.ip = ip;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getOperateuser() {
        return operateuser;
    }

    public void setOperateuser(String operateuser) {
        this.operateuser = operateuser;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getOperateusername() {
        return operateusername;
    }

    public void setOperateusername(String operateusername) {
        this.operateusername = operateusername;
    }
}
