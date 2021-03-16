package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_yearlycheck_electronic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2020/10/14.
 */
public interface YearlyCheckElectronicRepository extends JpaRepository<Tb_yearlycheck_electronic, String> {

    List<Tb_yearlycheck_electronic> findByReportidIn(String[] reportids);

    int deleteByReportidIn(String[] reportids);

    Tb_yearlycheck_electronic findByReportid(String id);
}
