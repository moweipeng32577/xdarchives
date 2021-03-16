package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_codeset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by tanly on 2017/11/6 0006.
 */
public interface CodesetRepository extends JpaRepository<Tb_codeset, Integer> {

    List<Tb_codeset> findByDatanodeidOrderByOrdernum(String datanodeid);
    @Query(value = "select * from tb_codeset_sx where datanodeid=?1 order by ordernum",nativeQuery = true)
    List<Tb_codeset> findSxByDatanodeidOrderByOrdernum(String datanodeid);

    @Query(value = "select fieldcode from Tb_codeset where datanodeid=?1 order by ordernum")
    List<String> findFieldcodeByDatanodeid(String datanodeid);

    @Query(value = "select ordernum from Tb_codeset where fieldcode=?1 and datanodeid=?2")
    int findOrdernumByFieldcodeAndDatanodeid(String fieldcode,String datanodeid);

    @Query(value = "select splitcode from Tb_codeset where datanodeid=?1 order by ordernum")
    List<String> findSplitcodeByDatanodeid(String datanodeid);
    
    @Query(value = "select fieldlength from Tb_codeset where datanodeid=?1 order by ordernum")
    List<Object> findFieldlengthByDatanodeid(String datanodeid);

    @Query(value = "select fieldlength from Tb_codeset where datanodeid=?1 and fieldcode=?2")
    Integer findFieldlengthByDatanodeidAndFieldcode(String datanodeid,String fieldcode);

    List<Tb_codeset> findByDatanodeidIn(String[] nodeId);

    Integer deleteByDatanodeidAndFieldcode(String nodeId,String fieldCode);

    Integer deleteByDatanodeidIn(String[] nodeids);

    Integer deleteByDatanodeid(String nodeid);

    @Query(value = "select distinct datanodeid from Tb_codeset where datanodeid in ?1")
    Set<String> getNodeidByNodeidIn(String[] nodeid);

    @Query(value = "select distinct datanodeid from tb_codeset_sx where datanodeid in ?1",nativeQuery = true)
    Set<String> getSxNodeidByNodeidIn(String[] nodeid);

    @Query(value = "select * from  tb_codeset where datanodeid=?1 and fieldcode in (select fieldcode from tb_data_template t where archivecodeedit = 1 and nodeid =?1)",nativeQuery = true)
    List<Tb_codeset> findEditCodeset(String datanodeid);


    @Query(value = "select t from Tb_codeset t where t.datanodeid=?1 order by t.ordernum")
    List<Tb_codeset> findAllByDatanodeid(String datanodeid);

}
