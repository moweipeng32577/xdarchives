package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Storage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 实体档案数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
public interface StorageRepository extends JpaRepository<Storage, String> {

    /**
     * 根据主键查找实体档案对象
     * 包含其历史入库记录
     * @param id    主键ID
     * @return  实体档案对象
     */
    @EntityGraph(attributePaths = "inwares")
    Storage findWithInWareByStid(String id);

    /**
     * 根据主键查找实体档案对象
     * 包含其历史出库记录
     * @param id    主键ID
     * @return  实体档案对象
     */
    @EntityGraph(attributePaths = "outwares")
    Storage findWithOutWareByStid(String id);

    /**
     * 通过编码查找对应的实体档案
     * @param chipcode  编码
     * @return  实体档案对象
     */
    @Query(value = "select * from st_storage where entry  in (select entryid from tb_entry_index where archivecode=?1)",nativeQuery=true)
    Storage findByChipcode(String chipcode);

    /**
     * 批量查找实体档案对象
     * @param ids   主键ID字符串，多ID用'，'分隔
     * @return  实体档案集合
     */
    List<Storage> findByStidIn(String[] ids);

    /**
     * 修改实体档案存储位置
     * @param target    目的单元格
     * @param source    源单元格
     * @return  修改的数量
     */
    @Modifying
    @Transactional
    @Query(value = "update Storage set zoneShelves = ?1 where zoneShelves = ?2")
    Integer changeShelvesBatch(ZoneShelves target, ZoneShelves source);

    /**
     * 修改实体档案库存状态
     * @param stid      实体档案主键ID
     * @param status    修改的状态
     * @return  修改的数据
     */
    @Modifying
    @Transactional
    @Query(value = "update Storage set storestatus = ?2 where stid = ?1")
    Integer changeStatus(String stid,String status);



    /**
     * 检索库房中的所有实体档案
     * @param room  库房编号
     * @return  实体档案集合
     */
//    List<Storage> findByZoneRoomShelves_Zone(String room);

    /**
     * 检索密集架区中的所有实体档案
     * @param room  库房编号
     * @param zone  密集架区编号
     * @return  实体档案集合
     */
//    List<Storage> findByShelves_RoomAndShelves_Zone(String room, String zone);

    /**
     * 检索密集架列中的所有实体档案
     * @param room  库房编号
     * @param zone  密集架区编号
     * @param col   密集架列编号
     * @return  实体档案集合
     */
//    List<Storage> findByShelves_RoomAndShelves_ZoneAndShelves_Col(String room, String zone, String col);

    /**
     * 通过entry查找对应的实体档案
     * @param entry  编码
     * @return  实体档案对象
     */
    Storage findByEntry(String entry);

    @Query(value = "select t from  Storage t where entry = ?1")
    List<Storage> getByEntry(String entry);

    List<Storage> findByEntryIn(String[] entry);

    /**
     * 查找指定库房的实体档案
     * @param room
     * @return
     */
    @Query(value = "select * from st_storage where zone_shelves_shid in (select shid from st_zone_shelves where zoneid in (select zoneid from st_zones where citydisplay=?1 and unitdisplay=?2 and roomdisplay=?3))",nativeQuery=true)
    List<Storage> findByShelves_Room(String city,String unit,String room);

    /**
     * 查找库房指定区的实体档案
     * @param zoneid
     * @return
     */
    @Query(value = "select * from st_storage where zone_shelves_shid in (select shid from st_zone_shelves where zoneid=?1)",nativeQuery=true)
    List<Storage>findByShelves_zone(String zoneid);

    /**
     * 查找库房指定区指定列的实体档案
     * @param zoneid
     * @param col
     * @return
     */
    @Query(value = "select * from st_storage where zone_shelves_shid in (select shid from st_zone_shelves where zoneid=?1 and coldisplay=?2)",nativeQuery=true)
    List<Storage>findByShelves_zoneAndCol(String zoneid,String col);

    @Query(value = "select * from st_storage where entry=?1",nativeQuery=true)
    List<Storage> getShelves(String entryid);

    @Modifying
    @Transactional
    @Query(value = "update Storage set zoneShelves = ?1 where zoneShelves = ?2 and entry=?3")
    Integer changeShelves(ZoneShelves target, ZoneShelves source,String entryid);

    @Modifying
    @Transactional
    @Query(value = "update st_storage set storestatus =?2 where chipcode in (?1)",nativeQuery=true)
    Integer changeInventoryStatus(String[] chips,String inStr);

    /**
     * 按档号查storage
     * @param dhCode
     * @return
     */
    @Query(value = "select * from st_storage where  entry in (select entryid from tb_entry_index where archivecode=?1)",nativeQuery=true)
    List<Storage>  findByArchivecode(String dhCode);


    @Query(value = "select archivecode from tb_entry_index where entryid=?1",nativeQuery=true)
    String  findArchivecode(String entryid);

    @Query(value = "select ss.entry from st_storage ss where ss.zone_shelves_shid=?1",nativeQuery=true)
    String[] findByShid(String shid);

    @Query(value = "select entryid from tb_entry_index where entryid in (select ss.entry from st_storage ss where ss.zone_shelves_shid=?1)",nativeQuery=true)
    List<String> findEntryByShid(String shid);

    @Query(value = "select * from st_storage ss where ss.zone_shelves_shid=?1",nativeQuery=true)
    List<Storage> findStoragesByShid(String shid);

    @Query(value = "select ss.entry from st_storage ss where ss.zone_shelves_shid=?1 and storestatus=?2",nativeQuery=true)
    String[] findByShidAndStatus(String shid,String status);

    @Modifying
    @Transactional
    @Query(value = "delete from  Storage s where s.entry in (?1)")
    Integer deleteInEntryid(String[] entryids);

    @Query(value = "select t1.waretime from (SELECT o.waretime,os.storages_stid,o.waretype,s.storestatus FROM st_outware_storages os" +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where " +
            "  storestatus = '已出库'  and zone_shelves_shid = ?1) t1,(SELECT max(o.waretime) waretime,os.storages_stid FROM st_outware_storages os" +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where " +
            "  storestatus = '已出库'  and zone_shelves_shid = ?1 GROUP BY os.storages_stid )t2 where t1.waretime =t2.waretime " +
            " and t1.storages_stid = t2.storages_stid and waretype != '转递出库'",nativeQuery=true)
    List<Object> findStoragesOutByShid(String shid);

    @Modifying
    @Transactional
    @Query(value = "update tb_entry_index set entrystorage=?2 where entryid in (?1)",nativeQuery=true)
    int savePosition(String[] entryids,String savePosition);

    @Modifying
    @Transactional
    @Query(value = "update tb_entry_index set entrystorage='' where entryid in (?1)",nativeQuery=true)
    int clearPosition(String[] entryids);

    @Query(value = "select s from Storage s where s.entry = ?1 and s.storestatus = ?2")
    Storage findStorage(String entryid, String storestatus);

    @Query(value = "select ss.entry from st_storage ss where ss.zone_shelves_shid=?1 and storestatus = '已入库' ",nativeQuery=true)
    String[] findByShidInwares(String shid);

    @Query(value = "select stid from st_storage ss where ss.zone_shelves_shid=?1 and storestatus='已出库'",nativeQuery=true)
    List<String> findStoragesOutByShid1(String shid);

    @Modifying
    @Transactional
    @Query(value = "delete from st_storage where stid  in ?1",nativeQuery = true)
    Integer deleteAllByStid(String[] stids);

}
