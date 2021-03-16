package com.wisdom.web.repository;

import com.wisdom.web.entity.AlgorithmRetention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Rong on 2018/10/30.
 */
public interface AlgorithmRetentionRepository extends JpaRepository<AlgorithmRetention, String>,JpaSpecificationExecutor<AlgorithmRetention> {

    AlgorithmRetention findByWordAndRetention(String word, String retention);

    List<AlgorithmRetention> findAllByWordIn(String[] words);

}
