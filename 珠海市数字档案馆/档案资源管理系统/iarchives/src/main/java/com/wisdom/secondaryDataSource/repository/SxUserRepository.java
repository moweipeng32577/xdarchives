package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_sx;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxUserRepository extends JpaRepository<Tb_user_sx, Integer> ,
        JpaSpecificationExecutor<Tb_user_sx> {

    Tb_user_sx findByUserid(String userid);

    @Query(value = "select t from Tb_user_sx t where userid in (?1)")
    Page<Tb_user_sx> findByUserid(List<String> organids, Pageable pageable);

    @Query(value = "select * from tb_user t where userid in (?1) order by sortsequence",nativeQuery = true)
    List<Tb_user_sx> findByUserid(String[] id);

    @Query(value = "select loginname from tb_user where realname in (?1)",nativeQuery = true)
    List<String> findLoginnameByRealnameIn(String[] realname);

    @Query(value = "select loginname from tb_user where realname not in (?1)",nativeQuery = true)
    List<String> findLoginnameByRealnameNotIn(String[] realname);

    @Query(value = "select organid from tb_user where userid=?1",nativeQuery = true)
    String findOrganidByUserid(String userid);

    @Query(value = "select userid from tb_user where organid=?1",nativeQuery = true)
    List<String> findUseridByOrganid(String organid);

    @Query(value = "select realname from tb_user where  organid=?1",nativeQuery = true)
    List<String> findUseridByOrganidOrderByUserid(String organid);

    @Query(value = "select u from Tb_user_sx u where outuserstate is not null and outuserstate != ''")
    Page<Tb_user_sx> findByOutuserstateIsNotNull(Pageable pageable);

    Tb_user_sx findByLoginname(String loginname);

    Tb_user_sx findByLoginnameAndStatusNot(String loginname, long status);

    @Query(value = "select * from tb_user u where organid = ?1",nativeQuery = true)
    List<Tb_user_sx> findByOrganid(String organid);

    @Query(value = "select u.userid from tb_user u where userid in (select n.userid from tb_user_node n where n.nodeid in ?1)",nativeQuery = true)
    List<String> findNodeUserIn(String[] nodeids);

    @Query(value = "select * from tb_user u where userid in (select n.userid from tb_user_node n where n.nodeid in ?1)",nativeQuery = true)
    List<Tb_user_sx> getNodeUserIn(String[] nodeids);

    /**
     * 按照账号删除用户
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
     Integer deleteByLoginnameIn(String[] logins);

    /**
     * 根据id数组查找用户
     * @param logins
     * @return
     */
    List<Tb_user_sx> findByLoginnameIn(String[] logins);

    List<Tb_user_sx> findByUseridIn(List<String> ids);
    /**
     * 根据id数组查找用户
     * @param ids
     * @return
     */
    List<Tb_user_sx> findByUseridIn(String[] ids);
    /**
      * 按照id删除用户
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] ids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user set loginpassword = ?1 where userid=?2",nativeQuery = true)
    int updateLoginPasswordByUserId(String loginpassword, String userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user  set loginname = ?1,realname=?2,phone=?3,address=?4,sex=?5,usertype=?6 where userid=?7",nativeQuery = true)
    int updateNameById(String loginname, String realname, String phone, String address, String sex, String usertype, String userid);

    @Query(value = "select u.userid from tb_user u where u.userid in (select userid from Tb_user_node where nodeid=?1)",nativeQuery = true)
    List<String> getNodeUserId(String nodeId);

    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_node_temp t where nodeid=?1)",nativeQuery = true)
    List<Tb_user_sx> getNodeUser(String nodeId);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2",nativeQuery = true)
    int modifyUserOrder(int start, int end);

    Tb_user_sx findByRealname(String realname);

    @Query(value = "select max(sortsequence) from tb_user where  organid=?1",nativeQuery = true)
    Integer findOrdersByOrganid(String organid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user set  organid = ?2 where userid=?1",nativeQuery = true)
    int changeOrgan(String userId, String organId);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);

    @Query(value = "select * from tb_user u where  organid =(" +
            "select organid from tb_user where userid=?2) and userid in (select userid from tb_user_role where roleid=(select roleid from tb_role where rolename=?1))",nativeQuery = true)
    List<Tb_user_sx> getUserByRolenameAndUserid(String roleName, String userId);

    @Modifying
    @Transactional(value = "transactionManagerSecondary")
    @Query(value = "update tb_user set   organid=(select organid from tb_right_organ where organname=?1) " +
            "where loginname in (?2) ",nativeQuery = true)
    int updateAdminOrgan(String organName, String[] loginName);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user set organ=null where loginname in (?1)",nativeQuery = true)
    int setAdminNull(String[] loginName);

    /**
     * 根据角色名字 获取角色中的用户
     * @param roleName
     * @return
     */
    @Query(value = "select * from tb_user u where u.userid in (" +
            "select userid from tb_user_role where roleid in (" +
            "SELECT roleid FROM tb_role where rolename in (?1)))",nativeQuery = true)
    List<Tb_user_sx> getUserListByRolename(String[] roleName);

    @Query(value = "select realname from tb_user where  organid=?1",nativeQuery = true)
    Set<String> getRealNameByOrganid(String organId);

	@Query(value = "select * from tb_user u where  organid=?1 and (realname like concat('%',?2,'%') or realname"
			+ " like concat('%',?3,'%') or realname like concat('%',?4,'%'))",nativeQuery = true)
	List<Tb_user_sx> getByOrganidAndRealname(String organId, String aqbm, String xitong, String aqsj);

    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_organ where organid=?1)",nativeQuery = true)
    List<Tb_user_sx> getUserFromUserOrgan(String organId);

    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_data_node where nodeid=?1)",nativeQuery = true)
    List<Tb_user_sx> getUserFromUserNode(String nodeId);
    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_data_node where nodeid=?1)",nativeQuery = true)
    List<Tb_user_sx> getUserFromSxUserNode(String nodeId);

    @Query(value = "select * from tb_user u where u.userid in (select userid from tb_user_data_node where nodeid=?1)",nativeQuery = true)
    List<Tb_user_sx> getSxUserFromUserNode(String nodeId);

    @Query(value = "select new tb_user(u.userid,u.realname) from tb_user u",nativeQuery = true)
    List<Tb_user_sx> findAllUseridRealname();

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_user c set c.loginname = ?1,c.realname=?2,c.phone=?3,c.address=?4,c.sex=?5,c.usertype=?6 ,c.letternumber=?7 ,c.remark=?8 ,c.birthday=?10 ,c.ethnic=?11 ,c.infodate=?12 ,c.exdate=?13 where c.userid=?9",nativeQuery = true)
    int updateOutNameById(String loginname, String realname, String phone, String address, String sex, String usertype, String letternumber, String remark, String userid, String birthday, String ethnic, String infodate, Date exdate);

    @Query(value = "select * from tb_user t where t.userid in(" +
            "select loginname from tb_task where state='待处理' and loginname in (?2) " +
            "and taskid not in (select taskid from tb_flows where nodeid in (select nodeid from tb_user_node where userid=?1)))",nativeQuery = true)
    List<Tb_user_sx> getApprovingUsers(String sourceUserId, String[] copyUserIds);

    @Query(value = "select * from tb_user u where userid in (select userid from szh_assembly_user where assemblyid = ?1 and assemblyflowid =?2)",nativeQuery = true)
    List<Tb_user_sx> getUserByAssemblyid(String assemblyid, String assemblyflowid);

    @Query(value = "select * from tb_user u where userid in (select userid from szh_assembly_user where assemblyid = ?1 and assemblyflowid =?2 and userid !=?3)",nativeQuery = true)
    List<Tb_user_sx> getUserByAssemblyidAndUserid(String assemblyid, String assemblyflowid, String userid);

    @Query(value = "select * from tb_user u where userid in (select userid from szh_assembly_user where assemblyid = ?1)",nativeQuery = true)
    List<Tb_user_sx> getUserByAssemblyid(String assemblyid);

    @Query(value = "SELECT organid FROM tb_user WHERE userid = ?1",nativeQuery = true)
    String getOrganidByUserid(String userid);

    @Query(value = "select count(*) from tb_user where  loginname=?1",nativeQuery = true)
    Integer findCountByName(String loginname);
}