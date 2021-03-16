package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/2/27 0027.
 */
public interface ReportRepository extends JpaRepository<Tb_report, String>,JpaSpecificationExecutor<Tb_report> {

    Page<Tb_report> findByNodeid(Pageable pageable, String nodeid);

    Page<Tb_report> findByNodeidIn(Pageable pageable, String[] nodeids);

    Page<Tb_report> findByNodenameIn(Pageable pageable, String[] nodenames);

    Page<Tb_report> findByReporttype(Pageable pageable, String reporttype);

    Tb_report findByReportid(String reportid);

    Integer deleteByReportidIn(String[] reportidData);

    Integer deleteByNodeidIn(String[] nodeids);

    @Query(value = "select filename from Tb_report where reportid=?1")
    String findFilenameByReportid(String reportid);

    @Query(value = "select reportname from Tb_report where filename=?1")
    String findReportnameByFilename(String filename);

    @Query(value = "select modul from Tb_report where filename=?1")
    String findModulByFilename(String filename);

    @Query(value = "select count(r) from Tb_report r where r.reportid=?1")
    Integer findCountByReportid(String reportid);

    @Query(value = "select count(r) from Tb_report r where r.filename=?1")
    Integer findCountByFilename(String filename);

    @Query(value = "select r from Tb_report r where r.reporttype='公有报表'")
    Page<Tb_report> findByRporttype(Pageable pageable);

    @Query(value = "select r from Tb_report r where r.nodename in ?1 or (nodeid !='publicreportfnid' and r.reporttype='公有报表')")
    Page<Tb_report> findByNodenameInAndReporttype(String[] nodenames,Pageable pageable);

    @Query(value = "select r from Tb_report r where r.nodeid != 'publicreportfnid' and r.reporttype='公有报表'")
    Page<Tb_report> getReportBytype(Pageable pageable);

    @Query(value = "select r from Tb_report r where r.nodeid in (select nodeid from Tb_user_data_node where userid = ?1)")
    Page<Tb_report> getByNodeidIn(Pageable pageable,String userid);

    @Query(value = "select r from Tb_report r where r.nodeid != 'publicreportfnid'")
    Page<Tb_report> findReportOutElse(Pageable pageable);

}
