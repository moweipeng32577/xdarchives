package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_opendoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2017/12/4 0004.
 */
public interface OpendocRepository extends JpaRepository<Tb_opendoc, Integer>,JpaSpecificationExecutor<Tb_opendoc> {

    @Query(value = "select t from Tb_opendoc t where batchnum in (select msgid from Tb_flows where taskid=?1)")
    Tb_opendoc getOpendoc(String taskid);

    @Query(value = "select t from Tb_opendoc t where batchnum in (select msgid from Tb_flows where taskid=?1)")
    List<Tb_opendoc> getOpendocList(String taskid);

    List<Tb_opendoc> findByNodeidIn(String[] nodeids);
    
    Tb_opendoc findByBatchnum(String batchnum);
    
    @Query(value = "select t from Tb_opendoc t where docid = ?1")
    Tb_opendoc getDocumentInfo(String docid);
}