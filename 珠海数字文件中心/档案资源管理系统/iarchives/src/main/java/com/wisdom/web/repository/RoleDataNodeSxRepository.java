package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_data_node;
import com.wisdom.web.entity.Tb_role_data_node_sx;
import com.wisdom.web.entity.Tb_role_function_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleDataNodeSxRepository extends JpaRepository<Tb_role_data_node_sx, String> {

    /**
     * 根据权限id数组批量删除用户组数据权限
     * @param roleids 用户组id
     * @return
     */
    Integer deleteAllByRoleidIn(String[] roleids);

    Page<Tb_role_data_node_sx> findByRoleidIn(String[] roleids, Pageable pageable);

    Integer deleteAllByNodeidIn(String[] nodeids);

    @Query(value = "select rtrim(r.nodeid) from Tb_role_data_node_sx r where r.roleid = ?1")
    List<String> findSxByRoleid(String roleid);
}
