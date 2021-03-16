package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_bill_approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yl on 2017/12/5.
 */
public interface BillApprovalRepository
		extends JpaRepository<Tb_bill_approval, String>, JpaSpecificationExecutor<Tb_bill_approval> {

	Tb_bill_approval findByTaskidContains(String taskid);

	Tb_bill_approval findByBillidContains(String billid);

	List<Tb_bill_approval> findByBillidIn(String[] billids);

	Integer deleteByBillidIn(String[] billids);

    @Query(value = "select t from Tb_bill_approval t where t.code in (select msgid from Tb_flows where taskid=?1)")
    Tb_bill_approval findByTaskid(String taskid);
}