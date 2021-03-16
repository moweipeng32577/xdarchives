package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_node_sx;
import com.wisdom.web.entity.Tb_user_node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface SxUserNodeRepository extends JpaRepository<Tb_user_node_sx, Integer> {
    /**
     * 根据节点id删除节点用户数据
     * @param nodeId 节点id
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    int deleteByNodeid(String nodeId);

    /**
     * 删除用户节点中间表
     * @param userids 用户id数组
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_node where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] userids);

    List<Tb_user_node_sx> findByNodeid(String nodeid);

    List<Tb_user_node_sx> findByNodeidIn(String[] nodeids);

    @Query(value = "select * from tb_user_node n where nodeid in ( select nodeid from tb_node where workid=?1 and sortsequence='2')",nativeQuery = true)
    List<Tb_user_node_sx> getUserNodes(String workid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);

    @Query(value = "select t.nodeid from tb_user_node t where t.userid = ?1",nativeQuery = true)
    List<String> findByUserid(String userid);

    @Query(value = "select t.userid from tb_user_node t where t.nodeid = ?1 order by t.sortsequence asc ",nativeQuery = true)
    List<String> findUserids(String nodeid);

    @Query(value = "select * from tb_user_node t where t.nodeid = ?1 order by t.sortsequence asc ",nativeQuery = true)
    List<Tb_user_node_sx> findUserNodes(String nodeid);

    @Query(value = "select * from tb_user_node t where t.nodeid in (?1) order by t.sortsequence asc ",nativeQuery = true)
    List<Tb_user_node_sx> findUserNodesIn(String[] nodeid);
}
