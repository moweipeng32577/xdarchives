package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 系统常量配置实体
 *
 * Created by rong on 2020/5/6
 */
@Entity
public class Tb_system_constants {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36) comment '主键'")
    private String constantid;
    @Column(columnDefinition = "varchar(50) comment '常量类型'")
    private String constanttype;
    @Column(columnDefinition = "varchar(100) comment '常量编码'")
    private String constantcode;
    @Column(columnDefinition = "varchar(100) comment '常量值'")
    private String constantvalue;
    @Column(columnDefinition = "varchar(100) comment '备注描述'")
    private String describe;

    public String getConstantid() {
        return constantid;
    }

    public void setConstantid(String constantid) {
        this.constantid = constantid;
    }

    public String getConstanttype() {
        return constanttype;
    }

    public void setConstanttype(String constanttype) {
        this.constanttype = constanttype;
    }

    public String getConstantcode() {
        return constantcode;
    }

    public void setConstantcode(String constantcode) {
        this.constantcode = constantcode;
    }

    public String getConstantvalue() {
        return constantvalue;
    }

    public void setConstantvalue(String constantvalue) {
        this.constantvalue = constantvalue;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
