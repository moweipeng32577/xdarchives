package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 实体档案盘点结果实体
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Entity
@Table(name = "ST_INVENTORY_RESULT")
public class InventoryResult {

    //盘点多出的（结果中有，盘点范围没有，数据库没有）
    public static final String RESULT_TYPE_MORE = "1";
    //盘点有误的（结果中有，盘点范围没有，数据库有）
    public static final String RESULT_TYPE_DIFF_SHELVES = "4";
    //盘点有误的（结果中有，盘点范围有，标记为不在库，状态有误）
    public static final String RESULT_TYPE_DIFF_STATUS = "3";
    //盘点缺少的（结果中没有，盘点范围有）
    public static final String RESULT_TYPE_LESS = "2";

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String resultid;                            //主键ID
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checkid", columnDefinition = "char(36)")
    private Inventory check;
    @Column(columnDefinition = "char(1)")
    private String resulttype;
    @Column(columnDefinition = "varchar(64)")
    private String chipcode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storageid", columnDefinition = "char(36)")
    private Storage storage;


    public String getResultid() {
        return resultid;
    }

    public void setResultid(String resultid) {
        this.resultid = resultid;
    }

    public Inventory getCheck() {
        return check;
    }

    public void setCheck(Inventory check) {
        this.check = check;
    }

    public String getResulttype() {
        return resulttype;
    }

    public void setResulttype(String resulttype) {
        this.resulttype = resulttype;
    }

    public String getChipcode() {
        return chipcode;
    }

    public void setChipcode(String chipcode) {
        this.chipcode = chipcode;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}
