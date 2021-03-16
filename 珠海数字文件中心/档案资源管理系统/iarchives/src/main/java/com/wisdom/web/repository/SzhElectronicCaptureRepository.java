package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_electronic_capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Rong on 2018-12-06.
 */
public interface SzhElectronicCaptureRepository extends JpaRepository<Szh_electronic_capture, String> {

    @Query(value = "select ei.archivecode,t.filepath,t.filename from szh_electronic_capture t left join szh_entry_index_capture ei on t.entryid = ei.entryid " +
            "where t.entryid in (select entryid from szh_entry_index_capture where archivecode in (?1))", nativeQuery = true)
    List<Object[]> findByArchivecodeIn(String[] archivecodes);
    List<Szh_electronic_capture> findByEntryidIn(String[] entryids);
    List<Szh_electronic_capture> findByEntryid(String entryid);
    Integer deleteByEntryidIn(String[] entryid);
    Szh_electronic_capture findByEntryidAndFilename(String entryid,String filename);

    List<Szh_electronic_capture> findByEntryidOrderByFilename(String entryid);

    Szh_electronic_capture findByEleid(String eleid);

    @Query(value = "select t.eleid from Szh_electronic_capture t where t.entryid in (?1)")
    String[] findEleids(String[] entryid);
}
