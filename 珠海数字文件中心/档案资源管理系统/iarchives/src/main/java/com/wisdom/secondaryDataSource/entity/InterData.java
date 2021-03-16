package com.wisdom.secondaryDataSource.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

/**
 * Created by tanly on 2018/4/12 0012.
 */
@Entity
@Table(name = "t_bankoa_ygd")
public class InterData {
    @Id
    private String id;
    private String fileid;
    private String title;
    private String drafter;
    private String draftUnitcode;
    private String draftUnitname;
    private String typeurl;
    private String wjlx;
    private String fileno;
    private String nd;
    private String orgname;
    private String gdflag;
    private String extend1;
    private String extend2;
    private String extend3;
    private String gdtype;
    private String fwZs;//主送单位
    private String fwCs;//抄送单位
    private String fwYjr;//发文类别
    private String fwSecret;//文件密级
    private String fwFileno;//发文字号
    private String fwSyscode;//行文类型
    private String swLwdw;//来文单位
    private String swSwdw;//收文单位
    private String swFileno;//收文编号
    private String swSwdate;//收文日期
    private String swSecret;//文件密级
    private String swExigency;//紧急程度
    private String swZbdw;//主办部门
    private String swYbjlb;//行文类型
    private String qbTypes;//签报类型
    private String qbBm;//签报部门
    private String qbEmergency;//签报紧急程度
    private String qbPerson;//拟稿人
    private String qbQbno;//签报字号
    private String qbMainsender;//主送人
    private String qbSecret;//文件密级
    private String qbNgdate;//拟稿日期
    private String docjkStatus;//接口状态反馈，0：未接收；1：已接收。暂未使用
    private java.sql.Date oaClsj;//档案系统反馈的处理状态时间
    private String swWjbh;//文件编号
    private String archiveFlag;//1:已接收；0：未接收

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDrafter() {
        return drafter;
    }

    public void setDrafter(String drafter) {
        this.drafter = drafter;
    }

    public String getDraftUnitcode() {
        return draftUnitcode;
    }

    public void setDraftUnitcode(String draftUnitcode) {
        this.draftUnitcode = draftUnitcode;
    }

    public String getDraftUnitname() {
        return draftUnitname;
    }

    public void setDraftUnitname(String draftUnitname) {
        this.draftUnitname = draftUnitname;
    }

    public String getTypeurl() {
        return typeurl;
    }

    public void setTypeurl(String typeurl) {
        this.typeurl = typeurl;
    }

    public String getWjlx() {
        return wjlx;
    }

    public void setWjlx(String wjlx) {
        this.wjlx = wjlx;
    }

    public String getFileno() {
        return fileno;
    }

    public void setFileno(String fileno) {
        this.fileno = fileno;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getGdflag() {
        return gdflag;
    }

    public void setGdflag(String gdflag) {
        this.gdflag = gdflag;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getGdtype() {
        return gdtype;
    }

    public void setGdtype(String gdtype) {
        this.gdtype = gdtype;
    }

    public String getFwZs() {
        return fwZs;
    }

    public void setFwZs(String fwZs) {
        this.fwZs = fwZs;
    }

    public String getFwCs() {
        return fwCs;
    }

    public void setFwCs(String fwCs) {
        this.fwCs = fwCs;
    }

    public String getFwYjr() {
        return fwYjr;
    }

    public void setFwYjr(String fwYjr) {
        this.fwYjr = fwYjr;
    }

    public String getFwSecret() {
        return fwSecret;
    }

    public void setFwSecret(String fwSecret) {
        this.fwSecret = fwSecret;
    }

    public String getFwFileno() {
        return fwFileno;
    }

    public void setFwFileno(String fwFileno) {
        this.fwFileno = fwFileno;
    }

    public String getFwSyscode() {
        return fwSyscode;
    }

    public void setFwSyscode(String fwSyscode) {
        this.fwSyscode = fwSyscode;
    }

    public String getSwLwdw() {
        return swLwdw;
    }

    public void setSwLwdw(String swLwdw) {
        this.swLwdw = swLwdw;
    }

    public String getSwSwdw() {
        return swSwdw;
    }

    public void setSwSwdw(String swSwdw) {
        this.swSwdw = swSwdw;
    }

    public String getSwFileno() {
        return swFileno;
    }

    public void setSwFileno(String swFileno) {
        this.swFileno = swFileno;
    }

    public String getSwSwdate() {
        return swSwdate;
    }

    public void setSwSwdate(String swSwdate) {
        this.swSwdate = swSwdate;
    }

    public String getSwSecret() {
        return swSecret;
    }

    public void setSwSecret(String swSecret) {
        this.swSecret = swSecret;
    }

    public String getSwExigency() {
        return swExigency;
    }

    public void setSwExigency(String swExigency) {
        this.swExigency = swExigency;
    }

    public String getSwZbdw() {
        return swZbdw;
    }

    public void setSwZbdw(String swZbdw) {
        this.swZbdw = swZbdw;
    }

    public String getSwYbjlb() {
        return swYbjlb;
    }

    public void setSwYbjlb(String swYbjlb) {
        this.swYbjlb = swYbjlb;
    }

    public String getQbTypes() {
        return qbTypes;
    }

    public void setQbTypes(String qbTypes) {
        this.qbTypes = qbTypes;
    }

    public String getQbBm() {
        return qbBm;
    }

    public void setQbBm(String qbBm) {
        this.qbBm = qbBm;
    }

    public String getQbEmergency() {
        return qbEmergency;
    }

    public void setQbEmergency(String qbEmergency) {
        this.qbEmergency = qbEmergency;
    }

    public String getQbPerson() {
        return qbPerson;
    }

    public void setQbPerson(String qbPerson) {
        this.qbPerson = qbPerson;
    }

    public String getQbQbno() {
        return qbQbno;
    }

    public void setQbQbno(String qbQbno) {
        this.qbQbno = qbQbno;
    }

    public String getQbMainsender() {
        return qbMainsender;
    }

    public void setQbMainsender(String qbMainsender) {
        this.qbMainsender = qbMainsender;
    }

    public String getQbSecret() {
        return qbSecret;
    }

    public void setQbSecret(String qbSecret) {
        this.qbSecret = qbSecret;
    }

    public String getQbNgdate() {
        return qbNgdate;
    }

    public void setQbNgdate(String qbNgdate) {
        this.qbNgdate = qbNgdate;
    }

    public String getDocjkStatus() {
        return docjkStatus;
    }

    public void setDocjkStatus(String docjkStatus) {
        this.docjkStatus = docjkStatus;
    }

    public Date getOaClsj() {
        return oaClsj;
    }

    public void setOaClsj(Date oaClsj) {
        this.oaClsj = oaClsj;
    }

    public String getSwWjbh() {
        return swWjbh;
    }

    public void setSwWjbh(String swWjbh) {
        this.swWjbh = swWjbh;
    }

    public String getArchiveFlag() {
        return archiveFlag;
    }

    public void setArchiveFlag(String archiveFlag) {
        this.archiveFlag = archiveFlag;
    }
}
