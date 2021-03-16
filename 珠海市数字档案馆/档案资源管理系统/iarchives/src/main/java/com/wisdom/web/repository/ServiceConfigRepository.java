package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_accredit;
import com.wisdom.web.entity.Tb_service_config;
import com.wisdom.web.entity.Tb_system_config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by SunK on 2020/5/12 0012.
 */
public interface ServiceConfigRepository extends
        JpaRepository<Tb_service_config,String>, JpaSpecificationExecutor<Tb_service_config> {

    List<Tb_service_config> findByParentidIsNullOrderBySortsequence();

    List<Tb_service_config> findByParentidOrderBySortsequence(String parentid);

    Tb_service_config findByCid(String cid);

    Tb_service_config findByParentidAndOperation(String parentconfigid, String value);

    Tb_service_config findByOperationAndParentidIsNull(String parentconfigid);

    Tb_service_config findByParentidAndParentidIsNull(String parentid);

    @Query(value = "select max(sortsequence) from Tb_service_config where parentid=?1 or parentid is null")
    Integer findMaxSequenceByParentidOrNull(String pcid);

    @Query(value = "select max(sortsequence) from Tb_service_config where parentid = ?1")
    Integer findMaxSequenceByParentid(String configvalue);

    Integer countByParentidAndOperation(String patentid,String operation);

    Integer countByOperationAndParentidIsNull(String operation);

    Integer deleteByCidIn(String[] configids);

    Integer deleteByCid(String configids);
}
