package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yl on 2017/10/31.
 */
public interface BillRepository extends JpaRepository<Tb_bill, String>,
        JpaSpecificationExecutor<Tb_bill> {
        	
    Page<Tb_bill> findByState(Pageable pageable, String state);

    List<Tb_bill> findByBillidIn(String[] billid);
    
    List<Tb_bill> findByNodeidIn(String[] nodeid);

    Integer deleteAllByBillidIn(String[] billids);

    @Modifying
    @Query(value = "update Tb_bill set state = ?1 where billid = ?2")
    int updateBillByBillid(String state,String billid);
    
    @Modifying
    @Query(value = "update Tb_bill set state = ?1, approvetext = ?2, approveman = ?3  where billid = ?4")
    int updateStateByBillid(String state, String approvetext, String approveman, String billid);

    @Modifying
    @Query(value = "update Tb_bill set approvetext = ?1, approveman = ?2  where billid = ?3")
    int updateByBillid(String approvetext, String approveman, String billid);

    @Query(value = "SELECT b from Tb_bill b where b.state NOT IN ('3','4','5') and b.billid IN " +
            "(select billid from Tb_bill_entry where entryid=?1)")
    List<Tb_bill> getBothBillTitle(String entryId);

    List<Tb_bill> findByState(String staStr);

}