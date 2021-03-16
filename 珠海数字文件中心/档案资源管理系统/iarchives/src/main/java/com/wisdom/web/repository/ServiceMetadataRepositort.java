package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_service_metadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by SunK on 2020/5/9 0009.
 */
public interface ServiceMetadataRepositort extends JpaRepository<Tb_service_metadata, Integer>,
        JpaSpecificationExecutor<Tb_service_metadata> {

    Page<Tb_service_metadata> findByEntryids(String entryid, Pageable pageable);

    Tb_service_metadata findAllBySid(String sid);

    @Modifying
    @Transactional
    Integer deleteBySidIn(String[] entryidData);

    @Modifying
    @Transactional
    @Query(value = "delete from tb_service_metadata where entryids in ?1" , nativeQuery = true)
    Integer deleteByEntryids(String[] entryidData);

    @Modifying
    @Transactional
    Integer deleteByEntryidsIn(String[] entryidData);
}
