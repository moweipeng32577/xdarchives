package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface UserRoleRepository extends JpaRepository<Tb_user_role, Integer> {
    /**
     * 根据用户id删除数据
     * @param userids
     * @return
     */
    Integer deleteAllByUseridIn(String[] userids);

    Integer deleteAllByRoleidIn(String[] roleids);

    @Query(value = "select roleid from Tb_user_role where userid=?1")
    List<String> findRoleidByUserid(String userid);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);

    List<Tb_user_role> findByRoleid(String roleid);

    @Query(value = "select t.roleid from Tb_user_role t where t.userid = ?1")
    List<String> findByUserid(String userid);

    Integer deleteByRoleid(String roleid);

    int deleteByRoleidAndUseridIn(String roleid,String[] userids);

    @Query(value = "select t.userid from Tb_user_role t where roleid in (select roleid from Tb_role_function where fnid in (select fnid from Tb_right_function where " +
            "functionname = ?1))")
    List<String> findUseridsByFunctionname(String functionname);
}
