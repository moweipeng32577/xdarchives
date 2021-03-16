package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_template_desc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zengdw on 2019/10/29 0001.
 */
public interface TemplateDescRepository extends JpaRepository<Tb_template_desc, Integer>,
        JpaSpecificationExecutor<Tb_template_desc> {
    @Query(value = "select descs from Tb_template_desc where fieldcode = ?1")
    String findDescsByFieldcode(String fieldcode);

    List<Tb_template_desc> findByFieldcode(String fieldcode);
}
