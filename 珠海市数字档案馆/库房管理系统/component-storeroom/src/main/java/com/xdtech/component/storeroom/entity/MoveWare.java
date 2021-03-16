package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 档案移库实体对象
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/24.
 */
@Entity
@Table(name = "ST_MOVEWARE")
public class MoveWare {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String moveid;                          //主键ID
    @Column(columnDefinition = "varchar(15)")
    private String warenum;                         //移库编号
    @Column(columnDefinition = "char(19)")
    private String waretime;                        //移库时间
    @Column(columnDefinition = "varchar(50)")
    private String wareuser;                        //移库人
    @Column(columnDefinition = "varchar(500)")
    private String description;                     //备注
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Storage> storages;                  //关联档案集合，，一个单据对应多个实体

    public String getMoveid() {
        return moveid;
    }

    public void setMoveid(String moveid) {
        this.moveid = moveid;
    }

    public String getWarenum() {
        return warenum;
    }

    public void setWarenum(String warenum) {
        this.warenum = warenum;
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