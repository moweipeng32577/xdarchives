package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
/**设备信息*/
public class Tb_equipment {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String equipmentID;//设备ID

    @Column(columnDefinition = "varchar(200)")
    private String name;//设备名称

    @Column(columnDefinition = "varchar(200)")
    private String type;//设备类型

    @Column(columnDefinition = "varchar(200)")
    private String brand;//品牌

    @Column(columnDefinition = "varchar(100)")
    private String model;//型号

    @Column(columnDefinition = "varchar(100)")
    private String specifications;//规格

    @Column(columnDefinition = "money")
    private BigDecimal price;//单价

    @Column(columnDefinition = "integer")
    private Integer amount;//数量

    @Column(columnDefinition = "varchar(20)")
    private String purchasetime;//购买时间

    @Column(columnDefinition = "varchar(20)")
    private String acceptancetime;//收货时间

    @Column(columnDefinition = "varchar(150)")
    private String ipaddress;//ip地址

    @Column(columnDefinition = "varchar(150)")
    private String organname;//所属部门

    @Column(columnDefinition = "varchar(200)")
    private String remarks;//备注

    @Column(columnDefinition = "varchar(200)")
    private String enclosure;//附件

    public Tb_equipment() {
    }

    public Tb_equipment(String name, String type, String brand, String model, String specifications, BigDecimal price, Integer amount, String purchasetime, String acceptancetime, String remarks, String enclosure) {
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.specifications = specifications;
        this.price = price;
        this.amount = amount;
        this.purchasetime = purchasetime;
        this.acceptancetime = acceptancetime;
        this.remarks = remarks;
        this.enclosure = enclosure;
        this.ipaddress = ipaddress;
        this.organname = organname;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getOrganname() {
        return organname;
    }

    public void setOrganname(String organname) {
        this.organname = organname;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPurchasetime() {
        return purchasetime;
    }

    public void setPurchasetime(String purchasetime) {
        this.purchasetime = purchasetime;
    }

    public String getAcceptancetime() {
        return acceptancetime;
    }

    public void setAcceptancetime(String acceptancetime) {
        this.acceptancetime = acceptancetime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    @Override
    public String toString() {
        return "Tb_equipment{" +
                "equipmentID='" + equipmentID + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", specifications='" + specifications + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", purchasetime='" + purchasetime + '\'' +
                ", acceptancetime='" + acceptancetime + '\'' +
                ", remarks='" + remarks + '\'' +
                ", enclosure='" + enclosure + '\'' +
                '}';
    }
}
