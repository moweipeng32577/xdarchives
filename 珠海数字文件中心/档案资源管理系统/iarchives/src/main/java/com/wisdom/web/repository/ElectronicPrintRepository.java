package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_print;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2019/5/18.
 */
public interface ElectronicPrintRepository extends JpaRepository<Tb_electronic_print,String> {


    List<Tb_electronic_print> findByEntryidAndBorrowcode(String entryid,String borrowcode);

    Page<Tb_electronic_print> findByEntryidAndBorrowcode(String entryid, String borrowcode, Pageable pageable);

    List<Tb_electronic_print> findByIdIn(String[] ids);

    int deleteByEntryidInAndBorrowcode(String[] entryids,String borrowcode);

    List<Tb_electronic_print> findByEntryidInAndBorrowcode(String[] entryids,String borrowcode);
}
