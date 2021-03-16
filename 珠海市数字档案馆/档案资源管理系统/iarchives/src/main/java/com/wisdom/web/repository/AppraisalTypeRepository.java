package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_appraisal_type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/5/17 0017.
 */
public interface AppraisalTypeRepository extends JpaRepository<Tb_appraisal_type, String>{

    Integer deleteByAppraisaltypevalue(String appraisaltypevalue);

    @Query(value = "select appraisaltypevalue from Tb_appraisal_type")
    List<String> findAllAppraisaltypevalue();

    @Query(value = "select appraisaltypeid from Tb_appraisal_type where appraisaltypevalue=?1")
    String findAppraisaltypeidByAppraisaltypevalue(String appraisaltypevalue);
}
