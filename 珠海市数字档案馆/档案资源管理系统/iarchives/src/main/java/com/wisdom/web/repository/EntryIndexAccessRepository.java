package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_index_access;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Rong on 2017/11/13.
 */
public interface EntryIndexAccessRepository extends JpaRepository<Tb_entry_index_access, String> {
    Integer deleteByNodeidIn(String[] nodeids);
}
