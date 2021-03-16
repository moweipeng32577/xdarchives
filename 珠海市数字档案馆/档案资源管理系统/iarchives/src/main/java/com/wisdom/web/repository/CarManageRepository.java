package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_car_manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2020/4/17.
 */
public interface CarManageRepository extends JpaRepository<Tb_car_manage,String>,JpaSpecificationExecutor<Tb_car_manage> {


    Tb_car_manage findByCarnumber(String carnumber);

    Tb_car_manage findById(String id);

    int deleteByIdIn(String[] ids);
}
