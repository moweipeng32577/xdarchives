package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_assembly_node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/2/26.
 */
public interface SzhAssemblyNodeRepository extends JpaRepository<Szh_assembly_node, String> {


    @Query(value = "select t.nodeid from Szh_assembly_node t where t.assemblyid = ?1 order by t.sorting ASC ")
    List<String> getNodeByAssemblyid(String assemblyid);

    int deleteByAssemblyid(String assemblyid);

    List<Szh_assembly_node> findByAssemblyid(String assemblyid);

    Szh_assembly_node findByAssemblyidAndNodeid(String assemblyid, String assemblyflowid);

    List<Szh_assembly_node> findByAssemblyidOrderBySorting(String assemblyid);
}
