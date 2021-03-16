package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_question;
import com.wisdom.web.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public List<Tb_question> findQuestionsByQuestionnaireID(String questionnaireID) {

       return questionRepository.findByQuestionnaireIDOrderBySort(questionnaireID);

    }
}
