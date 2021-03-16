package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface UserNodeRepository extends JpaRepository<Tb_user_node, Integer> {
    /**
     * 根据节点id删除节点用户数据
     * @param nodeId 节点id
     * @return
     */
    int deleteByNodeid(String nodeId);

    /**
     * 删除用户节点中间表
     * @param userids 用户id数组
     * @return
     */
    Integer deleteAllByUseridIn(String[] userids);

    List<Tb_user_node> findByNodeid(String nodeid);

    List<Tb_user_node> findByNodeidIn(String[] nodeids);

    @Query(value = "select n from Tb_user_node n where nodeid in ( select nodeid from Tb_node where workid=?1 and sortsequence='2')")
    List<Tb_user_node> getUserNodes(String workid);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);

    @Query(value = "select t.nodeid from Tb_user_node t where t.userid = ?1")
    List<String> findByUserid(String userid);

    @Query(value = "select t.userid from Tb_user_node t where t.nodeid = ?1 order by t.sortsequence asc ")
    List<String> findUserids(String nodeid);

    @Query(value = "select t from Tb_user_node t where t.nodeid = ?1 order by t.sortsequence asc ")
    List<Tb_user_node> findUserNodes(String nodeid);

    @Query(value = "select t from Tb_user_node t where t.nodeid in (?1) order by t.sortsequence asc ")
    List<Tb_user_node> findUserNodesIn(String[] nodeid);

    @Query(value = "select t from Tb_user_node t where t.nodeid in(select id from Tb_node where workid in (select workid from Tb_work where worktext=?1)) and t.userid=?2")
    List<Tb_user_node> findNodeidUser(String workName,String userid);

    @Query(value = "select userid from tb_user_node where nodeid=(select nodeid from tb_node where workid=(select workid from tb_work where worktext=?1) and sortsequence=?2)",nativeQuery = true)
    List<String> findAllUserids(String worktext,int sortsequence);

    @Query(value = "select count(*) from tb_node where workid=(select workid from tb_work where worktext=?1)",nativeQuery = true)
    Integer findCountByName(String worktext);
}
