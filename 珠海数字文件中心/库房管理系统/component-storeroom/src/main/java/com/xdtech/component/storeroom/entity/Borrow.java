package com.xdtech.component.storeroom.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 借阅档案关联库房位置
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/25.
 */
@Entity
@Table(name = "ST_BORROW")
public class Borrow {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String id;                            //主键ID
    @Column(columnDefinition = "char(36)")
    private String entryid;                           //entryid
    @Column(columnDefinition = "char(36)")
    private String docid;                          //借阅单id
    @Column(columnDefinition = "char(36)")
    private String stid;                          //实体库房ID
    @Column(columnDefinition = "varchar(20)")
    private String status;                        //是否已经查看


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
