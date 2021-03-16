package com.wisdom.web.repository;

import com.wisdom.web.entity.RetentionInitProbability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Leo on 2019/5/8 0008.
 */
public interface RetentionInitProbabilityRepository  extends JpaRepository<RetentionInitProbability, String>,JpaSpecificationExecutor<RetentionInitProbability> {

    RetentionInitProbability findByDr(int dr);

}
