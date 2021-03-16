package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.InWare;
import com.xdtech.component.storeroom.entity.InWare_History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 入库数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
@Component
public interface InWareHistoryRepository extends JpaRepository<InWare_History, String> {

    /**
     * 检索入库记录
     * @return 入库记录分页结果
     */
    @Query(value = "select distinct si.* from st_inware si inner join  st_inware_storages  sis on si.inid=sis.inwares_inid inner join  st_storage ss on sis.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry order by waretime desc", nativeQuery=true)
    List<InWare_History> findAll();

    /**
     * 按权限检索入库记录
     * @param condition
     * @param code
     * @param pageable
     * @return
     */
    @Query(value = "select distinct si.* from st_inware si inner join  st_inware_storages  sis on si.inid=sis.inwares_inid inner join  st_storage ss on sis.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') and tei.organ not like concat('%',?2) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<InWare_History> findAllByJuPermision(String code,String condition,Pageable pageable);

    /**
     * 按权限检索入库记录
     * @param code
     * @param pageable
     * @return
     */
    @Query(value = "select distinct si.* from st_inware si inner join  st_inware_storages  sis on si.inid=sis.inwares_inid inner join  st_storage ss on sis.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<InWare_History> findAllByPermision(String code,Pageable pageable);

    /**
     * 按档号搜索入库记录
     * @param dhCode
     * @return
     */
    @Query(value = "select * from st_inware  where inid in(select inwares_inid from  st_inware_storages where storages_stid = (select stid from st_storage where  entry =(select entryid from tb_entry_index where archivecode=?1)))", nativeQuery=true)
    List<InWare_History> findByArchivecode(String dhCode);

    /**
     * 按Entryudid搜索入库记录
     * @param entryid
     * @return
     */
    @Query(value = "select * from st_inware  where inid in(select inwares_inid from  st_inware_storages where storages_stid in (select stid from st_storage where  entry =?1))", nativeQuery=true)
    List<InWare_History> findByEntryid(String entryid);
}
