package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail_capture;
import com.wisdom.web.entity.Tb_reserve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/4/17.
 */
public interface ReserveRepository extends JpaRepository<Tb_reserve, Integer>, JpaSpecificationExecutor<Tb_reserve> {

    Tb_reserve findByDocid(String id);

    List<Tb_reserve> findByDocidIn(String[] id);

    @Query(value = "select b from Tb_reserve b where b.docid =(select borrowmsgid from Tb_task  where taskid = ?1)")
    Page<Tb_reserve> findByBorrowmig(Pageable pageable, String taskid);

    @Query(value = "select b from Tb_reserve b where b.docid =(select borrowmsgid from Tb_task  where taskid = ?1)")
    Tb_reserve findByBorrowmig(String taskid);

    List<Tb_reserve> findBySubmiterid(String userid);
}
