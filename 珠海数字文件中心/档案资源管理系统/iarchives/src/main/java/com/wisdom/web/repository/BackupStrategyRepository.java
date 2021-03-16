package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_backup_strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by RonJiang on 2018/3/23 0023.
 */
public interface BackupStrategyRepository extends JpaRepository<Tb_backup_strategy, String>{

    @Query(value = "select count(bs) from Tb_backup_strategy bs where bs.backupcontent=?1")
    Integer findCountByBackupcontent(String backupContent);

    Integer deleteByBackupcontent(String backupContent);

    Tb_backup_strategy findByBackupcontent(String backupContent);

    @Query(value = "select backupfrequency from Tb_backup_strategy where backupcontent=?1")
    String findBackupfrequencyByBackupcontent(String backupContent);

    @Query(value = "select backuptime from Tb_backup_strategy where backupcontent=?1")
    String findBackuptimeByBackupcontent(String backupContent);

}
