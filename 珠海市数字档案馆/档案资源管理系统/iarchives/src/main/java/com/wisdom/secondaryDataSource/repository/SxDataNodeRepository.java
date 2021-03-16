package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo on 2020/6/8 0008.
 */
public interface SxDataNodeRepository extends JpaRepository<Tb_data_node_sx, String>, JpaSpecificationExecutor<Tb_data_node_sx> {

    /**
     * @param nodeid
     * @return Tb_data_node
     */
    Tb_data_node_sx findByNodeid(String nodeid);

    @Query(value = "select nodename from Tb_data_node where nodeid=?1",nativeQuery = true)
    String findNodenameByNodeid(String nodeid);

    @Query(value = "select parentnodeid from Tb_data_node where nodeid=?1",nativeQuery = true)
    String findParentNodeidByNodeid(String nodeid);

    @Query(value = "select nodelevel from Tb_data_node where nodeid=?1",nativeQuery = true)
    String findNodeLevelByNodeid(String nodeid);

}
