package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_work_sx;
import com.wisdom.web.entity.Tb_work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface SxWorkRepository extends JpaRepository<Tb_work_sx, String>, JpaSpecificationExecutor<Tb_work_sx> {
    Tb_work_sx findByWorktext(String worktext);

    Tb_work_sx findByWorkid(String workid);

    List<Tb_work_sx> findByWorkidIsNotNullOrderBySortsequence();

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update Tb_work_sx set urgingstate=?2 where workid= ?1")
    Integer updateUrgingByid(String workid, String state);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update Tb_work_sx set sendmsgstate=?2 where workid= ?1")
    Integer updateSendmsgByid(String workid, String state);
}
