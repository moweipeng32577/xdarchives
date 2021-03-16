package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_appraisal_standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/5/10 0010.
 */
public interface AppraisalStandardRepository extends JpaRepository<Tb_appraisal_standard, String>,JpaSpecificationExecutor<Tb_appraisal_standard> {

    Integer deleteByAppraisalstandardidIn(String[] appraisalstandardidData);

    Tb_appraisal_standard findByAppraisalstandardid(String appraisalstandardid);

    @Query(value = "select appraisalstandardvalue from Tb_appraisal_standard where appraisaltypevalue=?1")
    List<String> findAppraisalstandardvalueByAppraisaltypevalue(String appraisaltypevalue);

    @Query(value = "select appraisalretention from Tb_appraisal_standard where appraisalstandardvalue=?1 and appraisaltypevalue=?2")
    String findAppraisalretentionByAppraisalstandardvalueAndAppraisaltypevalue(String appraisalstandardvalue,String appraisaltypevalue);
}
