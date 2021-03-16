package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_role_data_node_sx;
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
public interface SxRoleDataNodeRepository extends JpaRepository<Tb_role_data_node_sx, String> {

    /**
     * 根据权限id数组批量删除用户组数据权限
     * @param roleids 用户组id
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role_data_node where roleid in(?1)",nativeQuery = true)
    Integer deleteAllByRoleidIn(String[] roleids);

    Page<Tb_role_data_node_sx> findByRoleidIn(String[] roleids, Pageable pageable);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role_data_node where nodeid in ?1",nativeQuery = true)
    Integer deleteAllByNodeidIn(String[] nodeids);

    @Query(value = "select nodeid from tb_role_data_node  where roleid = ?1",nativeQuery = true)
    List<String> findSxByRoleid(String roleid);
}
