package com.wisdom.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
public class Tb_category_dictionary {
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
	@GeneratedValue(generator="idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String categoryid;
	@Column(columnDefinition = "varchar(20)")
	private String name;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String parentid;
	@Column(columnDefinition = "varchar(50)")
	private String remark;
	@Column(columnDefinition = "integer")
	private Integer sequence;
	public String getCategoryid() {
		return categoryid;
	}
	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	@Override
	public String toString() {
		return "Tb_category_dictionary [categoryid=" + categoryid + ", name=" + name + ", parentid=" + parentid
				+ ", remark=" + remark + ", sequence=" + sequence + "]";
	}
}