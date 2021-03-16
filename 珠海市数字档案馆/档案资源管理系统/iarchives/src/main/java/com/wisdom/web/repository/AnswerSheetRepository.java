package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_answer_sheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerSheetRepository extends JpaRepository<Tb_answer_sheet,String>, JpaSpecificationExecutor<Tb_answer_sheet> {

    List<Tb_answer_sheet> findByQuestionnaireID(String questionnaireID);

    @Modifying
    @Query(value = "delete from tb_answer_sheet where questionnaireID in (?1)",nativeQuery=true)
    Integer deleteByQuestionnaireID(String[] questionnaireIDs);

    @Query(value = "select * from tb_answer_sheet where questionnaireID = ?1 and IDcard = ?2",nativeQuery=true)
    Tb_answer_sheet findByQuestionnaireIDAndIDcard(String questionnaireID,String IDcard);

    @Query(value = "select questionnaireID from tb_answer_sheet where IDcard = ?1",nativeQuery=true)
    List<String> findByIDcard(String IDcard);

    @Query(value = "select count(t) from Tb_answer_sheet t where t.questionnaireID=?1")
    Integer findCountByQuestionnaireID(String questionnaireID);
}
