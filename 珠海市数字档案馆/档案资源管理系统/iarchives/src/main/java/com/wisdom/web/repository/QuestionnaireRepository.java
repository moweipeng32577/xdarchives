package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_questionnaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;


public interface QuestionnaireRepository extends JpaRepository<Tb_questionnaire,String>, JpaSpecificationExecutor<Tb_questionnaire> {

    @Query(value = "select * from tb_questionnaire where questionnaireID in (?1)",nativeQuery=true)
    List<Tb_questionnaire> findByQuestionnaireIDs(String[] questionnaireIDs);

    @Modifying
    @Query(value = "delete from tb_questionnaire where questionnaireID in (?1)",nativeQuery=true)
    Integer deleteByQuestionnaireID(String[] questionnaireID);

    @Modifying
    @Query(value = "update tb_questionnaire set publishstate = ?2, publishtime = ?3 where questionnaireID in (?1)",nativeQuery=true)
    Integer updatePublishstate(String[] questionnaireIDs,Integer publishstate,String publishtime);

    @Query(value = "select * from tb_questionnaire where publishstate = ?1 order by stick desc",nativeQuery=true)
    List<Tb_questionnaire> findByPublishstate(String publishstate);

    Tb_questionnaire findByQuestionnaireID(String questionnaireID);

    @Modifying
    @Query(value = "update tb_questionnaire set isanswer = ?1 where questionnaireID in (?2)",nativeQuery=true)
    Integer updateIsAnswer(Integer isAnswer, String questionnaireID);


    @Query(value = "SELECT createTime,COUNT(*) counts from tb_questionnaire GROUP BY createTime ORDER BY createTime",nativeQuery=true)
    String getStatisticsByCreateTime();

    List<Tb_questionnaire> findByUserID(String userid);
}
