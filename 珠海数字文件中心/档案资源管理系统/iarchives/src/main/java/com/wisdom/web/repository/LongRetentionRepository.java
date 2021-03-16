package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_long_retention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by yl on 2019/12/27.
 */
public interface LongRetentionRepository extends JpaRepository<Tb_long_retention, String>,
        JpaSpecificationExecutor<Tb_long_retention> {
    Tb_long_retention findByEntryid(String entryid);

    List<Tb_long_retention> findByEntryidIn(String[] entryid);

    @Query(value = "select count(entryid) from Tb_long_retention where entryid in (?1) and  checkstatus like '%不通过%'")
    Long getAllCountByState(String[] entryids);

    Page<Tb_long_retention> findByReceiveid(Pageable pageable,String receiveid);

    @Transactional
    Integer deleteByEntryidIn(String[] entryids);

    @Transactional
    Integer deleteByReceiveidIn(String[] receiveids);

    List<Tb_long_retention> findByParententryid(String id);
}
