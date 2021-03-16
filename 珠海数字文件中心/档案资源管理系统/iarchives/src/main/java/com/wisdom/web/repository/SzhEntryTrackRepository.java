package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_entry_track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yl on 2019/1/25.
 */
public interface SzhEntryTrackRepository extends JpaRepository<Szh_entry_track, String>,JpaSpecificationExecutor<Szh_entry_track> {
    @Query("select t from Szh_entry_track t where t.entryid = ?1 and t.nodename = ?2 and t.status = ?3 and t.entrysigntime = ( select max(s.entrysigntime) from Szh_entry_track s where s.entryid = ?1 and s.nodename = ?2)")
    Szh_entry_track findMaxByEntryidAndNodename(String entryid, String nodeName, String status);

    Szh_entry_track findByEntryidAndNodename(String entryid, String nodeName);

    List<Szh_entry_track> findByEntryid(String entryid);

    Integer deleteByArchivecodeIn(String[] archivecodes);

    @Query("select t.nodename,t.status,t.entrysigner,t.entrysigntime,t.depict from Szh_entry_track t where t.entryid=?1 order by t.entrysigntime desc ")
    List<Szh_entry_track> findByEntryidGroupByStatus(String entryid);

    Integer deleteByEntryidIn(String[] entryids);
}
