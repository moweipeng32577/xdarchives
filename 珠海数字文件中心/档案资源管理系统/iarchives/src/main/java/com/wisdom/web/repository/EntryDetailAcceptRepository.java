package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_detail_accept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/6/24.
 */
public interface EntryDetailAcceptRepository extends JpaRepository<Tb_entry_detail_accept, String> {

    Tb_entry_detail_accept findByEntryid(String entryid);

    int deleteByEntryidIn(String[] entryids);

    List<Tb_entry_detail_accept> findByEntryidIn(String[] entryids);

    @Modifying
    @Query(value = "insert into tb_entry_detail_accept select * from tb_entry_detail_manage where entryid in ?1", nativeQuery = true)
    int moveAcceptdetails(String[] entryidData);
}
