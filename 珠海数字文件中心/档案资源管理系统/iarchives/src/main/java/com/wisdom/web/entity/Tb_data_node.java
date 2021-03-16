package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_data_node {

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String parentnodeid;
	@Column(columnDefinition = "integer")
	private long nodelevel;
	@Column(columnDefinition = "char(1)")
	private boolean leaf;
	@Column(columnDefinition = "integer")
	private long nodetype; // nodetype:2为分类节点，1为机构节点
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String refid;
	@Column(columnDefinition = "varchar(150)")
	private String nodename;
	@Column(columnDefinition = "varchar(60)")
	private String nodecode;
	@Column(columnDefinition = "integer")
	private Integer sortsequence;
	@Column(columnDefinition = "integer")
	private Integer classlevel; // 冗余字段，记录当前节点的分类层级
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String classid; // 冗余字段，记录当前节点的分类ID
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String organid; // 冗余字段，记录当前节点的机构ID
	@Column(columnDefinition = "char(1)")
	private String luckstate; // 模板锁定状态,0:未锁定,1:已锁定

	public Integer getOrders() {
		return sortsequence;
	}

	public void setOrders(Integer orders) {
		this.sortsequence = orders;
	}

	public String getNodeid() {
		return nodeid==null?null:nodeid.trim();
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public long getLevel() {
		return nodelevel;
	}

	public void setLevel(long level) {
		this.nodelevel = level;
	}

	public String getParentnodeid() {
		return parentnodeid==null?null:parentnodeid.trim();
	}

	public void setParentnodeid(String parentnodeid) {
		this.parentnodeid = parentnodeid;
	}

	public boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public long getNodetype() {
		return nodetype;
	}

	public void setNodetype(long nodetype) {
		this.nodetype = nodetype;
	}

	public String getRefid() {
		return refid==null?null:refid.trim();
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public Integer getClasslevel() {
		return classlevel;
	}

	public void setClasslevel(Integer classlevel) {
		this.classlevel = classlevel;
	}

	public String getClassid() {
		return classid==null?null:classid.trim();
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getOrganid() {
		return organid==null?null:organid.trim();
	}

	public void setOrganid(String organid) {
		this.organid = organid;
	}

	public String getLuckstate() {
		return luckstate;
	}

	public void setLuckstate(String luckstate) {
		this.luckstate = luckstate;
	}

	public long getNodelevel() {
		return nodelevel;
	}

	public void setNodelevel(long nodelevel) {
		this.nodelevel = nodelevel;
	}

	public Integer getSortsequence() {
		return sortsequence;
	}

	public void setSortsequence(Integer sortsequence) {
		this.sortsequence = sortsequence;
	}

	public Tb_data_node() {
	}

	public Tb_data_node(String nodeid, String parentnodeid) {
		this.nodeid = nodeid;
		this.parentnodeid = parentnodeid;
	}

	public Tb_data_node(String nodeid, String nodename, long nodetype, Integer classlevel, Integer sortsequence,String organid) {
		try {
			this.nodeid = nodeid;
			this.nodename = nodename;
			this.nodetype = nodetype;
			this.classlevel = classlevel == null ? 0 : classlevel;
			this.sortsequence = sortsequence == null ? 0 : sortsequence;
			this.organid = organid;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Tb_data_node node = (Tb_data_node) o;
		return nodeid.equals(node.nodeid);
	}
}