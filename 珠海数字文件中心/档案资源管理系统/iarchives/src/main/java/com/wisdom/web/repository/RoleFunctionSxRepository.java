package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_function;
import com.wisdom.web.entity.Tb_role_function_sx;
import com.wisdom.web.entity.Tb_user_function_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RoleFunctionSxRepository extends JpaRepository<Tb_role_function_sx, Integer> {

    Integer deleteAllByRoleidIn(String[] roleids);

    List<Tb_role_function_sx> findByRoleidIn(String[] roleids);

    Page<Tb_role_function_sx> findByRoleidIn(String[] roleids, Pageable pageable);

}
