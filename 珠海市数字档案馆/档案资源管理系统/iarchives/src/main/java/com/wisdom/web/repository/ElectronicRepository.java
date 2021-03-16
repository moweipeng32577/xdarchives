package com.wisdom.web.repository;

import com.wisdom.web.entity.Elecapacity;
import com.wisdom.web.entity.Tb_electronic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by RonJiang on 2017/11/6 0006.
 */
public interface ElectronicRepository extends JpaRepository<Tb_electronic,String>,JpaSpecificationExecutor<Tb_electronic> {

    Tb_electronic findByEleid(String eleid);

    List<Tb_electronic> findByEntryidOrderBySortsequence(String entryid);

    List<Tb_electronic> findByEntryidAndFileclassid(String entryid,String fileClassId);

    @Query(value = "select * from tb_electronic where entryid =?1 and (fileclassid is null or fileclassid='')",nativeQuery = true)
    List<Tb_electronic> findByEntryidAndFileclassidNull(String entryid);

    List<Tb_electronic> findByEntryidAndFilepathIsNotNull(String entryid);

    @Query(value = "select t from Tb_electronic t where t.eleid in(?1) and fileclassid is not null")
    List<Tb_electronic> findByEleidAndFileclassidNotNUll(String[] eleid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic set filename = ?2 where eleid= ?1")
    Integer updateFolderName(String eleId, String name);

    List<Tb_electronic> findByEntryidInOrderBySortsequence(String[] entryids);

    List<Tb_electronic> findByEleidInOrderBySortsequence(String[] eleids);

    Integer deleteByEleidIn(String[] ids);

    Integer deleteByEntryidIn(String[] entryids);

    Integer deleteByEntryid(String entryid);

    @Query(value = "select entryid from Tb_electronic where eleid in (?1)")
    List<String> findEntryidByEleidIn(String[] ids);

    @Query(value = "select entryid from Tb_electronic where eleid = ?1")
    String findEntryidByEleid(String ids);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic set entryid = ?1 where entryid = ?2")
    Integer updateEntryidByEntryid(String entryid, String oldEntryid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_electronic set entryid = ?1 where eleid in ?2")
    Integer updateEntryid(String entryid, String[] ids);

    @Query(value = "select e from Tb_electronic e where e.entryid in (select thematicdetilid from Tb_thematic_detail where thematicid in (?1))")
    List<Tb_electronic> getElectronics(String[] ThematicIDs);

    @Modifying
    @Query(value = "insert into tb_electronic select * from tb_electronic_capture where entryid in ?1", nativeQuery = true)
    int moveeletronics(String[] entryidData);

    List<Tb_electronic> findByEntryidIn(String[] entryIds);

    List<Tb_electronic> findByEntryidAndFilenameStartsWithAndEleidNot(String enrtyId,String startStr,String eleid);

    List<Tb_electronic> findByEntryidAndFilename(String entryid,String filename);

    List<Tb_electronic> findByFilepathAndFilename(String filepath,String filename);

    List<Tb_electronic> findByEntryid(String entryid);

    @Modifying
    @Query(value = "update Tb_electronic set pages=?1 where eleid=?2")
    Integer updatePagesByEleid(String pages,String eleid);

    //数字化
    @Query(value = "select * from tb_electronic where entryid in (select entryid from tb_entry_index where archivecode in (select archivecode from szh_batch_entry where batchcode in (?1)))",nativeQuery=true)
    List<Tb_electronic> findBatchcodesByAll(String[] batchcodes);

    List<Tb_electronic> findByEleidIn(String[] eleids, Sort sort);

    List<Tb_electronic> findByEleidIn(String[] eleids);

    List<Tb_electronic> findByEntryid(String entryid,Sort sort);

    @Query(value = "SELECT e.eleid FROM Tb_electronic e WHERE e.entryid = ?1")
    String[] findEleIdByEntryid(String entryid);

    @Modifying
    @Transactional
    @Query(value = "update  Tb_electronic set filename=?1 where eleid=?2")
    Integer updateFilenameByEleid(String fileName,String eleid);

    @Query(value = "select eleid from Tb_electronic where entryid=?1")
    List<String > findEleidByEntryid(String entryid);

    @Query(value = "select filename from Tb_electronic where eleid=?1")
    String findFileNameByEleid(String eleid);

    @Query(value ="select filetype from Tb_electronic where eleid=?1")
    String findFileTypeByEleid(String eleid);

    @Query(value = "select filepath from Tb_electronic where entryid =?1")
    String findFilepathByEntryid(String entryid);

    List<Tb_electronic> findByEleidInOrderByFilename(String[] eleids);

    @Query(value = "select count(*) as count,sum(cast(FileSize as bigint)) as size from tb_entry_index tei  left join  tb_electronic te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%'",nativeQuery = true)
    String getCapacity();

    @Query(value = "select count(*) from tb_entry_index tei  left join  tb_electronic te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
    String getSumCapacity(String date);

    @Query(value = "select sum(cast(FileSize as bigint)) from tb_entry_index tei  left join  tb_electronic te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/storages/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
    String getTotalCapacity (String date);

    @Query(value = "select tei.organ,tei.funds,count(*) as count ,sum(cast(FileSize as bigint)) as size from  tb_entry_index tei left join tb_electronic te on tei.entryid = te.entryid where tei.funds is not null and tei.organ is not null and tei.eleid is not null and  te.filepath like '%/electronics/storages/%' group by tei.organ,tei.funds",nativeQuery = true)
    List<Elecapacity> getList ();

    List<Tb_electronic> findByEntryidOrderByFilename(String entryid);
}
