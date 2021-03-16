package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_car_defend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2020/6/24.
 */
public interface CarDefendRepository extends JpaRepository<Tb_car_defend,String>,JpaSpecificationExecutor<Tb_car_defend> {


    Tb_car_defend findById(String id);

    int deleteByIdIn(String[] ids);
}
