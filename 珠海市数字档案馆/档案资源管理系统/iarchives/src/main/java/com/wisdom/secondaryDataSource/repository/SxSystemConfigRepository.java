package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_system_config_sx;
import com.wisdom.web.entity.Tb_system_config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Rong on 2017/11/3.
 */
public interface SxSystemConfigRepository extends JpaRepository<Tb_system_config_sx, String>,
        JpaSpecificationExecutor<Tb_system_config_sx> {

    @Query(value = "select t from Tb_system_config_sx t where parentconfigid in (select configid from Tb_system_config_sx where configvalue  = ?1)")
    List<Tb_system_config_sx> findByParentValue(String parentValue);

    @Query(value = "select t from Tb_system_config_sx t where parentconfigid in (select configid from Tb_system_config_sx where configcode  = ?1)")
    List<Tb_system_config_sx> findByParentCode(String parentValue);

    @Query(value = "select configid from Tb_system_config_sx where configcode = ?1")
    String findByConfigcode(String configcode);

    @Query(value = "select configcode from Tb_system_config_sx where parentconfigid = ?1")
    List<String> findConfigcodeByParentconfigidInfo(String parentconfigid);

    List<Tb_system_config_sx> findConfigcodeByParentconfigid(String parentconfigid);

    List<Tb_system_config_sx> findByConfigidAndParentconfigidIsNotNull(String parentconfigid);

    List<Tb_system_config_sx> findByParentconfigidOrderBySortsequence(String parentconfigid);

    List<Tb_system_config_sx> findByParentconfigidIsNullOrderBySortsequence();

    Tb_system_config_sx findByConfigid(String configid);

    Tb_system_config_sx findByConfigcodeAndParentconfigidIsNull(String code);

    Tb_system_config_sx findByConfigvalueAndParentconfigidIsNull(String value);

    Tb_system_config_sx findByParentconfigidAndConfigcode(String parentconfigid, String code);

    Tb_system_config_sx findByParentconfigidAndConfigvalue(String parentconfigid, String value);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from Tb_system_config_sx t where configid in ?1")
    Integer deleteByConfigidIn(String[] configids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByConfigid(String configid);

    List<Tb_system_config_sx> findByParentconfigid(String parentconfigid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByParentconfigid(String parentconfigid);

    @Query(value = "select configvalue from Tb_system_config_sx t where configcode = ?1")
    List<String> findConfigvalueByConfigcode(String configcode);

    @Query(value = "select configvalue from Tb_system_config_sx where configcode = ?1 and parentconfigid = (select configid from Tb_system_config_sx where configvalue = ?2)")
    String findConfigvalueByConfigcodeAndParentconfigvalue(String configcode, String parentconfigvalue);

    @Query(value = "select configcode from Tb_system_config_sx where configvalue = ?1 and parentconfigid = (select configid from Tb_system_config_sx where configvalue = ?2)")
    String findConfigcodeByConfigvalueAndParentconfigvalue(String configvalue, String parentconfigvalue);

    @Query(value = "select max(sortsequence) from Tb_system_config_sx where parentconfigid = ?1")
    Integer findMaxSequenceByParentid(String configvalue);

    @Query(value = "select max(sortsequence) from Tb_system_config_sx where parentconfigid=?1 or parentconfigid is null")
    Integer findMaxSequenceByParentidOrNull(String pcid);

    @Query(value = "update tb_system_config set sortsequence = sortsequence + 1 where parentconfigid = ?1 and sortsequence >= ?2",nativeQuery = true)
    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer orderConfig(String parentconfigid, int order);

    Tb_system_config_sx findAllByConfigvalue(String value);

    List<Tb_system_config_sx> findByConfigidIn(String[] ids);
}