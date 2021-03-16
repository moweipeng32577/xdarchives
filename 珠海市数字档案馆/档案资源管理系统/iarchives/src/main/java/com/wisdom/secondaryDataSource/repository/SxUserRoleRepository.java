package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_role_sx;
import com.wisdom.web.entity.Tb_user_role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface SxUserRoleRepository extends JpaRepository<Tb_user_role_sx, Integer> {
    /**
     * 根据用户id删除数据
     * @param userids
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_role where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] userids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_role where roleid in (?1)",nativeQuery = true)
    Integer deleteAllByRoleidIn(String[] roleids);

    @Query(value = "select roleid from tb_user_role where userid=?1",nativeQuery = true)
    List<String> findRoleidByUserid(String userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);

    List<Tb_user_role_sx> findByRoleid(String roleid);

    @Query(value = "select t.roleid from tb_user_role t where t.userid = ?1",nativeQuery = true)
    List<String> findByUserid(String userid);
}
