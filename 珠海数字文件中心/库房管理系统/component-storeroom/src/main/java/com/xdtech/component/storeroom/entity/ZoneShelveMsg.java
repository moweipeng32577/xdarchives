package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/24 0024.
 *
 */
public class ZoneShelveMsg{
    private String shid;                         //主键ID
    private String col;                           //列编码
    private String coldisplay;                   //列名称
    private String section;                      //节编码
    private String sectiondisplay;              //节名称
    private String layer;                         //层编码
    private String layerdisplay;                 //层名称
    private String side;                          //面号编码
    private String sidedisplay;                  //面号名称
    private Integer capacity;                     //存储量
    private Integer usecapacity;                  //使用量

    public ZoneShelveMsg(String shid, String col, String coldisplay, String section, String sectiondisplay, String layer, String layerdisplay, String side, String sidedisplay, Integer capacity, Integer usecapacity) {
        this.shid = shid;
        this.col = col;
        this.coldisplay = coldisplay;
        this.section = section;
        this.sectiondisplay = sectiondisplay;
        this.layer = layer;
        this.layerdisplay = layerdisplay;
        this.side = side;
        this.sidedisplay = sidedisplay;
        this.capacity = capacity;
        this.usecapacity = usecapacity;
    }


    public String getShid() {
        return shid;
    }

    public void setShid(String shid) {
        this.shid = shid;
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
}
