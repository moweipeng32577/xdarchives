package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_transdoc_preview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Leo on 2021/2/5 0005.
 */
public interface TransdocPreviewRepository extends JpaRepository<Tb_transdoc_preview, String>, JpaSpecificationExecutor<Tb_transdoc_preview> {

    @Query(value = "select t.entryid from Tb_transdoc_preview t where t.entryid in (?1)")
    List<String> findByEntryid(String[] entryids);

    @Query(value = "select t.entryid from Tb_transdoc_preview t where t.nodeid=?1")
    List<String> findEntryidByNodeid(String nodeid);

    @Transactional
    @Modifying
    List<String> deleteByEntryidIn(String[] entryids);

    List<String> deleteAllByNodeidIn(String[] nodeid);
}
