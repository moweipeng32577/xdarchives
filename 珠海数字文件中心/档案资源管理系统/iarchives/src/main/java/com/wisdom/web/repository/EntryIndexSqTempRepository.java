package com.wisdom.web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wisdom.web.entity.Tb_entry_index_sqtemp;

public interface EntryIndexSqTempRepository extends JpaRepository<Tb_entry_index_sqtemp,String>,JpaSpecificationExecutor<Tb_entry_index_sqtemp> {
    
	Integer deleteByUniquetag(String uniquetag);
	
    Tb_entry_index_sqtemp findByEntryidAndUniquetag(String entryid,String uniquetag);
    
    @Query(value = "select calvalue from Tb_entry_index_sqtemp where entryid = ?1 and uniquetag = ?2")
    String findCalvalueByEntryidAndUniquetag(String entryid,String uniquetag);
    
    @Query(value = "select max(calvalue) from Tb_entry_index_sqtemp where nodeid = ?1 and uniquetag = ?2")
    String findMaxCalvalue(String nodeid, String uniquetag);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index_sqtemp set calvalue = ?1 where entryid = ?2 and uniquetag = ?3")
    Integer updateCalvalueByEntryidAndUniquetag(String calvalue, String entryid, String uniquetag);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index_sqtemp set newarchivecode = ?1 where entryid = ?2 and uniquetag = ?3")
    Integer updateNewarchivecodeByEntryidAndUniquetag(String newarchivecode, String entryid, String uniquetag);
    
    List<Tb_entry_index_sqtemp> findByNodeidAndUniquetag(String nodeid, String uniquetag);
    
    List<Tb_entry_index_sqtemp> findByUniquetagAndEntryidIn(String uniquetag, String[] entryList);
    
    @Query(value = "select archivecode from Tb_entry_index_sqtemp where nodeid = ?1 and uniquetag = ?2")
    List<String> findArchivecodeByNodeidAndUniquetag(String nodeid, String uniquetag);
    
    @Query(value = "select newarchivecode from Tb_entry_index_sqtemp where entryid in (?1) and uniquetag = ?2")
    List<String> findNewarchivecodeByEntryidInAndUniquetag(String[] entryList, String uniquetag);
    
    @Query(value = "select t from Tb_entry_index_sqtemp t where entryid in (?1) and uniquetag = ?2")
    Page<Tb_entry_index_sqtemp> findByEntryidInAndUniquetag(List<String> entryids, String uniquetag, Pageable pageable);
    
    @Query(value = "select entryid from Tb_entry_index_sqtemp where nodeid = ?1 and uniquetag = ?2 order by newarchivecode asc")
    List<String> findEntryidByNodeidAndUniquetag(String nodeid, String uniquetag);
    
    Tb_entry_index_sqtemp findByArchivecode(String archive);
    
    Tb_entry_index_sqtemp findByNewarchivecode(String archive);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index_sqtemp set pageno = ?1 where entryid = ?2 and uniquetag = ?3")
    Integer updatePagenoByEntryidAndUniquetag(String pageno, String entryid, String uniquetag);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index_sqtemp set pages = ?1 where entryid = ?2 and uniquetag = ?3")
    Integer updatePagesByEntryidAndUniquetag(String pages, String entryid, String uniquetag);
}