package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Tb_equipment,String>, JpaSpecificationExecutor<Tb_equipment> {

    @Modifying
    @Query(value = "delete from tb_equipment where equipmentID in (?1)",nativeQuery=true)
    Integer deleteByEquipmentID(String[] equipmentID);

    List<Tb_equipment> findByEquipmentIDIn(String[] ids);
}
