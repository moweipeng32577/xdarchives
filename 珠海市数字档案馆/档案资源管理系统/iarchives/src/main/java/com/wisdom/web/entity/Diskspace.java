package com.wisdom.web.entity;

/**
 * Created by RonJiang on 2018/5/9 0009.
 */
public class Diskspace {
    private String drivenumber;
    private Long totalspace;
    private Double freespercent;
    private Double usedpercent;

    public Diskspace(String drivenumber, Long totalspace, Double freespercent, Double usedpercent) {
        this.drivenumber = drivenumber;
        this.totalspace = totalspace;
        this.freespercent = freespercent;
        this.usedpercent = usedpercent;
    }

    public String getDrivenumber() {
        return drivenumber;
    }

    public void setDrivenumber(String drivenumber) {
        this.drivenumber = drivenumber;
    }

    public Long getTotalspace() {
        return totalspace;
    }

    public void setTotalspace(Long totalspace) {
        this.totalspace = totalspace;
    }

    public Double getFreespercent() {
        return freespercent;
    }

    public void setFreespercent(Double freespercent) {
        this.freespercent = freespercent;
    }

    public Double getUsedpercent() {
        return usedpercent;
    }

    public void setUsedpercent(Double usedpercent) {
        this.usedpercent = usedpercent;
    }
}
