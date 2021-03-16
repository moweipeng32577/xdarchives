package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Set;

/**
 * 档案出库实体
 * 记录档案出库历史
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/19.
 */
@Entity
@Table(name="ST_OUTWARE")
public class OutWare {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String outid;                            //主键ID
    @Column(columnDefinition = "varchar(15)")
    private String warenum;                          //出库编号
    @Column(columnDefinition = "varchar(50)")
    private String waretype;                         //出库类型
    @Column(columnDefinition = "char(19)")
    private String waretime;                         //出库时间
    @Column(columnDefinition = "varchar(50)")
    private String wareuser;                         //出库人
    @Column(columnDefinition = "char(19)")
    private String confirmtime;                     //确认出库时间
    @Column(columnDefinition = "char(300)")
    private String borrowcodes;                     //出库单据编号
    @Column(columnDefinition = "varchar(500)")
    private String description;                     //备注
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Storage> storages;                   //关联档案集合，，一个单据对应多个实体

    public String getBorrowcodes() {
        return borrowcodes;
    }

    public void setBorrowcodes(String borrowcodes) {
        this.borrowcodes = borrowcodes;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }

    public String getWarenum() {
        return warenum;
    }

    public void setWarenum(String warenum) {
        this.warenum = warenum;
    }

    public String getWaretype() {
        return waretype;
    }

    public void setWaretype(String waretype) {
        this.waretype = waretype;
    }

    public String getWaretime() {
        return waretime;
    }

    public void setWaretime(String waretime) {
        this.waretime = waretime;
    }

    public String getWareuser() {
        return wareuser;
    }

    public void setWareuser(String wareuser) {
        this.wareuser = wareuser;
    }

    public String getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(String confirmtime) {
        this.confirmtime = confirmtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Storage> getStorages() {
        return storages;
    }

    public void setStorages(Set<Storage> storages) {
        this.storages = storages;
    }
}