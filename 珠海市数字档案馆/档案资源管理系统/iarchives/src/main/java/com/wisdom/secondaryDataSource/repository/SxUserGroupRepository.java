package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_user_group_sx;
import com.wisdom.web.entity.Tb_user_group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface SxUserGroupRepository extends JpaRepository<Tb_user_group_sx, String> {

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_user_group where userid in (?1)",nativeQuery = true)
    Integer deleteAllByUseridIn(String[] userids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteAllByUseridNotIn(String[] userids);
}
