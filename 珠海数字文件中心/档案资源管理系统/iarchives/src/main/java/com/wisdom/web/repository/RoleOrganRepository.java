package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_role_organ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tanly on 2018/4/23 0023.
 */
public interface RoleOrganRepository extends JpaRepository<Tb_role_organ, String> {

    /**
     * 根据用户组id查询用户组机构对应关系
     * @param roleid 权限id
     * @return
     */
    List<Tb_role_organ> findByRoleid(String roleid);

    /**
     * 根据权限id数组批量删除用户组机构权限
     * @param roleids 用户组id
     * @return
     */
    Integer deleteAllByRoleidIn(String[] roleids);
}
