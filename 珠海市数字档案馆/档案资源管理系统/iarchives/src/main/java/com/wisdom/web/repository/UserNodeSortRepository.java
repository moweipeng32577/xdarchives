package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_node_sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2020/5/12.
 */
public interface UserNodeSortRepository extends JpaRepository<Tb_user_node_sort, String> {


    int deleteByUseridAndAndNodeid(String userid,String nodeid);

    List<Tb_user_node_sort> findByNodeidAndUseridOrderBySortsequence(String nodeid,String userid);
}
