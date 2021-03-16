package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Rong on 2019-01-16.
 */

public interface DeviceWarningRepository extends JpaRepository<DeviceWarning, String> {

    @Query(value = "select dw.warningType,count(dw.warningType) from DeviceWarning dw where dw.status = 1 or dw.status =2 group by dw.warningType")
    List DeviceWarningCount();

    List<DeviceWarning> findAllByStatusInOrderByCreateTimeDesc(int[] effect);

    Page<DeviceWarning> findAllByDevice_Id(String deviceid, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update lot_device_warning set status = ?1 where warning_id in ?2" ,nativeQuery = true)
    Integer updataStatusByIds(String status,String[] Ids);


    List <DeviceWarning> findAllByWarningType(String type);
    //根据主机名称查询警告消息（主机名称封装在描述中）
    DeviceWarning findDeviceWarningByDescription (String desc);
    //分页查询警告信息
    Page<DeviceWarning> findAllByWarningTypeOrderByCreateTime(String warningType, Pageable pageable);

    @Query(value = "select max(warning_time) from  lot_device_warning  where device_id = ?1" ,nativeQuery = true)
    String findMaxWarningTimeByDeviceId(String deviceid);

}
