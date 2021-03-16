package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceDiagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface DeviceDiagnoseRepository extends JpaRepository<DeviceDiagnose, String>, JpaSpecificationExecutor<DeviceDiagnose> {

    @Modifying
    @Transactional
    Integer deleteByIdIn(String[] ids);

    @Query(value = "select d from DeviceDiagnose d where faultcause like concat('%',?1,'%')")
    List<DeviceDiagnose> findByFaultcauseLike(String faultcause);

    @Query(value = "select d from DeviceDiagnose d where faultcause not like concat('%',?1,'%')")
    List<DeviceDiagnose> findByFaultcauseNotLike(String faultcause);
}
