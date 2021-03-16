package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_index_temp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by huamx on 2018/03/22
 */
public interface EntryIndexTempRepository extends JpaRepository<Tb_entry_index_temp,String>,JpaSpecificationExecutor<Tb_entry_index_temp> {
    Integer deleteByUniquetag(String uniquetag);

    @Modifying
    @Transactional
    @Query(value = "delete from Tb_entry_index_temp  where uniquetag = ?1 and entryid in (?2)")
    Integer deleteByUniquetagAndEntryids(String uniquetag,String[] entryids);

    @Query(value = "select t from Tb_entry_index_temp t where entryid in (?1) and uniquetag = ?2")
    Page<Tb_entry_index_temp> findByEntryidInAndUniquetag(List<String> entryids,String uniquetag,Pageable pageable);

    @Query(value = "select t from Tb_entry_index_temp t where  uniquetag = ?1")
    Page<Tb_entry_index_temp> findByUniquetag(String uniquetag,Pageable pageable);
    
    @Query(value = "select t from Tb_entry_index_temp t where uniquetag = ?2 and entryid in (?1) order by serial,filenumber,descriptiondate,title")
    List<Tb_entry_index_temp> findByEntryidInAndUniquetag(String[] entryids,String uniquetag);

    @Query(value = "select t from Tb_entry_index_temp t where uniquetag = ?2 and entryid in (?1) order by sortsequence")
    List<Tb_entry_index_temp> findByEntryidInAndUniquetagOrderBySortsequence(String[] entryids,String uniquetag);

    Tb_entry_index_temp findByEntryidInAndUniquetag(String entryids,String uniquetag);

    @Query(value = "select entryid from Tb_entry_index_temp where nodeid = ?1 and uniquetag = ?2 order by sortsequence")
    List<String> findEntryidByNodeidAndUniquetag(String nodeid, String uniquetag);

    @Query(value = "select entryid from Tb_entry_index_temp where  uniquetag = ?1 order by sortsequence")
    List<String> findEntryidByUniquetag(String uniquetag);

    @Query(value = "select entryid from Tb_entry_index_temp where  uniquetag = ?1 and sparefield5 =?2")
    List<String> findEntryidByUniquetagAndSparefield5(String uniquetag, String sparefield5);

    @Query(value = "select * from tb_entry_index_temp where  uniquetag = ?1 and sparefield5 != '8' order by sparefield5",nativeQuery = true)
    List<Tb_entry_index_temp> getUniquetagAndSparefield5Less(String uniquetag);

    @Query(value = "select t from Tb_entry_index_temp t where  t.uniquetag = ?1 order by t.sortsequence")
    List<Tb_entry_index_temp> findByUniquetagOrderBySortsequence(String uniquetag);

    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index_temp set sortsequence = ?1 where entryid = ?2 and uniquetag = ?3")
    Integer updateSortsequenceByEntryidAndUniquetag(Integer sortsequence, String entryid, String uniquetag);

    @Modifying
    @Query(value = "delete from Tb_entry_index_temp t where uniquetag = ?2 and entryid in (?1) ")
    Integer deleteByEntryidInAndUniquetag(String[] entryids,String uniquetag);

    @Modifying
    @Query(value = "update Tb_entry_index_temp t set t.sparefield5=?1 where entryid = ?2 ")
    Integer  updateSparefield5(String sparefield5,String entryid);

    @Modifying
    @Query(value = "delete from Tb_entry_index_temp  where entryid in ?1 ")
    Integer  deleteByEntryidIn(String[] entryids);

    List<Tb_entry_index_temp> findByUniquetagOrderByFilenumberAscDescriptiondateAscTitleAsc(String uniquetag);

    List<Tb_entry_index_temp> findByEntryidIn(String[] entryids);


    @Query(value = "select t from  Tb_entry_index_temp t  where entryid=?1")
    Tb_entry_index_temp findByEntryid(String entryid);

    @Query(value = "select entryid from Tb_entry_index_temp where entryretention=?1 and uniquetag = ?2")
    List<String> findEntryidByEntryretention(String entryretention, String uniquetag);
    
    @Query(value = "select entryid from Tb_entry_index_temp where archivecode=?1 and nodeid = ?2 and uniquetag = ?3")
    List<String> findByNodeid(String archivecode, String nodeid, String uniquetag);
    
    Integer deleteByEntryid(String entryid);

    Integer deleteByNodeidIn(String[] nodeids);

    @Query(value = "select max(sortsequence) from Tb_entry_index_temp t where  uniquetag = ?1")
    String findByUniquetag(String uniquetag);

    @Query(value = "select archivecode from Tb_entry_index_temp where nodeid = ?1 and archivecode=?2 and uniquetag like concat('%',?3,'%') ")
    List<String> findArchivecodeByNodeidAndCode(String nodeid,String archivecode,String uniquetag);

    @Query(value = "select t from Tb_entry_index_temp t where  uniquetag = ?1 and sortsequence is not null and (sortsequence = ?2 or sortsequence > ?2) order by sortsequence")
    List<Tb_entry_index_temp> findByUniquetagAndSortsequence(String uniquetag,Integer sortsequence);

    @Query(value = "select archivecode from  Tb_entry_index_temp where entryid =?1")//根据临时表id去获取档号
    String  findArchivecodeByEntryid(String entryid);


    @Query(value = "select entryid from  Tb_entry_index_temp where nodeid =?1 and uniquetag=?2 and (archivecode is null or archivecode='')")
    List<String>  checkTempArchivecode(String  nodeid,String uniquetag);

    @Query(value = "select t from  Tb_entry_index_temp t where nodeid =?1 and uniquetag=?2")
    List<Tb_entry_index_temp>  getTempEntryids(String  nodeid,String uniquetag);

    @Modifying
    @Query(value = "update Tb_entry_index_temp set archivecode = ?1 where entryid = ?2 and uniquetag = ?3")
    int updateArchivecode(String archivecode,String entryid,String uniquetag);
}
