package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_organ_sx;
import com.wisdom.web.entity.Tb_user_organ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by tanly on 2018/04/21.
 */
public interface SxUserOrganRepository extends JpaRepository<Tb_user_organ_sx, String> {

    /**
     * 删除机构权限中间表
     * @param userids 用户id数组
     * @return
     */
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_organ  where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] userids);
    
    @Query(value = "select loginname from tb_user where organid = ?1",nativeQuery = true)
    List<String> findLoginnameByOrganid(String organid);
    
    @Query(value = "select realname from tb_user where organid = ?1",nativeQuery = true)
    List<String> findRealnameByOrganid(String organid);
    
    /**
     * 根据用户id获取用户机构权限
     * @param userid 用户id
     * @return
     */
    List<Tb_user_organ_sx> findByUserid(String userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByOrganid(String organId);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);
}