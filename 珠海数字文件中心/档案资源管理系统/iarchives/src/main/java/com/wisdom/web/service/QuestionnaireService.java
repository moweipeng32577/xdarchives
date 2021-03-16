package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_answer_sheet;
import com.wisdom.web.entity.Tb_inform;
import com.wisdom.web.entity.Tb_question;
import com.wisdom.web.entity.Tb_questionnaire;
import com.wisdom.web.repository.AnswerRepository;
import com.wisdom.web.repository.AnswerSheetRepository;
import com.wisdom.web.repository.QuestionRepository;
import com.wisdom.web.repository.QuestionnaireRepository;
import com.wisdom.web.security.SecurityUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class QuestionnaireService {

    @Autowired
    QuestionnaireRepository questionnaireRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    AnswerSheetRepository answerSheetRepository;

    /**
     * 打开问卷管理的页面获取问卷数据
     * @param page
     * @param start
     * @param limit
     * @param condition
     * @param operator
     * @param content
     * @param sort
     * @return
     */
    public Page<Tb_questionnaire> getQuestionnaires(int page, int start, int limit, String condition, String operator,
                                                    String content, String sort,String type) {
        Specifications specifications = null;
        if ("1".equals(type)){
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentime = format.format(date);
            Specification<Tb_questionnaire> searchid = new Specification<Tb_questionnaire>() {
                @Override
                public Predicate toPredicate(Root<Tb_questionnaire> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Predicate[] predicates = new Predicate[3];
                    predicates[0] = cb.equal(root.get("publishstate"), "1");//已发布
                    predicates[1] = cb.lessThanOrEqualTo(root.get("starttime"), currentime);//小于或等于
                    predicates[2] = cb.greaterThanOrEqualTo(root.get("endtime"), currentime);//大于或等于
                    return cb.and(predicates);
                }
            };
            specifications = Specifications.where(searchid);
        }
        if("2".equals(type)){
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
            List<String> questionnaireIDs = answerSheetRepository.findByIDcard(userDetails.getUserid());
            Specification<Tb_questionnaire> searchid = new Specification<Tb_questionnaire>() {
                @Override
                public Predicate toPredicate(Root<Tb_questionnaire> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    CriteriaBuilder.In<String> predicate = cb.in(root.get("questionnaireID"));
                    for(String item : questionnaireIDs) {
                        predicate.value(item);
                    }
                    return predicate;
                }
            };

            specifications = Specifications.where(searchid);
        }

        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"stick"));//置顶
        sorts.add(new Sort.Order(Sort.Direction.DESC,"createtime"));//创建时间降序
        return questionnaireRepository.findAll(specifications, new PageRequest(page - 1, limit, (Sort)(sort == null ? new Sort(sorts) : sort)));
    }

    /**
     *添加问卷信息以及问题信息
     * @param title 问卷标题
     * @param createtime 创建时间
     * @param starttime 开始答题时间
     * @param endtime 结束答题时间
     * @param publishstate 发布状态
     * @param stick 置顶等级
     * @param data 问卷的问题
     * @return
     */
    public Tb_questionnaire addQuestionnaires(String questionnaireID,String title, String createtime, String starttime, String endtime, String publishstate, String stick,String data) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        //添加问卷信息
        Tb_questionnaire questionnaire = new Tb_questionnaire();
        questionnaire.setTitle(title);
        questionnaire.setCreatetime(createtime);
        questionnaire.setStarttime(starttime);
        questionnaire.setEndtime(endtime);
        questionnaire.setIsanswer(0);
        if ("1".equals(publishstate)){//已发布
            questionnaire.setPublishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            questionnaire.setPublishstate(1);
        }
        if (!("".equals(stick)) && 0 != stick.length() && null != stick){//设置置顶登记
            questionnaire.setStick(Integer.valueOf(stick));
        }
        questionnaire.setUserID(userDetails.getUserid());
        String[] questionnaireIDs = {questionnaireID};
        if (null!=questionnaireID && (!"".equals(questionnaireID))){//如果是修改，先将问题全部删除再更新
//            questionnaire.setQuestionnaireID(questionnaireID);
//            questionRepository.deleteByQuestionnaireID(questionnaireIDs);
//            if(delQuestionnaire(questionnaireIDs)){
//                questionnaire.setIsanswer(0);
//            }
            delQuestionnaire(questionnaireIDs);
        }
        Tb_questionnaire saveQuestionnaire = questionnaireRepository.save(questionnaire);

        //添加问题信息
        JSONArray json= JSONArray.fromObject(data);
        JSONObject jsonOne;
        for(int i=0;i<json.size();i++){
            jsonOne = json.getJSONObject(i);
            Tb_question question = new Tb_question();
            question.setQuestionnaireID(saveQuestionnaire.getQuestionnaireID());
            String type = (String) jsonOne.get("type");
            question.setType(Integer.parseInt(type));
            String isNecessary = (String) jsonOne.get("isNecessary");
            question.setIsnecessary(Integer.parseInt(isNecessary));
            String content = (String) jsonOne.get("content");
            question.setContent(content);
            question.setSort(i+1);
            if (!"1".equals(type)){//如果是选择题
              JSONArray optionContents =  jsonOne.getJSONArray("optionContents");
              StringBuffer optionContent = new StringBuffer();
              for (int j = 0; j < optionContents.size(); j++ ){
                  optionContent = optionContent.append(optionContents.get(j)).append(",");
              }
              optionContent.delete(optionContent.length()-1,optionContent.length());
              question.setOptional(optionContent.toString());
            }

            Tb_question saveQuestion = questionRepository.save(question);
            if (!(null != saveQuestion)){
                return null;
            }
        }
        return saveQuestionnaire;
    }

    /**
     * 删除问卷信息
     * @param questionnaireIDs
     * @return
     */
    public boolean delQuestionnaire(String[] questionnaireIDs) {
        //获取删除的问卷信息
        List<Tb_questionnaire> questionnaires = questionnaireRepository.findByQuestionnaireIDs(questionnaireIDs);
        for (int i = 0;i < questionnaires.size();i++){
           Integer isanswer = questionnaires.get(i).getIsanswer();
           if ("1".equals(isanswer.toString())){//如果存在答卷，先删除答卷
               //获取该问卷的所有答卷
                List<Tb_answer_sheet> answerSheet = answerSheetRepository.findByQuestionnaireID(questionnaires.get(i).getQuestionnaireID());
                //删除每份答卷下的回答
                for (int j = 0;j < answerSheet.size();j++){
                    int resultAnswer = answerRepository.deleteByAnswersheetID(answerSheet.get(j).getAnswerSheetID());
                    if (resultAnswer < 0){//删除不成功时
                        return false;
                    }
                }
                //删除答卷信息
               int resultAnswerSheet = answerSheetRepository.deleteByQuestionnaireID(questionnaireIDs);
               if (resultAnswerSheet < 0){//删除不成功时
                   return false;
               }
           }
           //删除问卷问题
            int resultQuestion = questionRepository.deleteByQuestionnaireID(questionnaireIDs);
            if (resultQuestion < 0){//删除不成功时
                return false;
            }
            //删除问卷
            int resultQuestionnaire = questionnaireRepository.deleteByQuestionnaireID(questionnaireIDs);
            if (resultQuestionnaire < 0){//删除不成功时
                return false;
            }
        }
        return true;
    }

    /**
     * 更新问卷的发布状态
     * @param questionnaireIDs
     * @param state
     * @return
     */
    public boolean updatePublishQuestionnaire(String[] questionnaireIDs,String state) {
        int publishstate = Integer.parseInt(state);
        int result = questionnaireRepository.updatePublishstate(questionnaireIDs,"1".equals(state) ? publishstate:null,
                "1".equals(state) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : "");
        if (result < 0){
            return false;
        }
        return true;
    }

    /**
     * 设置置顶等级
     * @param ids
     * @param level
     */
    public void setStick(String[] ids, String level) {
        List<Tb_questionnaire> questionnaires = questionnaireRepository.findByQuestionnaireIDs(ids);
        for(Tb_questionnaire questionnaire:questionnaires){
            questionnaire.setStick(Integer.parseInt(level));
        }
    }

    /**
     * 获取已发布的问卷
     * @param publishstate
     * @return
     */
    public List<Tb_questionnaire> getPublishQuestionnaire(String publishstate){
        List<Tb_questionnaire> questionnaires = questionnaireRepository.findByPublishstate(publishstate);
        return questionnaires;
    }

    /**
     * 根据问卷ID获取问卷信息
     * @param questionnaireID
     * @return
     */
    public Tb_questionnaire findQuestionnaire(String questionnaireID) {
        return questionnaireRepository.findByQuestionnaireID(questionnaireID);
    }

    /**
     * 更新问卷信息中是否存在答卷的标志
     * @param isAnswer
     */
    public Integer updateIsAnswer(Integer isAnswer,String questionnaireID) {
        return questionnaireRepository.updateIsAnswer(isAnswer,questionnaireID);
    }

    public String getStatisticsByCreateTime() {
       return questionnaireRepository.getStatisticsByCreateTime();
    }

    public boolean cancelStick(String[] ids) {
        boolean state = false;
        List<Tb_questionnaire> questionnaires = questionnaireRepository.findByQuestionnaireIDs(ids);
        for(Tb_questionnaire questionnaire:questionnaires){
            if(questionnaire.getStick()!=null){
                questionnaire.setStick(null);
                state = true;
            }
        }
        return state;
    }
}
