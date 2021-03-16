package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.OutWare;
import com.xdtech.component.storeroom.entity.OutWare_History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 出库数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
public interface OutWareHistoryRepository extends JpaRepository<OutWare_History, String> {


    /**
     * 检索出库记录
     * @return  出库记录分页结果
     */
    @Query(value = "select distinct so.* from st_outware so inner join  st_outware_storages  sos on so.outid=sos.outwares_outid inner join  st_storage ss on sos.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry order by waretime desc", nativeQuery=true)
    List<OutWare_History> findAll();

    /**
     * 按权限出库记录
     * @param pageable  分页信息
     * @return  出库记录分页结果
     */
    @Query(value = "select distinct so.* from st_outware so inner join  st_outware_storages  sos on so.outid=sos.outwares_outid inner join  st_storage ss on sos.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') and tei.organ not like concat('%',?2) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<OutWare_History> findAllByJuPermision(String code, String condition, Pageable pageable);

    /**
     * 按权限出库记录
     * @param pageable  分页信息
     * @return  出库记录分页结果
     */
    @Query(value = "select distinct so.* from st_outware so inner join  st_outware_storages  sos on so.outid=sos.outwares_outid inner join  st_storage ss on sos.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<OutWare_History> findAllByPermision(String code, Pageable pageable);

    /**
     * 按档号搜索出库记录
     * @param dhCode
     * @return
     */
    @Query(value = "select * from st_outware  where outid in(select outwares_outid from  st_outware_storages where storages_stid in (select stid from st_storage where  entry in (select entryid from tb_entry_index where archivecode=?1)))", nativeQuery=true)
    List<OutWare_History> findByArchivecode(String dhCode);

    /**
     * 按Entryudid搜索出库记录
     * @param entryid
     * @return
     */
    @Query(value = "select * from st_outware  where outid in(select outwares_outid from  st_outware_storages where storages_stid in (select stid from st_storage where  entry =?1))", nativeQuery=true)
    List<OutWare_History> findByEntryid(String entryid);

}
