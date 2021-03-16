package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Rong on 2019-06-17.
 */
@Entity
@Table(name = "lot_device_link")
public class DeviceLink {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String id;
    @ManyToOne
    @JoinColumn(name = "deviceId", columnDefinition = "char(36) comment '响应设备'")
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private Device device;
    @Column(columnDefinition = "varchar(50) comment '响应事件'")
    private String event;
    @ManyToOne
    @JoinColumn(name = "linkAreaId", columnDefinition = "char(36) comment '联动分区' ")
    private DeviceArea linkArea;
    @ManyToOne
    @JoinColumn(name = "linkDeviceId", columnDefinition = "char(36) comment '联动设备'")
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private Device linkDevice;
    @Column(columnDefinition = "varchar(50) comment '联动事件'")
    private String linkEvent;
    @Column(columnDefinition = "int comment '排序字段'")
    private int sequence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public DeviceArea getLinkArea() {
        return linkArea;
    }

    public void setLinkArea(DeviceArea linkArea) {
        this.linkArea = linkArea;
    }

    public Device getLinkDevice() {
        return linkDevice;
    }

    public void setLinkDevice(Device linkDevice) {
        this.linkDevice = linkDevice;
    }

    public String getLinkEvent() {
        return linkEvent;
    }

    public void setLinkEvent(String linkEvent) {
        this.linkEvent = linkEvent;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
