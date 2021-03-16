package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_user_device {
	
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Column(columnDefinition = "char(36)")
	private String udid;
	@Column(columnDefinition = "char(36)")
	private String deviceid;
	@Column(columnDefinition = "char(36)")
	private String userid;

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}