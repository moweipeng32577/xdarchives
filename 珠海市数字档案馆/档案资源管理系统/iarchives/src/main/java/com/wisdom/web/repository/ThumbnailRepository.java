package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Leo .
 */
public interface ThumbnailRepository extends JpaRepository<Tb_thumbnail, String>,JpaSpecificationExecutor<Tb_thumbnail> {

    List<Tb_thumbnail> findByEntryid(String entryid);

    Tb_thumbnail findByEleidOrEntryid(String eleid, String entryid);

    @Modifying
    @Query(value = "delete from tb_thumbnail where entryid = ?1", nativeQuery = true)
    Integer deleteByEntryid(String entryid);

    List<Tb_thumbnail> findByEntryidIn(String[] entryids);

    Integer deleteByEntryidIn(String[] entryids);

}
