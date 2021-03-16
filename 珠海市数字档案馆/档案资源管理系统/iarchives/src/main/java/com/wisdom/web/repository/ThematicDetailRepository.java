package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_thematic_detail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yl on 2017/11/1.
 */
public interface ThematicDetailRepository extends JpaRepository<Tb_thematic_detail, String>,
        JpaSpecificationExecutor<Tb_thematic_detail> {
    Page<Tb_thematic_detail> findByThematicid(Pageable pageable, String thematicid);

    List<Tb_thematic_detail> findByThematicid(String thematicid);

    List<Tb_thematic_detail> findByThematicidIn(String[] thematicids);

    Integer deleteByThematicdetilidIn(String[] thematicdetilids);

    Tb_thematic_detail findByThematicdetilid(String id);

    Integer countByThematicid(String thematicid);

    List<Tb_thematic_detail> findByThematicidAndTitle(String thematicid,String title);

    List<Tb_thematic_detail> findByEntryidAndThematicid(String entryid,String thematicid);

    @Query(value = "select distinct entryid from tb_thematic_detail",nativeQuery = true)
    List<String> findAllEntryid();
}
