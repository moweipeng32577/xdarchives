package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_archives_migrate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Leo on 2020/8/12 0012.
 */
public interface ArchivesMigrateRepository extends JpaRepository<Tb_archives_migrate, String>, JpaSpecificationExecutor<Tb_archives_migrate> {

    Tb_archives_migrate findByMigid(String migid);

    List<Tb_archives_migrate> findByMigidIn(String[] migid);

    @Modifying
    @Transactional
    @Query(value = "update tb_archives_migrate set migratecount= cast(migratecount as int)+?2 where migid=?1 ", nativeQuery = true)
    int updateMigCount(String migid,int num);
}
