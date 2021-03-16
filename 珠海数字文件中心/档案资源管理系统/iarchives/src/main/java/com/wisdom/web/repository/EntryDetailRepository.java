package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by Rong on 2017/11/13.
 */
public interface EntryDetailRepository extends JpaRepository<Tb_entry_detail, String> {
	
    Tb_entry_detail findByEntryid(String entryid);

    @Modifying
    @Query(value = "delete from tb_entry_detail where entryid in ?1" , nativeQuery = true)
    Integer deleteByEntryidIn(String[] entryidData);

    Integer deleteByEntryid(String entryid);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_detail set entryid = ?1 where entryid = ?2")
    int updateEntryid(String newEntryid, String entryid);

    @Modifying
    @Query(value = "insert into tb_entry_detail select * from tb_entry_detail_capture where entryid in ?1", nativeQuery = true)
    int movedetails(String[] entryidData);

    @Modifying
    @Query(value = "update Tb_entry_detail e set e.f02=?2 where e.entryid=(select entryid from Tb_entry_index where archivecode=?1 and nodeid=?3)")
    int updatePagesByArchivecode(String archivecode, String fileSize,String nodeid);

    @Query(value = "select d from Tb_entry_detail d where entryid in (?1)")
    List<Tb_entry_detail> findByEntryidIn(String[] entryids);

    Tb_entry_detail findByF01(String f01);
}