package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_node_temp;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface UserNodeTempRepository extends JpaRepository<Tb_user_node_temp, Integer> {
	
	@Query(value = "select u.userid from Tb_user_node_temp u where u.nodeid = ?1 and u.uniquetag = ?2 order by u.sortsquence asc ")
	List<String> findByNodeidAndUniquetag(String nodeid, String uniquetag);
    
	Integer deleteByUniquetag(String uniquetag);
	
	@Modifying
    @Transactional
	Integer deleteByUseridInAndUniquetag(String[] userid, String uniquetag);

	@Query(value = "select u from Tb_user_node_temp u where u.nodeid = ?1 and u.uniquetag = ?2 order by u.sortsquence asc")
	List<Tb_user_node_temp> findUserByNodeidAndUniquetag(String nodeid, String uniquetag);

	Tb_user_node_temp findByUseridAndNodeidAndUniquetag(String userid,String nodeid,String uniquetag);

	Tb_user_node_temp findByNodeidAndUniquetagAndSortsquence(String nodeid,String uniquetag,int sortsquence);

	@Modifying
	@Query(value = "update Tb_user_node_temp set sortsquence = sortsquence + ?1 where sortsquence between ?2 and ?3")
	int modifyUserNodeTempOrderUp(int count,int start,int end);

	List<Tb_user_node_temp> findByUseridInAndNodeidAndUniquetagOrderBySortsquence(String[] userids,String nodeid, String uniquetag);

	@Modifying
	@Query(value = "update Tb_user_node_temp set sortsquence = sortsquence - ?1 where sortsquence between ?2 and ?3")
	int modifyUserNodeTempOrderDown(int count,int start,int end);

	@Query(value = "select u from Tb_user_node_temp u where u.nodeid = ?1 and u.uniquetag = ?2")
	List<Tb_user_node_temp> findByNodeidAndUniquetagAll(String nodeid,String uniquetag);

	@Query(value = "select u from Tb_user_node_temp u where u.userid in (?1) and u.nodeid = ?2 and u.uniquetag = ?3 order by u.sortsquence desc ")
	List<Tb_user_node_temp> findUserByNodeidAndUniquetagDesc(String[] userid,String nodeid, String uniquetag);
}