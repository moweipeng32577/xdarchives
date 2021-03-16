package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface UserFunctionRepository extends JpaRepository<Tb_user_function, String> {

    /**
     * 根据用户id删除数据
     * @param userids
     * @return
     */
    Integer deleteAllByUseridIn(String[] userids);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);

    @Modifying
    @Transactional
    @Query(value = "delete from Tb_user_function t where t.fnid in (select fnid from Tb_right_function where " +
            "functionName = ?1)")
    Integer deleteByFnid(String functionName);

    @Query(value = "select t.fnid from Tb_user_function t where t.userid = ?1")
    List<String> findByUserid(String userid);

    @Query(value = "select t.userid from Tb_user_function t where t.fnid in (select fnid from Tb_right_function where " +
            "functionname = ?1)")
    List<String> findUseridsByFunctionname(String functionname);

    @Query(value = "select userid from Tb_user_function where fnid=?1")
    List<String> findByFnid(String fnid);
}
