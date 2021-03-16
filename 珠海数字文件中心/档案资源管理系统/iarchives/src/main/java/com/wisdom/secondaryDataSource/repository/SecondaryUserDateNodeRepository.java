package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_data_node_sx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo on 2020/5/23 0023.
 */
public interface SecondaryUserDateNodeRepository extends JpaRepository<Tb_user_data_node_sx, String> {
    /**
     * 根据用户id获取用户数据权限
     * @param userid 用户id
     * @return
     */
    @Query(value = "select rtrim(t.nodeid) from Tb_user_data_node t where t.userid = ?1",nativeQuery = true)
    List<String> findByUserid(String userid);
}
