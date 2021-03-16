package com.wisdom.web.entity;

/**
 * Created by tanly on 2017/12/5 0005.
 */
public class Tb_entry_index_open extends Tb_entry_index{
    private String result;
    private String msgid;
    private String entryunit;  //档案所属单位
    private String appraisedata;  //鉴定依据
    private String appraisetext;  //初审鉴定意见
    private String updatetitle;  //修改题名
    private String firstresult;  //拟开放状态
    private String firstappraiser;  //初审人
    private String lastappraiser;  //复审人
    private String lastappraisetext;  //复审鉴定意见
    private String lastresult;  //复审开放状态

    public String getFirstresult() {
        return firstresult;
    }

    public void setFirstresult(String firstresult) {
        this.firstresult = firstresult;
    }

    public String getFirstappraiser() {
        return firstappraiser;
    }

    public void setFirstappraiser(String firstappraiser) {
        this.firstappraiser = firstappraiser;
    }

    public String getLastappraiser() {
        return lastappraiser;
    }

    public void setLastappraiser(String lastappraiser) {
        this.lastappraiser = lastappraiser;
    }

    public String getLastappraisetext() {
        return lastappraisetext;
    }

    public void setLastappraisetext(String lastappraisetext) {
        this.lastappraisetext = lastappraisetext;
    }

    public String getLastresult() {
        return lastresult;
    }

    public void setLastresult(String lastresult) {
        this.lastresult = lastresult;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getEntryunit() {
        return entryunit;
    }

    public void setEntryunit(String entryunit) {
        this.entryunit = entryunit;
    }

    public String getAppraisedata() {
        return appraisedata;
    }

    public void setAppraisedata(String appraisedata) {
        this.appraisedata = appraisedata;
    }

    public String getAppraisetext() {
        return appraisetext;
    }

    public void setAppraisetext(String appraisetext) {
        this.appraisetext = appraisetext;
    }

    public String getUpdatetitle() {
        return updatetitle;
    }

    public void setUpdatetitle(String updatetitle) {
        this.updatetitle = updatetitle;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
