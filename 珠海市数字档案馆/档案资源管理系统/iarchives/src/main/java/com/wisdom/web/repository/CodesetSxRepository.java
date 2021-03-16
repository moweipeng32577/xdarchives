package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_codeset;
import com.wisdom.web.entity.Tb_codeset_sx;
import com.wisdom.web.entity.Tb_data_template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by tanly on 2017/11/6 0006.
 */
public interface CodesetSxRepository extends JpaRepository<Tb_codeset_sx, Integer> {

    List<Tb_codeset_sx> findByDatanodeidOrderByOrdernum(String datanodeid);

    Integer deleteByDatanodeidAndFieldcode(String nodeId, String fieldCode);

    Integer deleteByDatanodeidIn(String[] nodeids);

    Integer deleteByDatanodeid(String nodeid);

    @Query(value = "select * from tb_codeset_sx where datanodeid in (select nodeid from tb_data_node_sx where organid=?1) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<Tb_codeset_sx> findByOrganid(String organid, Pageable pageable);

    @Query(value = "select distinct datanodeid from Tb_codeset_sx where datanodeid in ?1")
    Set<String> getNodeidByNodeidIn(String[] nodeid);
}
