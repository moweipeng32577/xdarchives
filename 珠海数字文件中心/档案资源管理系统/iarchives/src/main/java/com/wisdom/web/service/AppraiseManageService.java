package com.wisdom.web.service;

import com.wisdom.util.CreateExcel;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_feedback;
import com.wisdom.web.repository.FeedbackRepository;
import com.wisdom.web.security.SecurityUser;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2020/3/23.
 */
@Service
@Transactional
public class AppraiseManageService {


    @Autowired
    FeedbackRepository feedbackRepository;

    public Page<Tb_feedback> getAppraiseManage(String condition, String operator, String content, int page, int limit, Sort sort){
        Specifications sp = null;
        sp = Specifications.where(new SpecificationUtil("appraise", "isNotNull", null));
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return feedbackRepository.findAll(sp,pageRequest);
    }
}
