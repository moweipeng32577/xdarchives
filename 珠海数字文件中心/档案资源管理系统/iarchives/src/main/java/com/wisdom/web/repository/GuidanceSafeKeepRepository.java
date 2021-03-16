package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_guidance_safekeep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
public interface GuidanceSafeKeepRepository extends JpaRepository<Tb_guidance_safekeep,String> {

    Tb_guidance_safekeep findByOrganidAndSelectyear(String organid, String selectyear);

    @Query(value = "select distinct t.selectyear from Tb_guidance_safekeep t")
    List<String> getSelectYear();

    int deleteByIdIn(String[] ids);
}
