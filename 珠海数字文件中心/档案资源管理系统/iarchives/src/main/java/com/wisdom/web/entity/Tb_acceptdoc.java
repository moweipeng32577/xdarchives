package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 接收管理-新增单据表
 * Created by Administrator on 2019/6/14.
 */
@Entity
public class Tb_acceptdoc {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String acceptdocid;
    @Column(columnDefinition = "varchar(20)")
    private String submitter;//提交人
    @Column(columnDefinition = "varchar(20)")
    private String accepter;//接收人
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;
    @Column(columnDefinition = "varchar(200)")
    private String organ;//接收机构
    @Column(columnDefinition = "varchar(20)")
    private String accepdate;//接收日期
    @Column(columnDefinition = "varchar(10)")
    private String sterilizing;//正在消毒
    @Column(columnDefinition = "varchar(10)")
    private String sterilized;//已消毒
    @Column(columnDefinition = "varchar(10)")
    private String finishstore;//已入库
    @Column(columnDefinition = "varchar(255)")
    private String docremark;//备注
    @Column(columnDefinition = "varchar(200)")
    private String submitorgan; //提交机构
    @Column(columnDefinition = "varchar(20)")
    private String submitdate;//提交日期
    @Column(columnDefinition = "int")
    private Integer archivenum;

    public String getAcceptdocid() {
        return acceptdocid;
    }

    public void setAcceptdocid(String acceptdocid) {
        this.acceptdocid = acceptdocid;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getAccepdate() {
        return accepdate;
    }

    public void setAccepdate(String accepdate) {
        this.accepdate = accepdate;
    }

    public String getSterilizing() {
        return sterilizing;
    }

    public void setSterilizing(String sterilizing) {
        this.sterilizing = sterilizing;
    }

    public String getSterilized() {
        return sterilized;
    }

    public void setSterilized(String sterilized) {
        this.sterilized = sterilized;
    }

    public String getFinishstore() {
        return finishstore;
    }

    public void setFinishstore(String finishstore) {
        this.finishstore = finishstore;
    }

    public String getDocremark() {
        return docremark;
    }

    public void setDocremark(String docremark) {
        this.docremark = docremark;
    }

    public String getSubmitorgan() {
        return submitorgan;
    }

    public void setSubmitorgan(String submitorgan) {
        this.submitorgan = submitorgan;
    }

    public String getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(String submitdate) {
        this.submitdate = submitdate;
    }

    public Integer getArchivenum() {
        return archivenum;
    }

    public void setArchivenum(Integer archivenum) {
        this.archivenum = archivenum;
    }
}
