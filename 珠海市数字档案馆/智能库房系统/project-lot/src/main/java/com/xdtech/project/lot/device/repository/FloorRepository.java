package com.xdtech.project.lot.device.repository;

import com.xdtech.project.lot.device.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

/**
 * Created by wujy on 2019-09-04
 */

public interface FloorRepository extends JpaRepository<Floor, String> {

    @Modifying
    @Transactional
    Integer deleteByFlooridIn(String[] ids);

}
