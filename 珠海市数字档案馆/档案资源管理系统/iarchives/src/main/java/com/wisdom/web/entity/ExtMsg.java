package com.wisdom.web.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/10/24 0024.
 * Ext表单提交返回信息
 */
/*@XmlRootElement(name="ExtMsg")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({List.class})*/
@XmlRootElement(name = "ExtMsg")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Collections.class})
public class ExtMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;//请求状态
    private String msg;      //返回信息
    private Object data;     //返回对象

    public ExtMsg(){}

    public ExtMsg(boolean success,String msg,Object data){
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
