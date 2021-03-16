package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_funds_sx;
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
public interface SxFundsRepository extends JpaRepository<Tb_funds_sx,String>,JpaSpecificationExecutor<Tb_funds_sx> {

    Tb_funds_sx findByFundsid(String fundsid);

    @Query(value = "select funds from tb_funds where funds is not null",nativeQuery = true)
    List<String> findByFunds();

    @Query(value = "select funds from tb_funds where organid =?1",nativeQuery = true)
    List<String> findFundsByOrganid(String organid);

    Tb_funds_sx findByOrganid(String organid);//机构管理业务调整后,机构节点中的名称变成唯一,全宗管理数据对应机构管理中单位信息

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_funds set organname = ?1 where organid = ?2",nativeQuery = true)
    Integer modifyByOrganid(String organname, String organid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByFundsidIn(String[] fundsidData);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_funds set filingtotalnum = ?1,filingshortterm = ?2,filinglongterm = ?3,filingpermanent = ?4, filetotalnum = ?5,jntotalcopies = ?6,wsfilenum = ?7,wsjncopies = ?8,otherfilenum = ?9,jnothercopies = ?10,filingnum = ?11 where fundsid = ?12",nativeQuery = true)
    Integer modifyFundsByOrganid(String filingtotalnum, String filingshortterm, String filinglongterm, String filingpermanent, String filetotalnum,
                                 String jntotalcopies, String wsfilenum, String wsjncopies, String otherfilenum, String jnothercopies, String filingnum, String organid);

    Tb_funds_sx findByFundsnameAndIsinit(String fundsname, String isinit);
}