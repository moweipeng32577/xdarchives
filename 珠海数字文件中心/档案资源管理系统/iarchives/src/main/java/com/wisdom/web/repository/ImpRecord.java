package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_imp_record;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ImpRecord extends JpaRepository<Tb_imp_record, String>, JpaSpecificationExecutor<Tb_imp_record> {

List<Tb_imp_record> findByImptype(String imptype);

Page<Tb_imp_record> findByImptype(String imptype,Pageable pageable);
}
