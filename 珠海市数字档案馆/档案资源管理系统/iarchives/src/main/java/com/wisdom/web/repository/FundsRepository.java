package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_funds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by RonJiang on 2018/4/8 0008.
 */
public interface FundsRepository extends JpaRepository<Tb_funds,String>,JpaSpecificationExecutor<Tb_funds> {

    Tb_funds findByFundsid(String fundsid);
    
    @Query(value = "select funds from Tb_funds where funds is not null")
    List<String> findByFunds();

    @Query(value = "select funds from Tb_funds where organid =?1")
    List<String> findFundsByOrganid(String organid);

    @Query(value = "select organid from Tb_funds where funds is not null")
    List<String> getOrganids();
    
    Tb_funds findByOrganid(String organid);//机构管理业务调整后,机构节点中的名称变成唯一,全宗管理数据对应机构管理中单位信息
    
    @Modifying
    @Query(value = "update Tb_funds set organname = ?1 where organid = ?2")
    Integer modifyByOrganid(String organname,String organid);

    Integer deleteByFundsidIn(String[] fundsidData);
    
    @Modifying
    @Transactional
    @Query(value = "update Tb_funds set filingtotalnum = ?1,filingshortterm = ?2,filinglongterm = ?3,filingpermanent = ?4, filetotalnum = ?5,jntotalcopies = ?6,wsfilenum = ?7,wsjncopies = ?8,otherfilenum = ?9,jnothercopies = ?10,filingnum = ?11 where fundsid = ?12")
    Integer modifyFundsByOrganid(String filingtotalnum,String filingshortterm, String filinglongterm, String filingpermanent, String filetotalnum,
    		String jntotalcopies, String wsfilenum, String wsjncopies, String otherfilenum, String jnothercopies, String filingnum, String organid);

    Tb_funds findByFundsnameAndIsinit(String fundsname,String isinit);
}