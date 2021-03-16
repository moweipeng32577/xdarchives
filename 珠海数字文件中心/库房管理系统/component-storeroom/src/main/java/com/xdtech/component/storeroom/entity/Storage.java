package com.xdtech.component.storeroom.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 实体档案类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/19.
 */
@Entity
@Table(name="ST_STORAGE")
public class Storage {

    public static final String STATUS_IN = "已入库";
    public static final String STATUS_OUT = "已出库";
    public static final String STATUS_TRANSFOR = "已调出";
    public static final String STATUS_DESTORY = "已销毁";

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String stid;                             //主键ID
    @Column(columnDefinition = "varchar(64)")
    private String chipcode;                        //rfid标签号
    @Column(columnDefinition = "varchar(20)")
    private String storestatus;                     //库存状态
    @Column(columnDefinition = "char(36)")
    private String entry;                            //电子档案主键


    @ManyToOne(fetch = FetchType.LAZY)
    private ZoneShelves zoneShelves;
    @JSONField(serialize = false)
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "storages")
    private Set<InWare> inwares;
    @JSONField(serialize = false)
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "storages")
    private Set<OutWare> outwares;

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getChipcode() {
        return chipcode;
    }

    public void setChipcode(String chipcode) {
        this.chipcode = chipcode;
    }

    public String getStorestatus() {
        return storestatus;
    }

    public void setStorestatus(String storestatus) {
        this.storestatus = storestatus;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public ZoneShelves getZoneShelves() { return zoneShelves; }

    public void setZoneShelves(ZoneShelves zoneShelves) {
        this.zoneShelves = zoneShelves;
    }

    public Set<InWare> getInwares() {
        return inwares;
    }

    public void setInwares(Set<InWare> inwares) {
        this.inwares = inwares;
    }

    public Set<OutWare> getOutwares() {
        return outwares;
    }

    public void setOutwares(Set<OutWare> outwares) {
        this.outwares = outwares;
    }

}