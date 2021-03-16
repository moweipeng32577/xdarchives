package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_place_defend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2020/6/24.
 */
public interface PlaceDefendRepository extends JpaRepository<Tb_place_defend,String>,JpaSpecificationExecutor<Tb_place_defend> {


    Tb_place_defend findById(String id);

    int deleteByIdIn(String[] ids);
}
