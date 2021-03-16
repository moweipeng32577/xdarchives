package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_reserve {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String docid;
    @Column(columnDefinition = "varchar(100)")
    private String borrowman;//预约者
    @Column(columnDefinition = "varchar(100)")
    private String borrowmantel;// 查档者电话号码
    @Column(columnDefinition = "varchar(100)")
    private String borroworgan;//查档者单位
    @Column(columnDefinition = "varchar(50)")
    private String borrowmantime;//来馆人数
    @Column(columnDefinition = "varchar(50)")
    private String certificatestype;//证件类型
    @Column(columnDefinition = "varchar(50)")
    private String certificatenumber;//证件号码
    @Column(columnDefinition = "varchar(100)")
    private String borrowmd;//查档目的
    @Column(columnDefinition = "varchar(255)")
    private String borrowcontent;//查档内容
    @Column(columnDefinition = "varchar(50)")
    private String lymode;//预约类型
    @Column(columnDefinition = "varchar(30)")
    private String yystate;//预约状态(未回复/已回复/已取消)
    @Column(columnDefinition = "varchar(100)")
    private String replier;//回复者(当前登录者)
    @Column(columnDefinition = "varchar(30)")
    private String replytime;//回复时间
    @Column(columnDefinition = "varchar(255)")
    private String replycontent;//回复内容
    @Column(columnDefinition = "varchar(30)")
    private String yytime;//预约时间
    @Column(columnDefinition = "varchar(30)")
    private String djtime;//登记时间
    @Column(columnDefinition = "varchar(100)")
    private String canceler;//取消人(当前登录者)
    @Column(columnDefinition = "varchar(30)")
    private String canceltime;//取消时间
    @Column(columnDefinition = "varchar(100)")
    private String borrowdate;//展厅id
    @Column(columnDefinition = "varchar(100)")
    private String submiterid;//提交人id
    @Column(columnDefinition = "varchar(500)")
    private String evidencetext;//证明文件

    public String getEvidencetext() {
        return evidencetext;
    }

    public void setEvidencetext(String evidencetext) {
        this.evidencetext = evidencetext;
    }

    public String getSubmiterid() {
        return submiterid;
    }

    public void setSubmiterid(String submiterid) {
        this.submiterid = submiterid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getBorrowman() {
        return borrowman;
    }

    public void setBorrowman(String borrowman) {
        this.borrowman = borrowman;
    }

    public String getBorrowmantel() {
        return borrowmantel;
    }

    public void setBorrowmantel(String borrowmantel) {
        this.borrowmantel = borrowmantel;
    }

    public String getBorroworgan() {
        return borroworgan;
    }

    public void setBorroworgan(String borroworgan) {
        this.borroworgan = borroworgan;
    }

    public String getBorrowmantime() {
        return borrowmantime;
    }

    public void setBorrowmantime(String borrowmantime) {
        this.borrowmantime = borrowmantime;
    }

    public String getCertificatenumber() {
        return certificatenumber;
    }

    public void setCertificatenumber(String certificatenumber) {
        this.certificatenumber = certificatenumber;
    }

    public String getBorrowmd() {
        return borrowmd;
    }

    public void setBorrowmd(String borrowmd) {
        this.borrowmd = borrowmd;
    }

    public String getBorrowcontent() {
        return borrowcontent;
    }

    public void setBorrowcontent(String borrowcontent) {
        this.borrowcontent = borrowcontent;
    }

    public String getLymode() {
        return lymode;
    }

    public void setLymode(String lymode) {
        this.lymode = lymode;
    }

    public String getYystate() {
        return yystate;
    }

    public void setYystate(String yystate) {
        this.yystate = yystate;
    }

    public String getReplier() {
        return replier;
    }

    public void setReplier(String replier) {
        this.replier = replier;
    }

    public String getReplytime() {
        return replytime;
    }

    public void setReplytime(String replytime) {
        this.replytime = replytime;
    }

    public String getReplycontent() {
        return replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public String getYytime() {
        return yytime;
    }

    public void setYytime(String yytime) {
        this.yytime = yytime;
    }

    public String getDjtime() {
        return djtime;
    }

    public void setDjtime(String djtime) {
        this.djtime = djtime;
    }

    public String getCanceler() {
        return canceler;
    }

    public void setCanceler(String canceler) {
        this.canceler = canceler;
    }

    public String getCanceltime() {
        return canceltime;
    }

    public void setCanceltime(String canceltime) {
        this.canceltime = canceltime;
    }

    public String getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(String borrowdate) {
        this.borrowdate = borrowdate;
    }


    public String getCertificatestype() {
        return certificatestype;
    }

    public void setCertificatestype(String certificatestype) {
        this.certificatestype = certificatestype;
    }
}
