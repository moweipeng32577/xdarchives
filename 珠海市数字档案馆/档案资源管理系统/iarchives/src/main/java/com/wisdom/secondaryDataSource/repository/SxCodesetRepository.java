package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.web.entity.Tb_codeset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by tanly on 2017/11/6 0006.
 */
public interface SxCodesetRepository extends JpaRepository<Tb_codeset_sx, Integer> , JpaSpecificationExecutor<Tb_codeset_sx> {

    List<Tb_codeset_sx> findByDatanodeidOrderByOrdernum(String datanodeid);

    List<Tb_codeset_sx> findByDatanodeidAndFiledtableInOrderByOrdernum(String datanodeid,String[] filedtable);

    @Query(value = "select * from Tb_codeset where datanodeid=?1 and ISNULL(filedtable,'')=?2 order by ordernum",nativeQuery=true)
    List<Tb_codeset_sx> findFieldlengthByDatanodeidfAndFiledtable(String datanodeid,String filedtable);

    List<Tb_codeset_sx> findByDatanodeidIn(String[] nodeId);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByDatanodeidAndFieldcode(String nodeId, String fieldCode);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_codeset where datanodeid in ?1 ",nativeQuery = true)
    Integer deleteByDatanodeidIn(String[] nodeids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_codeset where datanodeid =?1 and filedtable in(?2)",nativeQuery = true)
    Integer deleteByDatanodeidAndTable(String nodeid,String[] table);

    @Query(value = "select * from tb_codeset where datanodeid in (select nodeid from tb_data_node_sx where organid=?1) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<Tb_codeset_sx> findByOrganid(String organid, Pageable pageable);

    @Query(value = "select distinct datanodeid from Tb_codeset where datanodeid in ?1",nativeQuery = true)
    Set<String> getNodeidByNodeidIn(String[] nodeid);

    Integer deleteByDatanodeidInAndFiledtableIn(String[] nodeid,String[] filedtable);
}
