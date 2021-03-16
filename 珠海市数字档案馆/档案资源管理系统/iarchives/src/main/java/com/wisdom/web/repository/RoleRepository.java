package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role;
import com.wisdom.web.entity.Tb_user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleRepository extends JpaRepository<Tb_role, Integer>,JpaSpecificationExecutor<Tb_role> {

    @Query(value = "select t from Tb_role t where t.roleid in (select ur.roleid from Tb_user_role ur where ur.userid=?2) or t.roleid in (select roleid from Tb_group_role where groupid in (?1))")
    List<Tb_role> findByroles(String[] groupids, String userid);

    List<Tb_role> findByRoleidIn(String[] roleids);

    Integer deleteAllByRoleidIn(String[] roleids);

    @Modifying
    @Query(value = "update Tb_role set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2")
    int modifyUsergroupOrder(int start,int end);

    @Query(value = "select t.rolename from Tb_role t where roleid in (select roleid from Tb_user_role where userid=?1)")
    List<String> findByuserid(String userid);

    @Query(value = "select t from Tb_role t where roleid in (select roleid from Tb_user_role where userid=?1)")
    List<Tb_role> findBygroups(String userid);

    Tb_role findByRoleid(String roleid);

//    Page<Tb_role> findByOrganid(Pageable pageable, String organId);

//    List<Tb_role> findByOrganidOrderBySortsequence( String organId);

    /**
     * 删除 userids所在的角色以外 的角色
     * @param userids
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "delete from Tb_role where roleid not in (" +
            "select roleid from Tb_user_role where userid in (?1))")
    Integer deleteByIds(String[] userids);

//    Integer deleteByOrganid(String organId);

//    @Modifying
//    @Transactional
//    @Query(value = "update tb_role set organid=(" +
//            "SELECT organid FROM tb_user where LoginName = ?1)" +
//            "where rolename in (?2)", nativeQuery = true)
//    int updateAdminRoleOrgan(String loginName, String[] realName);

    List<Tb_role> findByRolenameIn(String[] roleName);

    @Query(value = "select new Tb_role(r.roleid,r.rolename) from Tb_role r")
    List<Tb_role> findAllRoleidRolename();
}
