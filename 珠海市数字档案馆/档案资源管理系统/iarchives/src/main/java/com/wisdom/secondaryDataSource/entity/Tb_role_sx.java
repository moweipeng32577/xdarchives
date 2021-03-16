package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_role")
public class Tb_role_sx {
    @Id
    /*@GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")*/
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String roleid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String parentroleid;
    @Column(columnDefinition = "varchar(100)")
    private String rolename;
    @Column(columnDefinition = "varchar(500)")
    private String desciption;
    @Column(columnDefinition = "integer")
    private Integer status;
    @Column(columnDefinition = "varchar(50)")
    private String roletype;
    @Column(columnDefinition = "integer")
    private Integer sortsequence;

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getParentroleid() {
        return parentroleid;
    }

    public void setParentroleid(String parentroleid) {
        this.parentroleid = parentroleid;
    }

    public Integer getOrders() {
        return sortsequence;
    }

    public void setOrders(Integer orders) {
        this.sortsequence = orders;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRoletype() {
        return roletype;
    }

    public void setRoletype(String roletype) {
        this.roletype = roletype;
    }

    public Integer getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(Integer sortsequence) {
        this.sortsequence = sortsequence;
    }


    public Tb_role_sx(){}

    public Tb_role_sx(String roleid, String rolename){
        this.roleid = roleid;
        this.rolename = rolename;
    }
}
