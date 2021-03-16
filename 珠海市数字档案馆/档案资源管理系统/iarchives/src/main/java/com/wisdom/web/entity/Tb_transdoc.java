package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class Tb_transdoc {

  public static final String STATE_TRANSFOR = "已移交";
  public static final String STATE_AUDIT = "已审核";
  public static final String STATE_SENDBACK = "已退回";

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
  @GeneratedValue(generator = "idGenerator")
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String docid;
  @Column(columnDefinition = "varchar(5000)")
  private String transdesc;
  @Column(columnDefinition = "varchar(30)")
  private String transuser;
  @Column(columnDefinition = "varchar(100)")
  private String transorgan;
  @Column(columnDefinition = "varchar(30)")
  private String transdate;
  @Column(columnDefinition = "integer")
  private Long transcount;
  @Column(columnDefinition = "varchar(30)")
  private String state;
  @Column(columnDefinition = "varchar(255)")
  private String sendbackreason;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String nodeid;
  @Type(type = "com.wisdom.util.OracleCharIDType")
  @Column(columnDefinition = "char(36)")
  private String approvemanid;

  @Column(columnDefinition = "varchar(50)")
  private String approveman;    //审批人
  @Column(columnDefinition = "varchar(50)")
  private String transfercode;    //移交审批单据号
  @Column(columnDefinition = "varchar(30)")
  private String approvetime;    //审核时间
  @Column(columnDefinition = "char(1)")
  private String transforcasign;//是否有移交签章 0没有 1有
  @Column(columnDefinition = "varchar(30)")
  private String transforcasigndate;  //移交盖章时间
  @Column(columnDefinition = "char(1)")
  private String editcasign;//是否有审核签章 0没有 1有
  @Column(columnDefinition = "varchar(30)")
  private String editcasigndate;  //审核盖章时间
  @Column(columnDefinition = "varchar(200)")
  private String transfertitle;    //交接工作名称
  @Column(columnDefinition = "varchar(50)")
  private String sequencecode;    //载体起止顺序号
  @Column(columnDefinition = "varchar(50)")
  private String transfersize;    //移交数据量
  @Column(columnDefinition = "varchar(30)")
  private String establishleader;  //立档领导
  @Column(columnDefinition = "varchar(30)")
  private String establishleaderdate;  //立档领导审核时间
  @Column(columnDefinition = "varchar(30)")
  private String archivesleader;  //档案馆领导
  @Column(columnDefinition = "varchar(30)")
  private String archivesleaderdate;  //档案馆领导审核时间
  @Column(columnDefinition = "varchar(30)")
  private String archivesuser;  //档案员
  @Column(columnDefinition = "varchar(30)")
  private String archivesuserdate;  //档案员接收时间
  @Column(columnDefinition = "char(36)")
  private String volumenodeid;  //卷内节点


  public String getVolumenodeid() {
    return volumenodeid;
  }

  public void setVolumenodeid(String volumenodeid) {
    this.volumenodeid = volumenodeid;
  }

  public String getEstablishleader() {
    return establishleader;
  }

  public void setEstablishleader(String establishleader) {
    this.establishleader = establishleader;
  }

  public String getEstablishleaderdate() {
    return establishleaderdate;
  }

  public void setEstablishleaderdate(String establishleaderdate) {
    this.establishleaderdate = establishleaderdate;
  }

  public String getArchivesleader() {
    return archivesleader;
  }

  public void setArchivesleader(String archivesleader) {
    this.archivesleader = archivesleader;
  }

  public String getArchivesleaderdate() {
    return archivesleaderdate;
  }

  public void setArchivesleaderdate(String archivesleaderdate) {
    this.archivesleaderdate = archivesleaderdate;
  }

  public String getArchivesuser() {
    return archivesuser;
  }

  public void setArchivesuser(String archivesuser) {
    this.archivesuser = archivesuser;
  }

  public String getArchivesuserdate() {
    return archivesuserdate;
  }

  public void setArchivesuserdate(String archivesuserdate) {
    this.archivesuserdate = archivesuserdate;
  }

  public String getTransforcasigndate() {
    return transforcasigndate;
  }

  public void setTransforcasigndate(String transforcasigndate) {
    this.transforcasigndate = transforcasigndate;
  }

  public String getEditcasigndate() {
    return editcasigndate;
  }

  public void setEditcasigndate(String editcasigndate) {
    this.editcasigndate = editcasigndate;
  }

  public String getTransfertitle() {
    return transfertitle;
  }

  public void setTransfertitle(String transfertitle) {
    this.transfertitle = transfertitle;
  }

  public String getSequencecode() {
    return sequencecode;
  }

  public void setSequencecode(String sequencecode) {
    this.sequencecode = sequencecode;
  }

  public String getTransfersize() {
    return transfersize;
  }

  public void setTransfersize(String transfersize) {
    this.transfersize = transfersize;
  }

  public void setApproveman(String approveman) {
    this.approveman = approveman;
  }

  public void setTransfercode(String transfercode) {
    this.transfercode = transfercode;
  }

  public void setApprovetime(String approvetime) {
    this.approvetime = approvetime;
  }

  public static String getStateTransfor() {
    return STATE_TRANSFOR;
  }

  public static String getStateAudit() {
    return STATE_AUDIT;
  }

  public static String getStateSendback() {
    return STATE_SENDBACK;
  }

  public String getApproveman() {
    return approveman;
  }

  public String getTransfercode() {
    return transfercode;
  }

  public String getApprovetime() {
    return approvetime;
  }

  public String getApprovemanid() {
    return approvemanid;
  }

  public void setApprovemanid(String approvemanid) {
    this.approvemanid = approvemanid;
  }

  public String getDocid() {
    return docid;
  }

  public void setDocid(String docid) {
    this.docid = docid;
  }

  public String getTransdesc() {
    return transdesc;
  }

  public void setTransdesc(String transdesc) {
    this.transdesc = transdesc;
  }

  public String getTransuser() {
    return transuser;
  }

  public void setTransuser(String transuser) {
    this.transuser = transuser;
  }

  public String getTransorgan() {
    return transorgan;
  }

  public void setTransorgan(String transorgan) {
    this.transorgan = transorgan;
  }

  public String getTransdate() {
    return transdate;
  }

  public void setTransdate(String transdate) {
    this.transdate = transdate;
  }

  public Long getTranscount() {
    return transcount;
  }

  public void setTranscount(Long transcount) {
    this.transcount = transcount;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getNodeid() {
    return nodeid;
  }

  public void setNodeid(String nodeid) {
    this.nodeid = nodeid;
  }

  public String getSendbackreason() {
    return sendbackreason;
  }

  public void setSendbackreason(String sendbackreason) {
    this.sendbackreason = sendbackreason;
  }

  public String getTransforcasign() {
    return transforcasign;
  }

  public void setTransforcasign(String transforcasign) {
    this.transforcasign = transforcasign;
  }

  public String getEditcasign() {
    return editcasign;
  }

  public void setEditcasign(String editcasign) {
    this.editcasign = editcasign;
  }
}
