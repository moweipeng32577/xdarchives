package com.wisdom.web.entity;

/**
 * Created by tanly on 2018/5/1 0001.
 */

public class Tb_user_node_parents {
    private String nodeid;
    private String nodename;
    private long nodetype;
    private String parents;
    private Integer classlevel;
    private Integer orders;
    private String refid;
    private String organid;

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodename() {
        return nodename;
    }

    public long getNodetype() {
        return nodetype;
    }

    public void setNodetype(long nodetype) {
        this.nodetype = nodetype;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getParents() {
        return parents;
    }

    public void setParents(String parents) {
        this.parents = parents;
    }

    public Integer getClasslevel() {
        return classlevel;
    }

    public void setClasslevel(Integer classlevel) {
        this.classlevel = classlevel;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }
}
