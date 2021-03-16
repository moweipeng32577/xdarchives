package com.wisdom.web.entity;

import com.alibaba.fastjson.JSON;
import org.springframework.data.domain.Sort;

/**
 * 排序实体类
 * Created by Rong on 2018/6/2.
 */
public class WebSort {

    private String property;    //排序字段
    private String direction;   //排序方式

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }


    public static Sort getSortByJson(String json){
        WebSort webSort = null;
        Sort sortobj = null;
        if(json != null && !"".equals(json)){
            webSort = JSON.parseArray(json, WebSort.class).get(0);
            sortobj = new Sort(Sort.Direction.fromString(webSort.getDirection()), webSort.getProperty());
        }
        return sortobj;
    }

}
