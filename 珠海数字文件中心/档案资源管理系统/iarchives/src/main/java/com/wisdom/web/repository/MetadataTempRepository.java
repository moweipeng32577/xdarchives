package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_metadata_temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MetadataTempRepository extends JpaRepository<Tb_metadata_temp,String>, JpaSpecificationExecutor<Tb_metadata_temp> {

    @Query(value = "select t from Tb_metadata_temp t where t.classify=?1")
    List<Tb_metadata_temp> findAllByClassify(String classify);

    List<Tb_metadata_temp> findByClassifyOrderByFsequence(String classify);

    Tb_metadata_temp findByTemplateid(String templateid);

    Integer deleteByClassify(String classify);

}
