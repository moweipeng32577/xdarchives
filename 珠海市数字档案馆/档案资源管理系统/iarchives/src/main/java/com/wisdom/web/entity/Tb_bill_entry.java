package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2017/10/31.
 * 单据、条目的中间表
 */
@Entity
public class Tb_bill_entry {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String ugid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String billid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;
    @Column(columnDefinition = "varchar(1000)")
    private String title;
    @Column(columnDefinition = "varchar(50)")
    private String archivecode;
    @Column(columnDefinition = "varchar(15)")
    private String entryretention;
    @Column(columnDefinition = "varchar(20)")
    private String filedate;
    // 文件编号
    @Column(columnDefinition = "varchar(200)")
	private String filenumber;
    // 责任者
    @Column(columnDefinition = "varchar(100)")
    private String responsible;
    // 已保管时间
    @Column(columnDefinition = "varchar(20)")
    private String keepdate;
    // 销毁时间
    @Column(columnDefinition = "varchar(20)")
    private String destroydate;

	// 状态（变更，销毁，维持）
	@Column(columnDefinition = "varchar(100)")
	private String state;

	//存储位置
	private String entrystorage;
	@Column(columnDefinition = "varchar(100)")
    
	public String getUgid() {
		return ugid;
	}
	public void setUgid(String ugid) {
		this.ugid = ugid;
	}
	public String getBillid() {
		return billid;
	}
	public void setBillid(String billid) {
		this.billid = billid;
	}
	public String getEntryid() {
		return entryid;
	}
	public void setEntryid(String entryid) {
		this.entryid = entryid;
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
	public String getEntryretention() {
		return entryretention;
	}
	public void setEntryretention(String entryretention) {
		this.entryretention = entryretention;
	}
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	public String getFilenumber() {
		return filenumber;
	}
	public void setFilenumber(String filenumber) {
		this.filenumber = filenumber;
	}
	public String getResponsible() {
		return responsible;
	}
	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}
	public String getKeepdate() {
		return keepdate;
	}
	public void setKeepdate(String keepdate) {
		this.keepdate = keepdate;
	}
	public String getDestroydate() {
		return destroydate;
	}
	public void setDestroydate(String destroydate) {
		this.destroydate = destroydate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEntrystorage() {
		return entrystorage;
	}
	public void setEntrystorage(String entrystorage) {
		this.entrystorage = entrystorage;
	}

	@Override
	public String toString() {
		return "Tb_bill_entry [ugid=" + ugid + ", billid=" + billid + ", entryid=" + entryid + ", title=" + title
				+ ", archivecode=" + archivecode + ", entryretention=" + entryretention + ", filedate=" + filedate
				+ ", filenumber=" + filenumber + ", responsible=" + responsible + ", keepdate=" + keepdate
				+ ", entrystorage=\" + entrystorage + \", destroydate=" + destroydate + "]";
	}
}