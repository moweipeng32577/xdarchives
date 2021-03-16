package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_exchange_reception;
import com.wisdom.web.entity.Tb_filenone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wmh on 2017/11/4.
 */
public interface FileNoneRepository extends JpaRepository<Tb_filenone, String>,
        JpaSpecificationExecutor<Tb_filenone> {

       Tb_filenone findByDocid(String docid);

       @Query(value = "select max(substring(t.filenum,9,12))  from tb_filenone t where substring(t.filenum,1,8) = ?1",nativeQuery = true)
       String  findOrder(String filenumDate);

       List<Tb_filenone> findAllByRecodetypeAndTime(String type,String time);
}
