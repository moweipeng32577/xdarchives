package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_flows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface FlowsRepository extends JpaRepository<Tb_flows, Integer>, JpaSpecificationExecutor<Tb_flows> {
	
    Tb_flows findByTaskidAndSpmanAndState(String taskid,String spman,String state);

    Tb_flows findByTaskidAndTextAndState(String taskid,String text,String state);

    @Query(value = "select t from Tb_flows t where taskid = ?1 and spman = ?2 and state = ?3")
    List<Tb_flows> findFlows(String taskid,String spman,String type);

    @Query(value = "select t from Tb_flows t where taskid = ?1 and state = ?2")
    List<Tb_flows> findAuditFlows(String taskid,String type);
    
    @Query(value = "select t from Tb_flows t where taskid = ?1 and spman = ?2")
    List<Tb_flows> findFlowsInfo(String taskid,String spman);
    
    @Query(value = "select t from Tb_flows t where taskid = ?1 and spman = ?2")
    Tb_flows findByTaskidAndSpman(String taskid,String spman);
    
    Integer deleteByTaskid(String taskid);
    
    Integer deleteByTaskidIn(List<String> taskids);

    List<Tb_flows> findByTaskid(String taskid);

    List<Tb_flows> findBySpman(String spman);

    List<Tb_flows> findByMsgid(String borrowcode,Pageable pageable);

    @Query(value = "select t from Tb_flows t where msgid = ?1 and text = ?2")
    List<Tb_flows> findByMsgidAndText(String borrowcode, String text);

    Tb_flows findByTaskidAndState(String taskid,String state);

    @Query(value = "select distinct t.spman from Tb_flows t where t.msgid = ?1 and t.text != '结束' and t.text != '启动'")
    List<String> getFlows(String ordercode);

    List<Tb_flows> findByMsgid(String borrowcode);

    @Query(value = "select t from Tb_flows t where t.msgid = ?1 and t.state = ?2 ")
    List<Tb_flows> findWithMsgidAndState(String borrowcode,String state);

    @Query(value = "select t.msgid from Tb_flows t where taskid in(select taskid from Tb_task where loginname = ?1 and state = ?2 and tasktype =?3) and spman = ?1")
    List<String> getMsgids(String userid,String state,String tasktype);


    LinkedList<Tb_flows> findByMsgidOrderByApprovedate(String borrowcode);

    Tb_flows findByMsgidAndState(String msgid,String state);

    @Query(value = "select t from Tb_flows t where taskid =?1 and spman = ?2 and state =?3")
    Tb_flows getFlowsByTaskid(String taskid,String userid,String state);

    @Modifying
    @Transactional
    @Query(value = "update Tb_flows set state = ?1, text = ?2 where flowsid = ?3")
    Integer updateByTaskid(String state, String text, String flowsid);
}