package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_inform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by Rong on 2017/10/31.
 */
public interface InFormRepository extends JpaRepository<Tb_inform,String> ,JpaSpecificationExecutor<Tb_inform> {

    List<Tb_inform> findByInformidInOrderByLimitdateDesc(String[] ids);

    List<Tb_inform> findByInformidInOrderByInformdate(String[] ids);

    Page<Tb_inform> findByInformidInAndLimitdateGreaterThan(Pageable pageable, String[] ids,Date limitdate);

    Integer deleteByInformidIn(String[] ids);

    @Query(value = "select t from Tb_inform t where informid in ?1 and (t.limitdate> getdate() or (t.title ='场地取消预约' or t.title ='公车取消预约')) order by stick asc ,informdate desc ")
    List<Tb_inform>  getInForms(String[] ids);

    @Query(value = "select t from Tb_inform t where informid in (select informid from Tb_inform_user where userroleid = ?1 and state is null) and (t.title ='场地取消预约' or t.title ='公车取消预约') and t.limitdate> getdate() order by stick asc ,informdate desc ")
    List<Tb_inform>  getOrderInForms(String userid);
}
