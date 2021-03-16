package com.wisdom.secondaryDataSource.entity;

import com.wisdom.util.ExcelAttribute;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_log_msg")
public class Tb_log_msg_sx {

	private static final String SYSTEM = "xitong";
	private static final String AQBM = "aqbm";
	private static final String AQSJ = "aqsj";
	private static final String SYSTEMNAME = "系统管理员";
	private static final String AQBMNAME = "安全保密管理员";
	private static final String AQSJNAME = "安全审计员";

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	public String lmid;
	@Column(columnDefinition = "varchar(255)")
	@ExcelAttribute(name = "ip地址")
	public String ip;
	@Column(columnDefinition = "varchar(255)")
	@ExcelAttribute(name = "操作人")
	public String operate_user;
	@Column(columnDefinition = "varchar(30)")
	@ExcelAttribute(name = "用户名")
	public String realname;
	@Column(columnDefinition = "varchar(100)")
	@ExcelAttribute(name = "机构")
	public String organ;
	@Column(columnDefinition = "varchar(255)")
	@ExcelAttribute(name = "模块")
	public String module;
	@Column(name = "start_time", columnDefinition = "varchar(255)")
	@ExcelAttribute(name = "操作时间")
	public String startTime;
	@Column(columnDefinition = "varchar(255)")
	public String end_time;
	@Column(columnDefinition = "varchar(255)")
	public String consume_time;
	@Column(columnDefinition = "varchar(500)")
	@ExcelAttribute(name = "操作描述")
	public String desci;
	/*@Column(columnDefinition = "varchar(30)")
	@ExcelAttribute(name = "用户组名")
	public String rolename;*/

	public Tb_log_msg_sx() {
	}

	public Tb_log_msg_sx(String ip, String operate_user, String realname, String organ, String start_time, String end_time,
                         String consume_time, String module, String desci) {
		this.ip = ip;
		this.operate_user = operate_user;
		this.realname = realname;
		this.organ = organ;
		this.startTime = start_time;
		this.end_time = end_time;
		this.consume_time = consume_time;
		this.module = module;
		this.desci = desci;
	}

	public String getId() {
		return lmid;
	}

	public void setId(String id) {
		this.lmid = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOperate_user() {
		return operate_user;
	}

	public void setOperate_user(String operate_user) {
		this.operate_user = operate_user;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getConsume_time() {
		return consume_time;
	}

	public void setConsume_time(String consume_time) {
		this.consume_time = consume_time;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDesci() {
		return desci;
	}

	public void setDesci(String desci) {
		this.desci = desci;
	}

	public static String getSystem() {
		return SYSTEM;
	}

	public static String getAqbm() {
		return AQBM;
	}

	public static String getAqsj() {
		return AQSJ;
	}

	public static String getSystemname() {
		return SYSTEMNAME;
	}

	public static String getAqbmname() {
		return AQBMNAME;
	}

	public static String getAqsjname() {
		return AQSJNAME;
	}

	/*public String getRolename() {return rolename;}

	public void setRolename(String rolename) {this.rolename = rolename;}*/
}