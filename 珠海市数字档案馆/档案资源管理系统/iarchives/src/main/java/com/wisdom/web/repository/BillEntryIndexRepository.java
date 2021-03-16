package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_bill_entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by yl on 2017/10/31.
 */
public interface BillEntryIndexRepository extends JpaRepository<Tb_bill_entry,String>,JpaSpecificationExecutor<Tb_bill_entry> {

    Integer deleteAllByBillidIn(String[] billids);

    Page<Tb_bill_entry> findByBillid(Pageable pageable, String billId);

    List<Tb_bill_entry> findByBillidIn(String[] billids);

    List<Tb_bill_entry> findByBillid(String billid);
    
    @Query(value = "select entryid from Tb_bill_entry where billid in (?1)")
    List<String> findEntryidByBillidIn(String[] billid);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_bill_entry set entryretention = ?1 where entryid in (?2) ")
    Integer updateEntryIndex(String entryretention, String[] entryid);

    @Query(value = "select count(b) from Tb_bill_entry b where b.entryid = ?1 and b.billid IN (SELECT billid FROM Tb_bill WHERE state NOT IN ('3','4','5'))")
    int getBothBillEntry(String entryId);

    Page<Tb_bill_entry> findByBillidIn(Pageable pageable, String[] billId);

    Tb_bill_entry findByEntryid(String entryid);
}
