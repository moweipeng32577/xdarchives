package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Tb_task {

	public static final String STATE_WAIT_HANDLE = "待处理";
	public static final String STATE_FINISHED = "完成";
	public static final String STATE_END = "结束";

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String taskid;
	@Column(columnDefinition = "varchar(200)")
	private String text;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String loginname;
	@Column(columnDefinition = "varchar(200)")
	private String state;
	@Column(columnDefinition = "varchar(50)")
	private String approvetext;
	@Column(columnDefinition = "varchar(100)")
	private String approveman;
	@Column(columnDefinition = "char(36)")
	private String lastid;
	@Column(columnDefinition = "varchar(100)")
	private String tasktype;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String borrowmsgid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String agentuserid;
	@Column(columnDefinition = "varchar(10)")
	private String urgingstate;	//催办状态 1.自动催办 2.手动催办
	// @Column(columnDefinition = "datetime")//mysql
	// @Column(columnDefinition = "date")//oracle
	private Date tasktime;

	public String getUrgingstate() {
		return urgingstate;
	}

	public void setUrgingstate(String urgingstate) {
		this.urgingstate = urgingstate;
	}

	public Date getTime() {
		return tasktime;
	}

	public void setTime(Date time) {
		this.tasktime = time;
	}

	public String getId() {
		return taskid;
	}

	public void setId(String id) {
		this.taskid = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getLastid() {
		return lastid;
	}

	public void setLastid(String lastid) {
		this.lastid = lastid;
	}

	public String getType() {
		return tasktype;
	}

	public void setType(String type) {
		this.tasktype = type;
	}

	public String getBorrowmsgid() {
		return borrowmsgid;
	}

	public void setBorrowmsgid(String borrowmsgid) {
		this.borrowmsgid = borrowmsgid;
	}

	public String getAgentuserid() {
		return agentuserid;
	}

	public void setAgentuserid(String agentuserid) {
		this.agentuserid = agentuserid;
	}
}