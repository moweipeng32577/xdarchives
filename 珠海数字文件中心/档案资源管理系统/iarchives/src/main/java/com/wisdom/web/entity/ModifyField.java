package com.wisdom.web.entity;

/**
 * Created by RonJiang on 2018/1/26 0026.
 */
public class ModifyField {
    private String fieldcode;
    private String fieldname;
    private String fieldvalue;

    public ModifyField() {
    }

    public ModifyField(String fieldcode, String fieldname, String fieldvalue) {
        this.fieldcode = fieldcode;
        this.fieldname = fieldname;
        this.fieldvalue = fieldvalue;
    }

    public String getFieldcode() {
        return fieldcode;
    }

    public void setFieldcode(String fieldcode) {
        this.fieldcode = fieldcode;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldvalue(String fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

    @Override
    public String toString() {
        return "ModifyField{" +
                "fieldname='" + fieldname + '\'' +
                ", fieldcode='" + fieldcode + '\'' +
                ", fieldvalue='" + fieldvalue + '\'' +
                '}';
    }
}
