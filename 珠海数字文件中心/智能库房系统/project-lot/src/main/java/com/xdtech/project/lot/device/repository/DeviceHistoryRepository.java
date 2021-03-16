package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by Rong on 2019-03-25.
 */
public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, String>, JpaSpecificationExecutor<DeviceInformation> {

    Page<DeviceHistory> findAllByDevice_IdOrderByCaptureTimeDesc(String deviceid, Pageable pageable);

    Page<DeviceHistory> findAllByDevice_IdAndCaptureTimeGreaterThanAndCaptureTimeLessThanOrderByCaptureTimeDesc(String deviceid,Date min, Date max, Pageable pageable);

    List<DeviceHistory> findAllByDevice_IdAndCaptureTimeGreaterThanAndCaptureTimeLessThanOrderByCaptureTimeAsc(String deviceid,Date min, Date max);

    Page<DeviceHistory>findAllByType(String type,Pageable pageable);

    List<DeviceHistory> findByIdIn(String[] id);

    DeviceHistory findDeviceHistoriesByCaptureValue(String captureValue);

    DeviceHistory findDeviceHistoriesByCaptureTime(String captureTime);

    DeviceHistory findDeviceHistoriesByCaptureTimeAndType(String captureTime,String type);

}
