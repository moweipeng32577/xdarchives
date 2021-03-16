package com.wisdom.secondaryDataSource.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by tanly on 2018/4/17 0017.
 */
@Entity
@Table(name = "t_user")
public class OAUser {
    @Id
    private String idUser;
    private String idOrgunit;
    private String loginname;
    private String password;
    private String name;
    private String sex;
    private String phone;
    private String sort;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdOrgunit() {
        return idOrgunit;
    }

    public void setIdOrgunit(String idOrgunit) {
        this.idOrgunit = idOrgunit;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
