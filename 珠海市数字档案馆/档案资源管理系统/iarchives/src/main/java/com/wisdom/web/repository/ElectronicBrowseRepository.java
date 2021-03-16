package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_browse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2017/11/6 0006.
 */
public interface ElectronicBrowseRepository extends JpaRepository<Tb_electronic_browse, String>,JpaSpecificationExecutor<Tb_electronic_browse> {

    Tb_electronic_browse findByEleid(String eleid);

    Tb_electronic_browse findByEleidOrEntryid(String eleid, String entryid);

    List<Tb_electronic_browse> findByEntryidIn(String[] entryids);

    Tb_electronic_browse findByEntryid(String entryid);

    @Modifying
    @Query(value = "delete from tb_electronic_browse where entryid = ?1",nativeQuery = true)
    Integer deleteByEntryid(String entryid);

    Integer deleteByEntryidIn(String[] entryids);

    List<Tb_electronic_browse> findByEleidIn(String[] eleids);

}
