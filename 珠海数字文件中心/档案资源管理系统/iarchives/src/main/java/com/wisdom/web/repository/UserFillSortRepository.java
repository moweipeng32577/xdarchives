package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_fillsort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/7/27.
 */
public interface UserFillSortRepository extends JpaRepository<Tb_user_fillsort, String> {

    List<Tb_user_fillsort> findByUserid(String userid);

    @Query(value = "select t.userid from Tb_user_fillsort t")
    List<String> getUserids();
}
