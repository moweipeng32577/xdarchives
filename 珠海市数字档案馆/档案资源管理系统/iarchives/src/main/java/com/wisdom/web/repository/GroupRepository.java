package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface GroupRepository extends JpaRepository<Tb_group, Integer> {

    @Query(value = "select t from Tb_group t  where groupid in (select groupid from Tb_user_group where userid=?1)")
    List<Tb_group> findBygroups(String userid);

}
