package com.wisdom.web.service;


import com.wisdom.web.entity.Tb_answer;
import com.wisdom.web.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnswerService {

    @Autowired
    AnswerRepository answerRepository;

    public Tb_answer getAnswer(String questionID,String answerSheetID) {
        return answerRepository.findByQuestionIDAndAnswersheetID(questionID,answerSheetID);
    }
}
