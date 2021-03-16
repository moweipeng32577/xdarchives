package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2021/1/6.
 */
@Entity
public class Tb_project_doc {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "char(36)")
    private String fpspmanid;//审批人
    @Column(columnDefinition = "char(36)")
    private String spnodeid;//审批环节
    @Column(columnDefinition = "char(36)")
    private String projectid;//项目管理表id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFpspmanid() {
        return fpspmanid;
    }

    public void setFpspmanid(String fpspmanid) {
        this.fpspmanid = fpspmanid;
    }

    public String getSpnodeid() {
        return spnodeid;
    }

    public void setSpnodeid(String spnodeid) {
        this.spnodeid = spnodeid;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }
}
