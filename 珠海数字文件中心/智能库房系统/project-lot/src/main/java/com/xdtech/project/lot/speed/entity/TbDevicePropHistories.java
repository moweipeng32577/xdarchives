package com.xdtech.project.lot.speed.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 设备运行记录实体
 * Created by Rong on 2019-11-15.
 */
@Entity
@Table(name = "tb_device_prop_histories")
public class TbDevicePropHistories {

    @Id
    private String hisId;
    private String deviceId;       //设备编号
    private String propId;        //设备编号
    private String propValue;     //记录值
    private String valueTime;     //记录时间


    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getValueTime() {
        return valueTime;
    }

    public void setValueTime(String valueTime) {
        this.valueTime = valueTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
