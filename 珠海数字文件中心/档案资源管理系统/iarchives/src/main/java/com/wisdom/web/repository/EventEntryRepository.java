package com.wisdom.web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wisdom.web.entity.Tb_event_entry;

public interface EventEntryRepository
		extends JpaRepository<Tb_event_entry, String>, JpaSpecificationExecutor<Tb_event_entry> {
			
	@Query(value = "select eventid from Tb_event_entry where entryid = ?1")
	String findEventidByEntryid(String entryid);
			
	@Query(value = "select entryid from Tb_event_entry where entryid in (?1)")
	List<String> findByEntryidIn(String[] entryid);

	@Query(value = "select entryid from Tb_event_entry where eventid = ?1")
	List<String> findEntryidByEventId(String eventid);
	
	@Modifying
    @Transactional
	Integer deleteByEventid(String eventid);
	
	@Modifying
    @Transactional
	Integer deleteByEventidAndEntryidIn(String eventid, String[] entryid);
}