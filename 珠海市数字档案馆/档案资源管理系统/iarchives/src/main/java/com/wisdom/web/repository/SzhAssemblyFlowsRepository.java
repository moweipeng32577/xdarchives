package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_assembly_flows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SzhAssemblyFlowsRepository extends JpaRepository<Szh_assembly_flows, String>{

    @Query(value = "select t from Szh_assembly_flows t where t.id in ( select nodeid from Szh_assembly_node where assemblyid =?1) order by sorting ASC")
    List<Szh_assembly_flows> getFlowsByassemblyid(String assemblyid);

    List<Szh_assembly_flows> findByIdInOrderBySorting(String[] ids);

    @Query(value = "select t from Szh_assembly_flows t order by t.sorting ASC ")
    List<Szh_assembly_flows> getAllFlows();

    Szh_assembly_flows findById(String id);

    @Query(value = "select t from Szh_assembly_flows t where t.id in ( select assemblyflowid from Szh_assembly_preflow where assemblyid =?1)  order by t.sorting ASC")
    List<Szh_assembly_flows> getFlowsByassemblyid1(String assemblyid);

    @Query("select t from Szh_assembly_flows t where t.id in(select assemblyflowid from Szh_assembly_preflow where assemblyid=?1 and preflowid=?2) order by t.sorting")
    List<Szh_assembly_flows> findFlowsByAssemblyid(String assemblyid,String preflowid);

    @Query("select t from Szh_assembly_flows t where t.id in(select preflowid from Szh_assembly_preflow where assemblyid=?1 and assemblyflowid=?2)")
    List<Szh_assembly_flows> findFlowsByAssemblyidAndFlowid(String assemblyid,String assemblyflowid);

    @Query(value = "select t from Szh_assembly_flows t where t.id in ( select nodeid from Szh_assembly_node where assemblyid =?1  order by sorting ASC) and nodename !='完成环节'")
    List<Szh_assembly_flows> getFlowsByassemblyidnew(String assemblyid);

    Szh_assembly_flows findByNodename(String nodename);

    List<Szh_assembly_flows> findByIdIn(String[] ids);
}
