package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_delete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Leo on 2020/6/22 0022.
 */
public interface DataDeleteRepository extends JpaRepository<Tb_data_delete, String>, JpaSpecificationExecutor<Tb_data_delete> {

    @Query(value = "delete from tb_data_delete",nativeQuery = true)
    @Transactional
    @Modifying
    void deleteDate();

    @Query(value = "select number from tb_data_delete where nodeid=?1",nativeQuery = true)
    String findNumberByNodeId(String nodeId);

    @Query(value = "select nodeid from tb_data_delete where userid=?1",nativeQuery = true)
    String[] findByUserId(String uId);
}
