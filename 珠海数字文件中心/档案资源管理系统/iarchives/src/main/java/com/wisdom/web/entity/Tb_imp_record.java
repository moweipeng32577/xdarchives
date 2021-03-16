package com.wisdom.web.entity;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tb_imp_record {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(255)")
    private String impuser;
    @Column(columnDefinition = "varchar(255)")
    private String imptime;
    @Column(columnDefinition = "varchar(255)")
    private String successcount;
    @Column(columnDefinition = "varchar(255)")
    private String defeatedcount;
    @Column(columnDefinition = "varchar(255)")
    private String imptype;


    public String getImptype() {
        return imptype;
    }

    public void setImptype(String imptype) {
        this.imptype = imptype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImpuser() {
        return impuser;
    }

    public void setImpuser(String impuser) {
        this.impuser = impuser;
    }

    public String getImptime() {
        return imptime;
    }

    public void setImptime(String imptime) {
        this.imptime = imptime;
    }

    public String getSuccesscount() {
        return successcount;
    }

    public void setSuccesscount(String successcount) {
        this.successcount = successcount;
    }

    public String getDefeatedcount() {
        return defeatedcount;
    }

    public void setDefeatedcount(String defeatedcount) {
        this.defeatedcount = defeatedcount;
    }
}
