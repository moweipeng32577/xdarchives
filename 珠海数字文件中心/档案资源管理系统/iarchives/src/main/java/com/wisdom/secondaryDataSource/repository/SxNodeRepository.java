package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_node_sx;
import com.wisdom.web.entity.Tb_node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface SxNodeRepository extends JpaRepository<Tb_node_sx, Integer>,JpaSpecificationExecutor<Tb_node_sx> {

    /**
     * 根据工作流id查询节点
     * @param work_id 工作流id
     * @return
     */
    public List<Tb_node_sx> findByWorkidOrderBySortsequence(String work_id);


    /**
     * 根据下个节点id数组查找节点
     * @param nextids 节点id
     * @return
     */
    public List<Tb_node_sx> findByNextidIn(String[] nextids);

    /**
     * 根据节点id数组查找节点
     * @param ids 节点id
     * @return
     */
    public List<Tb_node_sx> findByNodeidIn(String[] ids);

    /**
     * 根据节点id查找节点
     * @param id 节点id
     * @return
     */
    public Tb_node_sx findByNodeid(String id);


    /**
     * 按单位查找用户
     * @param pageable
     * @param workid
     * @return
     */
    Page<Tb_node_sx> findByWorkidOrderBySortsequenceDesc(Pageable pageable, String workid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update Tb_node_sx set nextid = ?1,nexttext=?2,sortsequence=?3,desci=?4 where id=?5")
    int updateNodeById(String nextid, String nexttext, int orders, String desci, String id);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update Tb_node_sx n set n.text = ?1,n.desci=?2 where n.id=?3")
    int updateNodeById(String text, String desci, String id);

    //节点后移一位
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update Tb_node_sx set sortsequence=sortsequence+1 where workid=?1 and sortsequence >=?2")
    int updateNodeByOrders(String workid, int sortsequence);

    //调序选中的节点
    @Query(value = "select t from Tb_node_sx t where t.workid=?1 and sortsequence in (?2)")
    Page<Tb_node_sx> findBySortsequence(String workid, int[] sequences, Pageable pageable);

    @Query(value = "select t from Tb_node_sx t where t.workid=?1 and t.sortsequence in (?2)")
    List<Tb_node_sx> findBySortsequence(String workid, int[] sortquences);
    /**
     * 根据节点id删除节点
     * @param nodeid
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    int deleteByNodeid(String nodeid);

    /**
     * 根据流程id与顺序查找节点
     * @param workid
     * @param orders
     * @return
     */
    Tb_node_sx findByWorkidAndSortsequence(String workid, int orders);

    List<Tb_node_sx> findBySortsequenceGreaterThanAndWorkidOrderBySortsequence(int orders, String workid);

    @Query(value = "select t from Tb_node_sx t where workid in (select workid from Tb_work_sx where worktext=?1) and sortsequence=2")
    Tb_node_sx getNode(String workName);

    @Query(value = "select t from Tb_node_sx t where nodeid in (?1)")
    List<Tb_node_sx> getNodes(String[] ids);

    @Query(value = "select t from Tb_node_sx t where workid in (select workid from Tb_work_sx where worktext=?1) and sortsequence=1")
    Tb_node_sx getStartNode(String workName);

    @Query(value = "select n.nodeid from Tb_node_sx n where workid = ?1")
    List<String> findByWorkid(String workid);

    @Query(value = "select n from Tb_node_sx n where workid = ?1 order by n.sortsequence ASC ")
    List<Tb_node_sx> findNodeByWorkid(String workid);

    @Query(value = "select n.nodeid from Tb_node_sx n where workid in (?1)")
    List<String> findByWorkidIn(String[] workid);

    @Query(value = "select t from Tb_node_sx t where workid in (select workid from Tb_work_sx where worktext=?1) and sortsequence=2")
    Tb_node_sx getEndNode(String workName);
}