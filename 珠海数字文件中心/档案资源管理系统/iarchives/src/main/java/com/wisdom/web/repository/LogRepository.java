package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xd on 2017/10/10.
 */
public interface LogRepository  extends JpaRepository<Tb_log,Integer> {

    @Query(value = "select t from Tb_log t where leaf=1")
    List<Tb_log> findAllByLeaf();
}
