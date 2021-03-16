package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_data_node;
import com.wisdom.web.entity.Tb_user_data_node_sx;
import com.wisdom.web.entity.Tb_user_function_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface UserDataNodeSxRepository extends JpaRepository<Tb_user_data_node_sx, String> {

	/**
	 * 删除数据权限中间表
	 * 
	 * @param userids
	 *            用户id数组
	 * @return
	 */
	Integer deleteAllByUseridIn(String[] userids);

	Page<Tb_user_data_node_sx> findByUseridIn(String[] userids, Pageable pageable);

	Integer deleteAllByNodeidIn(String[] nodeids);

	@Query(value = "select rtrim(t.nodeid) from Tb_user_data_node_sx t where t.userid = ?1")
	List<String> findSxByUserid(String userid);

	@Query(value="select rtrim(t.nodeid) from Tb_data_node_sx t where nodeid in (select nodeid from Tb_role_data_node_sx where roleid in (?1)) or nodeid in (select nodeid from Tb_user_data_node_sx where userid=?2)")
	List<String> findSxBynodes(String[] roleids, String userid);

}