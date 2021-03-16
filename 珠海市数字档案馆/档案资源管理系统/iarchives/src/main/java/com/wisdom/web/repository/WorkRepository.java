package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface WorkRepository extends JpaRepository<Tb_work, String>, JpaSpecificationExecutor<Tb_work> {
    Tb_work findByWorktext(String worktext);

    Tb_work findByWorkid(String workid);

    List<Tb_work> findByWorkidIsNotNullOrderBySortsequence();

    @Modifying
    @Transactional
    @Query(value = "update Tb_work set urgingstate=?2 where workid= ?1")
    Integer updateUrgingByid(String workid,String state);

    @Modifying
    @Transactional
    @Query(value = "update Tb_work set sendmsgstate=?2 where workid= ?1")
    Integer updateSendmsgByid(String workid,String state);
}
