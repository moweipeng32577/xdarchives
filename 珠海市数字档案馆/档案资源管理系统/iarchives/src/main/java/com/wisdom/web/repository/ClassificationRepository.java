package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_classification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2017/10/27 0027.
 */
public interface ClassificationRepository extends JpaRepository<Tb_classification, String>,
        JpaSpecificationExecutor<Tb_classification> {
    /**
     *
     * @param pcid
     * @return List<Tb_classification>
     */
    List<Tb_classification> findByParentclassidOrderBySortsequence(String pcid);
    @Query(value = "select * from tb_classification_sx t where parentclassid =?1 order by sortsequence",nativeQuery = true)
    List<Tb_classification> findSxByParentclassidOrderBySortsequence(String pcid);

    List<Tb_classification> findByParentclassidIsNullOrParentclassidOrderBySortsequence(String pcid);
    @Query(value = "select * from tb_classification_sx t where parentclassid is null or parentclassid =?1 order by sortsequence ",nativeQuery = true)
    List<Tb_classification> findSxByParentclassidIsNullOrParentclassidOrderBySortsequence(String pcid);

    List<Tb_classification> findByParentclassidIsNull();
    @Query(value = "select * from tb_classification_sx t where parentclassid is null",nativeQuery = true)
    List<Tb_classification> findSxByParentclassidIsNull();

    @Query(value = "select t from Tb_classification t where (parentclassid is null or parentclassid='')  order by sortsequence")
    List<Tb_classification> findByParentclassidIsNullOrderBySortsequence();
    
    List<Tb_classification> findByClassname(String classname);

    List<Tb_classification> findByClassnameAndClassid(String classname, String classid);

    @Query(value = "select max(sortsequence) from Tb_classification where parentclassid=?1")
    Integer findMaxOrdersByParentclassid(String pcid);

    @Query(value = "select max(sortsequence) from Tb_classification where parentclassid=?1 or parentclassid is null")
    Integer findMaxOrdersByParentclassidOrNull(String pcid);

    List<Tb_classification> findByParentclassid(String classificationid);
    @Query(value = "select * from tb_classification_sx t where parentclassid =?1",nativeQuery = true)
    List<Tb_classification> findSxByParentclassid(String classificationid);

    List<Tb_classification> findByClassnameAndParentclassid(String name, String classificationid);

    /**
     *
     * @param classificationid
     * @return List<Tb_classification>
     */
    Tb_classification findByClassid(String classificationid);
    @Query(value = "select * from tb_classification_sx t where classid in (?1)",nativeQuery = true)
    Tb_classification findSxByClassid(String classificationid);

    @Query(value = "select t from Tb_classification t where classid in (?1)")
    Page<Tb_classification> findByClassid(List<String> classids, Pageable pageable);

    @Query(value = "select t from Tb_classification t where classid in (?1) order by sortsequence")
    List<Tb_classification> findByClassid(String[] id);

    @Modifying
    @Query(value = "update Tb_classification set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2")
    int modifyClassOrder(int start,int end);

    Integer deleteByClassid(String id);

    //更新分类从属级别codelevel
    @Query(value = "select t from Tb_classification t where length(t.parentclassid)<5  or t.parentclassid is null")
    List<Tb_classification> findFirstLevel();

    @Query(value = "select t from Tb_classification t where t.codelevel like concat(?1,'%') ")
    List<Tb_classification> findByCodelevel(String codelevel);

    @Query(value = "select t from Tb_classification t where t.parentclassid=?1")
    List<Tb_classification> findSubLevel(String parentclassid);

    @Modifying
    @Query(value = "update Tb_classification set codelevel = ?1 where classid=?2")
    int updateCodeLevel(String codelevel,String classid);

    @Query(value = "select tc from Tb_classification tc where parentclassid= (select t.classid from Tb_classification t where t.classname='已归管理' and(length(t.parentclassid)<5  or t.parentclassid is null))")
    List<Tb_classification> findYgdFirstLevel();

    @Query(value = "select t.codelevel from Tb_classification t where t.classname='案卷管理' and(length(t.parentclassid)<5  or t.parentclassid is null)")
    String findAjCodelevel();

    //档案鉴定判断是否为案卷
    @Query(value = "select  classname from tb_classification where classid = (select classid from tb_data_node where nodeid = (select nodeid from tb_entry_index where entryid = ?1 ))",nativeQuery = true)
    String findClassname(String entryid);

    @Query(value = "select c.classid from Tb_classification c where c.parentclassid = ?1")
    List<String> findClassIdByParent(String classis);
}
