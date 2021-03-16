package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_role_sx;
import com.wisdom.web.entity.Tb_role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxRoleRepository extends JpaRepository<Tb_role_sx, Integer>,JpaSpecificationExecutor<Tb_role_sx> {

    @Query(value = "select * from tb_role t where t.roleid in (select ur.roleid from tb_user_role ur where ur.userid=?2) or t.roleid in (select roleid from tb_group_role where groupid in (?1))",nativeQuery = true)
    List<Tb_role_sx> findByroles(String[] groupids, String userid);

    List<Tb_role_sx> findByRoleidIn(String[] roleids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role where roleid in (?1)",nativeQuery = true)
    Integer deleteAllByRoleidIn(String[] roleids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_role set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2",nativeQuery = true)
    int modifyUsergroupOrder(int start, int end);

    @Query(value = "select t.rolename from tb_role t where roleid in (select roleid from tb_user_role where userid=?1)",nativeQuery = true)
    List<String> findByuserid(String userid);

    @Query(value = "select * from tb_role t where roleid in (select roleid from tb_user_role where userid=?1)",nativeQuery = true)
    List<Tb_role_sx> findBygroups(String userid);

    Tb_role_sx findByRoleid(String roleid);

//    Page<Tb_role> findByOrganid(Pageable pageable, String organId);

//    List<Tb_role> findByOrganidOrderBySortsequence( String organId);

    /**
     * 删除 userids所在的角色以外 的角色
     * @param userids
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_role where roleid not in (" +
            "select roleid from tb_user_role where userid in (?1))",nativeQuery = true)
    Integer deleteByIds(String[] userids);

//    Integer deleteByOrganid(String organId);

//    @Modifying
//    @Transactional
//    @Query(value = "update tb_role set organid=(" +
//            "SELECT organid FROM tb_user where LoginName = ?1)" +
//            "where rolename in (?2)", nativeQuery = true)
//    int updateAdminRoleOrgan(String loginName, String[] realName);

    List<Tb_role_sx> findByRolenameIn(String[] roleName);

    @Query(value = "select new tb_role(r.roleid,r.rolename) from tb_role r",nativeQuery = true)
    List<Tb_role_sx> findAllRoleidRolename();
}
