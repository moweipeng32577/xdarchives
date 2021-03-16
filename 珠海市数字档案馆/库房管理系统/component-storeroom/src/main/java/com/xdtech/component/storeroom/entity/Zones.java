package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import javax.persistence.*;

/**
 * 实体档案库区实体类
 * 对应密集架
 *
 * Created by Rong on 2018/5/5.
 */
@Entity
@Table(name="ST_ZONES")
public class Zones {

    public Zones(){

    }

    public Zones(String zoneid){
        this.zoneid = zoneid;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String zoneid;                       //主键ID
    @Column(columnDefinition = "char(16)")
    private String city;                         //地区编码
    @Column(columnDefinition = "varchar(50)")
    private String citydisplay;                 //地区名称
    @Column(columnDefinition = "char(16)")
    private String unit;                         //单位编码
    @Column(columnDefinition = "varchar(50)")
    private String unitdisplay;                 //单位名称
    @Column(columnDefinition = "char(2)")
    private String room;                         //库房编码
    @Column(columnDefinition = "varchar(30)")
    private String roomdisplay;                 //库房名称
    @Column(columnDefinition = "char(2)")
    private String zone;                         //区编码
    @Column(columnDefinition = "varchar(20)")
    private String zonedisplay;                 //区名称
    @Column(columnDefinition = "integer")
    private Integer countcol;                    //列数
    @Column(columnDefinition = "integer")
    private Integer countsection;               //节数
    @Column(columnDefinition = "integer")
    private Integer countlayer;                 //层数
    @Column(columnDefinition = "varchar(500)")
    private String storageproperty;      //档案类型
    @Column(columnDefinition = "varchar(10)")
    private String fixed;      //固定列
    @Column(columnDefinition = "char(2)")
    private String floor;
    @Column(columnDefinition = "varchar(50)")
    private String floordisplay;
    @Column(columnDefinition = "char(36)") //设备id
    private String device;
    @Transient
    private Integer capacity; //临时容量
    @Transient
    private Integer usecapacity;//临时使用数量
    @Transient
    private String usage; //临时使用率

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCitydisplay() {
        return citydisplay;
    }

    public void setCitydisplay(String citydisplay) {
        this.citydisplay = citydisplay;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitdisplay() {
        return unitdisplay;
    }

    public void setUnitdisplay(String unitdisplay) {
        this.unitdisplay = unitdisplay;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoomdisplay() {
        return roomdisplay;
    }

    public void setRoomdisplay(String roomdisplay) {
        this.roomdisplay = roomdisplay;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getZonedisplay() {
        return zonedisplay;
    }

    public void setZonedisplay(String zonedisplay) {
        this.zonedisplay = zonedisplay;
    }

    public Integer getCountcol() {
        return countcol;
    }

    public void setCountcol(Integer countcol) {
        this.countcol = countcol;
    }

    public Integer getCountsection() {
        return countsection;
    }

    public void setCountsection(Integer countsection) {
        this.countsection = countsection;
    }

    public Integer getCountlayer() {
        return countlayer;
    }

    public void setCountlayer(Integer countlayer) {
        this.countlayer = countlayer;
    }

    public String getStorageproperty() {
        return storageproperty;
    }

    public void setStorageproperty(String storageproperty) {
        this.storageproperty = storageproperty;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFloordisplay() {
        return floordisplay;
    }

    public void setFloordisplay(String floordisplay) {
        this.floordisplay = floordisplay;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getUsecapacity() {
        return usecapacity;
    }

    public void setUsecapacity(Integer usecapacity) {
        this.usecapacity = usecapacity;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
