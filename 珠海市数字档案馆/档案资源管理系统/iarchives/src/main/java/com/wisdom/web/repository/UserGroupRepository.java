package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role;
import com.wisdom.web.entity.Tb_user_group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface UserGroupRepository extends JpaRepository<Tb_user_group, String> {

    Integer deleteAllByUseridIn(String[] userids);

    @Transactional
    Integer deleteAllByUseridNotIn(String[] userids);
}
