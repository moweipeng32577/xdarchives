package com.wisdom.secondaryDataSource.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by tanly on 2018/4/11 0011.
 */
@Entity
@Table(name = "t_orgunit")
public class OrgUnit {
    @Id
    private String idOrgunit;
    private String idParentorgunit;
    private String code;
    private String name;
    private String sort;

    public String getIdOrgunit() {
        return idOrgunit;
    }

    public void setIdOrgunit(String idOrgunit) {
        this.idOrgunit = idOrgunit;
    }

    public String getIdParentorgunit() {
        return idParentorgunit;
    }

    public void setIdParentorgunit(String idParentorgunit) {
        this.idParentorgunit = idParentorgunit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
