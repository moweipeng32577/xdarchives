package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.BorrowDocRepository;
import com.wisdom.web.repository.FeedbackRepository;
import com.wisdom.web.repository.QuestionnaireRepository;
import com.wisdom.web.repository.ReserveRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/4/13.
 */
@Service
@Transactional
public class SelfPerformanceService {


    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    ReserveRepository reserveRepository;

    @Autowired
    QuestionnaireRepository questionnaireRepository;



    public List<BackPerformance> getSelfPerformances(){
        List<BackPerformance> backPerformances = new ArrayList<>();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //查档
        List<Tb_borrowdoc> borrowdocs = borrowDocRepository.findByBorrowmanid(userDetails.getUserid());
        int successcount = 0;
        int failcount = 0;
        for(Tb_borrowdoc borrowdoc : borrowdocs){
            if("已通过".equals(borrowdoc.getState())){
                successcount++;
            }else if("不通过".equals(borrowdoc.getState())||"退回".equals(borrowdoc.getState())){
                failcount++;
            }
        }
        BackPerformance backPerformance = new BackPerformance();
        backPerformance.setTitle("查档申请");
        backPerformance.setSubmitcount(borrowdocs.size());
        backPerformance.setSuccesscount(successcount);
        backPerformance.setFailcount(failcount);
        backPerformances.add(backPerformance);
        successcount = 0;
        failcount = 0;
        //预约服务
        List<Tb_reserve> reserves = reserveRepository.findBySubmiterid(userDetails.getUserid());
        for(Tb_reserve reserve : reserves){
            if("已回复".equals(reserve.getYystate())){
                successcount++;
            }
            if("已取消".equals(reserve.getYystate())){
                failcount++;
            }
        }
        BackPerformance backPerformanceRe = new BackPerformance();
        backPerformanceRe.setTitle("预约服务");
        backPerformanceRe.setSubmitcount(reserves.size());
        backPerformanceRe.setSuccesscount(successcount);
        backPerformanceRe.setFailcount(failcount);
        backPerformances.add(backPerformanceRe);
        //问卷
        successcount = 0;
        failcount = 0;
        List<Tb_questionnaire> questionnaires = questionnaireRepository.findByUserID(userDetails.getUserid());
        for(Tb_questionnaire questionnaire :  questionnaires){
            if(questionnaire.getIsanswer()==1){
                successcount++;
            }else{
                failcount++;
            }
        }
        BackPerformance backPerformanceQu = new BackPerformance();
        backPerformanceQu.setTitle("问卷服务");
        backPerformanceQu.setSubmitcount(questionnaires.size());
        backPerformanceQu.setSuccesscount(successcount);
        backPerformanceQu.setFailcount(failcount);
        backPerformances.add(backPerformanceQu);
        return backPerformances;
    }
}
