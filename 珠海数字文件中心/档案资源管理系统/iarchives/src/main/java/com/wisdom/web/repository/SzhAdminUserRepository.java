package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_admin_user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2019/7/3.
 */
public interface SzhAdminUserRepository extends JpaRepository<Szh_admin_user,String> {

    int deleteByUseridIn(String[] userids);

    List<Szh_admin_user> findByUserid(String userid);
}
