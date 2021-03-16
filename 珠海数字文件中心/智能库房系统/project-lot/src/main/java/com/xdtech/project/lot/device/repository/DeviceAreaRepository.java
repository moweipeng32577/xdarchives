package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.DeviceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Rong on 2019-01-16.
 */
public interface DeviceAreaRepository extends JpaRepository<DeviceArea, String> {


    @Query(value = "select da from DeviceArea  da left join da.floor where da.floor.floorid = ?1 and da.type = ?2")
    List<DeviceArea> findByFlooridAndType(String floorid, String type);

    List<DeviceArea> findByIdIn(String[] rooms);

    DeviceArea findById(String room);

    DeviceArea findByName(String name);

    @Modifying
    @Transactional
    Integer deleteByIdIn(String[] ids);

    @Modifying
    @Transactional
    @Query(value = "update  lot_device  set area = ?1 where id in ?2",nativeQuery = true)
    Integer updateAreaByCheckedDeviceIn(String areaId,String[] deviceId);

    @Modifying
    @Transactional
    @Query(value = "update  lot_device  set area = null where id in ?1",nativeQuery = true)
    Integer updateAreaByCheckDeviceIn( String[] deviceId);

    @Query(value = "select da from DeviceArea  da left join da.floor where da.floor.floorid = ?1 order by da.name")
    List<DeviceArea> findByFloorid(String floorid);

    @Query(value = "select da from DeviceArea  da left join da.floor where da.floor.floorid in ?1 order by da.name")
    List<DeviceArea> findByFloorids(String[] floorids);

    DeviceArea findByIdOrderByName(String room);

    @Query(value = "select da from DeviceArea  da left join da.floor where da.floor.floorid = ?1 and da.id in (select areaid from Tb_user_area where userid = ?2) order by da.name")
    List<DeviceArea> findByFlooridOnUser(String floorid,String userid);

    @Query(value = "select da from DeviceArea  da where da.id in (select areaid from Tb_user_area where userid = ?1) order by da.name")
    List<DeviceArea> findByUser(String userid);
}
