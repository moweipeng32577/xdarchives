package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_right_organ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface RightOrganRepository extends JpaRepository<Tb_right_organ, String>,JpaSpecificationExecutor<Tb_right_organ> {

    @Query(value ="select * from tb_right_organ where parentid = '0'",nativeQuery = true)
    List<Tb_right_organ> findAllParent();

    @Query(value ="select * from tb_right_organ order by organlevel",nativeQuery = true)
    List<Tb_right_organ> findAllByOrganlevel();

    @Query(value ="select t.organid from Tb_right_organ t where t.organlevel like concat(?1,'%')")
    String[] findWithOrganlevel(String organlevel);

    @Query(value ="select t from Tb_right_organ t where t.organlevel like concat(?1,'%')")
    List<Tb_right_organ> getWithOrganlevel(String organlevel);

    /**
     *
     * @param parentid
     * @return List<Tb_right_organ>
     */
    List<Tb_right_organ> findByParentidOrderBySortsequence(String parentid);
    
    Tb_right_organ findByOrganname(String organname);

    @Query(value = "select max(sortsequence) from Tb_right_organ where parentid=?1")
    Integer findMaxOrdersByParentid(String parentid);
    
    @Query(value = "select t from Tb_right_organ t where organtype = 'unit' and organid not in (select organid from Tb_funds where organid is not null)")
    List<Tb_right_organ> findUnitInfo();
    
    /**
     *
     * @param id
     * @return Tb_right_organ
     */
    Tb_right_organ findByOrganid(String id);

    @Query(value = "select t from Tb_right_organ t where organid in (?1) order by sortsequence")
    List<Tb_right_organ> findByOrganid(String[] id);

    @Query(value = "select t from Tb_right_organ t where organid in (?1)")
    Page<Tb_right_organ> findByOrganid(List<String> organids, Pageable pageable);

    /**
     * @param organid
     * @return
     */
    List<Tb_right_organ> findByParentid(String organid);

    List<Tb_right_organ> findByParentidIn(String[] organids);

    @Modifying
    @Query(value = "update Tb_right_organ set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2")
    int modifyOrganOrder(int start,int end);

    List<Tb_right_organ> findByRefid(String refId);

    @Query(value = "select t from Tb_right_organ t where organid in (select parentid from Tb_right_organ where organid in (?1))")
    List<Tb_right_organ> getParentOrgans(String[] organids);

    @Query(value = "select t from Tb_right_organ t where organid in (" +
            "select uo.organid from Tb_user_organ uo,Tb_right_organ ro " +
            "where uo.organid=ro.organid and userid=?1) ORDER BY sortsequence")
    List<Tb_right_organ> getMyAuthWithParent(String userid);

    Integer deleteByOrganid(String organId);

    @Query(value = "select code from Tb_right_organ where organid=(select organ.organid from Tb_user where userid=?1)")
    String findCodeByUserid(String userid);

    @Query(value = "select code from Tb_right_organ where organid=?1")
    String findCodeByOrganid(String organid);

    @Query(value = "select organname from Tb_right_organ where organid=?1")
    String findOrganByOrganid(String organid);

    List<Tb_right_organ> findByOrgannameAndParentid(String name,String parentid);

    //初始化机构等级
    @Query(value = "select t from  Tb_right_organ t where t.parentid='0' order by sortsequence")
    List<Tb_right_organ> findFirstLevel();

    @Modifying
    @Query(value = "update Tb_right_organ set organlevel= ?1 where organid=?2 ")
    int updateOrganlevel(String organlevel,String organid);

    Tb_right_organ findByOrgannameAndIsinit(String organname,String isinit);

    @Query(value = "select t from  Tb_right_organ t order by sortsequence")
    List<Tb_right_organ> getAllOrgan();

    @Query(value = "select t from Tb_right_organ t where organname in (?1)")
    List<Tb_right_organ> findWithOrganname(String organname);

    @Query(value ="select t from Tb_right_organ t where (organlevel is null or organlevel='')")
    List<Tb_right_organ> findByOrganlevelNull();

    Tb_right_organ findByOrganlevel(String organlevel);

    Integer countByOrgannameAndParentid(String name,String id);

    List<Tb_right_organ> findByOrgantype(String organtype);

    @Query(value = "select * from tb_right_organ where organid=(select refid from tb_data_node where nodeid=?1)",nativeQuery = true)
    Tb_right_organ getWithNodeid(String id);
}