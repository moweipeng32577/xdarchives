package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceOperateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wujinyuan on 2019-09-11
 */

public interface DeviceOperateRecordRepository extends JpaRepository<DeviceOperateRecord, String> {

    List<DeviceOperateRecord> findByOrderByOperateTimeDesc();
}
