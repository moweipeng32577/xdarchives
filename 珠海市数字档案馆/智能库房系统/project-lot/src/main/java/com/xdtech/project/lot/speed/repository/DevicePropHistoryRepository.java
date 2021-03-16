package com.xdtech.project.lot.speed.repository;

import com.xdtech.project.lot.speed.entity.TbDevicePropHistories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 设备运行历史记录Repository
 * Created by Rong on 2019-11-15.
 */
public interface DevicePropHistoryRepository extends JpaRepository<TbDevicePropHistories, String>, JpaSpecificationExecutor<TbDevicePropHistories> {


    //查询温湿度分别的id
    @Query(value = "select distinct prop_id from tb_device_prop_histories  where  device_id = ?1",nativeQuery =true)
    String [] propId(String deviceid);


    //查询温湿度分别的id
    @Query(value = "select top(150) * from tb_device_prop_histories  where  device_id = ?1 and prop_id = ?2 order by create_time desc",nativeQuery =true)
    List<TbDevicePropHistories> findDeviceHistoriesByDeviceId(String deviceid, String propId);

}
