package com.wisdom.web.entity;

import org.springframework.beans.BeanUtils;

import java.util.List;

public class SzhEntryCapture extends SzhEntryBase {

    private Szh_entry_index_capture index;

    private Szh_entry_detail_capture detail;

    private String username;

    public Szh_entry_index_capture getEntryIndex(){ return index; }

    public Szh_entry_index_capture getRawEntryIndex() {
        Szh_entry_index_capture raw = new Szh_entry_index_capture();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryIndex(Szh_entry_index_capture index){
        if(index == null){
            return;
        }
        if(detail == null || (index.getEntryid() == null && detail.getEntryid() == null)
                || detail.getEntryid() != null && detail.getEntryid().equals(index.getEntryid())){
            this.index = index;

            BeanUtils.copyProperties(index,this);
        }else{
            throw new RuntimeException("不一致的主键EntryID");
        }
    }

    public Szh_entry_detail_capture getEntryDetail() {
        return detail;
    }

    public Szh_entry_detail_capture getRawEntryDetail() {
        Szh_entry_detail_capture raw = new Szh_entry_detail_capture();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryDetial(Szh_entry_detail_capture detail) {
        if(detail == null){
            return;
        }
        if(index == null || (index.getEntryid() == null && detail.getEntryid() == null)
                || index.getEntryid() != null && index.getEntryid().equals(detail.getEntryid())){
            this.detail = detail;

            BeanUtils.copyProperties(detail,this);
        }else{
            throw new RuntimeException("不一致的主键EntryID");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
