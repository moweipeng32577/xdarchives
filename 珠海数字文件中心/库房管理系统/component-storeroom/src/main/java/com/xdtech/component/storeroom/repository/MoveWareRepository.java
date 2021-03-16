package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.MoveWare;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 移库数据仓库
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/24.
 */
public interface MoveWareRepository extends JpaRepository<MoveWare, String> {

    /**
     * 查询最大移库编号
     * @return 最大编号字符串
     */
    @Query(value = "select max(i.warenum) from MoveWare i")
    String findMaxWarenum();

    /**
     * 通过主键获取移库记录
     * 其中包含了移库记录所关联的实体档案数据
     * @param id  主键ID
     * @return  移库对象
     */
    @EntityGraph(attributePaths = "storages")
    MoveWare findWithStorageByMoveid(String id);;

}