package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.Device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by Rong on 2019-01-16.
 */
public interface DeviceRepository extends JpaRepository<Device, String> {

    @Modifying
    @Transactional
    Integer deleteByIdIn(String[] ids);

    Device findByTypeAndCode(String type, String code);

    @Query(value = "select de from Device de left join fetch de.type where de.type.typeCode= ?1")
    List<Device> findByType(String type);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id= ?1")
    List<Device> findByArea(String id);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id= ?1 or de.area.id = '' or de.area.id is null")
    List<Device> findByAreaOrNull(String id);

    Device findById(String id);

    @Query(value = "select de from Device de left join fetch de.area where de.area.name= ?1")
    List<Device> findByAreaName(String name);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id = ?2 and de.type.typeCode = ?1")
    List<Device> findByTypeAndArea(String mjj, String area);

    @Query(value = "from Device de left join fetch de.area where de.area.id = ?1 and de.type.typeCode = ?2")
    List<Device> findByAreaAndType(String areaid, String type);

    @Query(value = "select de.type,count(de.type),max(de.typeName) from Device de where de.status = '1' group by de.type ")
    List<Object> findDeviceOnStatus();

    @Query(value = "select de.type,count(de.type),max(de.typeName) from Device de where de.status = '0' group by de.type ")
    List<Object> findDeviceOffStatus();

    @Modifying
    @Transactional
    @Query(value = "update  lot_device set enabled = '0' where id in ?1",nativeQuery = true)
    Integer saveDeviceJoinAuthority(String[] deviceIds);

    @Query(value = "select * from lot_device where type = ?1 limit 1",nativeQuery = true)
    Device findOneByType(String deviceType);

    @Query(value = "select * from lot_device where id = ?1 limit 1",nativeQuery = true)
    Device findOneById(String deviceId);

    @Query(value = "select * from lot_device where type = ?1 and sort =?2 limit 1",nativeQuery = true)
    Device findOneByTypeAndSort(String deviceType,String sort);

    @Query(value = "select max(sort+0) as maxSort  from lot_device where type = ?1",nativeQuery = true)
    String findMsxSort(String deviceType);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id= ?1 and de.enabled = '0'")
    List<Device> findByAreaAndEnabled(String id);

    @Query(value = "select de from Device de  where de.enabled = '0'")
    List<Device> findByEnabled();

    @Query(value = "select de from Device de  where de.id in (select deviceid from Tb_user_device where userid = ?1)")
    List<Device> findByUserdevice(String userid);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id= ?1 and de.id in (select deviceid from Tb_user_device where userid = ?2)")
    List<Device> findByAreaAndUserdevice(String id,String userid);

    @Query(value = "select de from Device de left join fetch de.area where de.area.id= ?1 and de.enabled = '0' and de.id in (select deviceid from Tb_user_device where userid = ?2)")
    List<Device> findByAreaAndEnabledAndUserdevice(String id,String userid);

    @Query(value = "select de from Device de  where de.enabled = '0' and de.id in (select deviceid from Tb_user_device where userid = ?1)")
    List<Device> findByEnabledAndUserdevice(String userid);

    //具有用户权限byFloor
    @Query("select de from Device de  where  de.id in (select deviceid from Tb_user_device where userid = ?1) and area.floor.floorCode =?2 order by de.type,de.name")
    List<Device> findByUserdeviceByFloor(String var1,String var2);

    //具有用户权限的视频监控
    @Query(value = "select de from Device de  where de.type.typeCode =?1 and  de.id in (select deviceid from Tb_user_device where userid = ?2) order by de.type,de.name")
    List<Device> findByUserAndType(String type,String userid);
}
