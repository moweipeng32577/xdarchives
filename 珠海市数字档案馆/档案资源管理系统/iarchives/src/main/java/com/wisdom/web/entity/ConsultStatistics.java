package com.wisdom.web.entity;

import java.io.Serializable;

/**
 * Created by Leo on 2020/7/6 0006.
 * 台账统计接收前端数据类
 */
public class ConsultStatistics implements Serializable {
    private String consultDate;
    private String[] type;
    private String[] company;   //单位
    private String[] personal;  //个人
    private String[] volume;    //卷
    private String[] piece;     //件
    private String[] tocopy;    //复印
    private String[] prove;     //证明

    public ConsultStatistics(){

    }

    public String getConsultDate() {
        return consultDate;
    }

    public void setConsultDate(String consultDate) {
        this.consultDate = consultDate;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getCompany() {
        return company;
    }

    public void setCompany(String[] company) {
        this.company = company;
    }

    public String[] getPersonal() {
        return personal;
    }

    public void setPersonal(String[] personal) {
        this.personal = personal;
    }

    public String[] getVolume() {
        return volume;
    }

    public void setVolume(String[] volume) {
        this.volume = volume;
    }

    public String[] getPiece() {
        return piece;
    }

    public void setPiece(String[] piece) {
        this.piece = piece;
    }

    public String[] getTocopy() {
        return tocopy;
    }

    public void setTocopy(String[] tocopy) {
        this.tocopy = tocopy;
    }

    public String[] getProve() {
        return prove;
    }

    public void setProve(String[] prove) {
        this.prove = prove;
    }
}
