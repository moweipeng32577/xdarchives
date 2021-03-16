package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Tb_opendoc {

	public static final String STATE_SEND_AUDIT = "已送审";
	public static final String STATE_WAIT_HANDLE = "待处理";
	public static final String STATE_FINISH_AUDIT = "已审核";
	public static final String STATE_SEND_BACK = "已退回";

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String docid;// 条目主键ID(GUID)
	@Transient
	private String nodefullname;
	@Column(columnDefinition = "varchar(200)")
	private String doctitle;
	@Column(columnDefinition = "varchar(30)")
	private String submitter;
	@Column(columnDefinition = "varchar(30)")
	private String submitdate;
	@Column(columnDefinition = "varchar(36)")
	private String batchnum;
	@Column(columnDefinition = "integer")
	private Long entrycount;
	@Column(columnDefinition = "char(1)")
	private String opened;
	@Column(columnDefinition = "varchar(30)")
	private String opentype;
	@Column(columnDefinition = "varchar(300)")
	private String remarks;
	@Column(columnDefinition = "varchar(1000)")
	private String approve;
	@Column(columnDefinition = "varchar(30)")
	private String state;
	@Column(columnDefinition = "varchar(30)")
	private String opendate;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Column(columnDefinition = "varchar(30)")
	private String nodename;
	@Column(columnDefinition = "varchar(36)")
	private String submitterid; //申请人id

	public String getSubmitterid() {
		return submitterid;
	}

	public void setSubmitterid(String submitterid) {
		this.submitterid = submitterid;
	}

	public String getId() {
		return docid;
	}

	public void setId(String docid) {
		this.docid = docid;
	}

	public String getNodefullname() {
		return nodefullname;
	}

	public void setNodefullname(String nodefullname) {
		this.nodefullname = nodefullname;
	}

	public String getDoctitle() {
		return doctitle;
	}

	public void setDoctitle(String doctitle) {
		this.doctitle = doctitle;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSubmitdate() {
		return submitdate;
	}

	public void setSubmitdate(String submitdate) {
		this.submitdate = submitdate;
	}

	public String getBatchnum() {
		return batchnum;
	}

	public void setBatchnum(String batchnum) {
		this.batchnum = batchnum;
	}

	public Long getEntrycount() {
		return entrycount;
	}

	public void setEntrycount(Long entrycount) {
		this.entrycount = entrycount;
	}

	public String getOpened() {
		return opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}

	public String getOpentype() {
		return opentype;
	}

	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getOpendate() {
		return opendate;
	}

	public void setOpendate(String opendate) {
		this.opendate = opendate;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
}