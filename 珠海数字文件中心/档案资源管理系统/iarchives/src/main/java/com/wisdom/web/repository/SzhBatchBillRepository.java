package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_batch_bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SzhBatchBillRepository extends JpaRepository<Szh_batch_bill,String>,JpaSpecificationExecutor<Szh_batch_bill> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
    Szh_batch_bill findByBatchcode(String batchcode);
    List<Szh_batch_bill> findByBatchcodeIn(String[] batchcodes);
}
