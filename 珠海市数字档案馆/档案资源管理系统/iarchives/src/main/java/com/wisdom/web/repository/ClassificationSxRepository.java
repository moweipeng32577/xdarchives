package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_classification;
import com.wisdom.web.entity.Tb_classification_sx;
import com.wisdom.web.entity.Tb_user_data_node_sx;
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
public interface ClassificationSxRepository extends JpaRepository<Tb_classification_sx, String>,
        JpaSpecificationExecutor<Tb_classification_sx> {

    List<Tb_classification_sx> findByClassname(String classname);

    List<Tb_classification_sx> findByClassnameAndClassid(String classname, String classid);

    List<Tb_classification_sx> findByParentclassidIsNullOrParentclassidOrderBySortsequence(String pcid);

    List<Tb_classification_sx> findByParentclassid(String classificationid);

    @Query(value = "select max(sortsequence) from Tb_classification_sx where parentclassid=?1")
    Integer findMaxOrdersByParentclassid(String pcid);

    Tb_classification_sx findByClassid(String classificationid);

    Page<Tb_classification_sx> findByClassid(String classid, Pageable pageable);

    @Query(value = "select max(sortsequence) from Tb_classification_sx where parentclassid=?1 or parentclassid is null")
    Integer findMaxOrdersByParentclassidOrNull(String pcid);

    Integer deleteByClassid(String id);

    List<Tb_classification_sx> findByParentclassidOrderBySortsequence(String pcid);
}
