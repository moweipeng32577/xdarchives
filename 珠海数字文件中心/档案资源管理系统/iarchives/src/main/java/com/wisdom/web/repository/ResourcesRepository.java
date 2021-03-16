package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface ResourcesRepository extends JpaRepository<Tb_Resources, String> {

    @Query(value = "select t from Tb_Resources t where t.functionid in (?1)")
    List<Tb_Resources> findByresources(String[] funids);

}
