package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by RonJiang on 2018/4/17 0017.
 */
public interface FeedbackRepository extends JpaRepository<Tb_feedback,String>,JpaSpecificationExecutor<Tb_feedback> {

    Tb_feedback findByFeedbackid(String feedbackid);

    Integer deleteByFeedbackidIn(String[] feedbackidData);

    Tb_feedback findByBorrowdocid(String borrowdocid);

    List<Tb_feedback> findByFeedbackidIn(String[] feedbackids);
}
