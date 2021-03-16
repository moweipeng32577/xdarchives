package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_data_node_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2017/10/30 0030.
 */
public interface DataNodeSxRepository extends JpaRepository<Tb_data_node_sx, String>, JpaSpecificationExecutor<Tb_data_node_sx> {

    Integer deleteByNodeid(String nodeId);

    Integer deleteByNodeidIn(String[] nodeIds);

    Page<Tb_data_node_sx> findByOrganid(String organid, Pageable pageable);

    /**
     * 获取第一层机构的父节点（分类）
     * @return
     */
    @Query(value = "select d from Tb_data_node_sx d where d.nodeid in (" +
            "select distinct parentnodeid from Tb_data_node_sx where refid in (" +
            "select organid from Tb_right_organ where parentid=?1))")
    List<Tb_data_node_sx> getParentOfFirstOrgan(String parentid);

    List<Tb_data_node_sx> findByRefid(String refId);

    /**
     * 获取：某节点下，最大的节点编码
     * @param parentid
     * @return
     */
    @Query(value = "SELECT max(nodecode) FROM Tb_data_node_sx where parentnodeid=?1")
    String getMaxNodecodeByParentnodeid(String parentid);

    List<Tb_data_node_sx> findByParentnodeidIsNullOrParentnodeid(String pcid);

    @Query(value = "SELECT max(nodecode) FROM Tb_data_node_sx where parentnodeid=?1 or parentnodeid is null")
    String getMaxNodecodeByParentnodeidOrNull(String parentid);

    @Modifying
    @Query(value = "update Tb_data_node_sx set nodelevel = nodelevel + 1,classid = ?2,classlevel = ?3 where nodeid in ?1")
    int moveOrganUpdate(String[] nodeids,String classid,int classlevel);

    @Query(value="select d from Tb_data_node_sx d where d.nodeid in ?1 and d.refid in (select organid from Tb_right_organ where parentid='0')")
    List<Tb_data_node_sx> findTopNodes(String[] ids);

    @Modifying
    @Query(value = "update Tb_data_node_sx set parentnodeid = ?1 where nodeid in ?2")
    int updateParentid(String pid,String[] nodeids);

    @Query(value = "select t from Tb_data_node_sx t where classid=?1")
    List<Tb_data_node_sx> findByClassid(String classid);

    Page<Tb_data_node_sx> findByClassid(String classid, Pageable pageable);
}