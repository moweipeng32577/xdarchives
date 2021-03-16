package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_place_manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Administrator on 2020/4/20.
 */
public interface PlaceManageRepository extends JpaRepository<Tb_place_manage,String>,JpaSpecificationExecutor<Tb_place_manage> {

    Tb_place_manage findById(String id);

    int deleteByIdIn(String[] ids);

    @Query("select t.placedesc from Tb_place_manage t where t.id=?1")
    String findPlacedescById(String placeid);
}
