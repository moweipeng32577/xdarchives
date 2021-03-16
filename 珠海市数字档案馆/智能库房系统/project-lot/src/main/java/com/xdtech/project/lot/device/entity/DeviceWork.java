package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;

/**
 * @author wujy 2019/09/15
 */
@Entity
@Table(name = "lot_device_work")
public class DeviceWork {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String workId;
    @Column(columnDefinition = "varchar(50)")
    private String workType;//作业类型
    @ManyToOne(fetch = FetchType.EAGER)
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    @JoinColumn(name = "device",columnDefinition = "char(36)")
    private Device device;//设备主键
    @Column(columnDefinition = "varchar(30)")
    private String rqtime;//日期
    @Column(columnDefinition = "varchar(30)")
    private String xqtime;//星期
    @Column(columnDefinition = "varchar(30)")
    private String startTime;//开始时间
    @Column(columnDefinition = "varchar(30)")
    private String endTime;//结束时间
    @Column(columnDefinition = "varchar(20)")
    private String mode;//操作模式
    @Column(columnDefinition = "varchar(40)")
    private String createUser;//创建人
    @Column(columnDefinition = "datetime")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间
    @Column(columnDefinition = "varchar(30)")
    private String period;//周期
    @Column(columnDefinition = "varchar(30)")
    private String workTime;//创建时间
    @Column(columnDefinition = "int")
    private int status;//状态 生效或失效

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getRqtime() {
        return rqtime;
    }

    public void setRqtime(String rqtime) {
        this.rqtime = rqtime;
    }

    public String getXqtime() {
        return xqtime;
    }

    public void setXqtime(String xqtime) {
        this.xqtime = xqtime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
