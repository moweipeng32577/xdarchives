package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_acceptdoc;
import com.wisdom.web.entity.Tb_acceptdoc_batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2019/6/18.
 */
public interface AcceptDocBatchRepository extends JpaRepository<Tb_acceptdoc_batch,String>,
        JpaSpecificationExecutor<Tb_acceptdoc_batch>{

    Integer deleteByAcceptdocidIn(String[] acceptdocid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_acceptdoc_batch set state = '正在消毒' where batchid in (?1)")
    Integer updateBatchByBatchidIn(String[] batchid);

    List<Tb_acceptdoc_batch> findByAcceptdocid(String acceptdocid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_acceptdoc_batch set state = ?2 where batchid in (?1)")
    Integer updateStateByBatchidIn(String[] batchid,String state);

    List<Tb_acceptdoc_batch> findByBatchidIn(String[] batchid);

}
