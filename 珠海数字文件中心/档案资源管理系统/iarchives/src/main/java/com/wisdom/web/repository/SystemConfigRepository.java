package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_system_config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Rong on 2017/11/3.
 */
public interface SystemConfigRepository extends JpaRepository<Tb_system_config, String>,
        JpaSpecificationExecutor<Tb_system_config> {

    @Query(value = "select t from Tb_system_config t where parentconfigid in (select configid from Tb_system_config " +
            "where configvalue  = ?1) order by sortsequence asc")
    List<Tb_system_config> findByParentValue(String parentValue);

    @Query(value = "select t from Tb_system_config t where parentconfigid in (select configid from Tb_system_config where configcode  = ?1)")
    List<Tb_system_config> findByParentCode(String parentValue);

    @Query(value = "select configid from Tb_system_config where configcode = ?1")
    String findByConfigcode(String configcode);
    
    @Query(value = "select configcode from Tb_system_config where parentconfigid = ?1")
    List<String> findConfigcodeByParentconfigidInfo(String parentconfigid);
    
    List<Tb_system_config> findConfigcodeByParentconfigid(String parentconfigid);

    List<Tb_system_config> findByConfigidAndParentconfigidIsNotNull(String parentconfigid);

    List<Tb_system_config> findByParentconfigidOrderBySortsequence(String parentconfigid);

    List<Tb_system_config> findByParentconfigidIsNullOrderBySortsequence();

    Tb_system_config findByConfigid(String configid);

    Tb_system_config findByConfigcodeAndParentconfigidIsNull(String code);

    Tb_system_config findByConfigvalueAndParentconfigidIsNull(String value);

    Tb_system_config findByParentconfigidAndConfigcode(String parentconfigid,String code);

    Tb_system_config findByParentconfigidAndConfigvalue(String parentconfigid,String value);

    Integer deleteByConfigidIn(String[] configids);

    Integer deleteByConfigid(String configid);

    List<Tb_system_config> findByParentconfigid(String parentconfigid);

    Integer deleteByParentconfigid(String parentconfigid);

    @Query(value = "select configvalue from Tb_system_config t where configcode = ?1")
    List<String> findConfigvalueByConfigcode(String configcode);
    
    @Query(value = "select configvalue from Tb_system_config where configcode = ?1 and parentconfigid = (select configid from Tb_system_config where configvalue = ?2)")
    String findConfigvalueByConfigcodeAndParentconfigvalue(String configcode,String parentconfigvalue);

    @Query(value = "select configcode from Tb_system_config where configvalue = ?1 and parentconfigid = (select configid from Tb_system_config where configvalue = ?2)")
    String findConfigcodeByConfigvalueAndParentconfigvalue(String configvalue,String parentconfigvalue);

    @Query(value = "select max(sortsequence) from Tb_system_config where parentconfigid = ?1")
    Integer findMaxSequenceByParentid(String configvalue);

    @Query(value = "select max(sortsequence) from Tb_system_config where parentconfigid=?1 or parentconfigid is null")
    Integer findMaxSequenceByParentidOrNull(String pcid);

    @Query(value = "update tb_system_config set sortsequence = sortsequence + 1 where parentconfigid = ?1 and sortsequence >= ?2",nativeQuery = true)
    @Modifying
    Integer orderConfig(String parentconfigid,int order);

    Tb_system_config findAllByConfigvalue(String value);

    List<Tb_system_config> findByConfigidIn(String[] ids);
}