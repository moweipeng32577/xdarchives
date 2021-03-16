package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "lot_device_information")
public class DeviceInformation {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String inforid;//设备信息id
    private String id;//设备id
    private String devicename;//设备名
    private String devicecode;//设备编号
    private String manufacturers;//制作商
    private String installdate;//安装日期
    private String pthone;//联系电话
    private String adminuser;//管理
    private String maintenance;//维护


    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }

    public String getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(String manufacturers) {
        this.manufacturers = manufacturers;
    }

    public String getInstalldate() {
        return installdate;
    }

    public void setInstalldate(String installdate) {
        this.installdate = installdate;
    }

    public String getPthone() {
        return pthone;
    }

    public void setPthone(String pthone) {
        this.pthone = pthone;
    }

    public String getAdminuser() {
        return adminuser;
    }

    public void setAdminuser(String adminuser) {
        this.adminuser = adminuser;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }
}
