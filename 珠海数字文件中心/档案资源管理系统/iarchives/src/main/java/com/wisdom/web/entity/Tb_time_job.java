package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2020/1/3.
 * 定时任务实体
 */
@Entity
public class Tb_time_job {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    /**
     * 任务名称
     */
    @Column(columnDefinition = "varchar(150)")
    private String jobname;
    /**
     * 任务执行类
     */
    @Column(columnDefinition = "varchar(60)")
    private String jobclass;
    /**
     * 任务状态（0：未开启 1：开启）
     */
    @Column(columnDefinition = "char(1)")
    private String jobstate;
    /**
     * 运行周期
     */
    @Column(columnDefinition = "varchar(10)")
    private String runcycle;
    /**
     * 每月
     */
    @Column(columnDefinition = "varchar(10)")
    private String monthly;
    /**
     * 每周
     */
    @Column(columnDefinition = "varchar(10)")
    private String weekly;
    /**
     * 启动时间
     */
    @Column(columnDefinition = "varchar(10)")
    private String starttime;
    /**
     * 任务执行时间表达式
     */
    @Column(columnDefinition = "varchar(60)")
    private String cronexpression;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getJobclass() {
        return jobclass;
    }

    public void setJobclass(String jobclass) {
        this.jobclass = jobclass;
    }

    public String getJobstate() {
        return jobstate;
    }

    public void setJobstate(String jobstate) {
        this.jobstate = jobstate;
    }

    public String getRuncycle() {
        return runcycle;
    }

    public void setRuncycle(String runcycle) {
        this.runcycle = runcycle;
    }

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }

    public String getWeekly() {
        return weekly;
    }

    public void setWeekly(String weekly) {
        this.weekly = weekly;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getCronexpression() {
        return cronexpression;
    }

    public void setCronexpression(String cronexpression) {
        this.cronexpression = cronexpression;
    }
}
