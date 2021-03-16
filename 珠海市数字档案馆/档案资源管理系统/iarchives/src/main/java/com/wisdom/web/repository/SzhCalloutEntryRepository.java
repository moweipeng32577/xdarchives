package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_callout_entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SzhCalloutEntryRepository extends JpaRepository<Szh_callout_entry,String>,JpaSpecificationExecutor<Szh_callout_entry> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
    Integer deleteByIdIn(String[] ids);
    List<Szh_callout_entry> findByIdIn(String[] ids);

    @Modifying
    @Transactional
    @Query(value = "update Szh_callout_entry set a4 = a4 + 1, za4 = za4 + 1, pages = pages + 1 where archivecode = (select archivecode from Szh_entry_index_capture where entryid = ?1)")
    void pageIncrease(String entryid);

    List<Szh_callout_entry> findByArchivecode(String archivecode);

    @Query(value = "select id from Szh_callout_entry where batchcode in (?1)")
    String[] findIdByBatchcodes(String[] batchcodes);

    List<Szh_callout_entry> findByBatchcode(String batchcode);

    Integer countByBatchcode(String batchcode);

    @Modifying
    @Query(value = "update Szh_callout_entry set a4 = 0, za4 = 0, pages = 0 where archivecode = (select archivecode from Szh_entry_index_capture where entryid = ?1)")
    void pageReset(String entryid);

    @Query(value = "select sc.batchname from Szh_archives_callout sc,Szh_callout_entry se where sc.batchcode = se.batchcode and archivecode = ?1")
    String queryBatchname(String archivecode);

    List<Szh_callout_entry> findByIdInAndAudit(String[] ids,String audit);
}
