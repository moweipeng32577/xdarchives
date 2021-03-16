package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_transdoc_entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/4/19 0019.
 */
public interface TransdocEntryRepository extends JpaRepository<Tb_transdoc_entry, String>,JpaSpecificationExecutor<Tb_transdoc_entry> {

    List<Tb_transdoc_entry> findByDocid(String docid);
    
    @Query(value = "select t.entryid from Tb_transdoc_entry t where docid = ?1")
    List<String> findEntryidByDocid(String docid);

    @Modifying
    @Query(value = "update Tb_transdoc_entry set status = ?2 where entryid in ?1")
    int changeStatusByEntryid(String[] entryidData,String status);

    @Modifying
    @Query(value = "update Tb_transdoc_entry set status = ?2 where docid=?1")
    int changeStatusByDocid(String docid,String status);

    List<Tb_transdoc_entry> findByStatusAndDocid(String status,String docid);

    List<Tb_transdoc_entry> findByStatusAndDocidIn(String status,String[] docids);

    List<Tb_transdoc_entry> findByStatus(String status);

    @Query(value = "select status from Tb_transdoc_entry where entryid=?1")
    List<String> findStatusByEntryid(String entryid);

    List<Tb_transdoc_entry> findByEntryidIn(String[] entryid);

    Integer deleteAllByDocidIn(String[] docids);

    @Query(value = "select count(entryid) from Tb_transdoc_entry where status = ?1")
    Long getNumByState(String state);
}
