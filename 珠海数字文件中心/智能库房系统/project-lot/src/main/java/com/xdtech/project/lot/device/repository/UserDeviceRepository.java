package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.Tb_user_device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDeviceRepository extends JpaRepository<Tb_user_device,String> {

    /**
     * 根据用户id删除所有权限
     * @param userArr
     * @return
     */
    @Modifying
    @Transactional
    Integer deleteAllByUseridIn(String[] userArr);

    List<Tb_user_device> findByUserid(String userId);
}
