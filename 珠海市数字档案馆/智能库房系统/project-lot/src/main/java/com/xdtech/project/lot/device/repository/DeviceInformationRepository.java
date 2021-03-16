package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface DeviceInformationRepository extends JpaRepository<DeviceInformation, String>, JpaSpecificationExecutor<DeviceInformation> {


    @Query(value = "select t from DeviceInformation t where inforid in (?1)")
    List<DeviceInformation> findByInfroIdIn(String[] id);

    @Modifying
    @Transactional
    Integer deleteByInforidIn(String[] ids);

}
