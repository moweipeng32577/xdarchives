package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/7/3.
 */
@Entity
public class Szh_assembly_preflow {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String id;         //主键
    private String assemblyid;   //流水线id
    private String assemblyflowid;  //环节id
    private String preflowid;   //前置环节id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssemblyid() {
        return assemblyid;
    }

    public void setAssemblyid(String assemblyid) {
        this.assemblyid = assemblyid;
    }

    public String getAssemblyflowid() {
        return assemblyflowid;
    }

    public void setAssemblyflowid(String assemblyflowid) {
        this.assemblyflowid = assemblyflowid;
    }

    public String getPreflowid() {
        return preflowid;
    }

    public void setPreflowid(String preflowid) {
        this.preflowid = preflowid;
    }
}
