package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_feedback;
import com.wisdom.web.repository.FeedbackRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RonJiang on 2018/4/17 0017.
 */
@Service
@Transactional
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    public Page<Tb_feedback> findBySearch(String type,String condition, String operator, String content, int page, int limit, Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Specifications sp = null;
        //自助查询只显示当前操作人的个人意见，管理平台显示所有
        if("self".equals(type)){
            sp = Specifications.where(new SpecificationUtil("submiterid", "equal", userDetails.getUserid()));
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return feedbackRepository.findAll(sp,pageRequest);
    }

    public Tb_feedback getFeedback(String feedbackid){
        return feedbackRepository.findByFeedbackid(feedbackid);
    }

    public Integer delFeedback(String[] feedbackidData){
        return feedbackRepository.deleteByFeedbackidIn(feedbackidData);
    }

    public Tb_feedback saveFeedback(Tb_feedback feedback){
        return feedbackRepository.save(feedback);
    }

    public Tb_feedback replyFeedback(Tb_feedback feedback1){
        Tb_feedback feedback = feedbackRepository.findByFeedbackid(feedback1.getFeedbackid());
        feedback.setFlag("已回复");
        feedback.setReplyby(feedback1.getReplyby());
        feedback.setReplycontent(feedback1.getReplycontent());
        feedback.setReplytime(feedback1.getReplytime());
        return feedbackRepository.save(feedback);
    }

    public void setAppraise(String feedbackid,String labeltext,String content){
        Tb_feedback feedback = feedbackRepository.findByFeedbackid(feedbackid);
        feedback.setAppraise(labeltext);
        feedback.setAppraisetext(content);
        feedbackRepository.save(feedback);
    }

    public Page<Tb_feedback> getAllAppraise(String condition, String operator, String content, int page, int limit, Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Specifications sp = null;
        //显示个人的所有评价
        sp = Specifications.where(new SpecificationUtil("submiterid", "equal", userDetails.getUserid())).
                and(new SpecificationUtil("appraise", "isNotNull", null));
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return feedbackRepository.findAll(sp,pageRequest);
    }
}
