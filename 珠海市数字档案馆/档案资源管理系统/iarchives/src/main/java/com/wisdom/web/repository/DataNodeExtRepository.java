package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_node_mdaflag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo on 2019/12/13 0013.
 */
public interface DataNodeExtRepository extends JpaRepository<Tb_data_node_mdaflag, String> {
    @Query( value = "select * from tb_data_node_mdaflag where nodeid = ?1 and is_media = 1", nativeQuery = true)
    Tb_data_node_mdaflag findNodeid(String nodeid);

    @Query( value = "select a.* from tb_data_node_mdaflag a where a.nodeid = (select b.nodeid from tb_data_node b where refid = ?1) and a.is_media = 1", nativeQuery = true)
    Tb_data_node_mdaflag findNodeidByClassid(String classid);

    @Query( value = "select a.nodeid from tb_data_node_mdaflag a where a.is_media = 1", nativeQuery = true)
    List<String> findMediaNodeid();

    @Modifying
    @Query(value = "insert into tb_data_node_mdaflag (nodeid,is_media) select nodeid,?2 as is_media from tb_data_node where tb_data_node.classid = ?1", nativeQuery = true)
    void insertTb_data_node_mdaflagByClassid(String classid, int is_media);

    @Modifying
    @Query(value = "delete from tb_data_node_mdaflag where tb_data_node_mdaflag.nodeid in ( select nodeid from tb_data_node where tb_data_node.classid = ?1)", nativeQuery = true)
    void deleteTb_data_node_mdaflagByClassid(String classid);


    void deleteByNodeidIn(String[] nodeids);
}
