package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Rong on 2019-01-16.
 */
public interface DeviceLinkRepository extends JpaRepository<DeviceLink, String> , JpaSpecificationExecutor<DeviceLink> {

    @Modifying
    @Transactional
    @Query(value = "delete from DeviceLink  WHERE id in ?1")
    Integer deleteByIds(String[] id);

}
