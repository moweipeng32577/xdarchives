package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_feedback;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.FeedbackService;
import javassist.tools.web.Webserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈管理控制器
 * Created by RonJiang on 2018/4/17 0017.
 */
@Controller
@RequestMapping(value = "/feedback")
public class FeedbackController {

    @Autowired
    LogAop logAop;

    @Autowired
    FeedbackService feedbackService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/main")
    public String feedback(Model model, String flag) {
        model.addAttribute("buttonflag",flag);
        return "/inlet/feedback";
    }

    @RequestMapping("/mainly")
    public String feedbackly(Model model, String flag) {
        model.addAttribute("buttonflag",flag);
        return "/inlet/feedback";
    }

    @RequestMapping("/getFeedback")
    @ResponseBody
    public Page<Tb_feedback> getFeedback(String type,int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        return feedbackService.findBySearch(type,condition,operator,content,page,limit,sortobj);
    }

    @RequestMapping(value = "/feedbacks/{feedbackid}", method = RequestMethod.GET)
    @ResponseBody
    public Tb_feedback getFeedback(@PathVariable String feedbackid){
        return feedbackService.getFeedback(feedbackid);
    }

//    @LogAnnotation(module = "反馈管理",startDesc = "删除反馈记录操作，反馈id为：",sites = "1")
    @RequestMapping(value = "/feedbacks/{feedbackids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delFeedback(@PathVariable String feedbackids) {
        String startTime = LogAop.getCurrentSystemTime();//开始时间
        long startMillis = System.currentTimeMillis();//开始毫秒数
        String[] feedbackidData = feedbackids.split(",");
        Integer del = feedbackService.delFeedback(feedbackidData);
        for(String feedbackid:feedbackidData){
            logAop.generateManualLog(startTime,LogAop.getCurrentSystemTime(),System.currentTimeMillis()-startMillis,"反馈管理","删除反馈记录操作，反馈id为："+feedbackid);
        }
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    @LogAnnotation(module = "反馈管理",sites = "1",fields = "title,askman",connect = "##标题；,##投件人；",startDesc = "增加反馈操作，反馈信息详情：")
    @RequestMapping(value = "/feedbacks", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveFeedback(@ModelAttribute("form") Tb_feedback feedback){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        feedback.setFlag("未回复");
        feedback.setSubmiterid(userDetails.getUserid());  //设置提交人id
        Tb_feedback result = feedbackService.saveFeedback(feedback);
        if(result != null){
            return new ExtMsg(true,"保存成功",result);
        }
        return new ExtMsg(false,"保存失败",null);
    }

    @LogAnnotation(module = "反馈管理",sites = "1",fields = "title,replyby",connect = "##标题；,##提交人；",startDesc = "回复反馈操作，反馈信息详情：")
    @RequestMapping(value = "/feedbackReply", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg replyFeedback(@ModelAttribute("form") Tb_feedback feedback){
        Tb_feedback result = feedbackService.replyFeedback(feedback);
        if(result != null){
            return new ExtMsg(true,"保存成功",result);
        }
        return new ExtMsg(false,"保存失败",null);
    }

    /**
     * 设置评分
     *
     *
     * @return
     */
    @RequestMapping("/setAppraise")
    @ResponseBody
    public ExtMsg setAppraise(String feedbackid,String labeltext,String content) {
        feedbackService.setAppraise(feedbackid,labeltext,content);
        return new ExtMsg(true,"",null);
    }

    //获取所有我的评价
    @RequestMapping("/getAllAppraise")
    @ResponseBody
    public Page<Tb_feedback> getAllAppraise(int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        return feedbackService.getAllAppraise(condition,operator,content,page,limit,sortobj);
    }
}
