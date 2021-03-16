package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.service.AnswerService;
import com.wisdom.web.service.AnswerSheetService;
import com.wisdom.web.service.QuestionService;
import com.wisdom.web.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问卷管理控制器
 */
@Controller
@RequestMapping(value = "/questionnaireManagement")
public class QuestionnaireManagementController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionService questionService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    AnswerSheetService answerSheetService;

    @Autowired
    AnswerService answerService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String questionManagement(Model model){
        model.addAttribute("reportServer",reportServer);
        return "/inlet/questionnaireManagement";
    }

    @RequestMapping("/mainly")
    public String questionnaireManagementLY(Model model,String flag,String type,String quesid,String title) {
        model.addAttribute("flag", flag);
        model.addAttribute("type",type);
        model.addAttribute("quesid",quesid);
        model.addAttribute("title",title);
        return "/inlet/questionnaireManagement";
    }


    @RequestMapping("/getQuestionnaires")
    @ResponseBody
    public Page<Tb_questionnaire> getQuestionnaires(int page, int start, int limit, String condition, String operator,
                                                    String content, String sort,String type){
        return questionnaireService.getQuestionnaires(page,start,limit,condition,operator,content,sort,type);
    }

    @RequestMapping("/getState")
    @ResponseBody
    public ExtMsg getState(String questionnaireID){
        Tb_answer_sheet answerSheet = answerSheetService.findByQuestionnaireIDAndUserID(questionnaireID);
        if (answerSheet != null){
            return new ExtMsg(true,"已答题",answerSheet);
        }
        return new ExtMsg(false,"未答题",answerSheet);
    }

    @RequestMapping("/getAnswer")
    @ResponseBody
    public ExtMsg getAnswer(String questionID,String answerSheetID){
        Tb_answer answer = answerService.getAnswer(questionID,answerSheetID);
        return new ExtMsg(false,"",answer);
    }

    @RequestMapping("/cancelStick")
    @ResponseBody
    public ExtMsg cancelStick(String[] ids) {
        ExtMsg msg = new ExtMsg(true,"取消置顶成功",null);
        try {
            boolean state = questionnaireService.cancelStick(ids);
            if(state){
                webSocketService.noticeRefresh();
            }else{
                msg.setMsg("非置顶问卷");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setMsg("取消置顶失败");
        }
        return msg;
    }

    @RequestMapping("/addQuestionnaires")
    @ResponseBody
    public ExtMsg addQuestionnaires(String questionnaireID,String title, String createtime, String starttime, String endtime,
                                    String publishstate, String stick,String data){
        Tb_questionnaire questionnaire = questionnaireService.addQuestionnaires(questionnaireID,title,createtime,starttime,endtime,publishstate,stick,data);
        if (questionnaire != null){
            return new ExtMsg(true, "操作成功",null);
        }
        return new ExtMsg(false, "操作失败", null);
    }

    @RequestMapping("/findQuestions")
    @ResponseBody
    public List<Tb_question> findQuestions(String questionnaireID){
        return questionService.findQuestionsByQuestionnaireID(questionnaireID);
    }

    @RequestMapping("/delQuestionnaire")
    @ResponseBody
    public ExtMsg delQuestionnaire(String[] questionnaireIDs){
        boolean result = questionnaireService.delQuestionnaire(questionnaireIDs);
        if (result){
            return new ExtMsg(true, "操作成功",null);
        }
        return new ExtMsg(false, "操作失败", null);
    }

    @RequestMapping("/updatePublishQuestionnaire")
    @ResponseBody
    public ExtMsg updatePublishQuestionnaire(String[] questionnaireIDs,String state){
        boolean result = questionnaireService.updatePublishQuestionnaire(questionnaireIDs,state);
        if (result){
            return new ExtMsg(true, "操作成功",null);
        }
        return new ExtMsg(false, "操作失败", null);
    }

    @RequestMapping("/setStick")
    @ResponseBody
    public ExtMsg setStick(String[] ids,String level){
        ExtMsg msg = new ExtMsg(true,"置顶成功",null);
        try {
            questionnaireService.setStick(ids,level);
            webSocketService.noticeRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            msg.setMsg("置顶失败");
        }
        return msg;
    }

    @RequestMapping("/checkVerificationCode")
    @ResponseBody
    public ExtMsg checkVerificationCode(String verificationCode){
        ExtMsg msg = new ExtMsg(true,"",null);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String cacheCode = session.getAttribute("verificationCode") != null
                ? ((String) session.getAttribute("verificationCode")).toLowerCase() : "";
        if (!cacheCode.equals(verificationCode)){
            msg.setMsg("验证码有误！");
        }
        return msg;
    }

    @RequestMapping("/submitAnswer")
    @ResponseBody
    public ExtMsg submitAnswer(String questionnaireID,String data){
        Tb_answer_sheet answerSheet = answerSheetService.addAnswerSheet(questionnaireID,data);
        if (answerSheet == null){
            return new ExtMsg(true, "提交失败",null);
        }else {
            questionnaireService.updateIsAnswer(1,questionnaireID);
            return new ExtMsg(true, "提交成功",null);
        }
    }

    @RequestMapping("/getStatistics")
    @ResponseBody
    public String getStatistics(){
        return "/inlet/Statistics";
    }

    @RequestMapping("/getByCreateTime")
    @ResponseBody
    public ExtMsg getByCreateTime(){
       String timeStatistics =  questionnaireService.getStatisticsByCreateTime();
       if (timeStatistics != null && !timeStatistics.isEmpty()){
           return new ExtMsg(true, "信息获取成功",null);
       }
        return new ExtMsg(true, "信息获取失败",null);
    }

    @RequestMapping("/getCountByquestionnaireID")
    @ResponseBody
    public Integer getCountByquestionnaireID(String questionnaireID){
        return answerSheetService.findCountByQuestionnaireID(questionnaireID);
    }
}
