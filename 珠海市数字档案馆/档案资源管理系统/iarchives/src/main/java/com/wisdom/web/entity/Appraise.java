package com.wisdom.web.entity;

/**
 * Created by Administrator on 2020/3/24.
 */
public class Appraise {
    private String askman;//评价人
    private String appraise;  //评分
    private String appraisestar; //评分星数
    private String appraisetext;  //评价内容
    private String appraisetype;  //评价类型

    public String getAskman() {
        return askman;
    }

    public void setAskman(String askman) {
        this.askman = askman;
    }

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }

    public String getAppraisestar() {
        return appraisestar;
    }

    public void setAppraisestar(String appraisestar) {
        this.appraisestar = appraisestar;
    }

    public String getAppraisetext() {
        return appraisetext;
    }

    public void setAppraisetext(String appraisetext) {
        this.appraisetext = appraisetext;
    }

    public String getAppraisetype() {
        return appraisetype;
    }

    public void setAppraisetype(String appraisetype) {
        this.appraisetype = appraisetype;
    }
}
