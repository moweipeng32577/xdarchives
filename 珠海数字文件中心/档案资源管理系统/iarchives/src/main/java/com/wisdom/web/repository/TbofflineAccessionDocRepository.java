package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_offline_accession_batch;
import com.wisdom.web.entity.Tb_offline_accession_batchdoc;
import com.wisdom.web.entity.Tb_textopen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by RonJiang on 2018/3/30 0030.
 */
public interface TbofflineAccessionDocRepository extends JpaRepository<Tb_offline_accession_batchdoc, String>,
        JpaSpecificationExecutor<Tb_offline_accession_batchdoc> {


    Page<Tb_offline_accession_batchdoc> findByBatchid(Pageable pageRequest, String batchid);

    Integer deleteByBatchidIn(String[] batchids);

    @Modifying
    @Transactional
    @Query(value = "update Tb_offline_accession_batchdoc set isaccess = '已接入' where id = ?1")
    Integer updateIsaccessByid(String id);
}