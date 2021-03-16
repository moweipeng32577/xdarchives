package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.web.entity.Tb_data_template;
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
 * Created by Rong on 2017/10/30.
 */
public interface SxTemplateRepository extends JpaRepository<Tb_data_template_sx, String>,
        JpaSpecificationExecutor<Tb_data_template_sx> {

    List<Tb_data_template_sx> findByNodeid(String nodeid);

    List<Tb_data_template_sx> findByNodeidIn(String[] nodeId);

    Tb_data_template_sx findByTemplateid(String templateid);

    List<Tb_data_template_sx> findByNodeidAndFieldtableAndFieldcodeNotIn(String datanodeid, String fieldtable, String[] excludes);
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_data_template where nodeid in ?1", nativeQuery = true)
    Integer deleteByNodeidIn(String[] nodeids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByNodeid(String nodeid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByNodeidAndFieldtableIn(String nodeid,String[] fieldTable);

    @Query(value = "select * from tb_data_template where nodeid in (select nodeid from tb_data_node where organid=?1) ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<Tb_data_template_sx> findByOrganid(String organid, Pageable pageable);

    @Query(value = "select distinct nodeid from tb_data_template where nodeid in ?1", nativeQuery = true)
    Set<String> getNodeidByNodeidIn(String[] nodeid);

    List<Tb_data_template_sx> findByNodeidOrderByFsequence(String nodeid);

    @Query(value = "select count(*) from tb_data_template t where t.metadataid = ?1 and t.nodeid = ?2", nativeQuery = true)
    Integer findCountByMetadataidAndNodeid(String metadataid, String nodeid);

    @Query(value = "select count(*) from tb_data_template where nodeid = ?1 and  fieldname = ?2 and templateid != ?3 ", nativeQuery = true)
    Integer findCount(String nodeid, String fieldname, String templateid);

    @Query(value = "select ftype from tb_data_template where fieldcode = ?1 and nodeid=?2", nativeQuery = true)
    String[] findFtypeByFieldcodeAndNodeid(String fieldcode, String nodeid);

    @Query(value = "select * from tb_data_template t where gfield = 1 and nodeid = ?1 order by gsequence", nativeQuery = true)
    List<Tb_data_template_sx> findGridByNode(String nodeid);

    @Query(value = "select * from Tb_data_template t where gfield = 1 and nodeid = ?1 and fieldtable in (?2) order by gsequence", nativeQuery = true)
    List<Tb_data_template_sx> findGridByNode(String nodeid,String[] table);

    @Query(value = "select * from tb_data_template t where qfield = 1 and nodeid = ?1 order by qsequence", nativeQuery = true)
    List<Tb_data_template_sx> findQueryByNode(String nodeid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query("update Tb_data_template_sx t set t.ffield = ?1,t.gfield = ?2 where nodeid = ?3 and fieldcode = ?4 and fieldtable=?5")
    Integer updateFsquenceAndGfield(boolean ffield, boolean gfield, String nodeid, String fieldcode,String table);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query("update Tb_data_template_sx t set t.ffield = ?1,t.qfield = ?2 where nodeid = ?3 and fieldcode = ?4 and fieldtable=?5")
    Integer updateFsquenceAndQfield(boolean ffield, boolean qfield, String nodeid, String fieldcode,String table);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query("update Tb_data_template_sx t set t.gfield = ?1,t.gsequence = ?2 where nodeid = ?3 and fieldcode = ?4 and fieldtable=?5")
    Integer updateGequence(boolean gfield, long gsequence, String nodeid, String fieldcode,String table);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query("update Tb_data_template_sx t set t.qfield = ?1,t.qsequence = ?2 where nodeid = ?3 and fieldcode = ?4 and fieldtable=?5")
    Integer updateQequence(boolean qfield, long qsequence, String nodeid, String fieldcode,String table);

    @Query(value = "select * from tb_data_template t where ffield = 1 and nodeid = ?1 order by fsequence",nativeQuery = true)
    List<Tb_data_template_sx> findFormByNode(String nodeid);

    Tb_data_template_sx findByNodeidAndFieldcode(String nodeid, String fieldcode);

    Tb_data_template_sx findByNodeidAndFieldcodeAndFieldtable(String nodeid, String fieldcode, String fieldtable);

    List<Tb_data_template_sx> findByNodeidAndFieldtableInAndFieldcodeNotIn(String datanodeid,String[] fieldtable, String[] excludes);


    List<Tb_data_template_sx> findAllByNodeidAndFieldtableInOrderByFsequence(String nodeid,String[] fieldtable);

    @Query(value = "select * from Tb_data_template t where nodeid = ?1 and fieldtable in ('tb_entry_index','tb_entry_detail')", nativeQuery = true)
    List<Tb_data_template_sx> findIndexTempByNodeId(String nodeid);

    List<Tb_data_template_sx> findByNodeidAndFieldtableIn(String nodeid, String[] fieldtable);


    Integer deleteByNodeidInAndFieldtableIn(String[] nodeid, String[] fieldtable);

    @Query(value = "select distinct t.fieldname from Tb_data_template_sx t where t.fieldcode = ?1")
    String[]  getOrderFileName(String fieldcode);

    @Query(value = "select distinct (t.fieldcode) from Tb_data_template_sx t ")
    List<String>  getAllFilecode();
}