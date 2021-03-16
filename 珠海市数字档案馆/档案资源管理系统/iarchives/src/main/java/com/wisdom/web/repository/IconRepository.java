package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface IconRepository extends JpaRepository<Tb_Icon, String> {

    //根据用户id查询记录
    List<Tb_Icon> findByUseridAndSystypeOrderBySortsequence(String userid,String sysType);

    //根据用户id数组查询记录
    List<Tb_Icon> findByUseridInOrderBySortsequence(String[] userids);

    //根据用户id删除记录
    Integer deleteByUseridAndSystype(String userid,String systype);

    //根据orders删除记录
    Integer deleteBySortsequenceAndUseridAndSystype(Integer sortsequence,String userid,String sysType);

    //根据code删除记录
    Integer deleteByCodeAndUseridAndSystype(String code,String userid,String sysType);
}
