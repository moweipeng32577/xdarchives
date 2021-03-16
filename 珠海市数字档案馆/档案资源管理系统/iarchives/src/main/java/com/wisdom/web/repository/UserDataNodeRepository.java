package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_data_node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface UserDataNodeRepository extends JpaRepository<Tb_user_data_node, String> {

	/**
	 * 删除数据权限中间表
	 * 
	 * @param userids
	 *            用户id数组
	 * @return
	 */
	Integer deleteAllByUseridIn(String[] userids);

	/**
	 * 删除数据权限中间表
	 * 
	 * @param nodeids
	 *            节点id数组
	 * @return
	 */
	Integer deleteAllByNodeidIn(String[] nodeids);
	@Modifying
	@Transactional
	@Query(value = "delete from Tb_user_data_node where userid =?1")
	Integer deleteAllByUserid(String userid);


	/**
	 * 根据用户id获取用户数据权限
	 * 
	 * @param userid
	 *            用户id
	 * @return
	 */
	@Query(value = "select rtrim(t.nodeid) from Tb_user_data_node t where t.userid = ?1")
	List<String> findByUserid(String userid);
	@Query(value = "select rtrim(t.nodeid) from tb_user_data_node_sx t where t.userid = ?1",nativeQuery = true)
	List<String> findSxByUserid(String userid);

	Integer deleteByNodeid(String nodeId);

	@Transactional
	Integer deleteAllByUseridNotIn(String[] userid);

	@Modifying
	@Transactional
	@Query(value = "delete from Tb_user_data_node where userid in (select userid from Tb_user_role where roleid=?1)")
	Integer deleteByRoleId(String roleid);

	@Modifying
	@Transactional
	@Query(value = "delete from Tb_user_data_node where userid in (select userid from Tb_user_node where nodeid=?1)")
	Integer deleteByWorkNodeId(String nodeid);

	@Query(value = "select t from Tb_user_data_node t where t.userid = ?1 and t.nodeid = ?2")
	List<Tb_user_data_node> findByUseridAndNodeid(String userid, String nodeid);

	@Query(value = "select count(nodeid) from Tb_user_data_node where userid = ?1")
	Integer findCountByUserId(String userid);

	/**
	 * 查询全部数据权限
	 * @param roleids
	 * @param userid
	 * @return
	 */
	@Query(value="select rtrim(t.nodeid) from Tb_data_node t where nodeid in (select nodeid from Tb_role_data_node where roleid in (?1)) or nodeid in (select nodeid from Tb_user_data_node where userid=?2)")
	List<String> findBynodes(String[] roleids, String userid);
	@Query(value="select rtrim(t.nodeid) from tb_data_node_sx t where nodeid in (select nodeid from tb_role_data_node_sx where roleid in (?1)) or nodeid in (select nodeid from tb_user_data_node_sx where userid=?2)",nativeQuery = true)
	List<String> findSxBynodes(String[] roleids, String userid);
}