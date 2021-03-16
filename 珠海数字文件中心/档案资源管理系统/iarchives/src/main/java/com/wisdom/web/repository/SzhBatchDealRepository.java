package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_batch_deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2018/12/4.
 */
public interface SzhBatchDealRepository extends JpaRepository<Szh_batch_deal,String> {


    List<Szh_batch_deal> findByCheckgroupid(String checkgroupid);

    Szh_batch_deal findByBatchidAndAndState(String batchid, String state);
}
