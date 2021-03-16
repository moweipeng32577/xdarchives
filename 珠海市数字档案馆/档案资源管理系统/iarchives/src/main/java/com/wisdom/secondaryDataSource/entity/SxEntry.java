package com.wisdom.secondaryDataSource.entity;

import org.springframework.beans.BeanUtils;

/**
 * Created by Leo on 2020/9/11 0011.
 */
public class SxEntry extends SxEntryBase {

        private Tb_entry_index_sx index;

        private Tb_entry_detail_sx detail;

        public Tb_entry_index_sx getEntryIndex(){ return index; }

        public Tb_entry_index_sx getRawEntryIndex() {
            Tb_entry_index_sx raw = new Tb_entry_index_sx();
            BeanUtils.copyProperties(this,raw);
            return raw;
        }

        public void setEntryIndex(Tb_entry_index_sx index){
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

        public Tb_entry_detail_sx getEntryDetail() {
            return detail;
        }

        public Tb_entry_detail_sx getRawEntryDetail() {
            Tb_entry_detail_sx raw = new Tb_entry_detail_sx();
            BeanUtils.copyProperties(this,raw);
            return raw;
        }

        public void setEntryDetial(Tb_entry_detail_sx detail) {
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
