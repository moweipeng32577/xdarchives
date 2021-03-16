package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_borrowdoc {

  public static final String STATE_FINISH_AUDIT = "已送审";
  public static final String STATE_SEND_BACK = "退回";
  public static final String STATE_END = "结束";

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String docid;
  @Column(columnDefinition = "varchar(100)")
  private String borrowcode;//查档号
  @Column(columnDefinition = "varchar(100)")
  private String borrowdate;//查档时间
  @Column(columnDefinition = "varchar(100)")
  private String borrowman;//查档人
  @Column(columnDefinition = "varchar(100)")
  private String borrowmantel;// 查档者电话号码
  @Column(columnDefinition = "varchar(100)")
  private String borroworgan;//查档者单位
  @Column(columnDefinition = "integer")
  private int borrowts;//申请查档天数
  @Column(columnDefinition = "varchar(50)")
  private String certificatetype;//证件类型
  @Column(columnDefinition = "varchar(50)")
  private String certificatenumber;//证件号码
  @Column(columnDefinition = "varchar(100)")
  private String lymode;//查档类型
  @Column(columnDefinition = "varchar(100)")
  private String borrowmd;//查档目的
  @Column(columnDefinition = "varchar(50)")
  private String borrowmantime;//查档人次
  @Column(columnDefinition = "varchar(255)")
  private String borrowcontent;//查档内容
  @Column(columnDefinition = "varchar(100)")
  private String type;//与workid相关联(后台处理)
  @Column(columnDefinition = "varchar(500)")
  private String desci;//查档描述
  @Column(columnDefinition = "varchar(1000)")
  private String approve;//批示
  @Column(columnDefinition = "varchar(50)")
  private String state;//审批状态
  @Column(columnDefinition = "integer")
  private int borrowtyts;//查档同意天数
  @Column(columnDefinition = "integer")
  private int copies;//查档同意天数
  @Column(columnDefinition = "varchar(50)")
  private String approvetext;//审批环节
  @Column(columnDefinition = "varchar(100)")
  private String approveman;//审批人
  @Column(columnDefinition = "varchar(100)")
  private String returnstate;//归还状态
  @Column(columnDefinition = "varchar(100)")
  private String clearstate;//消息清除状态
  @Column(columnDefinition = "varchar(100)")
  private String finishtime;//办理完结时间
  @Column(columnDefinition = "char(36)")
  private String borrowmanid;//查档人id
  @Column(columnDefinition = "varchar(30)")
  private String submitstate;//提交状态
  @Column(columnDefinition = "varchar(30)")
  private String letternumber;//介绍信编号
  @Column(columnDefinition = "varchar(200)")
  private String evidencetext;//证明文件
  @Column(columnDefinition = "varchar(30)")
  private String comaddress;//地址
  @Column(columnDefinition = "varchar(30)")
  private String relationship;//与当事人的关系
  @Column(columnDefinition = "varchar(30)")
  private String acceptdate;//受理时间
  @Column(columnDefinition = "varchar(30)")
  private String outwarestate;//出库状态
  @Column(columnDefinition = "varchar(30)")
  private String datasourcetype;  //申请数据类型
  @Column(columnDefinition = "varchar(200)")
  private String copycontent;  //复制内容
  @Column(columnDefinition = "varchar(200)")
  private String copymd;  //复制目的

  public String getCopycontent() {
    return copycontent;
  }

  public void setCopycontent(String copycontent) {
    this.copycontent = copycontent;
  }

  public String getCopymd() {
    return copymd;
  }

  public void setCopymd(String copymd) {
    this.copymd = copymd;
  }

  public String getDatasourcetype() {
    return datasourcetype;
  }

  public void setDatasourcetype(String datasourcetype) {
    this.datasourcetype = datasourcetype;
  }

  public String getOutwarestate() {
    return outwarestate;
  }

  public void setOutwarestate(String outwarestate) {
    this.outwarestate = outwarestate;
  }

  public String getAcceptdate() {
    return acceptdate;
  }

  public void setAcceptdate(String acceptdate) {
    this.acceptdate = acceptdate;
  }

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }

  public String getComaddress() {
    return comaddress;
  }

  public void setComaddress(String comaddress) {
    this.comaddress = comaddress;
  }

  public String getEvidencetext() {
    return evidencetext;
  }

  public void setEvidencetext(String evidencetext) {
    this.evidencetext = evidencetext;
  }

  public String getLetternumber() {
    return letternumber;
  }

  public void setLetternumber(String letternumber) {
    this.letternumber = letternumber;
  }

  public String getSubmitstate() {
    return submitstate;
  }

  public void setSubmitstate(String submitstate) {
    this.submitstate = submitstate;
  }

  public String getBorrowmanid() {
    return borrowmanid;
  }

  public void setBorrowmanid(String borrowmanid) {
    this.borrowmanid = borrowmanid;
  }

  public String getFinishtime() {
    return finishtime;
  }

  public void setFinishtime(String finishtime) {
    this.finishtime = finishtime;
  }

  public String getClearstate() {
    return clearstate;
  }

  public void setClearstate(String clearstate) {
    this.clearstate = clearstate;
  }

  public String getId() {
    return docid;
  }

  public void setId(String id) {
    this.docid = id;
  }

  public String getBorrowcode() {
    return borrowcode;
  }

  public void setBorrowcode(String borrowcode) {
    this.borrowcode = borrowcode;
  }

  public String getBorrowdate() {
    return borrowdate;
  }

  public void setBorrowdate(String borrowdate) {
    this.borrowdate = borrowdate;
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

  public int getBorrowts() {
    return borrowts;
  }

  public void setBorrowts(int borrowts) {
    this.borrowts = borrowts;
  }

  public String getCertificatetype() {
    return certificatetype;
  }

  public void setCertificatetype(String certificatetype) {
    this.certificatetype = certificatetype;
  }

  public String getCertificatenumber() {
    return certificatenumber;
  }

  public void setCertificatenumber(String certificatenumber) {
    this.certificatenumber = certificatenumber;
  }

  public String getLymode() {
    return lymode;
  }

  public void setLymode(String lymode) {
    this.lymode = lymode;
  }

  public String getBorrowmd() {
    return borrowmd;
  }

  public void setBorrowmd(String borrowmd) {
    this.borrowmd = borrowmd;
  }

  public String getBorrowmantime() {
    return borrowmantime;
  }

  public void setBorrowmantime(String borrowmantime) {
    this.borrowmantime = borrowmantime;
  }

  public String getBorrowcontent() {
    return borrowcontent;
  }

  public void setBorrowcontent(String borrowcontent) {
    this.borrowcontent = borrowcontent;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDesci() {
    return desci;
  }

  public void setDesci(String desci) {
    this.desci = desci;
  }

  public String getApprove() {
    return approve;
  }

  public void setApprove(String approve) {
    this.approve = approve;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public int getBorrowtyts() {
    return borrowtyts;
  }

  public void setBorrowtyts(int borrowtyts) {
    this.borrowtyts = borrowtyts;
  }

  public int getCopies() {
    return copies;
  }

  public void setCopies(int copies) {
    this.copies = copies;
  }

  public String getApprovetext() {
    return approvetext;
  }

  public void setApprovetext(String approvetext) {
    this.approvetext = approvetext;
  }

  public String getApproveman() {
    return approveman;
  }

  public void setApproveman(String approveman) {
    this.approveman = approveman;
  }

  public String getReturnstate() {
    return returnstate;
  }

  public void setReturnstate(String returnstate) {
    this.returnstate = returnstate;
  }
}
