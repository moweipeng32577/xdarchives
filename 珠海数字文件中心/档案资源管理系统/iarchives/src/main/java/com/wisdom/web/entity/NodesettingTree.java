package com.wisdom.web.entity;

/**
 * Created by tanly on 2017/10/30 0030.
 */
public class NodesettingTree {

    private String fnid;
    private String text;
    private String cls;
    private boolean leaf;
    private boolean expanded;
    private String roottype;
    private NodesettingTree[] children ;
    private Integer classlevel;
    //用于区分树节点是分类还是机构(1.机构 2.分类)
    private long nodeType;
    private Integer sortsequence;
    private String treetype;   //区分是档案系统节点还是声像系统节点
    private String organid;    //机构id

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getTreetype() {
        return treetype;
    }

    public void setTreetype(String treetype) {
        this.treetype = treetype;
    }

    public Integer getSortsequence() {
        return sortsequence;
    }

    public void setSortsequence(Integer sortsequence) {
        this.sortsequence = sortsequence;
    }

    public NodesettingTree[] getChildren() {
        return children;
    }

    public void setChildren(NodesettingTree[] children) {
        this.children = children;
    }

    public String getFnid() {
        return fnid==null?null:fnid.trim();
    }

    public void setFnid(String fnid) {
        this.fnid = fnid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setRoottype(String roottype) { this.roottype = roottype; }

    public String getRoottype() { return roottype; }

    public Integer getClasslevel() {
        return classlevel;
    }

    public void setClasslevel(Integer classlevel) {
        this.classlevel = classlevel;
    }

    public long getNodeType() {
        return nodeType;
    }

    public void setNodeType(long nodeType) {
        this.nodeType = nodeType;
    }
}
