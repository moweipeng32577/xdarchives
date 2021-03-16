package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_ca;
import com.wisdom.web.entity.Tb_ca_transfor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zengdw on 2020/06/13 .
 */
public interface CaRepository extends JpaRepository<Tb_ca, String>,JpaSpecificationExecutor<Tb_ca> {

    List<Tb_ca> findByCaid(String caid);

    @Query(value = "select t.signcode from Tb_ca t where caid = ?1")
    String findSigncodeByCaid(String caid);
}