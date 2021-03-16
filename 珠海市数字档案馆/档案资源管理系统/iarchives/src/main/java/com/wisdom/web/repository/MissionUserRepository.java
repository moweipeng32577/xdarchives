package com.wisdom.web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wisdom.web.entity.Tb_mission_user;

public interface MissionUserRepository
		extends JpaRepository<Tb_mission_user, String>, JpaSpecificationExecutor<Tb_mission_user> {
		
	Tb_mission_user findByMuid(String muid);
	
	@Query(value = "select userid from Tb_mission_user where agentuserid = ?1")
	List<String> findByAgentuserid(String userid);
	
	@Query(value = "select distinct userid from Tb_mission_user where agentuserid in (?1)")
	List<String> findByAgentuseridIn(String[] userid);
	
	@Query(value = "select agentuserid from Tb_mission_user where userid in (?1)")
	List<String> findAgentUseridByUserid(String userid);
	
	@Modifying
    @Transactional
    @Query(value = "delete from Tb_mission_user where agentuserid = ?1 and userid in (?2)")
	Integer deleteByAgentUseridAndUseridIn(String agentuserid, String[] userid);
}