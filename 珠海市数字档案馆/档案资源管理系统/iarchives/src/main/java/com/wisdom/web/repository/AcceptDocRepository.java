package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_acceptdoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/6/17.
 */

public interface AcceptDocRepository extends JpaRepository<Tb_acceptdoc,String>,
        JpaSpecificationExecutor<Tb_acceptdoc>{

    Integer deleteByAcceptdocidIn(String[] acceptdocid);

    @Modifying
    @Query(value = "update Tb_acceptdoc set sterilizing = ?2 where acceptdocid = ?1")
    Integer updateSterilizingByAcceptdocid(String acceptdocid,String docstate);

    @Modifying
    @Query(value = "update Tb_acceptdoc set sterilized = ?2,sterilizing = ?3 where acceptdocid = ?1")
    Integer updateSterilizedByAcceptdocid(String acceptdocid,String sterilized,String sterilizing);

    @Modifying
    @Query(value = "update Tb_acceptdoc set sterilized = ?2,finishstore = ?3 where acceptdocid = ?1")
    Integer updateFinishstoreByAcceptdocid(String acceptdocid,String sterilized,String finishstore);

    Tb_acceptdoc findByAcceptdocid(String acceptdocid);
}
