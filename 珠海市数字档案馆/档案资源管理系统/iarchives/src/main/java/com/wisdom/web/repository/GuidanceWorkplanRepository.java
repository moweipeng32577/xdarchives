package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_guidance_workplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
public interface GuidanceWorkplanRepository extends JpaRepository<Tb_guidance_workplan,String>,JpaSpecificationExecutor<Tb_guidance_workplan> {


    @Query(value = "select distinct t.selectyear from Tb_guidance_workplan t")
    List<String> getSelectYear();

    int deleteByIdIn(String[] ids);
}
