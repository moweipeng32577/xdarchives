package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Leo on 2019/5/8 0008.
 */
@Entity
@Table(name = "RETENTION_INIT_PROBABILITY",indexes = {@Index(name="dr_index",columnList ="dr")})
public class RetentionInitProbability {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "double(3,2)")
    private Double Y;
    @Column(columnDefinition = "double(3,2)")
    private Double CQ;
    @Column(columnDefinition = "double(3,2)")
    private Double DQ;
    @Column(columnDefinition = "date")
    private Date createdate;
    @Column(columnDefinition = "int(1)")
    private Integer dr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getY() {
        return Y;
    }

    public void setY(Double y) {
        Y = y;
    }

    public Double getCQ() {
        return CQ;
    }

    public void setCQ(Double CQ) {
        this.CQ = CQ;
    }

    public Double getDQ() {
        return DQ;
    }

    public void setDQ(Double DQ) {
        this.DQ = DQ;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }
}
