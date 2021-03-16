package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_right_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface FunctionRepository extends JpaRepository<Tb_right_function, String> {

    /**
     * 查询桌面功能
     * @param roleids
     * @param userid
     * @return
     */
    @Query(value = "select t from Tb_right_function t where (fnid in (select fnid from Tb_role_function where roleid in (?1)) or fnid in (select fnid from Tb_user_function where userid=?3)) and functiontype=?2 and status='1' order by sortsequence")
    List<Tb_right_function> findByfunctionsdesktop(String[] roleids,String fucType,String userid);

    /**
     * 查询全部功能
     * @param roleids
     * @param userid
     * @return
     */
    @Query(value = "select t from Tb_right_function t where fnid in (select fnid from Tb_role_function where roleid in (?1)) or fnid in (select fnid from Tb_user_function where userid=?2) and status='1' order by sortsequence")
    List<Tb_right_function> findByfunctions(String[] roleids, String userid);

    @Query(value = "select * from tb_right_function_sx t where fnid in (select fnid from tb_role_function_sx where roleid in (?1)) or fnid in (select fnid from tb_user_function_sx where userid=?2) and status='1' order by sortsequence",nativeQuery = true)
    List<Tb_right_function> findSxByfunctions(String[] roleids, String userid);

    @Query(value = "select t from Tb_right_function t where isp =?1 and status =?2 order by sortsequence")
    List<Tb_right_function> findByIspAndStatusOrderBySortsequence(String isp,String status);

    @Query(value = "select t from Tb_right_function t where status =?1 order by sortsequence")
    List<Tb_right_function> findByStatusOrderBySortsequence(String status);

    @Query(value = "select * from tb_right_function_sx t where isp =?1 and status =?2 order by sortsequence",nativeQuery = true)
    List<Tb_right_function> findSxByIspAndStatusOrderBySortsequence(String isp,String status);

    @Query(value = "select t from Tb_right_function t where isp =?1 and status =?2 and functionname !='公告管理'" +
            " and functionname !='焦点图管理' and functionname !='个人意见' and functionname !='全文检索' " +
            "and functionname !='查档管理' and functionname != '查档登记' and functionname !='电子文件利用统计' order by sortsequence")
    List<Tb_right_function> findByIspAndStatusOrderBySortsequenceFalse(String isp,String status);

    @Query(value = "select * from tb_right_function_sx t where isp =?1 and status =?2 and functionname !='公告管理'" +
            " and functionname !='焦点图管理' and functionname !='个人意见' and functionname !='全文检索' " +
            "and functionname !='查档管理' and functionname != '查档登记' and functionname !='电子文件利用统计' order by sortsequence",nativeQuery = true)
    List<Tb_right_function> findSxByIspAndStatusOrderBySortsequenceFalse(String isp,String status);

    Tb_right_function findByFnid(String fnid);
    @Query(value = "select * from tb_right_function_sx t where fnid =?1",nativeQuery = true)
    Tb_right_function findSxByFnid(String fnid);

    @Query(value = "select t from Tb_right_function t where tkey in(select DISTINCT isp from Tb_right_function where fnid in (?1)) and status='1' order by sortsequence")
    List<Tb_right_function> findParentFn(String[] fnids);

    List<Tb_right_function> findByFnidIn(String[] fnids);
    @Query(value = "select * from tb_right_function_sx t where fnid in (?1)",nativeQuery = true)
    List<Tb_right_function> findSxByFnidIn(String[] fnids);

    @Query(value = "select t from Tb_right_function t where t.functionname =?1 and t.functiontype ='desktop'")
    List<Tb_right_function> findByFunctionname(String name);

    /**
     * 安全平台获取菜单数据
     * @param type 类型
     * @param isp 父级
     * @return
     */
    List<Tb_right_function> findByFunctiontypeAndIsp(String type,String isp);

    List<Tb_right_function> findByFunctiontypeAndIspInAndStatusOrderBySortsequence(String type,String[] isp,String status);

    @Query(value = "select t from Tb_right_function t where  functiontype=?1  and status=?4 and ( isp in ?2  or  tkey in ?3 ) order by sortsequence asc")
    List<Tb_right_function> findByFunctiontypeAndIspInOrTkeyInAndStatusOrderBySortsequence(String type,String[] isp,String[] tkey,String status);

    @Query(value = "select functionname from Tb_right_function where functiontype='desktop' and status='1'" +
            "and (fnid in (select fnid from " +
            "Tb_user_function where userid = ?1) or fnid in (select fnid " +
            "from Tb_role_function where roleid in (SELECT roleid from Tb_user_role where userid = ?1))) order by sortsequence")
    List<String> findUserRoleFunctionForUserId(String userid);

    List<Tb_right_function> findByFunctiontypeInAndStatus(String[] fntypes,String status);

    List<Tb_right_function> findByFunctiontypeAndIspInAndTkeyInOrderBySortsequence(String type,String[]isp,String[]tkey);
    /**
     * 查询无利用平台桌面功能
     * @param roleids
     * @param userid
     * @return
     */
    @Query(value = "select t from Tb_right_function t where (fnid in (select fnid from Tb_role_function where roleid in (?1)) or fnid in (select fnid from Tb_user_function where UserID=?3)) " +
            "and functiontype=?2 and status='1' and functionname !='公告管理' and functionname !='焦点图管理' and functionname !='个人意见' and functionname !='全文检索' and functionname !='电子文件利用统计'" +
            "and functionname !='查档管理' and functionname != '查档登记' order by sortsequence")
    List<Tb_right_function> findByfunctionsdesktopfalse(String[] roleids,String fucType,String userid);

    @Query(value = "select functionname from Tb_right_function where functiontype='desktop' and status='1' and functionname !='公告管理'" +
            " and functionname !='焦点图管理' and functionname !='个人意见' and functionname !='全文检索' " +
            "and functionname !='查档管理' and functionname != '查档登记' and functionname !='电子文件利用统计' and (fnid in (select fnid from " +
            "Tb_user_function where userid = ?1) or fnid in (select fnid " +
            "from Tb_role_function where roleid in (SELECT roleid from Tb_user_role where userid = ?1))) order by sortsequence")
    List<String> findUserRoleFunctionForUserIdfalse(String userid);

    @Modifying
    @Query(value = "update Tb_right_function set status=?1 where functionname=?2")
    int updataStatusByFunctionname(String status,String functionname);

    @Modifying
    @Query(value = "update tb_right_function set status=?1 where functionname=?2 or isp = (select t.tkey from (select tkey from tb_right_function where functionname=?2) t)", nativeQuery = true)
    int updateStatusByFunctionnameWithChilds(String status, String funtionname);

    @Modifying
    @Query(value = "update Tb_right_function set status=?1 where functionname in ?2")
    int updateStatusByFunctionnames(String status, String[] functionnames);

    @Query(value = "select fnid from Tb_right_function where tkey in ?1 and isp='1'")
    List<String> findFnidsByTkeyIn(String[] tkeys);

    @Query(value = "select distinct (t.functioncode) from Tb_right_function t where t.isp = ?1")
    List<String> findByIsp(String isp);

    @Query(value = "select distinct t.isp from Tb_right_function t where t.functioncode in ?1")
    List<String> findByFunctioncodeIn(String[] functionCode);
}
