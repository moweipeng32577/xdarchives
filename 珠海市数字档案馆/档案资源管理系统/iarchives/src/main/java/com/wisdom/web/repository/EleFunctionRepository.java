package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_ele_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface EleFunctionRepository extends JpaRepository<Tb_ele_function, String> {

    @Query(value = "select t from Tb_ele_function t where t.userid = ?1")
    List<Tb_ele_function> findByUserid(String userid);

    @Query(value = "select t from Tb_ele_function t where t.platform = ?1 and t.userid = ?2")
    Tb_ele_function findByPlatformAndUserid(String sysType, String userid);

    Integer deleteAllByUseridIn(String[] userids);
}
