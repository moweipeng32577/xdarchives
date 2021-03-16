package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author wujy 2019/09/11
 */
@Entity
@Table(name = "lot_device_operate_record")
public class DeviceOperateRecord {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)",name = "record_id")
    private String recordId;
    @ManyToOne
    @JoinColumn(columnDefinition = "char(36)",name = "device_id")
    private Device device;
    @Column(columnDefinition = "varchar(150)")
    private String description;
    @Column(columnDefinition = "datetime",name = "operate_time")
    private String operateTime;
    @Column(columnDefinition = "datetime",name = "operate_user")
    private String operateUser;
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }
}
