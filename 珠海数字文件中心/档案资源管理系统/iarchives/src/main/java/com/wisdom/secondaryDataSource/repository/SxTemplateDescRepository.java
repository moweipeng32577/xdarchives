package com.wisdom.secondaryDataSource.repository;/**
 * Created by yl on 2021-01-20.
 */

import com.wisdom.secondaryDataSource.entity.Tb_template_desc_sx;
import com.wisdom.web.entity.Tb_template_desc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wujy
 */
public interface SxTemplateDescRepository extends JpaRepository<Tb_template_desc_sx, Integer>,
        JpaSpecificationExecutor<Tb_template_desc_sx> {
    @Query(value = "select descs from Tb_template_desc_sx where fieldcode = ?1")
    String findDescsByFieldcode(String fieldcode);

    List<Tb_template_desc_sx> findByFieldcode(String fieldcode);
}
