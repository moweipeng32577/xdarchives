package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_flows {

	public static final String STATE_HANDLE = "处理中";
	public static final String STATE_FINISHED = "完成";
	public static final String STATE_END = "结束";

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String flowsid;
	@Column(columnDefinition = "varchar(100)")
	private String text;
	@Column(columnDefinition = "varchar(100)")
	private String state;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String taskid;
	@Column(columnDefinition = "varchar(36)")
	private String msgid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String spman;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Column(columnDefinition = "float(20)")
	private long approvedate;

	public long getDate() {
		return approvedate;
	}

	public void setDate(long date) {
		this.approvedate = date;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getId() {
		return flowsid;
	}

	public void setId(String id) {
		this.flowsid = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getSpman() {
		return spman;
	}

	public void setSpman(String spman) {
		this.spman = spman;
	}
}
