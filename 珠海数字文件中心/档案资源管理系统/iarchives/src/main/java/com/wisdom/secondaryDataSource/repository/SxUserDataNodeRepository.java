package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_data_node_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxUserDataNodeRepository extends JpaRepository<Tb_user_data_node_sx, String> {

	/**
	 * 删除数据权限中间表
	 * 
	 * @param userids
	 *            用户id数组
	 * @return
	 */
	@Transactional(value = "transactionManagerSecondary")
	@Modifying
	@Query(value = "delete from tb_user_data_node where userid in (?1)",nativeQuery = true)
	Integer deleteAllByUseridIn(String[] userids);

	Page<Tb_user_data_node_sx> findByUseridIn(String[] userids, Pageable pageable);

	@Transactional(value = "transactionManagerSecondary")
	@Modifying
	@Query(value = "DELETE from tb_user_data_node  where nodeid in ?1",nativeQuery = true)
	Integer deleteAllByNodeidIn(String[] nodeids);

	@Query(value = "select t.nodeid from tb_user_data_node t where t.userid = ?1",nativeQuery = true)
	List<String> findSxByUserid(String userid);

	@Query(value="select rtrim(t.nodeid) from tb_data_node t where nodeid in (select nodeid from tb_role_data_node where roleid in (?1)) or nodeid in (select nodeid from tb_user_data_node where userid=?2)",nativeQuery = true)
	List<String> findSxBynodes(String[] roleids, String userid);

	@Query(value="select rtrim(t.nodeid) from tb_data_node t where nodeid in (select nodeid from tb_role_data_node where roleid in (?1)) or nodeid in (select nodeid from tb_user_data_node where userid=?2)",nativeQuery = true)
	List<String> findBynodes(String[] roleids, String userid);
}