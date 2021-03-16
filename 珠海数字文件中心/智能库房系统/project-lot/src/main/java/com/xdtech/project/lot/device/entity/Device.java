package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 智能设备实体
 * Created by Rong on 2019-01-16.
 */
@Entity
@Table(name = "lot_device")
public class Device {

    public static final int STATUS_ONLINE = 1;        //在线
    public static final int STATUS_OFFLINE = 0;       //离线
    public static final int STATUS_PROTECTION = 2;     //布防
    public static final int STATUS_REMOVAL = 3;         //撤防

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '设备主键ID'")
    private String id;
    @Column(columnDefinition = "int comment '设备状态，1为在线，0为离线'")
    private int status;
    @Column(columnDefinition = "varchar(50) comment '设备名称'")
    private String name;
    @Column(columnDefinition = "varchar(50) comment '设备编码，部分设备通过编码进行通讯'")
    private String code;
    @ManyToOne(fetch = FetchType.EAGER)
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    @JoinColumn(name = "type", columnDefinition = "char(36)")
    private DeviceType type;
    @Column(columnDefinition = "varchar(50) comment '设备品牌'")
    private String brand;
    @Column(columnDefinition = "varchar(50) comment '设备型号'")
    private String model;
    @Column(columnDefinition = "varchar(1000) comment '设备属性'")
    private String prop;
    @Column(columnDefinition = "varchar(20) comment '设备坐标大小，格式如x，y，width，height'")
    private String coordinate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    @JoinColumn(name = "area", columnDefinition = "char(36)")
    private DeviceArea area;
    @Column(columnDefinition = "varchar(50) comment '设备类型名，如HT、JK'",name = "type_name")
    private String typeName;
    @Column(columnDefinition = "char(5) comment '禁用/启用设备'",name = "enabled")
    private String enabled ;
    @Column(columnDefinition = "varchar(20) comment '排序'")
    private String sort;

    public Device() {
    }

    public Device(String id, String name, DeviceType type,String typeName){
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeName = typeName;
    }

    public Device(String id, String name, DeviceType type,String typeName,String enabled){
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.enabled = enabled;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id==null?null:id.trim();
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public DeviceArea getArea() {
        return area;
    }

    public void setArea(DeviceArea area) {
        this.area = area;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;
        Device device = (Device) o;
        return Objects.equals(getId(), device.getId()) &&
                Objects.equals(getStatus(), device.getStatus()) &&
                Objects.equals(getName(), device.getName()) &&
                Objects.equals(getCode(), device.getCode()) &&
                Objects.equals(getType(), device.getType()) &&
                Objects.equals(getBrand(), device.getBrand()) &&
                Objects.equals(getModel(), device.getModel()) &&
                Objects.equals(getProp(), device.getProp()) &&
                Objects.equals(getCoordinate(), device.getCoordinate()) &&
                Objects.equals(getArea(), device.getArea()) &&
                Objects.equals(getTypeName(), device.getTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus(), getName(), getCode(), getType(), getBrand(), getModel(), getProp(), getCoordinate(), getArea(), getTypeName());
    }
}
