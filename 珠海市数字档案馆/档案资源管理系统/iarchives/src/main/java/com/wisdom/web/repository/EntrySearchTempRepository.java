package com.wisdom.web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import com.wisdom.web.entity.Tb_entry_search_temp;

public interface EntrySearchTempRepository extends JpaRepository<Tb_entry_search_temp,String>,JpaSpecificationExecutor<Tb_entry_search_temp> {
	
	Tb_entry_search_temp findByNodeidAndTypeAndUserid(String nodeid, String type, String userid);
	
	@Modifying
	@Transactional
	Integer deleteByNodeidAndTypeAndUserid(String nodeid, String type, String userid);
}