package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/6/24.
 */
@Entity
public class Tb_place_defend {


    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String placeid;  //场地id
    @Column(columnDefinition = "varchar(20)")
    private String defendtype;  //维护类型
    @Column(columnDefinition = "varchar(50)")
    private String defenduser;   //登记人
    @Column(columnDefinition = "varchar(20)")
    private String phonenum;   //电话
    @Column(columnDefinition = "varchar(30)")
    private String defendtime;   //时间
    @Column(columnDefinition = "varchar(50)")
    private String defendcost;   //时间
    @Column(columnDefinition = "varchar(500)")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getDefendtype() {
        return defendtype;
    }

    public void setDefendtype(String defendtype) {
        this.defendtype = defendtype;
    }

    public String getDefenduser() {
        return defenduser;
    }

    public void setDefenduser(String defenduser) {
        this.defenduser = defenduser;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getDefendtime() {
        return defendtime;
    }

    public void setDefendtime(String defendtime) {
        this.defendtime = defendtime;
    }

    public String getDefendcost() {
        return defendcost;
    }

    public void setDefendcost(String defendcost) {
        this.defendcost = defendcost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
