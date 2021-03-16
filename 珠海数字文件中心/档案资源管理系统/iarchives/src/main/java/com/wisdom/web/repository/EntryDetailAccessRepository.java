package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail_access;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Rong on 2017/11/13.
 */
public interface EntryDetailAccessRepository extends JpaRepository<Tb_entry_detail_access, String> {
    Integer deleteByEntryidIn(String[] entryidData);
}
