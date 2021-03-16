package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_check_user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2018/11/30.
 */
public interface SzhCheckUserRepository extends JpaRepository<Szh_check_user,String>{

    List<Szh_check_user> findByCheckgroupid(String checkgroupid);

    int deleteByCheckuseridIn(String[] checkuserids);

    int deleteByCheckgroupid(String checkgroupid);

    int deleteByCheckgroupidIn(String[] checkgroupids);

    List<Szh_check_user> findByUserid(String userid);
}
