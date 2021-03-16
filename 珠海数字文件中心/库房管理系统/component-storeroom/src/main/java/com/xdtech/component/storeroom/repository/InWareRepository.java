package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.InWare;
import com.xdtech.component.storeroom.entity.InWare_History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 入库数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
@Component
public interface InWareRepository extends JpaRepository<InWare, String> {

    /**
     * 查询最大入库编号
     * @return 最大编号字符串
     */
    @Query(value = "select max(i.warenum) from InWare i")
    String findMaxWarenum();

    /**
     * 通过主键获取入库记录
     * 其中包含了入库记录所关联的实体档案数据
     * @param id  主键ID
     * @return 入库记录对象Inware
     */
    @EntityGraph(attributePaths = "storages")
    InWare findWithStorageByInid(String id);

    /**
     * 根据主键批量删除
     * @param inids 主键ID数组
     * @return 删除的记录数量
     */
    Integer deleteAllByInidIn(String[] inids);

    /**
     * 根据主键获取入库条目ids
     * @param inid 主键ID数组
     */
    @Query(value = "select ss.entry from st_inware si left join st_inware_storages sit on si.inid = sit.inwares_inid left join st_storage ss on sit.storages_stid = ss.stid where si.inid= ?1", nativeQuery=true)
    String[] findEntryIdsByinid(String inid);

//    /**
//     * 检索入库记录
//     * @param pageable  分页信息
//     * @return 入库记录分页结果
//     */
//    Page<InWare> findAll(Pageable pageable);
//
//    /**
//     * 按权限检索入库记录
//     * @param condition
//     * @param code
//     * @param pageable
//     * @return
//     */
//    @Query(value = "select distinct si.* from st_inware si inner join  st_inware_storages  sis on si.inid=sis.inwares_inid inner join  st_storage ss on sis.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') and tei.organ not like concat('%',?2) ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<InWare> findAllByJuPermision(String code,String condition,Pageable pageable);
//
//    /**
//     * 按权限检索入库记录
//     * @param code
//     * @param pageable
//     * @return
//     */
//    @Query(value = "select distinct si.* from st_inware si inner join  st_inware_storages  sis on si.inid=sis.inwares_inid inner join  st_storage ss on sis.storages_stid=ss.stid inner join  tb_entry_index tei on tei.entryid=ss.entry where tei.organ like concat(?1,'%') ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<InWare> findAllByPermision(String code,Pageable pageable);
//
//    /**
//     * 按档号搜索入库记录
//     * @param dhCode
//     * @param pageable
//     * @return
//     */
//    @Query(value = "select * from st_inware  where inid in(select inwares_inid from  st_inware_storages where storages_stid = (select stid from st_storage where  entry =(select entryid from tb_entry_index where archivecode=?1))) ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<InWare> findByArchivecode(String dhCode,Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "delete from st_inware_storages where storages_stid  in ?1",nativeQuery = true)
    Integer deleteAllByStid(String[] stids);
}
