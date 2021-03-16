package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_thematic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yl on 2017/11/1.
 */
public interface ThematicRepository extends JpaRepository<Tb_thematic, String>,
        JpaSpecificationExecutor<Tb_thematic> {

    Integer deleteAllByThematicidIn(String[] thematicids);

    @Query(value = "UPDATE Tb_thematic SET title=?1,thematiccontent=?2 ,thematictypes=?3,backgroundpath=?4  where thematicid=?5 ")
    @Modifying
    Integer updateThematicid(String title, String content,String thematictypes,String backgroundpath,String thematicid);

    @Query(value = "UPDATE Tb_thematic  SET publishstate=?1,publishtime=?2  where thematicid=?3 ")
    @Modifying
    Integer updateThematicForPublishstate(String type,String publishtime,String thematicid);

    Page<Tb_thematic> findByPublishstate(Pageable pageable, String publish);

    Tb_thematic findByThematicid(String thematicid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Tb_thematic SET filepath=?1,filesize=?2 where thematicid=?3 ")
    Integer updateThematicidFilePath(String filePath,long fileSize,String thematicid);

    Tb_thematic findByTitle(String title);

    @Query(value = "select t from Tb_thematic t where publishstate='已发布' ")
    List<Tb_thematic> findThematicbyState();

    @Query(value = "select t from Tb_thematic t where publishstate='已发布' and thematictypes = ?1")
    Page<Tb_thematic> findThematicbyThematictypes(Pageable pageable,String thematictypes);

    List<Tb_thematic> findThematicByThematicidIn(String[] thematicIds);

    @Query(value = "UPDATE Tb_thematic  SET publishstate=?1,approvetext=?3 where thematicid=?2 ")
    @Modifying
    Integer updateThematicPublishstate(String type,String thematicid,String approvetext);

    @Query(value = "UPDATE Tb_thematic  SET publishstate=?1,submitedtime=?3 where thematicid=?2 ")
    @Modifying
    Integer updateThematicPublishstateAndSubmitedtime(String type,String thematicid,String submitedtime);

    @Query(value = "select t from Tb_thematic t where t.thematicid in (select borrowmsgid from Tb_task where taskid=?1)")
    Tb_thematic findByTaskid(String taskid);
}
