package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_user_function;
import com.wisdom.web.entity.Tb_user_function_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.method.P;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface UserFunctionSxRepository extends JpaRepository<Tb_user_function_sx, String> {

    /**
     * 根据用户id删除数据
     * @param userids
     * @return
     */
    Integer deleteAllByUseridIn(String[] userids);

    Page<Tb_user_function_sx> findByUseridIn(String[] userids, Pageable pageable);
}
