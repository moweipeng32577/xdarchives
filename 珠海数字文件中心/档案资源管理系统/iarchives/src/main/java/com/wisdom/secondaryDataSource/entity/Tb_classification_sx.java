package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_classification")
public class Tb_classification_sx {
	public static final String JUANNEI = "1";//卷内
	public static final String ANJUAN = "2";//案卷
	public static final String WEIGUI = "3";//未归
	public static final String YIGUI = "4";//已归
	public static final String ZILIAO = "5";//资料
	public static final String WENJIAN = "6";//文件

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String classid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String parentclassid;
	@Column(columnDefinition = "varchar(100)")
	private String classname;
	@Column(columnDefinition = "varchar(50)")
	private String code;
	@Column(columnDefinition = "integer")
	private Integer sortsequence;
	@Column(columnDefinition = "integer")
	private Integer classlevel; // 分类级别。项目3，案卷2，卷内/归档1

	@Column(columnDefinition = "char(36)")
	private String codelevel;

	public String getCodelevel() {
		return codelevel;
	}

	public void setCodelevel(String codelevel) {
		this.codelevel = codelevel;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getParentclassid() {
		return parentclassid;
	}

	public void setParentclassid(String parentclassid) {
		this.parentclassid = parentclassid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getClasslevel() {
		return classlevel;
	}

	public void setClasslevel(Integer classlevel) {
		this.classlevel = classlevel;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public Integer getSortsequence() {
		return sortsequence;
	}

	public void setSortsequence(Integer sortsequence) {
		this.sortsequence = sortsequence;
	}
}
