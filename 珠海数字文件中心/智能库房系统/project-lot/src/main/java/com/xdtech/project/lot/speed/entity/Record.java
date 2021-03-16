package com.xdtech.project.lot.speed.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 设备运行记录实体
 * Created by Rong on 2019-11-15.
 */
@Entity
public class Record {

    @Id
    private Integer no;                   //编号，主键
    @Column(name = "sensorindex")
    private Integer sensorIndex;        //设备编号
    @Column(name = "sensorvalue")
    private Double sensorValue;         //记录值
    private String time;                 //记录时间
    private String message;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getSensorIndex() {
        return sensorIndex;
    }

    public void setSensorIndex(Integer sensorIndex) {
        this.sensorIndex = sensorIndex;
    }

    public Double getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(Double sensorValue) {
        this.sensorValue = sensorValue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
