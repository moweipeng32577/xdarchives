package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_yearlycheck_approvedoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2020/10/15.
 */
public interface YearlyCheckApproveDocRepository extends JpaRepository<Tb_yearlycheck_approvedoc, String>,JpaSpecificationExecutor<Tb_yearlycheck_approvedoc> {


    @Query(value = "select b from Tb_yearlycheck_approvedoc b where b.approvecode in (select msgid from Tb_flows where taskid=?1)")
    Tb_yearlycheck_approvedoc getApproveDocByTaskid(String taskid);
}
