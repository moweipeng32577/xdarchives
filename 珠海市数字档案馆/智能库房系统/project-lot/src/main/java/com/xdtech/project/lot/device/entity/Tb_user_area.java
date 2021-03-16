package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_user_area {
	
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Column(columnDefinition = "char(36)")
	private String uaid;
	@Column(columnDefinition = "char(36)")
	private String userid;
	@Column(columnDefinition = "char(36)")
	private String areaid;

	public String getUaid() {
		return uaid;
	}

	public void setUaid(String uaid) {
		this.uaid = uaid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
}