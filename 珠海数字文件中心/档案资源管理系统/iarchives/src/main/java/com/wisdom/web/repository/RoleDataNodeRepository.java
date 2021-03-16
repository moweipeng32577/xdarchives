package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_data_node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleDataNodeRepository extends JpaRepository<Tb_role_data_node, String> {

    /**
     * 根据用户组id查询用户组数据节点对应关系
     * @param roleid 权限id
     * @return
     */
	@Query(value = "select rtrim(r.nodeid) from Tb_role_data_node r where r.roleid = ?1")
    List<String> findByRoleid(String roleid);
    @Query(value = "select rtrim(r.nodeid) from tb_role_data_node_sx r where r.roleid = ?1",nativeQuery = true)
    List<String> findSxByRoleid(String roleid);

    /**
     * 根据权限id数组批量删除用户组数据权限
     * @param roleids 用户组id
     * @return
     */
    Integer deleteAllByRoleidIn(String[] roleids);

    Integer deleteAllByNodeidIn(String[] nodeids);

    Integer deleteByNodeid(String nodeId);

    @Query(value = "select t.nodeid from Tb_role_data_node t where t.roleid in (select roleid from Tb_user_role where userid = ?1)")
    List<String> findRoleDataAuth(String userid);
}
