package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Tb_question,String>, JpaSpecificationExecutor<Tb_question> {

    List<Tb_question> findByQuestionnaireIDOrderBySort(String questionnaireID);

    List<String> findQuestionIDByQuestionnaireID(String questionnaireID);

    @Modifying
    @Query(value = "delete from tb_question where questionnaireID in (?1)",nativeQuery=true)
    Integer deleteByQuestionnaireID(String[] questionnaireIDs);

}
