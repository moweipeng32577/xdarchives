package com.wisdom.web.entity;

/**
 * Created by Rong on 2017/11/4.
 */
public class EntryAccess extends EntryBase{

    private Tb_entry_index_access index;

    private Tb_entry_detail_access detail;

    public Tb_entry_index_access getEntryIndex(){
        return index;
    }

    public void setEntryIndex(Tb_entry_index_access index){
        if(index == null){
            return;
        }
        if(detail == null || (index.getEntryid() == null && detail.getEntryid() == null)
                || detail.getEntryid() != null && detail.getEntryid().equals(index.getEntryid())){
            this.index = index;

            this.setNodeid(index.getNodeid());
            this.setEleid(index.getEleid());
            this.setTitle(index.getTitle());
            this.setFilenumber(index.getFilenumber());
            this.setArchivecode(index.getArchivecode());
            this.setFunds(index.getFunds());
        }else{
            throw new RuntimeException("不一致的主键EntryID");
        }
    }

    public Tb_entry_detail_access getEntryDetail() {
        return detail;
    }

    public void setEntryDetial(Tb_entry_detail_access detail) {
        if(detail == null){
            return;
        }
        if(index == null || (index.getEntryid() == null && detail.getEntryid() == null)
                || index.getEntryid() != null && index.getEntryid().equals(detail.getEntryid())){
            this.detail = detail;
        }else{
            throw new RuntimeException("不一致的主键EntryID");
        }
    }

}
