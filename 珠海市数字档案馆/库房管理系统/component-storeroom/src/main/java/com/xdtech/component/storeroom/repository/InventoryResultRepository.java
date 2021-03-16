package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.Inventory;
import com.xdtech.component.storeroom.entity.InventoryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 盘点结果数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
public interface InventoryResultRepository extends JpaRepository<InventoryResult, String> {

    /**
     * 根据结果类型检索盘点结果数据
     * @param pageable      分页信息
     * @param resulttype    结果类型
     * @return  盘点结果分页记录
     */
    Page<InventoryResult> findAllByResulttype(Pageable pageable, String resulttype);

    /**
     * 根据结果类型检索盘点结果数据
     * @param pageable
     * @param check
     * @param resulttype
     * @return盘点结果分页记录
     */
    //@Query(value = "select chipcode,resulttype,storage from InventoryResult where check=?2 and resulttype=?3 ")
    Page<InventoryResult> findAllByResulttypeAndCheck(Pageable pageable, String resulttype, Inventory check);

    /**
     * 根据结果类型检索盘点结果数据
     * @param resulttype
     * @param check
     * @return盘点结果记录
     */
    List<InventoryResult> findAllByResulttypeAndCheck(String resulttype, Inventory check);

    @Modifying
    @Transactional
    @Query(value = "update st_inventory_result set resulttype =?2 where chipcode in (?1)",nativeQuery=true)
    Integer changeResulttype(String[] chips,String newRt);

    @Modifying
    @Transactional
    @Query(value = "delete from st_inventory_result where storageid in ?1",nativeQuery = true)
    Integer deleteAllByStid(String[] stids);
}
