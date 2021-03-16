package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Administrator on 2020/4/28.
 */
@Entity
public class Tb_place_order {


    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(30)")
    private String placeuser;  //用车人
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
    private String placeid; //场地id
    @Column(columnDefinition = "varchar(50)")
    private String ordercode; //单号
    @Column(columnDefinition = "char(36)")
    private String submiterid; //提交人id
    @Column(columnDefinition = "varchar(50)")
    private String floor; //楼层
    @Column(columnDefinition = "varchar(20)")
    private String returnstate; //归还状态
    @Column(columnDefinition = "varchar(50)")
    private String canceluser; //取消用户
    @Column(columnDefinition = "varchar(30)")
    private String canceltime; //取消时间
    @Column(columnDefinition = "varchar(1000)")
    private String approve; //审核记录
    @Transient
    private String placeName;//场地名

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceuser() {
        return placeuser;
    }

    public void setPlaceuser(String placeuser) {
        this.placeuser = placeuser;
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

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getSubmiterid() {
        return submiterid;
    }

    public void setSubmiterid(String submiterid) {
        this.submiterid = submiterid;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
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
}
