package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_classification_sx;
import com.wisdom.web.entity.Tb_classification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zdw on 2017/10/27 0027.
 */
public interface SxClassificationRepository extends JpaRepository<Tb_classification_sx, String>,
        JpaSpecificationExecutor<Tb_classification_sx> {
    /**
     *
     * @param pcid
     * @return List<Tb_classification>
     */
    List<Tb_classification_sx> findByParentclassidOrderBySortsequence(String pcid);

    @Query(value = "select * from tb_classification t where parentclassid =?1 order by sortsequence",nativeQuery = true)
    List<Tb_classification_sx> findSxByParentclassidOrderBySortsequence(String pcid);

    @Query(value = "select * from tb_classification t where parentclassid is null",nativeQuery = true)
    List<Tb_classification_sx> findSxByParentclassidIsNull();

    List<Tb_classification_sx> findByParentclassidIsNullOrParentclassidOrderBySortsequence(String pcid);

    List<Tb_classification_sx> findByParentclassid(String classificationid);

    @Query(value = "select max(sortsequence) from tb_classification where parentclassid=?1",nativeQuery = true)
    Integer findMaxOrdersByParentclassid(String pcid);

    Tb_classification_sx findByClassid(String classificationid);

    Page<Tb_classification_sx> findByClassid(String classid, Pageable pageable);

    @Query(value = "select max(sortsequence) from tb_classification where parentclassid=?1 or parentclassid is null",nativeQuery = true)
    Integer findMaxOrdersByParentclassidOrNull(String pcid);

    @Transactional(value = "transactionManagerSecondary")
    @Modifying
    Integer deleteByClassid(String id);

    @Query(value = "select * from tb_classification t where parentclassid is null or parentclassid =?1 order by sortsequence ",nativeQuery = true)
    List<Tb_classification_sx> findSxByParentclassidIsNullOrParentclassidOrderBySortsequence(String pcid);

    @Query(value = "select * from tb_classification t where parentclassid =?1",nativeQuery = true)
    List<Tb_classification_sx> findSxByParentclassid(String classificationid);

    @Query(value = "select * from tb_classification t where classid in (?1)",nativeQuery = true)
    Tb_classification_sx findSxByClassid(String classificationid);
    @Query(value = "select * from tb_classification  where codelevel like concat(?1,'%') ",nativeQuery = true)
    List<Tb_classification_sx> findByCodelevel(String codelevel);

}
