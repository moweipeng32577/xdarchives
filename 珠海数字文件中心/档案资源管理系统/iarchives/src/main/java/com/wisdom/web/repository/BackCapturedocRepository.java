package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_backcapturedoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2019/10/30.
 */
public interface BackCapturedocRepository extends JpaRepository<Tb_backcapturedoc, String>,JpaSpecificationExecutor<Tb_backcapturedoc> {

}
