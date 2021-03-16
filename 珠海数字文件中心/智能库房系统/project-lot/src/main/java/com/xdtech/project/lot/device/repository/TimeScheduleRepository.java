package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceInformation;
import com.xdtech.project.lot.device.entity.Tb_TimeSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

public interface TimeScheduleRepository extends JpaRepository<Tb_TimeSchedule,String> {

    List<Tb_TimeSchedule> findByFlagAndType(String flag, String type);

    @Query(value = "select * from tb_Time_Schedule t  where t.flag = ?1",nativeQuery = true)
    List<Tb_TimeSchedule> findAllByFlag(String flag);


}
