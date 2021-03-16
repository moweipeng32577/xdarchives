package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/4/21.
 */
@Entity
public class Tb_car_order {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(30)")
    private String caruser;  //用车人
    @Column(columnDefinition = "varchar(100)")
    private String userorgan;  //单位
    @Column(columnDefinition = "varchar(20)")
    private String phonenumber;   //联系电话
    @Column(columnDefinition = "varchar(30)")
    private String ordertime; //预约时间
    @Column(columnDefinition = "varchar(30)")
    private String starttime;  //开始时间
    @Column(columnDefinition = "varchar(20)")
    private String endtime;   //结束时间
    @Column(columnDefinition = "varchar(200)")
    private String useway; //使用用途
    @Column(columnDefinition = "varchar(500)")
    private String remark;  //备注
    @Column(columnDefinition = "varchar(50)")
    private String auditlink;   //审核环节
    @Column(columnDefinition = "varchar(50)")
    private String auditer; //审核人
    @Column(columnDefinition = "varchar(20)")
    private String state; //预约状态
    @Column(columnDefinition = "varchar(500)")
    private String cancelreason; //取消原因
    @Column(columnDefinition = "char(36)")
    private String carid; //车辆id
    @Column(columnDefinition = "varchar(50)")
    private String ordercode; //单号
    @Column(columnDefinition = "char(36)")
    private String submiterid; //提交人id
    @Column(columnDefinition = "varchar(50)")
    private String carnumber; //车牌号码
    @Column(columnDefinition = "varchar(20)")
    private String returnstate; //归还状态
    @Column(columnDefinition = "varchar(50)")
    private String canceluser; //取消用户
    @Column(columnDefinition = "varchar(30)")
    private String canceltime; //取消时间
    @Column(columnDefinition = "varchar(1000)")
    private String approve; //审核记录
    @Column(columnDefinition = "varchar(30)")
    private String returntime; //归还时间

    public String getReturntime() {
        return returntime;
    }

    public void setReturntime(String returntime) {
        this.returntime = returntime;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public String getReturnstate() {
        return returnstate;
    }

    public void setReturnstate(String returnstate) {
        this.returnstate = returnstate;
    }

    public String getCanceluser() {
        return canceluser;
    }

    public void setCanceluser(String canceluser) {
        this.canceluser = canceluser;
    }

    public String getCanceltime() {
        return canceltime;
    }

    public void setCanceltime(String canceltime) {
        this.canceltime = canceltime;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public String getSubmiterid() {
        return submiterid;
    }

    public void setSubmiterid(String submiterid) {
        this.submiterid = submiterid;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCancelreason() {
        return cancelreason;
    }

    public void setCancelreason(String cancelreason) {
        this.cancelreason = cancelreason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaruser() {
        return caruser;
    }

    public void setCaruser(String caruser) {
        this.caruser = caruser;
    }

    public String getUserorgan() {
        return userorgan;
    }

    public void setUserorgan(String userorgan) {
        this.userorgan = userorgan;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getUseway() {
        return useway;
    }

    public void setUseway(String useway) {
        this.useway = useway;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAuditlink() {
        return auditlink;
    }

    public void setAuditlink(String auditlink) {
        this.auditlink = auditlink;
    }

    public String getAuditer() {
        return auditer;
    }

    public void setAuditer(String auditer) {
        this.auditer = auditer;
    }
}
