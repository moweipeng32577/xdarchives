package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_assembly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SzhAssemblyRepository extends JpaRepository<Szh_assembly,String>,JpaSpecificationExecutor<Szh_assembly> {
    Szh_assembly findByCode(String code);

    List<Szh_assembly> findByTitle(String title);

    @Query(value = "select substring(max(t.code),9,4) from Szh_assembly t where substring(t.code,1,8) = ?1 ")
    String getOrderNum(String code8);

    int deleteByCodeIn(String[] codes);
}
