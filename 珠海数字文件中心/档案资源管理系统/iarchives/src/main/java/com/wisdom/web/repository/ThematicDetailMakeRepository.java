package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_thematic_detail_make;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by yl on 2017/11/1.
 */
public interface ThematicDetailMakeRepository extends JpaRepository<Tb_thematic_detail_make, String>,
        JpaSpecificationExecutor<Tb_thematic_detail_make> {
    Page<Tb_thematic_detail_make> findByThematicid(Pageable pageable, String thematicid);

    List<Tb_thematic_detail_make> findByThematicid(String thematicid);

    List<Tb_thematic_detail_make> findByThematicidIn(String[] thematicids);

    Integer deleteByThematicdetilidIn(String[] thematicdetilids);

    Tb_thematic_detail_make findByThematicdetilid(String id);

    Integer countByThematicid(String thematicid);

    List<Tb_thematic_detail_make> findByThematicidAndTitle(String thematicid, String title);
}
