package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.web.entity.Tb_data_node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Leo on 2020/5/22 0022.
 */
public interface SecondaryDataNodeRepository extends JpaRepository<Tb_data_node_sx, String>, JpaSpecificationExecutor<Tb_data_node_sx> {

    @Query(value = "select * from tb_data_node where nodeid in(select nodeid from tb_user_data_node where userid=?1) ORDER BY sortsequence",nativeQuery=true)
    List<Tb_data_node_sx> getSxMyAuth(String userid);

    @Query(value = "select * from tb_data_node where nodeid in (select nodeid from tb_role_data_node where roleid in (select roleid from tb_user_role where userid=?1)) ORDER BY sortsequence",nativeQuery=true)
    List<Tb_data_node_sx> getSxMyAuthFromRole(String userid);

    @Query(value = "select * from tb_data_node n ORDER BY sortsequence",nativeQuery = true)
    List<Tb_data_node_sx> findSxAllNodeidParentid();

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByNodeid(String nodeId);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_data_node where nodeid in ?1",nativeQuery = true)
    Integer deleteByNodeidIn(String[] nodeIds);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_data_node where refid in ?1",nativeQuery = true)
    int deleteByRefidIN(String[] refIds);

    @Query(value = "select t.nodeid from tb_data_node t where refid in ?1",nativeQuery = true)
    String[] findByRefidIN(String[] refIds);

    Page<Tb_data_node_sx> findByOrganid(String organid, Pageable pageable);

    /**
     * 获取第一层机构的父节点（分类）
     * @return
     */
    @Query(value = "select * from tb_data_node d where d.nodeid in (" +
            "select distinct parentnodeid from tb_data_node where refid in (" +
            "select organid from tb_right_organ where parentid=?1))",nativeQuery = true)
    List<Tb_data_node_sx> getParentOfFirstOrgan(String parentid);

    List<Tb_data_node_sx> findByRefid(String refId);

    List<Tb_data_node_sx> findByRefidIn(String[] refIds);

    /**
     * @param nodeid
     * @return Tb_data_node
     */
    Tb_data_node_sx findByNodeid(String nodeid);

    List<Tb_data_node_sx> findByNodeidIn(String[] nodeIds);

    /**
     * 获取：某节点下，最大的节点编码
     * @param parentid
     * @return
     */
    @Query(value = "SELECT max(nodecode) FROM tb_data_node where parentnodeid=?1",nativeQuery = true)
    String getMaxNodecodeByParentnodeid(String parentid);

    List<Tb_data_node_sx> findByParentnodeidIsNullOrParentnodeid(String pcid);

    @Query(value = "SELECT max(nodecode) FROM tb_data_node where parentnodeid=?1 or parentnodeid is null",nativeQuery = true)
    String getMaxNodecodeByParentnodeidOrNull(String parentid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_data_node set nodelevel = nodelevel + 1,classid = ?2,classlevel = ?3 where nodeid in ?1",nativeQuery = true)
    int moveOrganUpdate(String[] nodeids,String classid,int classlevel);

    @Query(value="select * from tb_data_node d where d.nodeid in ?1 and d.refid in (select organid from tb_right_organ where parentid='0')",nativeQuery = true)
    List<Tb_data_node_sx> findTopNodes(String[] ids);

    @Modifying
    @Query(value = "update tb_data_node set parentnodeid = ?1 where nodeid in ?2",nativeQuery = true)
    int updateParentid(String pid,String[] nodeids);

    @Query(value = "select * from tb_data_node t where classid=?1",nativeQuery = true)
    List<Tb_data_node_sx> findByClassid(String classid);

    Page<Tb_data_node_sx> findByClassid(String classid, Pageable pageable);

    @Query(value = "SELECT * FROM tb_data_node where refid in (?1)",nativeQuery = true)
    List<Tb_data_node_sx> findSxByRefidIn(String[] refIds);

    @Query(value = "select * from tb_data_node t where parentnodeid=?1 or parentnodeid is null",nativeQuery = true)
    List<Tb_data_node_sx> findSxByParentnodeidIsNullOrParentnodeid(String pcid);

    @Query(value = "select * from tb_data_node u where u.refid=?1",nativeQuery = true)
    List<Tb_data_node_sx> findSxByRefid(String refId);

    @Query(value = "select * from tb_data_node d where d.nodeid in (" +
            "select distinct parentnodeid from tb_data_node where refid in (" +
            "select organid from tb_right_organ where parentid=?1))",nativeQuery = true)
    List<Tb_data_node_sx> getSxParentOfFirstOrgan(String parentid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_data_node set luckstate = ?2 where nodeid = ?1",nativeQuery = true)
    Integer updateNodeLuckState(String nodeid, String luckstate);

    @Query(value = "select nodename from tb_data_node where nodeid=?1",nativeQuery = true)
    String findNodenameByNodeid(String nodeid);
}
