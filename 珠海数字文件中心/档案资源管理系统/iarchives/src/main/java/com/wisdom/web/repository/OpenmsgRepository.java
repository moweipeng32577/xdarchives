package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_openmsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tanly on 2017/12/5 0005.
 */
public interface OpenmsgRepository extends JpaRepository<Tb_openmsg, Integer> , JpaSpecificationExecutor<Tb_openmsg> {
    @Query(value = "select t from Tb_openmsg t where batchnum in (select msgid from Tb_flows where taskid=?1)")
    List<Tb_openmsg> getOpenmsgs(String taskid);

    List<Tb_openmsg> findByBatchnumInAndEntryidIn(String[] batchnum,String[] entryids);

    @Query(value = "select t from Tb_openmsg t where batchnum in (select msgid from Tb_flows where taskid=?1)")
    Page<Tb_openmsg> getOpenmsgsPage(String taskid,Pageable pageable);

    List<Tb_openmsg> findByBatchnum(String batchnum);

    @Query(value = "select t from Tb_openmsg t where batchnum =?1 and (firstresult is null or firstresult='')")
    List<Tb_openmsg> findByBatchnumAndFirstresult(String batchnum);

    @Query(value = "select t from Tb_openmsg t where batchnum =?1 and (lastresult is null or lastresult='')")
    List<Tb_openmsg> findByBatchnumAndLastresult(String batchnum);

    @Query(value = "select t from Tb_openmsg t where batchnum =?1 and (finalresult is null or finalresult='')")
    List<Tb_openmsg> findByBatchnumAndFinalresult(String batchnum);

    Integer deleteByBatchnumIn(String[] batchnums);

    Tb_openmsg findByMsgid(String id);

    @Query(value = "select t from Tb_openmsg t where batchnum in (select msgid from Tb_flows where taskid=?1) and t.entryid not in (?2)")
    List<Tb_openmsg> getOpenmsgsByEntryIds(String taskid,String[] entryids);

    @Query(value = "select t from Tb_openmsg t where batchnum in (select msgid from Tb_flows where taskid=?1)")
    List<Tb_openmsg> getOpenmsgsByTask(String taskid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_openmsg set firstresult = '' where msgid = ?1")
    Integer updatefInfoById(String id);

    @Modifying
    @Transactional
    @Query(value = "update Tb_openmsg set lastresult = '' where msgid = ?1")
    Integer updatelInfoById(String id);
}
