package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_metadata_log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wjh
 */
public interface MetadataLogRepository extends JpaRepository<Tb_metadata_log, String>,JpaSpecificationExecutor<Tb_metadata_log> {
    List<Tb_metadata_log> findByEntryidIn(String[] entryid);
}