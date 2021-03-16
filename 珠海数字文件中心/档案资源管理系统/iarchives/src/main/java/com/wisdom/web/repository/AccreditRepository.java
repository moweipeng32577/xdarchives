package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_accredit;
import com.wisdom.web.entity.Tb_system_config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by SunK on 2020/5/7 0007.
 */
public interface AccreditRepository extends JpaRepository<Tb_accredit, String>, JpaSpecificationExecutor<Tb_accredit> {


    List<Tb_accredit> findByParentidIsNullOrderBySortsequence();

    List<Tb_accredit> findByParentidOrderBySortsequence(String parentconfigid);

    Tb_accredit findByAid(String configid);

    Tb_accredit findByParentidAndText(String parentconfigid, String value);

    Tb_accredit findByParentidAndShortname(String parentconfigid, String code);

    Tb_accredit findByTextAndParentidIsNull(String value);

    Tb_accredit findByShortnameAndParentidIsNull(String code);

    @Query(value = "select max(sortsequence) from Tb_accredit where parentid=?1 or parentid is null")
    Integer findMaxSequenceByParentidOrNull(String pcid);

    @Query(value = "select max(sortsequence) from Tb_accredit where parentid = ?1")
    Integer findMaxSequenceByParentid(String configvalue);

    Page<Tb_accredit> findByParentid(Pageable pageable, String parentid);

    Integer deleteByAidIn(String[] configids);

    List<Tb_accredit> findByParentid(String parentconfigid);

    Integer deleteByParentid(String parentconfigid);

    Integer deleteByAid(String configid);

    String findAidByShortnameAndParentidIsNull(String shortname);


    @Query(value = "select t from Tb_accredit t where t.parentid in (select aid from Tb_accredit where shortname  = ?1)")
    List<Tb_accredit> findByParentValue(String parentValue);
}
