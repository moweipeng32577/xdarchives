package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Tb_notice,String>, JpaSpecificationExecutor<Tb_notice> {

    @Modifying
    @Query(value = "delete from tb_notice where noticeID in (?1)",nativeQuery = true)
    Integer deleteByNoticeID(String[] noticeIDs);

    @Query(value = "select * from tb_notice where noticeID in (?1)",nativeQuery = true)
    List<Tb_notice> findByNoticeID(String[] ids);

    @Modifying
    @Query(value = "update tb_notice set publishstate = ?2, publishtime = ?3 where noticeID in (?1)",nativeQuery = true)
    Integer updatePublishstate(String[] noticeIDs, Integer integer, String s);

    @Query(value = "select * from tb_notice where publishstate = 1 order by stick DESC,publishtime DESC ",nativeQuery = true)
    List<Tb_notice> findByPublishstate();

    Tb_notice findByNoticeID(String id);
}
