package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_textopen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2018/9/27.
 */
public interface TextOpenRepository extends JpaRepository<Tb_textopen, String>{


    @Query(value = "select t from Tb_textopen t where borrowcode = ?1 ")
    List<Tb_textopen> findQueryByborrowcode(String borrowcode);

    List<Tb_textopen> findByEleidAndBorrowcode(String eleid, String borrowcode);

    @Query(value = "select eleid from Tb_textopen t where borrowcode = ?1 and  entryid=?2 and state='查看'")
    List<String> findEleidsByborrowcode(String borrowcode, String entryid);

    @Query(value = "select t from Tb_textopen t where borrowcode = ?1 and  entryid=?2 ")
    List<Tb_textopen> findByborrowcodeAndEntryid(String borrowcode, String entryid);

    Integer deleteByBorrowcodeAndEntryidIn(String borrowcode,String[] entryids);
}
