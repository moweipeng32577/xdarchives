package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_equipment_defend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

/**
 * Created by Leo on 2020/7/23 0023.
 */
public interface EquipmentDefendRepository extends JpaRepository<Tb_equipment_defend,String>, JpaSpecificationExecutor<Tb_equipment_defend> {

    Tb_equipment_defend findAllById(String id);

    int deleteByIdIn(String[] id);
}
