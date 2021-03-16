package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic;
import com.wisdom.web.entity.Tb_electronic_access;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Zdw on 2020/05/20 0001.
 */
public interface ElectronicAccessRepository extends JpaRepository<Tb_electronic_access,String>,JpaSpecificationExecutor<Tb_electronic_access> {

    Tb_electronic_access findByEleid(String eleid);

    List<Tb_electronic_access> findByEleidInOrderBySortsequence(String[] eleids);

    Integer deleteByEleidIn(String[] ids);
}
