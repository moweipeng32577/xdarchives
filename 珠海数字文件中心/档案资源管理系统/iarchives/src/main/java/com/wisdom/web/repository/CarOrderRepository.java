package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_car_order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/21.
 */
public interface CarOrderRepository extends JpaRepository<Tb_car_order,String>,JpaSpecificationExecutor<Tb_car_order> {


    List<Tb_car_order> findByCarid(String carid);

    Tb_car_order findById(String orderid);

    List<Tb_car_order> findByIdIn(String[] ids);

    int deleteByIdIn(String[] ids);

    List<Tb_car_order> findByCaridAndStateAndReturnstate(String carid,String state,String returnstate);

    @Query(value = "select b from Tb_car_order b where b.ordercode in (select msgid from Tb_flows where taskid=?1)")
    Tb_car_order getCarOrderByTaskid(String taskid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_car_order set returnstate=?2 where id =?1")
    int updateReturnstate(String id,String state);

    @Query(value = "select b from Tb_car_order b where b.id=?1 and b.returnstate=?2 and b.state=?3")
    Tb_car_order getCarOrderByIdAndReturnstateAndState(String id,String returnstate,String state);

	List<Tb_car_order> findByStateInOrderByOrdertimeDesc(String[] state);

    @Query(value = "select t from Tb_car_order t where t.carid=?1 and t.state !=?2")
    Page<Tb_car_order> getCarOrderByCarid(String id,String state, Pageable pageable);

    @Query(value = "select t from Tb_car_order t where t.carid=?1 and (( t.starttime >= ?2 and t.starttime < ?3 ) or ( t.endtime<= ?3 and t.endtime > ?2) or ( t.starttime < ?2 and t.endtime > ?3 )) and t.state!=?4")
    Page<Tb_car_order> getCarOrderByCaridAndTimelimit(String id, String starttime,String endtime,String state, Pageable pageable);
}
