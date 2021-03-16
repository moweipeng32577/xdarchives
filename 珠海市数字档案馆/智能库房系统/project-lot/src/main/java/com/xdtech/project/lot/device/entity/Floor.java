package com.xdtech.project.lot.device.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author wujy
 */
@Entity
@Table(name = "lot_floor")
public class Floor {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String floorid;
    @Column(columnDefinition = "varchar(60) comment '楼层名称'")
    private String floorName;
    @Column(columnDefinition = "varchar(50) comment '楼层编码'")
    private String floorCode;
    @Column(columnDefinition = "varchar(50) comment '楼层类型'")
    private String floorType;
    @Column(columnDefinition = "varchar(200) comment '楼层平面图'")
    private String floorMap;
    @Column(columnDefinition = "varchar(200) comment '描述'")
    private String description;

    public String getFloorid() {
        return floorid;
    }

    public void setFloorid(String floorid) {
        this.floorid = floorid;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getFloorCode() {
        return floorCode;
    }

    public void setFloorCode(String floorCode) {
        this.floorCode = floorCode;
    }

    public String getFloorType() {
        return floorType;
    }

    public void setFloorType(String floorType) {
        this.floorType = floorType;
    }

    public String getFloorMap() {
        return floorMap;
    }

    public void setFloorMap(String floorMap) {
        this.floorMap = floorMap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
