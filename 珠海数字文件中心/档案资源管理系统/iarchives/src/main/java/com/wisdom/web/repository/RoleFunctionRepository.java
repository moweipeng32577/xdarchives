package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleFunctionRepository extends JpaRepository<Tb_role_function, Integer> {

    List<Tb_role_function> findByRoleid(String roleid);
    @Query(value = "select * from tb_role_function_sx t where t.roleid=?1",nativeQuery = true)
    List<Tb_role_function> findSxByRoleid(String roleid);

    Integer deleteAllByRoleidIn(String[] roleids);

    @Modifying
    @Transactional
    @Query(value = "delete from Tb_role_function t where t.fnid in (select fnid from Tb_right_function where " +
            "functionName = ?1)")
    Integer deleteByFnid(String functionName);

    List<Tb_role_function> findByRoleidIn(String[] roleids);

    @Query(value = "select userid from Tb_user_role where roleid in(select roleid from Tb_role_function where fnid=?1)",nativeQuery = true)
    List<String> findUserIdByFnid(String fnid);

    @Query(value = "select fnid from Tb_role_function where roleid in (select roleid from Tb_user_role where userid = ?1)")
    List<String> findFnids(String userid);
}
