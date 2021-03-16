package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_funds")
public class Tb_funds_sx {

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String fundsid;
	@Column(columnDefinition = "varchar(255)")
	private String fundsname;// 全宗名称
	@Column(columnDefinition = "varchar(100)")
	private String fundsstarttime;// 全宗起始时间
	@Column(columnDefinition = "varchar(100)")
	private String fundsendtime;// 全宗终止时间
	@Column(columnDefinition = "varchar(100)")
	private String funds;// 全宗号
	@Column(columnDefinition = "varchar(255)")
	private String szdzh;
	@Column(columnDefinition = "varchar(255)")
	private String contactorgan;// 联系单位
	@Column(columnDefinition = "varchar(255)")
	private String fundsguidedoc;// 全宗指南文件
	@Column(columnDefinition = "varchar(100)")
	private String filetotalnum;// 案卷总数
	@Column(columnDefinition = "varchar(100)")
	private String filingtotalnum;// 归档文件总数
	@Column(columnDefinition = "varchar(100)")
	private String filingnum;// 归档文书总数
	@Column(columnDefinition = "varchar(255)")
	private String filingpermanent;// 归档永久（件）
	@Column(columnDefinition = "varchar(255)")
	private String filingshortterm;// 归档短期（件）
	@Column(columnDefinition = "varchar(255)")
	private String otherfilenum;// 其他案卷数
	@Column(columnDefinition = "varchar(255)")
	private String filinglongterm;// 归档长期（件）
	@Column(columnDefinition = "varchar(255)")
	private String wspermanent;// 文书永久（卷）
	@Column(columnDefinition = "varchar(255)")
	private String wslongterm;// 文书长期（卷）
	@Column(columnDefinition = "varchar(255)")
	private String wsshortterm;// 文书短期（卷）
	@Column(columnDefinition = "varchar(100)")
	private String organestablishtime;// 机构成立时间
	@Column(columnDefinition = "varchar(255)")
	private String remarks;// 备注
	@Column(columnDefinition = "varchar(100)")
	private String email;// 电子邮件
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String organid;// 单位id
	@Column(columnDefinition = "varchar(100)")
	private String organname;// 机构名称
	@Column(columnDefinition = "varchar(255)")
	private String fundsnameformername;// 全宗名称曾用名
	@Column(columnDefinition = "varchar(255)")
	private String jntotalcopies;// 卷内总份数
	@Column(columnDefinition = "varchar(255)")
	private String jnothercopies;// 其他卷内份数
	@Column(columnDefinition = "varchar(100)")
	private String wsfilenum;// 文书案卷数
	@Column(columnDefinition = "varchar(100)")
	private String wsjncopies;// 文书卷内份数
	@Column(columnDefinition = "char(1) comment '是否初始化'")
	private String isinit;

	public String getFundsid() {
		return fundsid;
	}

	public void setFundsid(String fundsid) {
		this.fundsid = fundsid;
	}

	public String getFundsname() {
		return fundsname;
	}

	public void setFundsname(String fundsname) {
		this.fundsname = fundsname;
	}

	public String getFundsstarttime() {
		return fundsstarttime;
	}

	public void setFundsstarttime(String fundsstarttime) {
		this.fundsstarttime = fundsstarttime;
	}

	public String getFundsendtime() {
		return fundsendtime;
	}

	public void setFundsendtime(String fundsendtime) {
		this.fundsendtime = fundsendtime;
	}

	public String getFunds() {
		return funds;
	}

	public void setFunds(String funds) {
		this.funds = funds;
	}

	public String getSzdzh() {
		return szdzh;
	}

	public void setSzdzh(String szdzh) {
		this.szdzh = szdzh;
	}

	public String getContactorgan() {
		return contactorgan;
	}

	public void setContactorgan(String contactorgan) {
		this.contactorgan = contactorgan;
	}

	public String getFundsguidedoc() {
		return fundsguidedoc;
	}

	public void setFundsguidedoc(String fundsguidedoc) {
		this.fundsguidedoc = fundsguidedoc;
	}

	public String getFiletotalnum() {
		return filetotalnum;
	}

	public void setFiletotalnum(String filetotalnum) {
		this.filetotalnum = filetotalnum;
	}

	public String getFilingtotalnum() {
		return filingtotalnum;
	}

	public void setFilingtotalnum(String filingtotalnum) {
		this.filingtotalnum = filingtotalnum;
	}

	public String getFilingnum() {
		return filingnum;
	}

	public void setFilingnum(String filingnum) {
		this.filingnum = filingnum;
	}

	public String getFilingpermanent() {
		return filingpermanent;
	}

	public void setFilingpermanent(String filingpermanent) {
		this.filingpermanent = filingpermanent;
	}

	public String getFilingshortterm() {
		return filingshortterm;
	}

	public void setFilingshortterm(String filingshortterm) {
		this.filingshortterm = filingshortterm;
	}

	public String getOtherfilenum() {
		return otherfilenum;
	}

	public void setOtherfilenum(String otherfilenum) {
		this.otherfilenum = otherfilenum;
	}

	public String getFilinglongterm() {
		return filinglongterm;
	}

	public void setFilinglongterm(String filinglongterm) {
		this.filinglongterm = filinglongterm;
	}

	public String getWspermanent() {
		return wspermanent;
	}

	public void setWspermanent(String wspermanent) {
		this.wspermanent = wspermanent;
	}

	public String getWslongterm() {
		return wslongterm;
	}

	public void setWslongterm(String wslongterm) {
		this.wslongterm = wslongterm;
	}

	public String getWsshortterm() {
		return wsshortterm;
	}

	public void setWsshortterm(String wsshortterm) {
		this.wsshortterm = wsshortterm;
	}

	public String getOrganestablishtime() {
		return organestablishtime;
	}

	public void setOrganestablishtime(String organestablishtime) {
		this.organestablishtime = organestablishtime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrganid() {
		return organid;
	}

	public void setOrganid(String organid) {
		this.organid = organid;
	}

	public String getOrganname() {
		return organname;
	}

	public void setOrganname(String organname) {
		this.organname = organname;
	}

	public String getFundsnameformername() {
		return fundsnameformername;
	}

	public void setFundsnameformername(String fundsnameformername) {
		this.fundsnameformername = fundsnameformername;
	}

	public String getJntotalcopies() {
		return jntotalcopies;
	}

	public void setJntotalcopies(String jntotalcopies) {
		this.jntotalcopies = jntotalcopies;
	}

	public String getJnothercopies() {
		return jnothercopies;
	}

	public void setJnothercopies(String jnothercopies) {
		this.jnothercopies = jnothercopies;
	}

	public String getWsfilenum() {
		return wsfilenum;
	}

	public void setWsfilenum(String wsfilenum) {
		this.wsfilenum = wsfilenum;
	}

	public String getWsjncopies() {
		return wsjncopies;
	}

	public void setWsjncopies(String wsjncopies) {
		this.wsjncopies = wsjncopies;
	}

	public String getIsinit() {
		return isinit;
	}

	public void setIsinit(String isinit) {
		this.isinit = isinit;
	}
}