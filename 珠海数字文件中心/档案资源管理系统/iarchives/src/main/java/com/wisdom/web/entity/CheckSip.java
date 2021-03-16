package com.wisdom.web.entity;

/**
 * Created by yl on 2018/5/15.
 * 数据接收SIP检测后返回的实体结果
 */
public class CheckSip {
    /**
     * 检测结果
     */
    private String result;
    /**
     * 电子文件数量
     */
    private String count;
    /**
     * 电子文件质量（）
     */
    private String quality;
    /**
     * 规范性
     */
    private String norm;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }
}
