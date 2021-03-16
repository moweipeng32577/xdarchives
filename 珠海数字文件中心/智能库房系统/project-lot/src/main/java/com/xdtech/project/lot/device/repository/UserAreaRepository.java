package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.Tb_user_area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserAreaRepository extends JpaRepository<Tb_user_area,String> {

    @Modifying
    @Transactional
    void deleteAllByUseridIn(String[] userArr);

    List<Tb_user_area> findByUserid(String userId);
}
