package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_log_msg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Created by RonJiang on 2018/3/30 0030.
 */
public interface LogMsgRepository extends JpaRepository<Tb_log_msg, String>,
        JpaSpecificationExecutor<Tb_log_msg> {

    Integer deleteByLmidIn(String[] ids);

    List<Tb_log_msg> findByLmidInOrderByStartTimeDesc(String[] ids);
    
    @Query(value = "select operate_user from Tb_log_msg where desci = '用户不存在！'")
    List<String> findByDesci();

    @Query(value = "select t from Tb_log_msg t where desci like concat('%',?1,'%') order by startTime desc ")
    List<Tb_log_msg> findBydesci(String desci);

    @Query(value = "select count(1) from tb_log_msg where desci like concat('%',?1,'%') ",nativeQuery = true)
    String findBydesciCount(String desci);

    @Query(value = "select t from Tb_log_msg t where desci like concat('%',?1,'%') order by startTime desc ")
    List<Tb_log_msg> findBydesci(Pageable pageable, String desci);

    @Query(value = "select count(lmid) from Tb_log_msg t where t.desci like concat(?1,'%') and t.end_time like concat(?2,'%')")
    Long getNumByDesciAndEndtime(String desci,String endtime);

    @Query(value = "select count(lmid) from Tb_log_msg t where t.desci like concat(?1,'%') and (t.end_time >= ?2 and t.end_time <= ?3)")
    Long getNumByDesciAndStarEndtime(String desci,String fristday,String lastday);

    @Query(value = "select max(end_time) from Tb_log_msg t where t.desci like concat(?1,'%')")
    String getMaxEndtime(String desci);

    @Query(value = "select count(1) from tb_log_msg where module='用户登录' and desci like '%档案%'",nativeQuery = true)
    String getVisitNum();

    @Query(value = "select count(1) from tb_log_msg where module='用户登录' and desci like '%档案%' and start_time >=?1 and start_time<=?2",nativeQuery = true)
    String getVisitNum(String start,String end);

    @Query(value = "select year(start_time) as year,month(start_time) as month,count(1) num from tb_log_msg where module='用户登录' and desci like '%档案%' and start_time >=?1 and start_time<=?2 GROUP BY year(start_time),month(start_time)",nativeQuery = true)
    List<Object[]> getVisitNumAvg(String start,String end);
}