package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * 记录档案入库历史
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/19.
 */
@Entity
@Table(name="ST_INWARE")
public class InWare_History {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String inid;                            //主键ID
    @Column(columnDefinition = "varchar(15)")
    private String warenum;                         //入库编号
    @Column(columnDefinition = "varchar(50)")
    private String waretype;                        //入库类型
    @Column(columnDefinition = "char(19)")
    private String waretime;                        //入库时间
    @Column(columnDefinition = "varchar(50)")
    private String wareuser;                        //入库人
    @Column(columnDefinition = "varchar(50)")
    private String returnware;                        //归还人
    @Column(columnDefinition = "char(19)")
    private String confirmtime;                     //确认入库时间
    @Column(columnDefinition = "varchar(500)")
    private String description;                        //备注
//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//    private Set<Storage> storages;                  //关联档案集合

    public String getInid() {
        return inid;
    }

    public void setInid(String inid) {
        this.inid = inid;
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

    public String getReturnware() {
        return returnware;
    }

    public void setReturnware(String returnware) {
        this.returnware = returnware;
    }


    //    public Set<Storage> getStorages() {
//        return storages;
//    }
//
//    public void setStorages(Set<Storage> storages) {
//        this.storages = storages;
//    }
}
