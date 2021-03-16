package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_guidance_organ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
public interface GuidanceOrganRepository extends JpaRepository<Tb_guidance_organ,String>,JpaSpecificationExecutor<Tb_guidance_organ> {


    @Query(value = "select distinct t.selectyear from Tb_guidance_organ t")
    List<String> getSelectYear();

    int deleteByIdIn(String[] ids);
}
