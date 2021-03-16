package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_organ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by tanly on 2018/04/21.
 */
public interface UserOrganRepository extends JpaRepository<Tb_user_organ, String> {

    /**
     * 删除机构权限中间表
     * @param userids 用户id数组
     * @return
     */
    Integer deleteAllByUseridIn(String[] userids);
    
    @Query(value = "select loginname from Tb_user where organid = ?1")
    List<String> findLoginnameByOrganid(String organid);
    
    @Query(value = "select realname from Tb_user where organid = ?1")
    List<String> findRealnameByOrganid(String organid);
    
    /**
     * 根据用户id获取用户机构权限
     * @param userid 用户id
     * @return
     */
    List<Tb_user_organ> findByUserid(String userid);

    Integer deleteByOrganid(String organId);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);
}