package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.OutWare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 出库数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
public interface OutWareRepository extends JpaRepository<OutWare, String> {

    /**
     * 查询最大出库编号
     * @return 最大编号字符串
     */
    @Query(value = "select max(i.warenum) from OutWare i")
    String findMaxWarenum();

    /**
     * 通过主键获取出库记录
     * 其中包含了出库记录所关联的实体档案数据
     * @param id  主键ID
     * @return  出库对象
     */
    @EntityGraph(attributePaths = "storages")
    OutWare findWithStorageByOutid(String id);


    /**
     * 根据主键批量删除
     * @param outids    主键ID数组
     * @return  删除的记录数量
     */
    Integer deleteAllByOutidIn(String[] outids);

//    /**
//     * 检索出库记录
//     * @param pageable  分页信息
//     * @return  出库记录分页结果
//     */
//    Page<OutWare> findAll(Pageable pageable);
//
//    /**
//     * 按权限出库记录
//     * @param pageable  分页信息
//     * @return  出库记录分页结果
//     */
//    @Query(value = "select distinct so.* from st_outware so inner join  st_outware_storages  sos on so.outid=sos.outwares_outid inner join  st_storage ss on sos.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') and tei.organ not like concat('%',?2) ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<OutWare> findAllByJuPermision(String code,String condition,Pageable pageable);
//
//    /**
//     * 按权限出库记录
//     * @param pageable  分页信息
//     * @return  出库记录分页结果
//     */
//    @Query(value = "select distinct so.* from st_outware so inner join  st_outware_storages  sos on so.outid=sos.outwares_outid inner join  st_storage ss on sos.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<OutWare> findAllByPermision(String code,Pageable pageable);
//
//    /**
//     * 按档号搜索出库记录
//     * @param dhCode
//     * @return
//     */
//    @Query(value = "select * from st_outware  where outid in(select outwares_outid from  st_outware_storages where storages_stid in (select stid from st_storage where  entry in (select entryid from tb_entry_index where archivecode=?1)))", nativeQuery=true)
//    List<OutWare> findByArchivecode(String dhCode);
//
//    /**
//     * 按Entryudid搜索出库记录
//     * @param entryid
//     * @return
//     */
//    @Query(value = "select * from st_outware  where outid in(select outwares_outid from  st_outware_storages where storages_stid in (select stid from st_storage where  entry =?1))", nativeQuery=true)
//    List<OutWare> findByEntryid(String entryid);

    /**
     * 根据主键获取出库条目ids
     * @param outid 主键ID数组
     */
    @Query(value = "select ss.entry from st_outware so left join st_outware_storages sot on so.outid = sot.outwares_outid left join st_storage ss on sot.storages_stid = ss.stid where so.outid= ?1", nativeQuery=true)
    String[] findEntryIdsByOutid(String outid);

    @Modifying
    @Transactional
    @Query(value = "delete from st_outware_storages where storages_stid  in ?1",nativeQuery = true)
    Integer deleteAllByStid(String[] stids);

}
