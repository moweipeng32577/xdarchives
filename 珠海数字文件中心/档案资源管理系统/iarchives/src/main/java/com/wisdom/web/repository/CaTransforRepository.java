package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_ca_transfor;
import com.wisdom.web.entity.Tb_transdoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zengdw on 2020/06/13 .
 */
public interface CaTransforRepository extends JpaRepository<Tb_ca_transfor, String>,JpaSpecificationExecutor<Tb_ca_transfor> {


    List<Tb_ca_transfor> findByDocid(String docid);
}