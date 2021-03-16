package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 设备类型
 * Created by wagnmh on 2020-03-16.
 */
@Entity
@Table(name = "lot_device_type")
public class DeviceType {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '设备类型主键ID'")
    private String id;
    @Column(columnDefinition = "varchar(50) comment '设备类型名称'")
    private String typeName;
    @Column(columnDefinition = "varchar(50) comment '设备类型编码'")
    private String typeCode;
    @Column(columnDefinition = "varchar(50) comment '设备平面图'")
    private String typeMap;

    public String getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(String typeMap) {
        this.typeMap = typeMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
