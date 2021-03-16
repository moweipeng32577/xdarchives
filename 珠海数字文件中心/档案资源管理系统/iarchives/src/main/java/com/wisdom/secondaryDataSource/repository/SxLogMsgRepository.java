package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_log_msg_sx;
import com.wisdom.web.entity.Tb_log_msg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/3/30 0030.
 */
public interface SxLogMsgRepository extends JpaRepository<Tb_log_msg_sx, String>,
        JpaSpecificationExecutor<Tb_log_msg_sx> {

    Integer deleteByLmidIn(String[] ids);

    List<Tb_log_msg_sx> findByLmidInOrderByStartTimeDesc(String[] ids);
    
    @Query(value = "select operate_user from Tb_log_msg_sx where desci = '用户不存在！'")
    List<String> findByDesci();
}