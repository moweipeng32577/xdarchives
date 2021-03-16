package com.xdtech.project.lot.util;

import java.io.Serializable;

/**
 * Created by wujy on 2019/09/29
 * http请求返回信息
 */
public class ResponseMsg implements Serializable {
    private boolean success;//请求状态
    private String msg;      //返回信息
    private Object data;     //返回对象

    public ResponseMsg(){}

    public ResponseMsg(boolean success, Object data){
        this.success = success;
        this.data = data;
    }

    public ResponseMsg(boolean success, String msg, Object data){
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
