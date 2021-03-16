package com.wisdom.web.entity;

import org.springframework.beans.BeanUtils;

/**
 * Created by Rong on 2017/11/4.
 */
public class Entry extends EntryBase {

    private Tb_entry_index index;

    private Tb_entry_detail detail;

    public Tb_entry_index getEntryIndex(){ return index; }

    public Tb_entry_index getRawEntryIndex() {
        Tb_entry_index raw = new Tb_entry_index();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryIndex(Tb_entry_index index){
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

    public Tb_entry_detail getEntryDetail() {
        return detail;
    }

    public Tb_entry_detail getRawEntryDetail() {
        Tb_entry_detail raw = new Tb_entry_detail();
        BeanUtils.copyProperties(this,raw);
        return raw;
    }

    public void setEntryDetial(Tb_entry_detail detail) {
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
