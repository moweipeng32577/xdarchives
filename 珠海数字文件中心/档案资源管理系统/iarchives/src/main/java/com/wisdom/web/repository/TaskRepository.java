package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_borrowdoc;
import com.wisdom.web.entity.Tb_task;
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
 * Created by Administrator on 2017/10/25 0025.
 */
public interface TaskRepository extends JpaRepository<Tb_task, Integer>, JpaSpecificationExecutor<Tb_task> {

    @Query(value = "select t from Tb_task t where loginname = ?1 and state = ?2 and tasktype !='实体出库'order by tasktime DESC ,taskid desc")
    List<Tb_task> findByLoginnameAndStateOrderByTasktimeDesc(String loginame,String state);

    Tb_task findByTaskid(String id);

    Tb_task findByBorrowmsgid(String borrowmsgid);

    Page<Tb_task> findByLoginnameAndStateAndTasktype(Pageable pageable,String loginname,String state,String type);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_task set state = '完成' where taskid = ?1")
    Integer updateByTaskid(String taskid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_task set urgingstate = '2         ' where taskid = ?1")
    Integer updateStateByTaskidIn(String taskid);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_task set approvetext = ?1, approveman = ?2, state = ?3 where taskid = ?4")
    Integer updateInfoByTaskid(String approvetext, String approveman, String state, String taskid);

    Integer deleteByTaskidIn(List<String> ids);

    @Modifying
    Integer deleteByTaskid(String id);

    Integer deleteByLoginnameInAndTasktype(String[] userid,String taskType);

    @Transactional
    @Modifying
    Integer deleteByLoginnameAndTasktype(String userid,String taskType);
    
    @Query(value = "select t.taskid from Tb_task t where agentUserid = ?1 and loginname = ?2 and tasktype in (?3)")
    List<String> findByAgentuseridAndLoginnameAndTasktypeIn(String agentUserid, String loginname, String[] tasktype);
    
    @Query(value = "select t.taskid from Tb_task t where loginname = ?1 and agentUserid = ?2")
    List<String> findByLoginnameAndAgentuserid(String loginname, String agentUserid);
    
    @Query(value = "select t from Tb_task t where loginname = ?1 and tasktime = ?2")
    Tb_task findByLoginnameAndTasktime(String loginname, Date tasktime);
    
    @Query(value = "select t from Tb_task t where loginname = ?1 and state = ?2")
    List<Tb_task> findByLoginnameAndState(String userid, String state);
    
    @Query(value = "select t.text from Tb_task t where loginname = ?1")
    List<String> findTaskTextByLoginname(String userid);
    
    @Query(value = "select t.text from Tb_task t where loginname = ?1 and tasktype = ?2")
    List<String> findTaskText(String userid, String taskType);
    
    List<Tb_task> findByLoginnameInAndTasktype(String[] userids,String taskType);
    
    @Query(value = "select t.taskid from Tb_task t where loginname in (?1) and tasktype = ?2 and agentuserid = ?3 and to_char(tasktime,'yyyy-mm-dd HH:mm:ss') = ?4")
    String findTaskidByInfo(String[] userid, String taskType, String agentuserid, String tasktime);
    
    @Query(value = "select t from Tb_task t where loginname = ?1 and tasktype = ?2 and state = ?3")
    List<Tb_task> findTaskByState(String userid,String taskType,String state);

    Tb_task findByLastid(String lastid);

    @Query(value = "select t from Tb_task t where tasktype in (?1) and loginname = ?2 and state = ?3   order by tasktime DESC ,taskid desc")
    List<Tb_task> findTasks(String[] tasktype,String loginame,String state);

    Page<Tb_task> findByLoginnameAndStateAndTasktypeIn(Pageable pageable,String loginname,String state,String[] types);

    @Query(value = "select t from Tb_task t where loginname = ?1 and state = ?2 and tasktype = ?3 order by tasktime DESC ,taskid desc")
    List<Tb_task> getOutWareTask(String loginame,String state,String tasktype);

    @Modifying
    @Transactional
    @Query(value = "update Tb_task set state = '完成' where borrowmsgid = ?1 and tasktype = ?2")
    Integer updateByBorrowmsgid(String borrowmsgid,String tasktype);

    @Modifying
    @Transactional
    @Query(value = "delete from Tb_task where state = ?3 and borrowmsgid = ?1 and tasktype = ?2")
    Integer deleteByBorrowmsgid(String borrowmsgid,String taskType,String state);

    @Modifying
    @Transactional
    @Query(value = "update Tb_task set state = '完成' where borrowmsgid = ?1 and tasktype = ?2 and loginname = ?3")
    Integer updateByBorrowmsgidAndUserid(String borrowmsgid,String tasktype,String loginname);

    @Modifying
    @Transactional
    @Query(value = "update Tb_task set state = '完成' where borrowmsgid = ?1 ")
    Integer updateByMsgid(String msgid);

    Tb_task findByBorrowmsgidAndLoginname(String borrowmsgid,String userid);

    @Query(value = "select t.taskid from Tb_task t where lastid = ?1")
    List<String> findTasksByLastid(String lastid);
}
