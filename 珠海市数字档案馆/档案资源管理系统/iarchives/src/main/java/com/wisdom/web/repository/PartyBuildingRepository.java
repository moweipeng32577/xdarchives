package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_partybuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyBuildingRepository extends JpaRepository<Tb_partybuilding,String>, JpaSpecificationExecutor<Tb_partybuilding> {

    @Modifying
    @Query(value = "delete from tb_partybuilding where partybuildingID in (?1)",nativeQuery = true)
    Integer deleteByPartybuildingID(String[] partybuildingIDs);

    @Modifying
    @Query(value = "update tb_partybuilding set publishstate = ?2, publishtime = ?3 where partybuildingID in (?1)",nativeQuery = true)
    Integer updatePublishstate(String[] partybuildingIDs, Integer integer, String s);

    @Query(value = "select * from tb_partybuilding where partybuildingID in (?1)",nativeQuery=true)
    List<Tb_partybuilding> findByPartybuildings(String[] ids);
}

