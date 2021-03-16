package com.wisdom.web.entity;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public class ExtNcTree {

    private String fnid;
    private String text;
    private String cls;
    private boolean leaf;
    private boolean expanded;

    public String getFnid() {
        return fnid;
    }

    public void setFnid(String fnid) {
        this.fnid = fnid;
    }
    //
//    public ExtTree(String text,String cls,boolean leaf,boolean checked,boolean expanded){
//        this.text = text;
//        this.cls = cls;
//        this.leaf = leaf;
//        this.checked = checked;
//        this.expanded = expanded;
//    }

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

}
