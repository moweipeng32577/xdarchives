package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_right_organ;
import com.wisdom.web.entity.Tb_user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface UserRepository extends JpaRepository<Tb_user, Integer> ,
        JpaSpecificationExecutor<Tb_user> {
        	
    Tb_user findByUserid(String userid);

    @Query(value = "select t from Tb_user t where userid in (?1)")
    Page<Tb_user> findByUserid(List<String> organids, Pageable pageable);

    @Query(value = "select t from Tb_user t where userid in (?1) order by sortsequence")
    List<Tb_user> findByUserid(String[] id);
    
    @Query(value = "select loginname from Tb_user where realname in (?1)")
    List<String> findLoginnameByRealnameIn(String[] realname);
    
    @Query(value = "select loginname from Tb_user where realname not in (?1)")
    List<String> findLoginnameByRealnameNotIn(String[] realname);
    
    @Query(value = "select organ.organid from Tb_user where userid=?1")
    String findOrganidByUserid(String userid);

    @Query(value = "select userid from Tb_user where organ.organid=?1")
    List<String> findUseridByOrganid(String organid);

    @Query(value = "select realname from Tb_user where  organ.organid=?1")
    List<String> findUseridByOrganidOrderByUserid(String organid);
    
    @Query(value = "select u from Tb_user u where outuserstate is not null and outuserstate != ''")
    Page<Tb_user> findByOutuserstateIsNotNull(Pageable pageable);

    Tb_user findByLoginname(String loginname);

    Tb_user findByNickname(String nickname);

    Tb_user findByLoginnameAndStatusNot(String loginname,long status);

    @Query(value = "select u from Tb_user u where organid = ?1")
    List<Tb_user> findByOrganid(String organid);
    
    @Query(value = "select u.userid from Tb_user u where userid in (select n.userid from Tb_user_node n where n.nodeid in ?1)")
    List<String> findNodeUserIn(String[] nodeids);
    
    @Query(value = "select u from Tb_user u where userid in (select n.userid from Tb_user_node n where n.nodeid in ?1)")
    List<Tb_user> getNodeUserIn(String[] nodeids);

    /**
     * 按照账号删除用户
     */
     Integer deleteByLoginnameIn(String[] logins);

    /**
     * 根据id数组查找用户
     * @param logins
     * @return
     */
    List<Tb_user> findByLoginnameIn(String[] logins);
    
    List<Tb_user> findByUseridIn(List<String> ids);
    /**
     * 根据id数组查找用户
     * @param ids
     * @return
     */
    List<Tb_user> findByUseridIn(String[] ids);
    /**
      * 按照id删除用户
     */
    Integer deleteAllByUseridIn(String[] ids);

    @Modifying
    @Query(value = "update Tb_user c set c.loginname = ?1,c.realname=?2,c.phone=?3,c.address=?4,c.sex=?5,c.usertype=?6,organusertype=?7,duty=?8 where c.userid=?9")
    int updateNameById(String loginname,String realname,String phone,String address,String sex,String usertype,String organusertype,String duty,String userid);

    @Modifying
    @Query(value = "update Tb_user c set c.nickname = ?1 where c.userid=?2")
    int updateNicknameById(String nickname,String userid);
    
    @Query(value = "select u.userid from Tb_user u where u.userid in (select userid from Tb_user_node where nodeid=?1)")
    List<String> getNodeUserId(String nodeId);

    @Query(value = "select u from Tb_user u where u.userid in (select userid from Tb_user_node_temp t where nodeid=?1)")
    List<Tb_user> getNodeUser(String nodeId);

    @Modifying
    @Query(value = "update Tb_user set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2")
    int modifyUserOrder(int start,int end);

    Tb_user findByRealname(String realname);

    @Query(value = "select max(sortsequence) from Tb_user where  organ.organid=?1")
    Integer findOrdersByOrganid(String organid);

    @Modifying
    @Query(value = "update Tb_user set  organ.organid = ?2 where userid=?1")
    int changeOrgan(String userId, String organId);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);

    @Query(value = "select u from Tb_user u where  organ.organid =(" +
            "select organ.organid from Tb_user where userid=?2) and userid in (select userid from Tb_user_role where roleid=(select roleid from Tb_role where rolename=?1))")
    List<Tb_user> getUserByRolenameAndUserid(String roleName,String userId);

    @Modifying
    @Transactional
    @Query(value = "update Tb_user set   organ.organid=(select organid from Tb_right_organ where organname=?1) " +
            "where loginname in (?2) ")
    int updateAdminOrgan(String organName, String[] loginName);

    @Modifying
    @Transactional
    @Query(value = "update Tb_user set organ=null where loginname in (?1)")
    int setAdminNull(String[] loginName);

    /**
     * 根据角色名字 获取角色中的用户
     * @param roleName
     * @return
     */
    @Query(value = "select u from Tb_user u where u.userid in (" +
            "select userid from Tb_user_role where roleid in (" +
            "SELECT roleid FROM Tb_role where rolename in (?1)))")
    List<Tb_user> getUserListByRolename(String[] roleName);

    @Query(value = "select realname from Tb_user where  organ.organid=?1")
    Set<String> getRealNameByOrganid(String organId);

	@Query(value = "select u from Tb_user u where  organ.organid=?1 and (realname like concat('%',?2,'%') or realname"
			+ " like concat('%',?3,'%') or realname like concat('%',?4,'%'))")
	List<Tb_user> getByOrganidAndRealname(String organId, String aqbm, String xitong, String aqsj);

    @Query(value = "select u from Tb_user u where u.userid in (select userid from Tb_user_organ where organid=?1)")
    List<Tb_user> getUserFromUserOrgan(String organId);

    @Query(value = "select u from Tb_user u where u.userid in (select userid from Tb_user_data_node where nodeid=?1)")
    List<Tb_user> getUserFromUserNode(String nodeId);
    @Query(value = "select u from Tb_user u where u.userid in (select userid from Tb_user_data_node_sx where nodeid=?1)")
    List<Tb_user> getUserFromSxUserNode(String nodeId);

    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_data_node_sx where nodeid=?1)",nativeQuery = true)
    List<Tb_user> getSxUserFromUserNode(String nodeId);

    @Query(value = "select new Tb_user(u.userid,u.realname) from Tb_user u")
    List<Tb_user> findAllUseridRealname();

    @Modifying
    @Query(value = "update Tb_user c set c.loginname = ?1,c.realname=?2,c.phone=?3,c.address=?4,c.sex=?5,c.usertype=?6 ,c.letternumber=?7 ,c.remark=?8 ,c.birthday=?10 ,c.ethnic=?11 ,c.infodate=?12 ,c.exdate=?13 where c.userid=?9")
    int updateOutNameById(String loginname,String realname,String phone,String address,String sex,String usertype,String letternumber,String remark,String userid,String birthday,String ethnic,String infodate,Date exdate);

    @Query(value = "select t from Tb_user t where t.userid in(" +
            "select loginname from Tb_task where state='待处理' and loginname in (?2) " +
            "and taskid not in (select taskid from Tb_flows where nodeid in (select nodeid from Tb_user_node where userid=?1)))")
    List<Tb_user> getApprovingUsers(String sourceUserId, String[] copyUserIds);

    @Query(value = "select u from Tb_user u where userid in (select userid from Szh_assembly_user where assemblyid = ?1 and assemblyflowid =?2)")
    List<Tb_user> getUserByAssemblyid(String assemblyid,String assemblyflowid);

    @Query(value = "select u from Tb_user u where userid in (select userid from Szh_assembly_user where assemblyid = ?1 and assemblyflowid =?2 and userid !=?3)")
    List<Tb_user> getUserByAssemblyidAndUserid(String assemblyid,String assemblyflowid,String userid);

    @Query(value = "select u from Tb_user u where userid in (select userid from Szh_assembly_user where assemblyid = ?1)")
    List<Tb_user> getUserByAssemblyid(String assemblyid);

    @Query(value = "SELECT organ.organid FROM Tb_user WHERE userid = ?1")
    String getOrganidByUserid(String userid);

    @Query(value = "select count(*) from Tb_user where  loginname=?1")
    Integer findCountByName(String loginname);

    @Query(value = "select u from Tb_user u where userid in(select userid from Tb_user_role where roleid = ?1)")
    Page<Tb_user> findUsersByRoleid(String roleid, Pageable pageable);

    @Query(value = "select u from Tb_user u where userid in(select userid from Tb_user_role where roleid = ?1)")
    List<Tb_user> findUsersByRoleid(String roleid);

    @Query(value = "select u.organ from Tb_user u where u.userid in (select userid from Tb_user_node where nodeid=?1)")
    List<Tb_right_organ> getOrganidByNodeid(String nodeId);

    @Query(value = "select u from Tb_user u where userid in (select userid from Tb_user_fillsort)")
    List<Tb_user> findUsersByFillSortid();
}