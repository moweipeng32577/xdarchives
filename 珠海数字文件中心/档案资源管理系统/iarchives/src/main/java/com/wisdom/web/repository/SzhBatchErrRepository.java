package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_batch_err;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SzhBatchErrRepository extends JpaRepository<Szh_batch_err,String>,JpaSpecificationExecutor<Szh_batch_err> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
    Integer deleteByIdIn(String[] errids);
    List<Szh_batch_err> findByIdIn(String[] errids);
    List<Szh_batch_err> findByBatchcode(String batchcode);
}
