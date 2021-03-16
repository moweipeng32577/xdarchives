package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_flows_record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SzhFlowsRecordRepository extends JpaRepository<Szh_flows_record, String> ,JpaSpecificationExecutor<Szh_flows_record> {

    @Query("select t from Szh_flows_record t where t.calloutid=?1 ORDER BY t.operatetime desc")
    List<Szh_flows_record> findByCalloutid(String calloutid);
}
