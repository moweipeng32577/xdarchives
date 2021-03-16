package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 实体档案盘点
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/25.
 */
@Entity
@Table(name = "ST_INVENTORY")
public class Inventory {

    public static final String RANGE_TYPE_ROOM = "room";
    public static final String RANGE_TYPE_ZONE = "zone";
    public static final String RANGE_TYPE_COL = "col";

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String checkid;                            //主键ID
    @Column(columnDefinition = "varchar(15)")
    private String checknum;                           //移库编号
    @Column(columnDefinition = "char(19)")
    private String checktime;                          //盘点时间
    @Column(columnDefinition = "varchar(50)")
    private String checkuser;                          //盘点人
    @Column(columnDefinition = "varchar(500)")
    private String description;                        //备注
    @Column(columnDefinition = "varchar(10)")
    private String rangetype;                          //盘点类型，库房room、区zone、列col
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shelvesid", columnDefinition = "char(36)")
    private ZoneShelves range;                          //盘点范围编号，对应密集架库房编号、区编号、列编号


    public String getCheckid() { return checkid; }

    public void setCheckid(String checkid) {
        this.checkid = checkid;
    }

    public String getChecknum() {
        return checknum;
    }

    public void setChecknum(String checknum) {
        this.checknum = checknum;
    }

    public String getChecktime() {
        return checktime;
    }

    public void setChecktime(String checktime) {
        this.checktime = checktime;
    }

    public String getCheckuser() {
        return checkuser;
    }

    public void setCheckuser(String checkuser) {
        this.checkuser = checkuser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRangetype() {
        return rangetype;
    }

    public void setRangetype(String rangetype) {
        this.rangetype = rangetype;
    }

    public ZoneShelves getRange() {
        return range;
    }

    public void setRange(ZoneShelves range) {
        this.range = range;
    }
}
