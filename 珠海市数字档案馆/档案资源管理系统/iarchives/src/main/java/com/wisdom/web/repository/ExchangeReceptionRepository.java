package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_exchange_reception;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by yl on 2017/11/4.
 */
public interface ExchangeReceptionRepository extends JpaRepository<Tb_exchange_reception, String>,
        JpaSpecificationExecutor<Tb_exchange_reception> {
    Tb_exchange_reception findByExchangeid(String exchangeid);

    @Query(value = "SELECT new Tb_exchange_reception(exchangeid,filename,filemd5,filesize) FROM Tb_exchange_reception",
            countQuery = "SELECT count(ex) FROM Tb_exchange_reception ex")
    Page<Tb_exchange_reception> findAll(Pageable pageable);

    Integer deleteByExchangeidIn(String[] exchangeids);
}
