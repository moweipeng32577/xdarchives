package com.wisdom.web.entity;

/**
 * Created by Administrator on 2019/3/15.
 */
public class RebackMissPageCheck {

    private String archivecode;
    private String page;
    private String elenumber;
    private String result;
    private String id;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArchivecode() {
        return archivecode;
    }

    public void setArchivecode(String archivecode) {
        this.archivecode = archivecode;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getElenumber() {
        return elenumber;
    }

    public void setElenumber(String elenumber) {
        this.elenumber = elenumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
