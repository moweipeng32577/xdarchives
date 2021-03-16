package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_metadata_temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by SunK on 2020/4/22 0022.
 */
public interface MetadataTemplateRepository extends JpaRepository<Tb_metadata_temp, String>,
        JpaSpecificationExecutor<Tb_metadata_temp> {

    @Query(value = "select templateid from Tb_metadata_temp where templateid in (select metadataid from Tb_data_template where nodeid = ?1) and fieldcode = ?2")
    String findTemplateidByNodeidAndFieldcode(String nodeid,String fieldcode);


    List<Tb_metadata_temp> findByClassify(String classify);
}
