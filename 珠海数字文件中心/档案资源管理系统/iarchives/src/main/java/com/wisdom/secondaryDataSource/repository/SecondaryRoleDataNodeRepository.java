package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_role_data_node_sx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo on 2020/5/23 0023.
 */
public interface SecondaryRoleDataNodeRepository extends JpaRepository<Tb_role_data_node_sx, String> {
    @Query(value = "select t.nodeid from Tb_role_data_node t where t.roleid in (select roleid from Tb_user_role where userid = ?1)",nativeQuery = true)
    List<String> findRoleDataAuth(String userid);
}
