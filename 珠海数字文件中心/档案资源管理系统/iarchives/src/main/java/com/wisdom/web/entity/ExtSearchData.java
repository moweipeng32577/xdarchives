package com.wisdom.web.entity;

/**
 * Created by RonJiang on 2017/11/20 0020.
 */
public class ExtSearchData {
    private String item;
    private String name;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExtSearchData{" +
                "item='" + item + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
