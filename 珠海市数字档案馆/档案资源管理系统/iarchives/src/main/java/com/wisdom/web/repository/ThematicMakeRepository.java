package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_thematic_make;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yl on 2017/11/1.
 */
public interface ThematicMakeRepository extends JpaRepository<Tb_thematic_make, String>,
        JpaSpecificationExecutor<Tb_thematic_make> {

    Integer deleteAllByThematicidIn(String[] thematicids);

    @Query(value = "UPDATE Tb_thematic_make SET title=?1,thematiccontent=?2 ,thematictypes=?3,backgroundpath=?4  where thematicid=?5 ")
    @Modifying
    Integer updateThematicid(String title, String content, String thematictypes, String backgroundpath, String thematicid);

    @Query(value = "UPDATE Tb_thematic_make  SET publishstate=?1,publishtime=?2  where thematicid=?3 ")
    @Modifying
    Integer updateThematicForPublishstate(String type, String publishtime, String thematicid);

    Page<Tb_thematic_make> findByPublishstate(Pageable pageable, String publish);

    Tb_thematic_make findByThematicid(String thematicid);

    @Query(value = "UPDATE Tb_thematic_make SET filepath=?1,filesize=?2 where thematicid=?3 ")
    @Modifying
    Integer updateThematicidFilePath(String filePath, long fileSize, String thematicid);

    Tb_thematic_make findByTitle(String title);

    @Query(value = "select t from Tb_thematic_make t where publishstate='已发布' ")
    List<Tb_thematic_make> findThematicbyState();

    @Query(value = "select t from Tb_thematic_make t where publishstate='已发布' and thematictypes = ?1")
    Page<Tb_thematic_make> findThematicbyThematictypes(Pageable pageable, String thematictypes);

    List<Tb_thematic_make> findThematicByThematicidIn(String[] thematicIds);
}
