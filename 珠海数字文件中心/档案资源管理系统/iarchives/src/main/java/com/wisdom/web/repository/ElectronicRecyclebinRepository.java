package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_recyclebin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/4/23 0023.
 */
public interface ElectronicRecyclebinRepository extends JpaRepository<Tb_electronic_recyclebin,String>,JpaSpecificationExecutor<Tb_electronic_recyclebin> {

    Tb_electronic_recyclebin findByRecycleid(String recycleid);

    Integer deleteByRecycleidIn(String[] recycleidData);

    Integer deleteByEntryidIn(String[] entryids);

    Integer deleteByRecycleid(String recycleid);

    @Query(value = "select recycleid from Tb_electronic_recyclebin where entryid in (?1)")
    List<String> findRecycleidByEntryidIn(String[] entryids);

    List<Tb_electronic_recyclebin> findByEntryidAndFilepathAndFilename(String entryid, String filepath, String
            filename);
}
