package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_inform_user {

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String iuid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String informid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String userroleid;
	@Column(columnDefinition = "varchar(10)")
	private String state;// 消息状态

	public String getId() {
		return iuid;
	}

	public void setId(String id) {
		this.iuid = id;
	}

	public String getInformid() {
		return informid;
	}

	public void setInformid(String informid) {
		this.informid = informid;
	}

	public String getUserroleid() {
		return userroleid;
	}

	public void setUserroleid(String userroleid) {
		this.userroleid = userroleid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}