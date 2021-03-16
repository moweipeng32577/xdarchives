package com.wisdom.web.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/11/16 0016.
 */
public class FunctionButton {
    private String text;
    private String itemId;
    private String iconCls;
    private List<FunctionButton> menu;
    public FunctionButton(){}

    public FunctionButton(String text,String itemId, String iconCls){
        this.text = text;
        this.itemId = itemId;
        this.iconCls = iconCls;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public List<FunctionButton> getMenu() {
        return menu;
    }

    public void setMenu(List<FunctionButton> menu) {
        this.menu = menu;
    }
}