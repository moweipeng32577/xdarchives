package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "lot_device_diagnose")
public class DeviceDiagnose {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String id;//id

    private String diagnosename;//故障名

    private String diagnosecode;//故障号

    private String faultcause;//故障描述

    private String suggest;//建议

    private String createdate;//创建时间

    private String modifydate;//修改时间


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiagnosename() {
        return diagnosename;
    }

    public void setDiagnosename(String diagnosename) {
        this.diagnosename = diagnosename;
    }

    public String getDiagnosecode() {
        return diagnosecode;
    }

    public void setDiagnosecode(String diagnosecode) {
        this.diagnosecode = diagnosecode;
    }

    public String getFaultcause() {
        return faultcause;
    }

    public void setFaultcause(String faultcause) {
        this.faultcause = faultcause;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getModifydate() {
        return modifydate;
    }

    public void setModifydate(String modifydate) {
        this.modifydate = modifydate;
    }
}
