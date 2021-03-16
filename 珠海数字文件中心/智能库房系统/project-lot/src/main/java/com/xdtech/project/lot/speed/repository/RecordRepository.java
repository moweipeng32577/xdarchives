package com.xdtech.project.lot.speed.repository;

import com.xdtech.project.lot.speed.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 设备运行历史记录Repository
 * Created by Rong on 2019-11-15.
 */
public interface RecordRepository extends JpaRepository<Record, Integer>, JpaSpecificationExecutor<Record> {

    /**
     * 构造设备历史运行数据
     * 通过设备编号构造分页数据
     * @param index         设备编号
     * @param pageable      分页对象
     * @return
     */
    Page<Record> findAllBySensorIndexOrderByTimeDesc(Integer index, Pageable pageable);

    List<Record> findAllBySensorIndexInAndTimeAfterAndMessageIsNotNull(Integer[] index, String time);

}
