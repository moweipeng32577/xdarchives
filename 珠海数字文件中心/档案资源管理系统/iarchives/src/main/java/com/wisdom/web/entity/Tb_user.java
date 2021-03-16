package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
// @XmlRootElement(name="Tb_user")
// @XmlAccessorType(XmlAccessType.FIELD)
public class Tb_user implements UserDetails, Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String userid;
	@Column(columnDefinition = "varchar(100)")
	private String servicesname;
	@Column(columnDefinition = "varchar(100)")
	private String systemname;
	@Transient
	private String organfullname;
	@Transient
	private String organid;
	@Column(columnDefinition = "varchar(30)")
	private String loginname;
	@Column(columnDefinition = "varchar(50)")
	private String loginpassword;
	@Column(columnDefinition = "varchar(30)")
	private String realname;
	@Column(columnDefinition = "varchar(10)")
	private String sex;
	@Column(columnDefinition = "varchar(50)")
	private String nickname;
	@Column(columnDefinition = "varchar(50)")
	private String phone;
	@Column(columnDefinition = "varchar(100)")
	private String address;
	@Column(columnDefinition = "datetime")
	private Date createtime;
	@Column(columnDefinition = "varchar(20)")
	private String login_ip;
	// @Column(columnDefinition = "datetime")//mysql
	// @Column(columnDefinition = "date")//oracle
	private Date login_time;
	// @Column(columnDefinition = "datetime")//mysql
	// @Column(columnDefinition = "date")//oracle
	private Date logout_time;
	@Column(columnDefinition = "integer")
	private Long status;
	@Column(columnDefinition = "varchar(10)")
	private String usertype;
	@Column(columnDefinition = "integer")
	private Integer sortsequence;
	@ManyToOne
	@JoinColumn(name = "organid", columnDefinition = "char(36)")
	private Tb_right_organ organ;
	@Column(columnDefinition = "varchar(10)")
	private String platformchange;
	@Column(columnDefinition = "varchar(50)")
	private String letternumber;  //外来人员介绍信编号
	@Column(columnDefinition = "varchar(200)")
	private String remark;  //备注
	@Column(columnDefinition = "varchar(30)")
	private String outuserstate;  //外来人员标识
	@Column(columnDefinition = "datetime")
	private Date outuserstarttime; //外来人员开始使用账号时间
	@Column(columnDefinition = "varchar(10)")
	private String infodate; //外来人员账号有效期
	@Column(columnDefinition = "datetime")
	private Date exdate; //外来人员账号到期时间
	@Column(columnDefinition = "varchar(10)")
	private String birthday; //出生年月
	@Column(columnDefinition = "varchar(30)")
	private String ethnic; //民族
	@Column(columnDefinition = "varchar(50)")
	private String duty;//职务
	@Column(columnDefinition = "varchar(50)")
	private String organusertype;//机构人员类型
    @Transient
    private String replaceOrganid;//oranid字段被设置为了organname 该字段替换为organid

    public String getReplaceOrganid() {
        return replaceOrganid;
    }

    public void setReplaceOrganid(String replaceOrganid) {
        this.replaceOrganid = replaceOrganid;
    }

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getOrganusertype() {
		return organusertype;
	}

	public void setOrganusertype(String organusertype) {
		this.organusertype = organusertype;
	}

	public Date getOutuserstarttime() {
		return outuserstarttime;
	}

	public void setOutuserstarttime(Date outuserstarttime) {
		this.outuserstarttime = outuserstarttime;
	}

	public String getLetternumber() {
		return letternumber;
	}

	public void setLetternumber(String letternumber) {
		this.letternumber = letternumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOutuserstate() {
		return outuserstate;
	}

	public void setOutuserstate(String outuserstate) {
		this.outuserstate = outuserstate;
	}

	public String getPlatformchange() {
		return platformchange;
	}

	public void setPlatformchange(String platformchange) {
		this.platformchange = platformchange;
	}

	public Tb_right_organ getOrgan() {
		return organ;
	}

	public void setOrgan(Tb_right_organ organ) {
		this.organ = organ;
	}

	public Integer getSortsequence() {
		return sortsequence;
	}

	public void setSortsequence(Integer sortsequence) {
		this.sortsequence = sortsequence;
	}

	// public List<Tb_role> getTb_user_role() {
	// return tb_user_role;
	// }
	//
	// public void setTb_user_role(List<Tb_role> tb_user_role) {
	// this.tb_user_role = tb_user_role;
	// }

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	// @ManyToMany
	// @JoinTable(
	// name="tb_user_role",
	// joinColumns = {
	// @JoinColumn(name="userid")
	// },
	// inverseJoinColumns = {
	// @JoinColumn(name="roleid")
	// }
	// )
	// private List<Tb_role> tb_user_role;

	// public List<Tb_role> getRoles() {
	// return tb_user_role;
	// }
	//
	// public void setRoles(List<Tb_role> roles) {
	// this.tb_user_role = roles;
	// }

	public Tb_user() {
	};

	public Tb_user(String userid, String realname) {
		this.userid = userid;
		this.realname = realname;
	}

	public Tb_user(String loginname, String realname, String sex, String phone, String address, String letternumber, String remark, String infodate, String birthday, String ethnic) {
		this.loginname = loginname;
		this.realname = realname;
		this.sex = sex;
		this.phone = phone;
		this.address = address;
		this.letternumber = letternumber;
		this.remark = remark;
		this.infodate = infodate;
		this.birthday = birthday;
		this.ethnic = ethnic;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getServicesname() {
		return servicesname;
	}

	public void setServicesname(String servicesname) {
		this.servicesname = servicesname;
	}

	public String getSystemname() {
		return systemname;
	}

	public void setSystemname(String systemname) {
		this.systemname = systemname;
	}
	
	public String getOrganfullname() {
		return organfullname;
	}

	public void setOrganfullname(String organfullname) {
		this.organfullname = organfullname;
	}

	public String getOrganid() {
		if (this.getOrgan() != null) {
			return this.getOrgan().getOrganid();
		}
		return this.organid;
	}

	public void setOrganid(String organid) {
		this.organid = organid;
		Tb_right_organ node = new Tb_right_organ();
		node.setOrganid(organid);
		this.setOrgan(node);
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getLoginpassword() {
		return loginpassword;
	}

	public void setLoginpassword(String loginpassword) {
		this.loginpassword = loginpassword;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogin_ip() {
		return login_ip;
	}

	public void setLogin_ip(String login_ip) {
		this.login_ip = login_ip;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getLogin_time() {
		return login_time;
	}

	public void setLogin_time(Date login_time) {
		this.login_time = login_time;
	}

	public Date getLogout_time() {
		return logout_time;
	}

	public void setLogout_time(Date logout_time) {
		this.logout_time = logout_time;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}
	
	public String getInfodate() {
		return infodate;
	}

	public void setInfodate(String infodate) {
		this.infodate = infodate;
	}
	
	public Date getExdate() {
		return exdate;
	}

	public void setExdate(Date exdate) {
		this.exdate = exdate;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEthnic() {
		return ethnic;
	}

	public void setEthnic(String ethnic) {
		this.ethnic = ethnic;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auths = new ArrayList<>();
		// List<Tb_role> roles = this.getRoles();
		// for (Tb_role role : roles) {
		// auths.add(new SimpleGrantedAuthority(role.getRolename()));
		// }
		return auths;
	}

	@Override
	public String getPassword() {
		return this.loginpassword;
	}

	@Override
	public String getUsername() {
		return this.loginname;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}