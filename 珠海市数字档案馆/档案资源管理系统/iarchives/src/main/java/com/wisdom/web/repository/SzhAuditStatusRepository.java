package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_audit_status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Administrator on 2019/9/20.
 */
public interface SzhAuditStatusRepository extends JpaRepository<Szh_audit_status,String>,JpaSpecificationExecutor<Szh_audit_status> {

    Szh_audit_status findByMediaid(String mediaid);

    List<Szh_audit_status> findByMediaidIn(String[] mediaids);
}
