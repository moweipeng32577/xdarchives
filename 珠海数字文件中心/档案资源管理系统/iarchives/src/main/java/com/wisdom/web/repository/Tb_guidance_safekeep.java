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
public class Tb_guidance_safekeep {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(10)")
    private String isonlystoom;  //是否设置独立的档案库房
    @Column(columnDefinition = "varchar(10)")
    private String isonlypreview;  //是否设置独立的预览室
    @Column(columnDefinition = "varchar(200)")
    private String address;  //档案库房地址
    @Column(columnDefinition = "varchar(30)")
    private String area;  //档案库房面积（m2）
    @Column(columnDefinition = "integer")
    private String airnum;  //配备空调机数量（台）
    @Column(columnDefinition = "integer")
    private String dehumidifiernum;  //配备除湿机数量（台）
    @Column(columnDefinition = "integer")
    private String firenum;  //配备灭火器数量（个）
    @Column(columnDefinition = "integer")
    private String filingnum;  //档案柜（套）
    @Column(columnDefinition = "integer")
    private String denseframenum;  //密集架（列）
    @Column(columnDefinition = "integer")
    private String computernum;  //计算机（台）
    @Column(columnDefinition = "integer")
    private String othernum;  //其他设备（台）
    @Column(columnDefinition = "varchar(10)")
    private String issecurity;  //是否有防盗措施
    @Column(columnDefinition = "varchar(10)")
    private String islightmeasure;  //是否有防光措施
    @Column(columnDefinition = "varchar(10)")
    private String isbiologicalmeasure;  //是否有防有毒生物措施
    @Column(columnDefinition = "varchar(10)")
    private String ismetalcabinet;  //是否有存放全部档案的金属柜架
    @Column(columnDefinition = "varchar(10)")
    private String ischeckrecord;  //是否定时对库房设备检查并记录
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;   //机构id
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;  //年度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsonlystoom() {
        return isonlystoom;
    }

    public void setIsonlystoom(String isonlystoom) {
        this.isonlystoom = isonlystoom;
    }

    public String getIsonlypreview() {
        return isonlypreview;
    }

    public void setIsonlypreview(String isonlypreview) {
        this.isonlypreview = isonlypreview;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAirnum() {
        return airnum;
    }

    public void setAirnum(String airnum) {
        this.airnum = airnum;
    }

    public String getDehumidifiernum() {
        return dehumidifiernum;
    }

    public void setDehumidifiernum(String dehumidifiernum) {
        this.dehumidifiernum = dehumidifiernum;
    }

    public String getFirenum() {
        return firenum;
    }

    public void setFirenum(String firenum) {
        this.firenum = firenum;
    }

    public String getFilingnum() {
        return filingnum;
    }

    public void setFilingnum(String filingnum) {
        this.filingnum = filingnum;
    }

    public String getDenseframenum() {
        return denseframenum;
    }

    public void setDenseframenum(String denseframenum) {
        this.denseframenum = denseframenum;
    }

    public String getComputernum() {
        return computernum;
    }

    public void setComputernum(String computernum) {
        this.computernum = computernum;
    }

    public String getOthernum() {
        return othernum;
    }

    public void setOthernum(String othernum) {
        this.othernum = othernum;
    }

    public String getIssecurity() {
        return issecurity;
    }

    public void setIssecurity(String issecurity) {
        this.issecurity = issecurity;
    }

    public String getIslightmeasure() {
        return islightmeasure;
    }

    public void setIslightmeasure(String islightmeasure) {
        this.islightmeasure = islightmeasure;
    }

    public String getIsbiologicalmeasure() {
        return isbiologicalmeasure;
    }

    public void setIsbiologicalmeasure(String isbiologicalmeasure) {
        this.isbiologicalmeasure = isbiologicalmeasure;
    }

    public String getIsmetalcabinet() {
        return ismetalcabinet;
    }

    public void setIsmetalcabinet(String ismetalcabinet) {
        this.ismetalcabinet = ismetalcabinet;
    }

    public String getIscheckrecord() {
        return ischeckrecord;
    }

    public void setIscheckrecord(String ischeckrecord) {
        this.ischeckrecord = ischeckrecord;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getSelectyear() {
        return selectyear;
    }

    public void setSelectyear(String selectyear) {
        this.selectyear = selectyear;
    }
}
