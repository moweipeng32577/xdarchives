package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_offline_accession_batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

/**
 * Created by RonJiang on 2018/3/30 0030.
 */
public interface TbofflineAccessionRepository extends JpaRepository<Tb_offline_accession_batch, String>,
        JpaSpecificationExecutor<Tb_offline_accession_batch> {

    @Modifying
    Integer deleteByBatchidIn(String[] batchids);
}