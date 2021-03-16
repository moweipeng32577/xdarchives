package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_capture;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by Rong on 2017/11/21.
 */
public interface ElectronicCaptureRepository extends JpaRepository<Tb_electronic_capture, String> {

    Tb_electronic_capture findByEleid(String eleid);

    List<Tb_electronic_capture> findByEntryidOrderBySortsequence(String entryid);

    List<Tb_electronic_capture> findByEntryid(String entryid);

    List<Tb_electronic_capture> findByEleidInOrderBySortsequence(String[] eleids);

    Integer deleteByEleidIn(String[] ids);

    Integer deleteByEntryidIn(String[] entryids);

    @Query(value = "select entryid from Tb_electronic_capture where eleid in (?1)")
    List<String> findEntryidByEleidIn(String[] ids);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic_capture set entryid = ?1 where entryid = ?2")
    Integer updateEntryidByEntryid(String entryid, String oldEntryid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic_capture set entryid = ?1 where eleid in ?2")
    Integer updateEntryid(String entryid, String[] ids);

    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic_capture set pages=?1 where eleid=?2")
    Integer updatePagesByEleid(String pages,String eleid);

    Integer deleteByEntryid(String entryid);

    List<Tb_electronic_capture> findByEntryidAndFilenameStartsWithAndEleidNot(String enrtyId,String startStr,String eleid);


    List<Tb_electronic_capture> findByEntryidIn(String[] entryIds);


    List<Tb_electronic_capture> findByFilepathAndFilename(String filepath, String filename);

    List<Tb_electronic_capture> findByEntryidAndFilename(String entryid,String filename);

    List<Tb_electronic_capture> findByEleidIn(String[] eleids,Sort sort);

    List<Tb_electronic_capture> findByEntryid(String entryid,Sort sort);

    @Modifying
    @Query(value = "delete from tb_electronic_capture  where eleid in ?1",  nativeQuery = true)
    int delectEles(String[] eleid);

    @Modifying
    @Query(value = "insert into tb_electronic_capture(eleid,entryid,filename,filepath,filesize,filetype,md5,sortsequence) select eleid,entryid,filename,filepath,filesize,filetype,md5,sortsequence from szh_electronic_capture where entryid in ?1", nativeQuery = true)
    int moveeletronics(String[] entryidData);

    @Query(value = "SELECT ec.eleid FROM Tb_electronic_capture ec WHERE ec.entryid = ?1")
    String[] findEleIdByEntryid(String entryid);


    @Modifying
    @Transactional
    @Query(value = "update  Tb_electronic_capture set filename=?1 where eleid=?2")
    Integer updateFilenameByEleid(String fileName,String eleid);

    @Query(value = "select eleid from Tb_electronic_capture where entryid=?1")
    List<String > findEleidByEntryid(String entryid);

    @Query(value = "select filename from Tb_electronic_capture where eleid=?1")
    String findFileNameByEleid(String eleid);

    @Query(value ="select filetype from Tb_electronic_capture where eleid=?1")
    String findFileTypeByEleid(String eleid);

    @Query(value = "select filepath from Tb_electronic_capture where entryid =?1")
    String findFilepathByEntryid(String entryid);

    List<Tb_electronic_capture> findByEleidInOrderByFilename(String[] eleids);

    @Modifying
    @Query(value = "insert into tb_electronic_capture select * from tb_electronic where entryid in ?1", nativeQuery = true)
    int moveCaptureEletronics(String[] entryidData);

    @Query(value = "select count(*) as count,sum(cast(FileSize as bigint)) as size from tb_entry_index_capture tei  left join  tb_electronic_capture te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%'",nativeQuery = true)
    String getCapacity();

    @Query(value = "select count(*) from tb_entry_index_capture tei  left join  tb_electronic_capture te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
    String getSumCapacity(String date);

    @Query(value = "select sum(cast(FileSize as bigint)) from tb_entry_index_capture tei  left join  tb_electronic_capture te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
    String getTotalCapacity(String date);

    List<Tb_electronic_capture> findByEntryidOrderByFilename(String entryid);
}
