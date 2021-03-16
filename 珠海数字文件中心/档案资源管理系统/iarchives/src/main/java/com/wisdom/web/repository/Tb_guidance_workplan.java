package com.wisdom.web.repository;

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
public class Tb_guidance_workplan {



    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;  //年度
    @Column(columnDefinition = "varchar(10)")
    private String isyearplan;  //是否纳入年度计划
    @Column(columnDefinition = "varchar(10)")
    private String isyearconclusion;  //是否纳入年度总结
    @Column(columnDefinition = "varchar(10)")
    private String isyearaduit;  //是否纳入目标考核
    @Column(columnDefinition = "varchar(500)")
    private String attachment;  //附件
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

    public String getIsyearplan() {
        return isyearplan;
    }

    public void setIsyearplan(String isyearplan) {
        this.isyearplan = isyearplan;
    }

    public String getIsyearconclusion() {
        return isyearconclusion;
    }

    public void setIsyearconclusion(String isyearconclusion) {
        this.isyearconclusion = isyearconclusion;
    }

    public String getIsyearaduit() {
        return isyearaduit;
    }

    public void setIsyearaduit(String isyearaduit) {
        this.isyearaduit = isyearaduit;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }
}
