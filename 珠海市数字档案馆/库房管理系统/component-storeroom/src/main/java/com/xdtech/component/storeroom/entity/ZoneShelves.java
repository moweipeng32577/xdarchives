package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 实体档案存储位置实体类
 * 对应单元格
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
@Entity
@Table(name="ST_ZONE_SHELVES")
public class ZoneShelves {
    
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String shid;                         //主键ID
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zoneid", columnDefinition = "char(36)")
    private Zones zone;                          //密集架区对象
    @Column(columnDefinition = "char(2)")
    private String col;                           //列编码
    @Column(columnDefinition = "varchar(20)")
    private String coldisplay;                   //列名称
    @Column(columnDefinition = "char(2)")
    private String section;                      //节编码
    @Column(columnDefinition = "varchar(20)")
    private String sectiondisplay;              //节名称
    @Column(columnDefinition = "char(2)")
    private String layer;                         //层编码
    @Column(columnDefinition = "varchar(20)")
    private String layerdisplay;                 //层名称
    @Column(columnDefinition = "char(2)")
    private String side;                          //面号编码
    @Column(columnDefinition = "varchar(20)")
    private String sidedisplay;                  //面号名称
    @Column(columnDefinition = "integer")
    private Integer capacity;                     //存储量
    @Column(columnDefinition = "integer")
    private Integer usecapacity;                  //使用量
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zoneShelves")
    private Set<Storage> storages;                //存储的档案集合
    @Transient
    private String rate;         //已使用比率

    public String getShid() {
        return shid;
    }

    public void setShid(String shid) {
        this.shid = shid;
    }

    public Zones getZone() {
        return zone;
    }

    public void setZone(Zones zone) {
        this.zone = zone;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getColdisplay() {
        return coldisplay;
    }

    public void setColdisplay(String coldisplay) {
        this.coldisplay = coldisplay;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectiondisplay() {
        return sectiondisplay;
    }

    public void setSectiondisplay(String sectiondisplay) {
        this.sectiondisplay = sectiondisplay;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getLayerdisplay() {
        return layerdisplay;
    }

    public void setLayerdisplay(String layerdisplay) {
        this.layerdisplay = layerdisplay;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getSidedisplay() {
        return sidedisplay;
    }

    public void setSidedisplay(String sidedisplay) {
        this.sidedisplay = sidedisplay;
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

    public Set<Storage> getStorages() {
        return storages;
    }

    public void setStorages(Set<Storage> storages) {
        this.storages = storages;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}