package com.wisdom.web.entity;

/**
 * Created by Administrator on 2019/7/24.
 */
public class Szh_CalloutSign_Return extends Szh_callout_entry{

    private String track;//字轨案号
    private String signer;//任务签收人

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }
}
