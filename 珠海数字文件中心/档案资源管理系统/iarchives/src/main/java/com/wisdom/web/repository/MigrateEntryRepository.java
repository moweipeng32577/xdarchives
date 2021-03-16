package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_migrate_entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo on 2020/8/12 0012.
 */
public interface MigrateEntryRepository extends JpaRepository<Tb_migrate_entry, String>, JpaSpecificationExecutor<Tb_migrate_entry> {

    @Query("select entryid from Tb_migrate_entry where migid=?1")
    List<String> findByMigid(String migid);

    int deleteByMigidAndEntryidIn(String migid,String[] entryid);
}
