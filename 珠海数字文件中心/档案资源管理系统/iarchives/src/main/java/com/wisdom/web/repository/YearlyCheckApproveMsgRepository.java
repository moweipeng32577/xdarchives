package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_yearlycheck_approvemsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2020/10/15.
 */
public interface YearlyCheckApproveMsgRepository extends JpaRepository<Tb_yearlycheck_approvemsg, String>,JpaSpecificationExecutor<Tb_yearlycheck_approvemsg> {

    @Query(value = "select b.reportid from Tb_yearlycheck_approvemsg b where b.approvecode in (select msgid from Tb_flows where taskid=?1)")
    String[] getReportIdsByTaskid(String taskid);

    @Query(value = "select b.reportid from Tb_yearlycheck_approvemsg b where b.approvecode =?1")
    String[] getReportIdsByApprovecode(String approvecode);
}
