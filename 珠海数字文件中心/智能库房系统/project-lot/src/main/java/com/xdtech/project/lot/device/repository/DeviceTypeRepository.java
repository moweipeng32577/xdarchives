package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceType;
import com.xdtech.project.lot.device.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wangmh on 2019-09-04
 */

public interface DeviceTypeRepository extends JpaRepository<DeviceType, String> {



    Integer deleteByIdIn(String[] ids);
}
