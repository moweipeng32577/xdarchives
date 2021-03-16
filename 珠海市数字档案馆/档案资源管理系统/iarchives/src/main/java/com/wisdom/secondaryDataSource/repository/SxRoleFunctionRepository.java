package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_role_function_sx;
import com.wisdom.web.entity.Tb_role_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxRoleFunctionRepository extends JpaRepository<Tb_role_function_sx, Integer> {

    List<Tb_role_function_sx> findByRoleid(String roleid);
    @Query(value = "select * from tb_role_function t where t.roleid=?1",nativeQuery = true)
    List<Tb_role_function_sx> findSxByRoleid(String roleid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role_function where roleid in (?1)",nativeQuery = true)
    Integer deleteAllByRoleidIn(String[] roleids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role_function t where t.fnid in (select fnid from tb_right_function where " +
            "functionName = ?1)",nativeQuery = true)
    Integer deleteByFnid(String functionName);

    List<Tb_role_function_sx> findByRoleidIn(String[] roleids);

}
