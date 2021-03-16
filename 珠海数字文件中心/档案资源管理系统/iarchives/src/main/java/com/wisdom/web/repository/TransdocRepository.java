package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_transdoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tanly on 2017/12/11 0011.
 */
public interface TransdocRepository extends JpaRepository<Tb_transdoc, String>,JpaSpecificationExecutor<Tb_transdoc> {

    @Modifying
    @Query(value = "update Tb_transdoc set state = ?2 where docid = ?1")
    int  changestate(String docid,String state);

    @Modifying
    @Query(value = "update Tb_transdoc set sendbackreason = ?2 where docid = ?1")
    int setSendbackreason(String docid,String sendbackreason);

    List<Tb_transdoc> findByNodeidIn(String[] nodeids);

    Tb_transdoc findByTransfercode(String nodeids);

    @Transactional
    Integer deleteByDocidIn(String[] docid);

    List<Tb_transdoc> findByState(String state);

    @Query(value = "select b from Tb_transdoc b where b.transfercode in (select msgid from Tb_flows where taskid=?1)")
    Tb_transdoc getByTaskid(String taskid);


}