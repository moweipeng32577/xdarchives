package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_assembly_user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/7/3.
 */
public interface SzhAssemblyUserRepository extends JpaRepository<Szh_assembly_user, String> {

    int deleteByAssemblyidAndAssemblyflowid(String assemblyid,String assemblyflowid);

    @Query(value = "select t.assemblyflowid from Szh_assembly_user t where t.assemblyid = ?1 and t.userid = ?2")
    List<String> getAssemblyflowidByUserid(String assemblyid, String userid);

    @Query(value = "select t.userid from Szh_assembly_user t where t.assemblyid = ?1")
    List<String> getByUseridByAssemblyid(String assemblyid);

    List<Szh_assembly_user> findByUserid(String userid);
}
