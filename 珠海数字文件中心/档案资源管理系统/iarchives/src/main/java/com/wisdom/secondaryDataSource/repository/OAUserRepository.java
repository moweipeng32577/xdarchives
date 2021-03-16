package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.OAUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2018/4/17 0017.
 */
public interface OAUserRepository extends JpaRepository<OAUser, String> {
    @Query(value = "select * from t_user where id_orgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where name=?1))))", nativeQuery = true)
    List<OAUser> getOAUserByParent(String branch);

    @Query(value = "select * from t_user where id_user not in (" +
            "select id_user from t_user where id_orgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where id_parentorgunit in (" +
            "select id_orgunit from t_orgunit where name=?1)))))", nativeQuery = true)
    List<OAUser> getOAUserByParentOther(String branch);

    @Query(value = "select code from t_orgunit where id_orgunit=?1", nativeQuery = true)
    String findCodeByIdOrgunit(String idOrgunit);

    @Query(value = "select code from t_orgunit where id_orgunit=(" +
            "select id_parentorgunit from t_orgunit where id_orgunit=?1)", nativeQuery = true)
    String findParentCodeByUnitid(String idOrgunit);
}
