package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author wujy 2019/09/12
 */
@Entity
@Table(name = "lot_device_warning")
public class DeviceWarning {

    public static final String HT_WARNING = "温湿度告警";
    public static final String AF_WARNING = "安防告警";
    public static final String SJ_WARNING = "水浸告警";
    public static final String XF_WARNING = "消防告警";

    public static final String TEMP_OVERTOP = "温度过高";
    public static final String TEMP_OVERLOW = "温度过低";
    public static final String HUMI_OVERTOP = "湿度过高";
    public static final String HUMI_OVERLOW = "湿度过低";
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '设备主键ID'")
    private String warningId;
    @Column(columnDefinition = "varchar(50) '告警类型'")
    private String warningType;
    @ManyToOne
    @JoinColumn(columnDefinition = "char(36) '关联设备'",name = "device_id")
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private Device device;
    @Column(columnDefinition = "varchar(150) '告警描述'")
    private String description;
    @Column(columnDefinition = "int '处理状态'1:已处理，2:未处理")
    private int status;
    @Column(columnDefinition = "datetime '告警时间'")
    private String warningTime;
    @Column(columnDefinition = "datetime '记录创建时间'")
    private String createTime;

    public String getWarningId() {
        return warningId;
    }

    public void setWarningId(String warningId) {
        this.warningId = warningId;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(String warningTime) {
        this.warningTime = warningTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
