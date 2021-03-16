package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_guidance_fileuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
public interface GuidanceFileUserRepository extends JpaRepository<Tb_guidance_fileuser,String>,JpaSpecificationExecutor<Tb_guidance_fileuser> {


    @Query(value = "select distinct t.selectyear from Tb_guidance_fileuser t")
    List<String> getSelectYear();

    int deleteByIdIn(String[] ids);
}
