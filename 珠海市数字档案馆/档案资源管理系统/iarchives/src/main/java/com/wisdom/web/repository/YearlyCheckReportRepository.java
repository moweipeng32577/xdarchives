package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_yearlycheck_report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Administrator on 2020/10/14.
 */
public interface YearlyCheckReportRepository extends JpaRepository<Tb_yearlycheck_report, String>,JpaSpecificationExecutor<Tb_yearlycheck_report> {

    Tb_yearlycheck_report findById(String id);

    int deleteByIdIn(String[] ids);

    List<Tb_yearlycheck_report> findByIdIn(String[] ids);
}
