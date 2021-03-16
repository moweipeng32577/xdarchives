package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/2/21.
 */
public interface ElectronicVersionRepository extends JpaRepository<Tb_electronic_version,String> {

    Page<Tb_electronic_version> findByEleid(String eleid, Pageable pageable);

    Tb_electronic_version findById(String id);

    int deleteByIdIn(String[] eleVersions);

    List<Tb_electronic_version> findByIdIn(String[] eleVersions);

    @Modifying
    @Query(value = "insert into tb_electronic_version select * from tb_electronic_version_capture where entryid in ?1", nativeQuery = true)
    int moveEletronicVersions(String[] entryidData);

    int deleteByEleidIn(String[] eleids);

    int deleteByEntryidIn(String[] entryids);

    List<Tb_electronic_version> findByEleid(String eleid);

    List<Tb_electronic_version> findByEntryidIn(String[] entryids);
}
