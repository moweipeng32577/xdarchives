package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tanly on 2018/4/11 0011.
 */
public interface OrgUnitRepository extends JpaRepository<OrgUnit, String>{
    List<OrgUnit> findByIdParentorgunit(String id_parentorgunit);

    List<OrgUnit> findByIdParentorgunitIsNull();
}
