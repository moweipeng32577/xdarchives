package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnswerRepository extends JpaRepository<Tb_answer,String>, JpaSpecificationExecutor<Tb_answer> {

    Integer deleteByAnswersheetID(String AnswersheetID);

    Tb_answer findByQuestionIDAndAnswersheetID(String questionID,String answersheetID);

}
