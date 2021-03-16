package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_fulltext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2017/11/17 0017.
 */
public interface FullTextRepository extends JpaRepository<Tb_fulltext,String>,JpaSpecificationExecutor<Tb_fulltext> {
    @Query(value = "select * from tb_fulltext where match(filetext) against(?1) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<Tb_fulltext> findByFilter(String filters, Pageable pageable);

    Integer deleteByEleidIn(String[] eleids);


    Integer deleteByEntryidIn(String[] entryids);
}
