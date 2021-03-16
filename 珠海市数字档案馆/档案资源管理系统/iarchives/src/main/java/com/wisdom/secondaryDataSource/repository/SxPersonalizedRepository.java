package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_Personalized_sx;
import com.wisdom.web.entity.Tb_Personalized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxPersonalizedRepository extends JpaRepository<Tb_Personalized_sx, String> {

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    int deleteByUserid(String userid);

    Tb_Personalized_sx findByUserid(String userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_personalized  where userid in (?1)",nativeQuery = true)
    Integer deleteByUseridIn(String[] userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userid);
}
