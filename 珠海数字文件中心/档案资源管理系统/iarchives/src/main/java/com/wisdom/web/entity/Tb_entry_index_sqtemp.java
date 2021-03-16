package com.wisdom.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class Tb_entry_index_sqtemp {
	@Id
//	@GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
//	@GeneratedValue(generator="idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String entryid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Column(columnDefinition = "varchar(1000)")
	private String title;
	@Column(columnDefinition = "varchar(100)")
	private String archivecode;
	@Column(columnDefinition = "varchar(100)")
	private String newarchivecode;
	@Column(columnDefinition = "varchar(10)")
	private String calvalue;//卷内顺序号
	@Column(columnDefinition = "varchar(15)")
	private String pageno;//页号
	@Column(columnDefinition = "varchar(10)")
	private String pages;//页数
	@Column(columnDefinition = "varchar(100)")
	private String uniquetag;
	public String getEntryid() {
		return entryid;
	}
	public void setEntryid(String entryid) {
		this.entryid = entryid;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArchivecode() {
		return archivecode;
	}
	public void setArchivecode(String archivecode) {
		this.archivecode = archivecode;
	}
	public String getNewarchivecode() {
		return newarchivecode;
	}
	public void setNewarchivecode(String newarchivecode) {
		this.newarchivecode = newarchivecode;
	}
	public String getCalvalue() {
		return calvalue;
	}
	public void setCalvalue(String calvalue) {
		this.calvalue = calvalue;
	}
	public String getPageno() {
		return pageno;
	}
	public void setPageno(String pageno) {
		this.pageno = pageno;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public String getUniquetag() {
		return uniquetag;
	}
	public void setUniquetag(String uniquetag) {
		this.uniquetag = uniquetag;
	}
}