package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_guidance_workfunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
public interface GuidanceWorkfundsRepository extends JpaRepository<Tb_guidance_workfunds,String>,JpaSpecificationExecutor<Tb_guidance_workfunds> {


    @Query(value = "select distinct t.selectyear from Tb_guidance_workfunds t")
    List<String> getSelectYear();

    int deleteByIdIn(String[] ids);
}
