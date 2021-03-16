package com.xdtech.project.lot.device.entity;

/**
 * Created by Administrator on 2017/10/24 0024.
 * layui 表格返回信息
 */
public class IndexMsg {
    private boolean success;//请求状态
    private String code;
    private String msg;      //返回信息
    private String count;      //总数
    private Object data;     //返回对象

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public IndexMsg(){}

    public IndexMsg(boolean success, String code, String count, String msg, Object data){
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.count=count;
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
