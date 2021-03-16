package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_answer;
import com.wisdom.web.entity.Tb_answer_sheet;
import com.wisdom.web.repository.AnswerRepository;
import com.wisdom.web.repository.AnswerSheetRepository;
import com.wisdom.web.security.SecurityUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AnswerSheetService {

    @Autowired
    AnswerSheetRepository answerSheetRepository;

    @Autowired
    AnswerRepository answerRepository;

    public Tb_answer_sheet addAnswerSheet(String questionnaireID, String data) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        Tb_answer_sheet answerSheet = new Tb_answer_sheet();
        answerSheet.setQuestionnaireID(questionnaireID);
        answerSheet.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        answerSheet.setName(userDetails.getUsername());
        answerSheet.setPhone(userDetails.getPhone());
        answerSheet.setIDcard(userDetails.getUserid());
        Tb_answer_sheet answerSheetSave = answerSheetRepository.save(answerSheet);
        if (answerSheetSave != null){
            JSONArray json= JSONArray.fromObject(data);
            JSONObject jsonOne;
            for(int i=0;i<json.size();i++){
                jsonOne = json.getJSONObject(i);
                Tb_answer answer = new Tb_answer();
                String questionID = (String) jsonOne.get("questionID");
                answer.setQuestionID(questionID);
                String type = (String) jsonOne.get("type");
                String options = "";
                if ("3".equals(type)){//多选题
                    String answers = (String) jsonOne.get("answer");
                    String[] option = answers.split(",");
                    for (int j = 0;j < option.length;j++){
                        options = options + option[j] + ",";
                    }
                    String optionValue = options.substring(0,options.length());
                    answer.setAnswer(optionValue);
                }else {
                    String answers = (String) jsonOne.get("answer");
                    answer.setAnswer(answers);
                }
                answer.setAnswersheetID(answerSheetSave.getAnswerSheetID());
                Tb_answer answerSave = answerRepository.save(answer);
                if(answerSave == null){
                   return  null;
                }
            }
        }
        return answerSheetSave;
    }

    public Tb_answer_sheet findByQuestionnaireIDAndUserID(String questionnaireID) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        Tb_answer_sheet answerSheet = answerSheetRepository.findByQuestionnaireIDAndIDcard(questionnaireID,userDetails.getUserid());
        return answerSheet;
    }

    public Integer findCountByQuestionnaireID(String questionnaireID){
        return answerSheetRepository.findCountByQuestionnaireID(questionnaireID);
    }
}
