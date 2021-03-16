package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_right_organ_sx;
import com.wisdom.web.entity.Tb_right_organ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
public interface SxRightOrganRepository extends JpaRepository<Tb_right_organ_sx, String>,JpaSpecificationExecutor<Tb_right_organ_sx> {

    @Query(value ="select * from tb_right_organ where parentid = '0'",nativeQuery = true)
    List<Tb_right_organ_sx> findAllParent();

    /**
     *
     * @param parentid
     * @return List<Tb_right_organ>
     */
    List<Tb_right_organ_sx> findByParentidOrderBySortsequence(String parentid);

    Tb_right_organ_sx findByOrganname(String organname);

    @Query(value = "select max(sortsequence) from tb_right_organ where parentid=?1",nativeQuery = true)
    Integer findMaxOrdersByParentid(String parentid);

    @Query(value = "select * from tb_right_organ t where organtype = 'unit' and organid not in (select organid from tb_funds where organid is not null)",nativeQuery = true)
    List<Tb_right_organ_sx> findUnitInfo();

    /**
     *
     * @param id
     * @return Tb_right_organ
     */
    Tb_right_organ_sx findByOrganid(String id);

    @Query(value = "select * from tb_right_organ t where organid in (?1) order by sortsequence",nativeQuery = true)
    List<Tb_right_organ_sx> findByOrganid(String[] id);

    @Query(value = "select t from Tb_right_organ_sx t where organid in (?1)")
    Page<Tb_right_organ_sx> findByOrganid(List<String> organids, Pageable pageable);

    /**
     * @param organid
     * @return
     */
    List<Tb_right_organ_sx> findByParentid(String organid);

    List<Tb_right_organ_sx> findByParentidIn(String[] organids);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_right_organ set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2",nativeQuery = true)
    int modifyOrganOrder(int start, int end);

    Tb_right_organ_sx findByRefid(String refId);

    @Query(value = "select * from tb_right_organ t where organid in (select parentid from tb_right_organ where organid in (?1))",nativeQuery = true)
    List<Tb_right_organ_sx> getParentOrgans(String[] organids);

    @Query(value = "select * from tb_right_organ t where organid in (" +
            "select uo.organid from tb_user_organ uo,tb_right_organ ro " +
            "where uo.organid=ro.organid and userid=?1) ORDER BY sortsequence",nativeQuery = true)
    List<Tb_right_organ_sx> getMyAuthWithParent(String userid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByOrganid(String organId);

    @Query(value = "select code from tb_right_organ where organid=(select organ.organid from tb_user where userid=?1)",nativeQuery = true)
    String findCodeByUserid(String userid);

    @Query(value = "select code from tb_right_organ where organid=?1",nativeQuery = true)
    String findCodeByOrganid(String organid);

    @Query(value = "select organname from tb_right_organ where organid=?1",nativeQuery = true)
    String findOrganByOrganid(String organid);

    List<Tb_right_organ_sx> findByOrgannameAndParentid(String name, String parentid);

    //初始化机构等级
    @Query(value = "select * from  tb_right_organ t where t.parentid='0' order by sortsequence",nativeQuery = true)
    List<Tb_right_organ_sx> findFirstLevel();

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "update tb_right_organ set organlevel= ?1 where organid=?2 ",nativeQuery = true)
    int updateOrganlevel(String organlevel, String organid);

    Tb_right_organ_sx findByOrgannameAndIsinit(String organname, String isinit);

    @Query(value = "select * from  tb_right_organ t order by sortsequence",nativeQuery = true)
    List<Tb_right_organ_sx> getAllOrgan();

    @Query(value = "select * from tb_right_organ t where organname in (?1)",nativeQuery = true)
    List<Tb_right_organ_sx> findWithOrganname(String organname);

    @Query(value ="select * from tb_right_organ t where (organlevel is null or organlevel='')",nativeQuery = true)
    List<Tb_right_organ_sx> findByOrganlevelNull();

    Tb_right_organ_sx findByOrganlevel(String organlevel);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    @Query(value = "delete from tb_right_organ where 1=1",nativeQuery = true)
    int deleteAllOrgan();

}