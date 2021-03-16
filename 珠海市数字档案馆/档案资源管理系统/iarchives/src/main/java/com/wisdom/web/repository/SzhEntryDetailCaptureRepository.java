package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_entry_detail_capture;
import com.wisdom.web.entity.Tb_entry_detail_capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Rong on 2017/11/13.
 */
public interface SzhEntryDetailCaptureRepository extends JpaRepository<Szh_entry_detail_capture, String> {

    Szh_entry_detail_capture findByEntryid(String entryid);

    Integer deleteByEntryidIn(String[] entryidData);

    @Modifying
    @Query(value = "update Szh_entry_detail_capture e set e.f02=?2 where e.entryid in (select entryid from Tb_entry_index_capture where archivecode=?1 and nodeid=?3)")
    Integer updateFileSizeByArchivecode(String archivecode, String fileSize, String nodeid);

    Integer deleteByEntryid(String entryid);

    List<Szh_entry_detail_capture> findByEntryidIn(String[] entryids);

}
