package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_inform_user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by Rong on 2017/10/31.
 */
public interface InFormUserRepository extends JpaRepository<Tb_inform_user,String> {

    List<Tb_inform_user> findByUserroleidIn(String[] userroleids);
    
    List<Tb_inform_user> findByUserroleidInAndStateIsNull(String[] userroleids);

    List<Tb_inform_user> findByUserroleid(String userroleid);

    List<Tb_inform_user> findByInformidIn(String[] informids);

    Integer deleteByInformidIn(String[] ids);

    Integer deleteByInformidAndUserroleidIn(String informid,String[] ids);
    
    @Modifying
    @Transactional
    @Query(value = "delete from Tb_inform_user where informid = ?1 and userroleid = ?2")
    Integer deleteByInfomidAndUserroleid(String informid,String id);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_inform_user set state = '清除' where informid = ?1 and userroleid = ?2")
    Integer updateStateByInfomidAndUserroleid(String informid, String id);
}