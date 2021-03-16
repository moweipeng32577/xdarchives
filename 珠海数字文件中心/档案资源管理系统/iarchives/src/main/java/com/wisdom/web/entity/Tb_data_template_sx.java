package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_data_template_sx {
	
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String templateid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Column(columnDefinition = "varchar(30)")
	private String fieldtable;
	@Column(columnDefinition = "varchar(20)")
	private String fieldcode;
	@Column(columnDefinition = "varchar(20)")
	private String fieldname;
	@Column(columnDefinition = "char(1)")
	private boolean gfield;
	@Column(columnDefinition = "char(1)")
	private boolean ghidden;
	@Column(columnDefinition = "integer")
	private Long gwidth;
	@Column(columnDefinition = "integer")
	private Long gsequence;
	@Column(columnDefinition = "char(1)")
	private boolean qfield;
	@Column(columnDefinition = "integer")
	private Long qsequence;
	@Column(columnDefinition = "char(1)")
	private boolean ffield;
	@Column(columnDefinition = "char(1)")
	private boolean frequired;
	@Column(columnDefinition = "varchar(50)")
	private String fdefault;
	@Column(columnDefinition = "char(1)")
	private boolean freadonly;
	@Column(columnDefinition = "varchar(20)")
	private String ftype;
	@Column(columnDefinition = "integer")
	private Long frows;
	@Column(columnDefinition = "varchar(50)")
	private String fenums;
	@Column(columnDefinition = "char(1)")
	private boolean fenumsedit;
	@Column(columnDefinition = "varchar(20)")
	private String fvalidate;
	@Column(columnDefinition = "varchar(200)")
	private String ftip;
	@Column(columnDefinition = "integer")
	private Long fsequence;
	@Column(columnDefinition = "char(1)")
	private boolean inactiveformfield;
	@Column(columnDefinition = "varchar(50)")
	private String fieldlength;

	public boolean isGfield() {
		return gfield;
	}

	public boolean isGhidden() {
		return ghidden;
	}

	public boolean isQfield() {
		return qfield;
	}

	public boolean isFfield() {
		return ffield;
	}

	public boolean isFrequired() {
		return frequired;
	}

	public boolean isFreadonly() {
		return freadonly;
	}

	public boolean isInactiveformfield() {
		return inactiveformfield;
	}

	public String getFieldlength() {
		return fieldlength;
	}

	public void setFieldlength(String fieldlength) {
		this.fieldlength = fieldlength;
	}

	public Long getGwidth() {
		return gwidth;
	}

	public void setGwidth(Long gwidth) {
		this.gwidth = gwidth;
	}

	public Long getGsequence() {
		return gsequence;
	}

	public void setGsequence(Long gsequence) {
		this.gsequence = gsequence;
	}

	public Long getQsequence() {
		return qsequence;
	}

	public void setQsequence(Long qsequence) {
		this.qsequence = qsequence;
	}

	public Long getFrows() {
		return frows;
	}

	public void setFrows(Long frows) {
		this.frows = frows;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public String getNodeid() {
		return nodeid == null ? null : nodeid.trim();
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getFieldtable() {
		return fieldtable;
	}

	public void setFieldtable(String fieldtable) {
		this.fieldtable = fieldtable;
	}

	public String getFieldcode() {
		return fieldcode;
	}

	public void setFieldcode(String fieldcode) {
		this.fieldcode = fieldcode;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public boolean getGfield() {
		return gfield;
	}

	public void setGfield(boolean gfield) {
		this.gfield = gfield;
	}

	public boolean getGhidden() {
		return ghidden;
	}

	public void setGhidden(boolean ghidden) {
		this.ghidden = ghidden;
	}

	public boolean getQfield() {
		return qfield;
	}

	public void setQfield(boolean qfield) {
		this.qfield = qfield;
	}

	public boolean getFfield() {
		return ffield;
	}

	public void setFfield(boolean ffield) {
		this.ffield = ffield;
	}

	public boolean getFrequired() {
		return frequired;
	}

	public void setFrequired(boolean frequired) {
		this.frequired = frequired;
	}

	public String getFdefault() {
		return fdefault;
	}

	public void setFdefault(String fdefault) {
		this.fdefault = fdefault;
	}

	public boolean getFreadonly() {
		return freadonly;
	}

	public void setFreadonly(boolean freadonly) {
		this.freadonly = freadonly;
	}

	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	public String getFenums() {
		return fenums;
	}

	public void setFenums(String fenums) {
		this.fenums = fenums;
	}

	public boolean getFenumsedit() {
		return fenumsedit;
	}

	public void setFenumsedit(boolean fenumsedit) {
		this.fenumsedit = fenumsedit;
	}

	public String getFvalidate() {
		return fvalidate;
	}

	public void setFvalidate(String fvalidate) {
		this.fvalidate = fvalidate;
	}

	public String getFtip() {
		return ftip;
	}

	public void setFtip(String ftip) {
		this.ftip = ftip;
	}

	public Long getFsequence() {
		return fsequence;
	}

	public void setFsequence(Long fsequence) {
		this.fsequence = fsequence;
	}

	public boolean getInactiveformfield() {
		return inactiveformfield;
	}

	public void setInactiveformfield(boolean inactiveformfield) {
		this.inactiveformfield = inactiveformfield;
	}
}
