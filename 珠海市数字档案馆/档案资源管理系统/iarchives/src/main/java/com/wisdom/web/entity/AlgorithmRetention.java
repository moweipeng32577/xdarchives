package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Rong on 2018/10/30.
 */
@Entity
@Table(name = "ALG_RENTION",indexes = {@Index(name="word_and_Retention",columnList ="retention,word")})
public class AlgorithmRetention {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(50)")
    private String word;
    @Column(columnDefinition = "varchar(10)")
    private String retention;
    @Column(columnDefinition = "integer")
    private int nums;
    @Column(columnDefinition = "date")
    private Date modifydate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getRetention() {
        return retention;
    }

    public void setRetention(String retention) {
        this.retention = retention;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public Date getModifydate() {
        return modifydate;
    }

    public void setModifydate(Date modifydate) {
        this.modifydate = modifydate;
    }
}
