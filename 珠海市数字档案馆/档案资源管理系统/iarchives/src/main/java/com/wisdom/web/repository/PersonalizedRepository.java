package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_Personalized;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface PersonalizedRepository extends JpaRepository<Tb_Personalized, String> {

    int deleteByUserid(String userid);

    Tb_Personalized findByUserid(String userid);

    Integer deleteByUseridIn(String[] userid);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userid);
}
