package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail_capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by Rong on 2017/11/13.
 */
public interface EntryDetailCaptureRepository extends JpaRepository<Tb_entry_detail_capture, String> {

    Tb_entry_detail_capture findByEntryid(String entryid);

    Integer deleteByEntryidIn(String[] entryidData);

    @Modifying
    @Query(value = "update Tb_entry_detail_capture e set e.f02=?2 where e.entryid in (select entryid from Tb_entry_index_capture where archivecode=?1 and nodeid=?3)")
    Integer updateFileSizeByArchivecode(String archivecode, String fileSize,String nodeid);

    Integer deleteByEntryid(String entryid);

    List<Tb_entry_detail_capture> findByEntryidIn(String[] entryids);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_detail_capture set entryid = ?1 where entryid = ?2")
    int updateEntryid(String newEntryid, String entryid);

    Tb_entry_detail_capture findByF01(String f01);

    @Modifying
    @Query(value = "delete from tb_entry_detail_capture  where entryid in ?1", nativeQuery = true)
    int deleteDetails(String[] entryidData);

    @Modifying
    @Query(value = "insert into tb_entry_detail_capture (entryid,f01,f02,f03,f04,f05,f06,f07,f08,f09,f10,f11,f12,f13,f14,f15,f16,f17,f18,f19,f20,f21,f22,f23,f24,f25,f26,f27,f28,f29,f30,f31,f32,f33,f34,f35,f36,f37,f38,f39,f40,f41,f42,f43,f44,f45,f46,f47,f48,f49,f50) select entryid,f01,f02,f03,f04,f05,f06,f07,f08,f09,f10,f11,f12,f13,f14,f15,f16,f17,f18,f19,f20,f21,f22,f23,f24,f25,f26,f27,f28,f29,f30,f31,f32,f33,f34,f35,f36,f37,f38,f39,f40,f41,f42,f43,f44,f45,f46,f47,f48,f49,f50 from szh_entry_detail_capture where entryid in ?1", nativeQuery = true)
    int movedetails(String[] entryidData);

    @Modifying
    @Query(value = "insert into tb_entry_detail_capture select * from tb_entry_detail where entryid in ?1", nativeQuery = true)
    int moveCapturedetails(String[] entryidData);
}