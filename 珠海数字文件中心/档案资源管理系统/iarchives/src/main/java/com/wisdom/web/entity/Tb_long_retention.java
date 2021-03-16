package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2019/12/27.
 * 长期保管包
 */
@Entity
public class Tb_long_retention {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;//主键
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目id
    @Column(columnDefinition = "char(36)")
    private String receiveid;//数据包id
    @Column(columnDefinition = "varchar(20)")
    private String checkstatus;//检测状态
    @Column(columnDefinition = "varchar(1000)")
    private String authenticity;//准确性
    @Column(columnDefinition = "varchar(1000)")
    private String integrity;//完整性
    @Column(columnDefinition = "varchar(1000)")
    private String usability;//可用性
    @Column(columnDefinition = "varchar(1000)")
    private String safety;//安全性
    @Column(columnDefinition = "char(36)")
    private String parententryid;//父级条目id

    public String getParententryid() {
        return parententryid;
    }

    public void setParententryid(String parententryid) {
        this.parententryid = parententryid;
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

    public String getReceiveid() {
        return receiveid;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getAuthenticity() {
        return authenticity;
    }

    public void setAuthenticity(String authenticity) {
        this.authenticity = authenticity;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public String getUsability() {
        return usability;
    }

    public void setUsability(String usability) {
        this.usability = usability;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }
}
