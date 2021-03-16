package com.wisdom.web.entity;

/**
 * Created by tanly on 2018/5/1 0001.
 */
public class Tb_user_organ_parents{
    private String organid;
    private String organname;
    private String parents;

    public String getOrganname() {
        return organname;
    }

    public void setOrganname(String organname) {
        this.organname = organname;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getParents() {
        return parents;
    }

    public void setParents(String parents) {
        this.parents = parents;
    }
}
