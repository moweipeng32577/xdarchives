package com.wisdom.web.entity;

import org.springframework.beans.BeanUtils;

/**
 * Created by Administrator on 2019/6/24.
 */
public class AcceptEntryCapture extends EntryBase{

    private Tb_entry_index_accept index;

    private Tb_entry_detail_accept detail;

    public Tb_entry_index_accept getEntryIndex(){ return index; }

    public Tb_entry_index_accept getRawEntryIndex() {
        Tb_entry_index_accept raw = new Tb_entry_index_accept();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryIndex(Tb_entry_index_accept index){
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

    public Tb_entry_detail_accept getEntryDetail() {
        return detail;
    }

    public Tb_entry_detail_accept getRawEntryDetail() {
        Tb_entry_detail_accept raw = new Tb_entry_detail_accept();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryDetial(Tb_entry_detail_accept detail) {
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
}
