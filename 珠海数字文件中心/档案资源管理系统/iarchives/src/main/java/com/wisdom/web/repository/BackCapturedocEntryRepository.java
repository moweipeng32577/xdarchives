package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_backcapturedoc_entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2019/10/30.
 */
public interface BackCapturedocEntryRepository extends JpaRepository<Tb_backcapturedoc_entry, String>,JpaSpecificationExecutor<Tb_backcapturedoc_entry> {

    @Query(value = "select entryid from Tb_backcapturedoc_entry where backdocid=?1")
    String[] getEntryids(String backdocid);
}
