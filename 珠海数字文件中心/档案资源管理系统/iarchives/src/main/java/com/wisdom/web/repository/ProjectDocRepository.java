package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_project_doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/7/21.
 */
public interface ProjectDocRepository extends JpaRepository<Tb_project_doc,String>,JpaSpecificationExecutor<Tb_project_doc> {

    List<Tb_project_doc> findByIdIn(String[] ids);

    @Query(value = "select distinct d.projectid from Tb_project_doc d where d.fpspmanid=?1 and d.spnodeid=?2")
    List<String> findByFpspmanidAndSpnodeid(String fpspmanid,String spnodeid);
}
