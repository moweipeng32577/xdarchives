package com.wisdom.web.entity;

/**
 * Created by yl on 2018/3/19.
 */
public class FileTree {
    private String fnid;
    private String parentid;
    private String text;
    private boolean leaf;
    private boolean expanded;

    public String getFnid() {
        return fnid;
    }

    public void setFnid(String fnid) {
        this.fnid = fnid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
