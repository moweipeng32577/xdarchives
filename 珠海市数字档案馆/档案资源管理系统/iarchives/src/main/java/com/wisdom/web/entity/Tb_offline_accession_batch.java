package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 离线接收（批次表）
 */
@Entity
public class Tb_offline_accession_batch {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String batchid;         //主键
    private String batchname;  //批次名
    private String nodeid;    //节点id
    private String batchcode;  //批次号
    private String workname;  //交接工作名称
    private String workvalue;    //内容描述
    private String elenum;         //移交电子档案数量
    private String datanum;  //移交数据量
    private String innercode;    //载体起止顺序号
    private String datatype;         //移交载体类型规格
    private String checkvalue;  //检验内容
    private String unitname;    //单位名称
    private String tfterunit;         //移交单位


    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getBatchname() {
        return batchname;
    }

    public void setBatchname(String batchname) {
        this.batchname = batchname;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getWorkname() {
        return workname;
    }

    public void setWorkname(String workname) {
        this.workname = workname;
    }

    public String getWorkvalue() {
        return workvalue;
    }

    public void setWorkvalue(String workvalue) {
        this.workvalue = workvalue;
    }

    public String getElenum() {
        return elenum;
    }

    public void setElenum(String elenum) {
        this.elenum = elenum;
    }

    public String getDatanum() {
        return datanum;
    }

    public void setDatanum(String datanum) {
        this.datanum = datanum;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getCheckvalue() {
        return checkvalue;
    }

    public void setCheckvalue(String checkvalue) {
        this.checkvalue = checkvalue;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getTfterunit() {
        return tfterunit;
    }

    public void setTfterunit(String tfterunit) {
        this.tfterunit = tfterunit;
    }

    public Tb_offline_accession_batch(String batchname, String nodeid, String batchcode, String workname, String workvalue, String elenum, String datanum, String innercode, String datatype, String checkvalue, String unitname, String tfterunit) {
        this.batchname = batchname;
        this.nodeid = nodeid;
        this.batchcode = batchcode;
        this.workname = workname;
        this.workvalue = workvalue;
        this.elenum = elenum;
        this.datanum = datanum;
        this.innercode = innercode;
        this.datatype = datatype;
        this.checkvalue = checkvalue;
        this.unitname = unitname;
        this.tfterunit = tfterunit;
    }
    public Tb_offline_accession_batch(){

    }
}
