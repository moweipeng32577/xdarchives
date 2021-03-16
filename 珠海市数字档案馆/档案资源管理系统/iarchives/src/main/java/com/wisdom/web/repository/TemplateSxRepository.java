package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_template;
import com.wisdom.web.entity.Tb_data_template_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by Rong on 2017/10/30.
 */
public interface TemplateSxRepository extends JpaRepository<Tb_data_template_sx, String>,
        JpaSpecificationExecutor<Tb_data_template_sx> {

    List<Tb_data_template_sx> findByNodeid(String nodeid);

    Integer deleteByNodeidIn(String[] nodeids);

    Integer deleteByNodeid(String nodeid);

    @Query(value = "select * from tb_data_template_sx where nodeid in (select nodeid from tb_data_node_sx where organid=?1) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<Tb_data_template_sx> findByOrganid(String organid, Pageable pageable);

    @Query(value = "select distinct nodeid from Tb_data_template_sx where nodeid in ?1")
    Set<String> getNodeidByNodeidIn(String[] nodeid);

}