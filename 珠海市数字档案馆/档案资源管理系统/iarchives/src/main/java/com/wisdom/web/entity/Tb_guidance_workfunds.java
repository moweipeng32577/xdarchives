package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/9/28.
 */
@Entity
public class Tb_guidance_workfunds {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;  //年度
    @Column(columnDefinition = "varchar(30)")
    private String archivesfunds;  //档案经费（万元）
    @Column(columnDefinition = "varchar(200)")
    private String situatuion;  //落实情况
    @Column(columnDefinition = "varchar(500)")
    private String remark;  //备注
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;   //机构id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelectyear() {
        return selectyear;
    }

    public void setSelectyear(String selectyear) {
        this.selectyear = selectyear;
    }

    public String getArchivesfunds() {
        return archivesfunds;
    }

    public void setArchivesfunds(String archivesfunds) {
        this.archivesfunds = archivesfunds;
    }

    public String getSituatuion() {
        return situatuion;
    }

    public void setSituatuion(String situatuion) {
        this.situatuion = situatuion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }
}
