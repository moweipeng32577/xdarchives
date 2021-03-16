package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 实体档案盘点数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    /**
     * 查询最大入库编号
     * @return 最大编号字符串
     */
    @Query(value = "select max(i.checknum) from Inventory i")
    String findMaxWarenum();

    /**
     * 检索盘点记录
     * @param pageable  分页信息
     * @return  盘点记录分页结果
     */
    Page<Inventory> findAllByOrderByChecktimeDesc(Pageable pageable);

    @Query(value = "select sit.* from st_inventory sit where shelvesid in (select shid from st_zone_shelves where zoneid in(select zoneid from st_zones where unit=?1)) ORDER BY ?#{#pageable}", nativeQuery=true)
    Page<Inventory> findAllByUnitOrderByChecktimeDesc(Pageable pageable,String unitcode);
}
