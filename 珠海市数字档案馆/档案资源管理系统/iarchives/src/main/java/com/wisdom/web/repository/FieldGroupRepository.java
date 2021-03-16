package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_field_group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldGroupRepository extends JpaRepository<Tb_field_group, String>, JpaSpecificationExecutor<Tb_field_group> {

    @Modifying
    @Query(value = "delete from Tb_field_group where groupid = ?1")
    int deleteByGroupid(String id);

    @Query(value = "select t from Tb_field_group t")
    List<Tb_field_group> findAllGroup();
}
