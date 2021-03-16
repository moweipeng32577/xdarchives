package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.OutWare;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import jdk.nashorn.internal.runtime.ListAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 存储区数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/5/5.
 */
public interface ZonesRepository extends JpaRepository<Zones, String> {


    /**
     * 查询所有库房
     * @return  库房集合
     */
    @Query(value = "select city,citydisplay,unit,unitdisplay,room,roomdisplay from Zones order by room")
    List<Zones> findAllRooms();

    /**
     * 查询库房内的所有密集架区
     * @param city  地区编号
     * @param unit  单位编号
     * @param room  库房编号
     * @return  库房内密集架区集合
     */
    List<Zones> findAllByCityAndUnitAndRoomOrderByZone(String city, String unit, String room);

    /**
     * 查询所有库房城市
     * @return  库房集合
     */
    @Query(value = "select distinct citydisplay from Zones where city=?1")
    List<String> findCity(String funds);

    /**
     * 查询所有库房城市
     * @return  库房集合
     */
    @Query(value = "select distinct citydisplay from Zones")
    List<String> findCitys();

    /**
     * 查询所有城区
     * @return  城区名称集合
     */
    @Query(value = "select distinct citydisplay from Zones")
    List<String> findUnitByCityDistinct();

    /**
     * 查询所有库房城市
     * @return  库房集合
     */
    @Query(value = "select distinct unitdisplay from Zones where citydisplay=?1 and unit=?2")
    List<String> findUnitByCity(String citydisplay,String unit);

    /**
     * 查询所有库房城市
     * @return  库房集合
     */
    @Query(value = "select distinct unitdisplay from Zones where citydisplay=?1")
    List<String> findUnitsByCity(String citydisplay);

    /**
     * 按已有地区和单位名查询所有库房
     * @return  库房集合
     */
    @Query(value = "select distinct roomdisplay from Zones where citydisplay=?1 and unitdisplay=?2 order by roomdisplay")
    List<String> findRoomsByUnit(String citydisplay, String unitdisplay);


    /**
     * 按已有地区和单位和库房名查询所有区名和zoinid
     * @return  库房集合
     */
    @Query(value = "select z from Zones z where citydisplay=?1 and unitdisplay=?2 and roomdisplay=?3 order by zoneid")
    List<Zones> findZonesByRoom(String citydisplay, String unitdisplay, String roomdisplay);


    /**
     * 按已有zoneid查询所有列名
     * @return  库房集合
     */
    @Query(value = "select distinct coldisplay from ZoneShelves where zoneid=?1  order by coldisplay")
    List<String> findColsByZoneid(String zoneid);

    /**
     * 按已有zoneid查询所有节名
     * @return  库房集合
     */
    @Query(value = "select distinct sectiondisplay from ZoneShelves where zoneid=?1 and coldisplay=?2  order by sectiondisplay")
    List<String> findSectionsByZoneid(String zoneid, String col);

    /**
     * 按已有zoneid查询所有层名
     * @return  库房集合
     */
    @Query(value = "select distinct layerdisplay from ZoneShelves where zoneid=?1 and coldisplay=?2 and sectiondisplay=?3  order by layerdisplay")
    List<String> findLayersByZoneid(String zoneid, String col, String section);

    /**
     * 按已有zoneid查询所有面名
     * @return  库房集合
     */
    @Query(value = "select distinct sidedisplay from ZoneShelves where zoneid=?1 and coldisplay=?2 and sectiondisplay=?3 and layerdisplay=?4 order by sidedisplay")
    List<String> findSidesByZoneid(String zoneid, String col, String section, String layer);

    /**
     * 按已有zoneid查询shid
     * @return  库房集合
     */
    @Query(value = "select shid from ZoneShelves where zoneid=?1 and coldisplay=?2 and sectiondisplay=?3 and layerdisplay=?4 and sidedisplay=?5 order by shid")
    List<String> findShid(String zoneid, String col, String section, String layer,String side);

    /**
     * 按已有zoneid查询layer层数
     * @return  库房集合
     */
    @Query(value = "select distinct layer from ZoneShelves where zoneid=?1 order by layer")
    List<String> getLayers(String zoneid);

    /**
     * 按已有zoneid查询列数
     * @return  库房集合
     */
    @Query(value = "select shid,col,section,sidedisplay from ZoneShelves where zoneid=?1 and layer=?2 order by col,sidedisplay,section")
    List<ZoneShelves> getCols(String zoneid,String layer);


    /**
     * 按已有zoneid查询shelves
     * @return  库房集合
     */
    @Query(value = "select shid,col,layer,section,sidedisplay,usecapacity from ZoneShelves where zoneid=?1 and layer=?2 order by col,layer,sidedisplay,section")
    List<ZoneShelves> getShelves(String zoneid, String layer);

    /**
     * 删除指定zoneid的zone
     * @param zoneid
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "delete from st_zones where zoneid=?1",nativeQuery=true)
    Integer delZones(String zoneid);

    @Query(value = "select * from st_zones where unit=?1 ", nativeQuery=true)
    List<Zones> findByUnit(String unit);

    @Query(value = "select distinct unitdisplay from st_zones where unit=?1 ", nativeQuery=true)
    List<String> findByUnitDistinct(String unit);

    @Query(value = "select nodeid from tb_data_node where refid in (select classid from tb_classification where classname=?1)", nativeQuery=true)
    List<String> findNodeids(String classname);

    @Query(value = "select nodeid from tb_data_node where nodename=?1 and nodetype='2')", nativeQuery=true)
    List<String> findNodeidsByName(String classname);

    @Query(value = "SELECT DISTINCT(z.citydisplay) FROM Zones z")
    List<String> findZonesDistinct();

    @Query(value = "SELECT z.zoneid FROM  Zones z WHERE z.citydisplay = ?1 and z.unitdisplay = ?2 and z.roomdisplay = ?3 and z.zonedisplay = ?4")
    String findZondId(String city, String unit, String room, String zone);

    @Query(value = "select z.floordisplay,z.roomdisplay from Zones z group by z.floordisplay,z.roomdisplay order by z.floordisplay,z.roomdisplay")
    List<Object> findFloorAndRoom();

    Zones findByFloordisplayAndRoomdisplayAndZonedisplay(String floordisplay, String roomdisplay, String zonedisplay);

    @Query(value = "select * from st_zones where zoneid=?1 ", nativeQuery=true)
    Zones findByZoneid(String zoneid);

    @Query(value = "select * from st_zones where zoneid=(select zoneid from st_zone_shelves where shid=?1) ", nativeQuery=true)
    Zones findByShid(String shid);

    Zones findByDevice(String deviceId);

    /**
     * 按已有地区和单位名查询所有库房
     * @return  库房集合
     */
    @Query(value = "select distinct room from Zones where citydisplay=?1 and unitdisplay=?2")
    String[] findRoomsByUnitString(String citydisplay, String unitdisplay);


    @Query(value = "select max(z.unitdisplay) unitdisplay,z.roomdisplay,sum(zs.capacity) capacity,sum(zs.usecapacity) usecapacity" +
            " from st_zones z left join st_zone_shelves zs on z.zoneid = zs.zoneid group by z.roomdisplay"
            ,nativeQuery = true)
    List findRooms();

    @Query(value = "select max(z.roomdisplay),z.zonedisplay,sum(zs.capacity) capacity,sum(zs.usecapacity) usecapacity,max(z.zoneid) zoneid  , max(device) device " +
            "from st_zones z left join st_zone_shelves zs on z.zoneid = zs.zoneid where z.roomdisplay = :roomDisplay group by z.zonedisplay ",nativeQuery = true)
    List findZonesByRoom(@Param("roomDisplay") String roomDisplay);
}
