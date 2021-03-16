package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.ZoneShelveMsg;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 存储单元格数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/21.
 */
public interface ZoneShelvesRepository extends JpaRepository<ZoneShelves, String>{

    /**
     * 查询密集架内所有单元格
     * @param zone  密集架区
     * @return  密集架区内所有单元格集合
     */
    Page<ZoneShelves> findByZone(Zones zone, Pageable pageable);

    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1")
    List<ZoneShelveMsg> findWithZoneid(String zoneid);

    @Query(value = "select * from st_zone_shelves where zoneid = ?1",nativeQuery=true)
    List<ZoneShelves> findByZoneid(String zoneid);

    /**
     * 查询密集架区的所有列
     * @param zone  密集架区信息
     * @return  密集架区内的列集合
     */
    @Query(value = "select distinct shid,col,coldisplay from ZoneShelves where zone = ?1")
    List<ZoneShelves> findColByZone(Zones zone);

    /**
     * 查询密集架列的所有节
     * @param zone  密集架区信息
     * @param col   密集架列
     * @return  密集架列的节集合
     */
    @Query(value = "select distinct shid,section,sectiondisplay from ZoneShelves where zone=?1 and col=?2")
    List<ZoneShelves> findSectionByCol(Zones zone, String col);

    /**
     * 查询密集架节的所有层
     * @param zone       密集架区信息
     * @param col        密集架列
     * @param section   密集架节
     * @return  密集架节的层集合
     */
    @Query(value = "select distinct shid,layer,layerdisplay from ZoneShelves where zone=?1 and col=?2 and section=?3")
    List<ZoneShelves> findLayerBySection(Zones zone, String col, String section);

    /**
     * 查询密集架层的所有面
     * @param zone       密集架区信息
     * @param col        密集架列
     * @param section    密集架节
     * @param layer      密集架层
     * @return  密集架层的面集合（A面和B面）
     */
    @Query(value = "select distinct shid,side,sidedisplay from ZoneShelves where zone=?1 and col=?2 and section=?3 and layer=?4")
    List<ZoneShelves> findSideByLayer(Zones zone, String col, String section, String layer);

    /**
     * 根据主键批量删除
     * @param shids 主键ID数组
     * @return  删除的数量
     */
    Integer deleteAllByShidIn(String[] shids);

    /**
     * 按已有zoneid查询列数
     * @return  库房集合
     */
    //@Query(value = "select * from st_zone_shelves where zoneid=?1 and layer=?2 order by col,sidedisplay,section",nativeQuery=true)
    //List<ZoneShelves> getCols(String zoneid,String layer);
    @Query(value = "select new com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and layer=?2 order by col,sidedisplay,section")
    List<ZoneShelveMsg> getCols(String zoneid, String layer);



    /**
     * 按已有zoneid查询shelves
     * @return  库房集合
     */
    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and layer=?2 order by col,layer,sidedisplay,section")
    List<ZoneShelveMsg> getShelves(String zoneid, String layer);

    /**
     * 按已有zoneid查询shelves
     * @return  库房集合
     */
    @Query(value = "select * from st_zone_shelves where zoneid=?1 and col=?2 and section=?3 and layer=?4 and sidedisplay=?5 ",nativeQuery=true)
    ZoneShelves findByDisplay(String zoneid,String col,String section,String layer,String sidedisplay);

    /**
     * 查找有库存的zoneshelves
     * @param zoneid
     * @return
     */
    @Query(value = "select * from st_zone_shelves where zoneid=?1 and usecapacity>0",nativeQuery=true)
    List<ZoneShelves> findShelvesHasCapa(String zoneid);

    /**
     * 删除指定zoneid的zoneshelves
     * @param zoneid
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "delete from st_zone_shelves where zoneid=?1",nativeQuery=true)
    Integer delZoneShelves(String zoneid);

    @Query(value = "select zs.coldisplay,sum(zs.usecapacity) as usecapacity,sum(zs.capacity)as capacity from ZoneShelves  zs where zs.zone.zoneid = ?1 group by zs.coldisplay")
    List<Object> findByZoneidGroupBy(String zoneid);

    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and coldisplay = ?3 and layer=?2 order by col,sidedisplay,section")
    List<ZoneShelveMsg> getCols(String zoneid, String layer, String coldisplay);

    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and layer=?2 and coldisplay = ?3 order by col,layer,sidedisplay,section")
    List<ZoneShelveMsg> getShelves(String zoneid, String lay, String coldisplay);

    @Query(value = "SELECT s FROM ZoneShelves s WHERE s.coldisplay = ?1 and s.sectiondisplay = ?2 and " +
            "s.layerdisplay = ?3 and s.sidedisplay = ?4 and s.zone.zoneid = ?5")
    ZoneShelves findZoneSheleves(String column, String section, String layer, String side, String zoneId);

    ZoneShelves findByShid(String shid);

    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and coldisplay = ?3 and layer=?2 and sidedisplay=?4 order by col,sidedisplay,section")
    List<ZoneShelveMsg> getCols(String zoneid, String layer, String column,String side);

    @Query(value = "select new  com.xdtech.component.storeroom.entity.ZoneShelveMsg (shid,col,coldisplay,section,sectiondisplay,layer,layerdisplay,side,sidedisplay,capacity,usecapacity)  from ZoneShelves where zoneid=?1 and layer=?2 and coldisplay = ?3 and sidedisplay=?4 order by col,layer,sidedisplay,section")
    List<ZoneShelveMsg> getShelves(String zoneid, String lay, String column,String side);

    @Query(value = "select max(z.zonedisplay) zonedisplay,zs.col,max(zs.coldisplay) coldisplay,sum(zs.capacity) capacity,sum(usecapacity) usecapacity from st_zone_shelves zs " +
            "left join st_zones z on zs.zoneid = z.zoneid  where zs.zoneid = :zoneid group by zs.col",nativeQuery = true)
    List findColumnsByZone( @Param("zoneid") String zoneid);

}