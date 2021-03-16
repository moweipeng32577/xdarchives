package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Leo on 2020/5/22 0022.
 */
@Entity
@Table(name = "tb_data_node")
public class Tb_data_node_sx {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String nodeid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String parentnodeid;
    @Column(columnDefinition = "integer")
    private Integer nodelevel;
    @Column(columnDefinition = "char(1)")
    private boolean leaf;
    @Column(columnDefinition = "integer")
    private int nodetype; // nodetype:2为分类节点，1为机构节点
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String refid;
    @Column(columnDefinition = "varchar(150)")
    private String nodename;
    @Column(columnDefinition = "varchar(60)")
    private String nodecode;
    @Column(columnDefinition = "integer")
    private Integer sortsequence;
    @Column(columnDefinition = "integer")
    private Integer classlevel; // 冗余字段，记录当前节点的分类层级
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String classid; // 冗余字段，记录当前节点的分类ID
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid; // 冗余字段，记录当前节点的机构ID
    @Column(columnDefinition = "char(1)")
    private String luckstate; // 模板锁定状态,0:未锁定,1:已锁定
    @Column(columnDefinition = "varchar(30)")
    private String type; // 冗余字段，记录当前节点的机构ID

    public Integer getOrders() {
        return sortsequence;
    }

    public void setOrders(Integer orders) {
        this.sortsequence = orders;
    }

    public String getNodeid() {
        return nodeid==null?null:nodeid.trim();
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public Integer getLevel() {
        return nodelevel;
    }

    public void setLevel(Integer level) {
        this.nodelevel = level;
    }

    public String getParentnodeid() {
        return parentnodeid==null?null:parentnodeid.trim();
    }

    public void setParentnodeid(String parentnodeid) {
        this.parentnodeid = parentnodeid;
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Integer getNodetype() {
        return nodetype;
    }

    public void setNodetype(int nodetype) {
        this.nodetype = nodetype;
    }

    public String getRefid() {
        return refid==null?null:refid.trim();
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getNodecode() {
        return nodecode;
    }

    public void setNodecode(String nodecode) {
        this.nodecode = nodecode;
    }

    public Integer getClasslevel() {
        return classlevel;
    }

    public void setClasslevel(Integer classlevel) {
        this.classlevel = classlevel;
    }

    public String getClassid() {
        return classid==null?null:classid.trim();
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getOrganid() {
        return organid==null?null:organid.trim();
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getLuckstate() {
        return luckstate;
    }

    public void setLuckstate(String luckstate) {
        this.luckstate = luckstate;
    }

    public Integer getNodelevel() {
        return nodelevel;
    }

    public void setNodelevel(Integer nodelevel) {
        this.nodelevel = nodelevel;
    }

    public Integer getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(Integer sortsequence) {
        this.sortsequence = sortsequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Tb_data_node_sx() {
    }

    public Tb_data_node_sx(String nodeid, String parentnodeid) {
        this.nodeid = nodeid;
        this.parentnodeid = parentnodeid;
    }

    public Tb_data_node_sx(String nodeid, String nodename, int nodetype, Integer classlevel, Integer sortsequence) {
        try {
            this.nodeid = nodeid;
            this.nodename = nodename;
            this.nodetype = nodetype;
            this.classlevel = classlevel == null ? 0 : classlevel;
            this.sortsequence = sortsequence == null ? 0 : sortsequence;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Tb_data_node_sx node = (Tb_data_node_sx) o;
        return nodeid.equals(node.nodeid);
    }
}
