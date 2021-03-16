package com.wisdom.web.entity;


/**
 * Created by Administrator on 2017/10/25 0025.
 */
public class ExtTree {

    private String fnid;
    private String text;
    private String cls;
    private boolean leaf;
    private boolean checked;
    private boolean expanded;
    private String roottype;
    private ExtTree[] children ;
    private String deviceid;
    private String type;
    private String areaid;
    private String fileClassId;

    public String getFileClassId() {
        return fileClassId;
    }

    public void setFileClassId(String fileClassId) {
        this.fileClassId = fileClassId;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExtTree[] getChildren() {
        return children;
    }

    public void setChildren(ExtTree[] children) {
        this.children = children;
    }

    public String getRoottype() {
        return roottype;
    }

    public void setRoottype(String roottype) {
        this.roottype = roottype;
    }

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}
