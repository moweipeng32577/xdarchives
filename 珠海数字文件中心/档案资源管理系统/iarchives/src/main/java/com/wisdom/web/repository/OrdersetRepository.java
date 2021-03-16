package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_orderset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by zengdw on 2019/07/09 .
 */
public interface OrdersetRepository extends JpaRepository<Tb_orderset, Integer> {

    List<Tb_orderset> findByDatanodeidOrderByOrdernum(String datanodeid);

    @Query(value = "select fieldcode from Tb_orderset where datanodeid=?1 order by ordernum")
    List<String> findFieldcodeByDatanodeid(String datanodeid);

    @Query(value = "select ordernum from Tb_orderset where fieldcode=?1 and datanodeid=?2")
    int findOrdernumByFieldcodeAndDatanodeid(String fieldcode, String datanodeid);

    List<Tb_orderset> findByDatanodeidIn(String[] nodeId);

    Integer deleteByDatanodeidAndFieldcode(String nodeId, String fieldCode);

    Integer deleteByDatanodeidIn(String[] nodeids);

    Integer deleteByDatanodeid(String nodeid);

    @Query(value = "select distinct datanodeid from Tb_orderset where datanodeid in ?1")
    Set<String> getNodeidByNodeidIn(String[] nodeid);
}
