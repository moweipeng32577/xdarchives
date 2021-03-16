package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_function_sx;
import com.wisdom.web.entity.Tb_transdoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxUserFunctionRepository extends JpaRepository<Tb_user_function_sx, String>,JpaSpecificationExecutor<Tb_user_function_sx> {

    /**
     * 根据用户id删除数据
     * @param userids
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_function  where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] userids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_function t where t.fnid in (select fnid from tb_right_function where " +
            "functionName = ?1)",nativeQuery = true)
    Integer deleteByFnid(String functionName);

    @Query(value = "select t.fnid from tb_user_function t where t.userid = ?1",nativeQuery = true)
    List<String> findByUserid(String userid);
}
