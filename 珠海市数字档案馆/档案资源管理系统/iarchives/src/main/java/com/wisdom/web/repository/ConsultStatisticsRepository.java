package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_consult_statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Leo on 2020/7/3 0003.
 */
public interface ConsultStatisticsRepository extends JpaRepository<Tb_consult_statistics, Integer>, JpaSpecificationExecutor<Tb_consult_statistics> {

    List<Tb_consult_statistics> findAllByDatetime(String date);

    Tb_consult_statistics findAllByDatetimeAndType(String date,String type);

    @Modifying
    @Transactional
    @Query(value = "update Tb_consult_statistics set company=?3,personal=?4,volume=?5,piece=?6,tocopy=?7,prove=?8 where type=?2 and datetime=?1")
    Integer updateConsultStatistics(String date,String type,String company ,String personal,String volume,String piece,String tocopy,String prove);


    Integer deleteAllByDatetime(String date);
}
