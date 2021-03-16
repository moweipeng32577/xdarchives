package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_datareceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yl on 2020/6/22.
 */
public interface DatareceiveRepository extends JpaRepository<Tb_datareceive, String>, JpaSpecificationExecutor<Tb_datareceive> {
    Page<Tb_datareceive> findByTypeAndState(Pageable pageable, String type, String state);

    List<Tb_datareceive> findByFilename(String filename);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Tb_datareceive SET state= '已接收' where filename =?1 ")
    Integer updateState(String filename);

    Integer deleteByFilename(String filename);

    Tb_datareceive findByReceiveid(String receiveid);
}
