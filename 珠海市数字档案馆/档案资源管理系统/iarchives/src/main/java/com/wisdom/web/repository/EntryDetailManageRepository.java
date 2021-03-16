package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail_manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/6/25.
 */
public interface EntryDetailManageRepository extends JpaRepository<Tb_entry_detail_manage, String> {

    Tb_entry_detail_manage findByEntryid(String entryid);

    int deleteByEntryidIn(String[] entryids);

    List<Tb_entry_detail_manage> findByEntryidIn(String[] entryids);

    @Modifying
    @Query(value = "insert into tb_entry_detail_manage select * from tb_entry_detail_accept where entryid in ?1", nativeQuery = true)
    int movedetails(String[] entryidData);
}
