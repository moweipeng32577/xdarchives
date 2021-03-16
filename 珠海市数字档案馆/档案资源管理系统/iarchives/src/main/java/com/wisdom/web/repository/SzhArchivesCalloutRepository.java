package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_archives_callout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SzhArchivesCalloutRepository extends JpaRepository<Szh_archives_callout,String>,JpaSpecificationExecutor<Szh_archives_callout> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
    List<Szh_archives_callout> findByAssemblycode(String batchcode);
    Szh_archives_callout findByBatchcode(String batchcode);

    @Query(value = "select t.batchcode from Szh_archives_callout t where t.assemblycode in (?1) ")
    String[] getBatchcodes(String[] codes);

    @Modifying
    @Transactional
    @Query(value = "update Szh_archives_callout t set t.assemblycode = (select sa.code from Szh_assembly sa where sa.id= ?2 ) ,t.assembly = ?3 where t.id = ?1")
    Integer updateAssemblycodeBy(String id,String assemblycode,String  assembly);
}
