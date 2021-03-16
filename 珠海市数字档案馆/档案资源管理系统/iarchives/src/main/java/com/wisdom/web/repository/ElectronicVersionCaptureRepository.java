package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_version_capture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/2/26.
 */
public interface ElectronicVersionCaptureRepository extends JpaRepository<Tb_electronic_version_capture,String> {

    Page<Tb_electronic_version_capture> findByEleid(String eleid, Pageable pageable);

    Tb_electronic_version_capture findById(String id);

    int deleteByIdIn(String[] eleVersions);

    List<Tb_electronic_version_capture> findByIdIn(String[] eleVersions);

    List<Tb_electronic_version_capture> findByEntryidIn(String[] entryids);

    int deleteByEntryidIn(String[] entryids);

    int deleteByEleidIn(String[] eleids);

    List<Tb_electronic_version_capture> findByEleid(String eleid);

    @Modifying
    @Query(value = "insert into tb_electronic_version_capture select * from tb_electronic_version where entryid in ?1", nativeQuery = true)
    int moveCaptureVersions(String[] entryidData);
}
