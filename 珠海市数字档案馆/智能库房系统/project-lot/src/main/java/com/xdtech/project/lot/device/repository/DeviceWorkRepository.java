package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wujy on 2019-09-18.
 */
public interface DeviceWorkRepository extends JpaRepository<DeviceWork, String>, JpaSpecificationExecutor<DeviceWork> {
        @Modifying
        @Query(value = "UPDATE DeviceWork set STATUS =?1 WHERE WORK_id in ?2")
        Integer updateDeviceStatus(String status,String[] id);

        @Query(value = "select * from lot_device_work d where d.device in (select id from lot_device where type in ?1)",nativeQuery = true)
        List<DeviceWork> findDeviceWorkByDeviceId(String[] ids);

        @Query(value = "select * from lot_device_work d where d.device in ?1",nativeQuery = true)
        List<DeviceWork> findBydeviceid(String[] ids);

        @Query(value = "select * from lot_device_work d where d.work_id in ?1",nativeQuery = true)
        List<DeviceWork> findByids(String[] ids);

        @Modifying
        @Transactional
        @Query(value = "delete from DeviceWork  WHERE WORK_id in ?1")
        Integer deleteByIds(String[] id);
}
