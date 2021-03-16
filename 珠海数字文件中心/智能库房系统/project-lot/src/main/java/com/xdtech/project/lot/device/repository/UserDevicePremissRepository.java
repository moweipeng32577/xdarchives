package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.Tb_user_devicepremiss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2020/3/6.
 */
public interface UserDevicePremissRepository extends JpaRepository<Tb_user_devicepremiss, String> {


    List<Tb_user_devicepremiss> findByUseridIn(String[] userids);

    @Modifying
    @Transactional
    int deleteByUseridIn(String[] userids);

    Tb_user_devicepremiss findByUseridAndAndDeviceid(String userid,String deviceid);
}
