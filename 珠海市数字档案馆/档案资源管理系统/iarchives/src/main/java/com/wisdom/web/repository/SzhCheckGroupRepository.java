package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_check_group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2018/11/30.
 */
public interface SzhCheckGroupRepository extends JpaRepository<Szh_check_group,String>{

    @Query(value = "select t from Szh_check_group t where checkgroupid = ?1 ")
    Szh_check_group getByCheckgroupid(String checkgroupid);

    int deleteByCheckgroupidIn(String[] checkgroupids);

    List<Szh_check_group> findByType(String type);
}
