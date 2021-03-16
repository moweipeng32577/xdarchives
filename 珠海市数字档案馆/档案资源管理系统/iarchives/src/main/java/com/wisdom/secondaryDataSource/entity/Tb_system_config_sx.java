package com.wisdom.secondaryDataSource.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_system_config")
public class Tb_system_config_sx {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    @ExcelIgnore
    private String configid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    @ExcelIgnore
    private String parentconfigid;
    @Column(columnDefinition = "varchar(50)")
    @ExcelProperty("参数名称")
    private String configcode;
    @Column(columnDefinition = "varchar(50)")
    @ExcelProperty("参数值")
    private String configvalue;
    @Column(columnDefinition = "integer")
    @ExcelProperty("排序")
    private Integer sortsequence;

    public void setConfigcode(String configcode) {
        this.configcode = configcode;
    }

    public void setConfigvalue(String configvalue) {
        this.configvalue = configvalue;
    }

    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid;
    }

    public String getParentconfigid() {
        return parentconfigid;
    }

    public void setParentconfigid(String parentconfigid) {
        this.parentconfigid = parentconfigid;
    }

    public String getCode() {
        return configcode;
    }

    public void setCode(String code) {
        this.configcode = code;
    }

    public String getValue() {
        return configvalue;
    }

    public void setValue(String value) {
        this.configvalue = value;
    }

    public Integer getSequence() {
        return sortsequence;
    }

    public void setSequence(Integer sequence) {
        this.sortsequence = sequence;
    }

    public String getConfigcode() {
        return configcode;
    }

    public String getConfigvalue() {
        return configvalue;
    }
}
