package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_batch_entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SzhBatchEntryRepository extends JpaRepository<Szh_batch_entry,String>,JpaSpecificationExecutor<SzhBatchEntryRepository> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
    Integer deleteByBatchcodeAndCaptureentryidIn(String batchcode, String[] entryids);
    List<Szh_batch_entry> findByBatchcode(String batchcode);
    List<Szh_batch_entry> findByBatchcodeAndIscheck(String batchcode, String isCheck);
    List<Szh_batch_entry> findByBatchcodeIn(String[] batchcode);
    Szh_batch_entry findByBatchcodeAndCaptureentryid(String batchcode, String entryId);
    List<Szh_batch_entry> findByBatchcodeInAndStatus(String[] batchcodes, String status);
    @Query(value = "select captureentryid from Szh_batch_entry where batchcode in(?1) and status=?2")
    List<String> findBatchccodeStatus(String[] batchcodes, String status);
    @Query(value = "select captureentryid from Szh_batch_entry where  status=?1 and type=?2")
    List<String> findStatusAndType(String status, String type);
    @Query(value = "select captureentryid from Szh_batch_entry where batchcode in(?1)")
    List<String> findBatchcodes(String[] batchcodes);
}
