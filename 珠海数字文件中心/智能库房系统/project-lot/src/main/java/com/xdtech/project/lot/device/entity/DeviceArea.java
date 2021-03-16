package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Rong on 2019-06-12.
 */
@Entity
@Table(name = "lot_device_area")
public class DeviceArea {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String id;
    @Column(columnDefinition = "varchar(50) comment '虚拟分区名称'")
    private String name;
    @Column(columnDefinition = "varchar(50)")
    private String type;
    @Column(columnDefinition = "varchar(50) comment '分区码'")
    private String code;
    @Column(columnDefinition = "int comment '排序字段'")
    private int sequence;
    @ManyToOne(fetch = FetchType.EAGER)
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    @JoinColumn(name = "floorid", columnDefinition = "char(36)")
    private Floor floor;
    @Column(columnDefinition = "varchar(500) comment '档案门类'")
    private String archivestype;

    public String getArchivestype() {
        return archivestype;
    }

    public void setArchivestype(String archivestype) {
        this.archivestype = archivestype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floorid) {
        this.floor = floorid;
    }
}
