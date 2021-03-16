package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by yl on 2017/10/31.
 */
@Entity
public class Tb_bill {

	public static final String STATE_NOT_SEND = "0";// 未送审
	public static final String STATE_WAIT_AUDIT = "1";// 待审核
	public static final String STATE_FINISH_AUDIT = "2";// 已审核
	public static final String STATE_AUDIT_FAILED = "3";// 已审核(不通过)
	public static final String STATE_FINISH_EXECUTE = "4";// 已执行电子条目相关的销毁
	public static final String STATE_SEND_BACK = "5";// 已退回
	public static final String STATE_WAIT_AUDIT_FAILED = "6";// 待审核(不通过))
	public static final String STATE_FINISH_KF_EXECUTE = "7";// 已执行实体库存相关的销毁

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String billid;
	@Column(columnDefinition = "varchar(50)")
	private String approvetext;// 审批环节
	@Column(columnDefinition = "varchar(100)")
	private String approveman;// 审批人
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String nodeid;
	@Column(columnDefinition = "varchar(1000)")
	private String title;
	@Column(columnDefinition = "varchar(20)")
	private String approvaldate;
	@Column(columnDefinition = "integer")
	private Long total;
	@Column(columnDefinition = "varchar(100)")
	private String reason;
	@Column(columnDefinition = "varchar(20)")
	private String submitter;
	@Column(columnDefinition = "char(1)")
	private String state;

	@Transient
	private String stateValue;

	public String getApprovetext() {
		return approvetext;
	}

	public void setApprovetext(String approvetext) {
		this.approvetext = approvetext;
	}

	public String getApproveman() {
		return approveman;
	}

	public void setApproveman(String approveman) {
		this.approveman = approveman;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getBillid() {
		return billid;
	}

	public void setBillid(String billid) {
		this.billid = billid;
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

	public String getApprovaldate() {
		return approvaldate;
	}

	public void setApprovaldate(String approvaldate) {
		this.approvaldate = approvaldate;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateValue() {
		return "3".equals(state)  || "5".equals(state) || "6".equals(state) ? "不通过" : "通过";
	}

	public void setStateValue(String stateValue) {
		this.stateValue = stateValue;
	}

	@Override
	public String toString() {
		return "Tb_bill{" + "billid='" + billid + '\'' + ", nodeid='" + nodeid + '\'' + ", title='" + title + '\''
				+ ", approvaldate='" + approvaldate + '\'' + ", total=" + total + ", reason='" + reason + '\''
				+ ", submitter='" + submitter + '\'' + ", state='" + state + '\'' + ", stateValue='" + stateValue + '\''
				+ '}';
	}
}