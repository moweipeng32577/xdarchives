package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface DataEventRepository
		extends JpaRepository<Tb_data_event, String>, JpaSpecificationExecutor<Tb_data_event> {
			
	Tb_data_event findByEventid(String eventid);

	Tb_data_event findByEventname(String eventname);
	
	@Modifying
	@Transactional
    @Query(value = "update Tb_data_event set eventname = ?1, eventnumber = ?2 where eventid = ?3")
	Integer updateByEventid(String eventname, String eventnumber, String eventid);

	@Modifying
    @Transactional
	Integer deleteByEventidIn(String[] eventid);
}