package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_ele_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleEleFunctionRepository extends JpaRepository<Tb_role_ele_function, String> {

    @Query(value = "select t from Tb_role_ele_function t where t.usergroupid = ?1")
     List<Tb_role_ele_function> findByUsergroupid(String usergroupid);

    @Query(value = "select t from Tb_role_ele_function t where t.platform = ?1 and t.usergroupid in ?2")
    List<Tb_role_ele_function> findByPlatformAndUsergroupid(String sysType, String[] usergroupid);

    int deleteAllByUsergroupidIn(String[] usergroupids);

}
