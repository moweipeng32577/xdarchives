package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_project_manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/7/21.
 */
public interface ProjectManageRepository extends JpaRepository<Tb_project_manage,String>,JpaSpecificationExecutor<Tb_project_manage> {
    int deleteByIdIn(String[] ids);

    List<Tb_project_manage> findByIdIn(String[] ids);


    @Query(value = "select b from Tb_project_manage b where b.id in(select borrowmsgid from Tb_task  where taskid = ?1)")
    Tb_project_manage findByTaskid(String taskid);
}
