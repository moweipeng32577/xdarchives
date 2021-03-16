package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_assembly_preflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/7/3.
 */
public interface SzhAssemblyPreflowRepository extends JpaRepository<Szh_assembly_preflow, String> {

    @Query(value = "select t.preflowid from Szh_assembly_preflow t where t.assemblyid =?1 and  t.assemblyflowid =?2")
    List<String> getPreflowid(String assemblyid, String assemblyflowid);

    Szh_assembly_preflow findByAssemblyidAndAssemblyflowid(String assemblyid,String flowsid);

    List<Szh_assembly_preflow> findByAssemblyidAndPreflowid(String assemblyid,String flowsid);

    int deleteByAssemblyidAndAssemblyflowid(String assemblyid,String assemblyflowid);

    int deleteByAssemblyid(String assemblyid);
}
