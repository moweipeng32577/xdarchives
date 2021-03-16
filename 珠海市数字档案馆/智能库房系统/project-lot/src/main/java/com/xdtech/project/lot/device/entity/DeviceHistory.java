package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 设备运行记录
 * Created by Rong on 2019-03-22.
 */
@Entity
@Table(name = "lot_device_history")
public class DeviceHistory {

    public static final String HISTORY_TYPE = "HT";//历史记录类型

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String id;
    @Column(columnDefinition = "varchar(50) comment '设备类型(冗余)'")
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    @JoinColumn(name = "deviceid", columnDefinition = "char(36) comment '关联设备' ")
    private Device device;
    @Column(columnDefinition = "varchar(1000) comment '采集值'")
    private String captureValue;
    @Column(columnDefinition = "datetime comment '采集值'")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String captureTime;                                         //采集时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getCaptureValue() {
        return captureValue;
    }

    public void setCaptureValue(String captureValue) {
        this.captureValue = captureValue;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

}
