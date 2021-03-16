package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_place_order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

/**
 * Created by Administrator on 2020/4/28.
 */
public interface PlaceOrderRepository extends JpaRepository<Tb_place_order,String>,JpaSpecificationExecutor<Tb_place_order> {


    Tb_place_order findById(String id);

    List<Tb_place_order> findByPlaceid(String placeid);

    //根据placeManger的id查找Tb_place_order
    @Query(value = "select t from Tb_place_order t where t.placeid = ?1 and t.state!=?2")
    Page<Tb_place_order> findByPlaceIds(String placeid,String state, Pageable pageable);

    List<Tb_place_order> findByIdIn(String[] Orderids);

    List<Tb_place_order> findByPlaceidInAndState(String[] ids,String state);

    int deleteByIdIn(String[] orderids);

    @Query(value = "select b from Tb_place_order b where b.ordercode in (select msgid from Tb_flows where taskid=?1)")
    Tb_place_order getPlaceOrderByTaskid(String taskid);

    @Query(value = "select b from Tb_place_order  b where b.id=?1 and  b.returnstate=?2 and b.state=?3")
    Tb_place_order getPlaceOrderByIdAndReturnstateAndState(String id,String returnstate,String state);

    List<Tb_place_order> findByStateInOrderByOrdertimeDesc(String[] state);

    @Query(value = "select t from Tb_place_order t where t.placeid = ?1 and (( t.starttime >= ?2 and t.starttime < ?3 ) or ( t.endtime<= ?3 and t.endtime > ?2) or ( t.starttime < ?2 and t.endtime > ?3 )) and t.state!=?4")
    Page<Tb_place_order> findByPlaceIdsAndTimelimit(String placeid,String starttime,String endtime,String state, Pageable pageable);
}
