package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_oa_record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OaRecordRepository extends JpaRepository<Tb_oa_record, String>, JpaSpecificationExecutor<Tb_oa_record> {

    @Modifying
    @Query(value = "update Tb_oa_record set receivestate=?1,entryid=?2,title=?3,code=?5 where filename=?4 ")
    Integer updateStateByName(String receivestate, String entryid,String title, String filename,String code);

    @Query(value = "select count(*) from Tb_oa_record where filename = ?1")
    Long findCountByName(String filename);

    @Modifying
    @Query(value = "update Tb_oa_record set filestate=?1,filepath=?2 where filename=?3 ")
    Integer updatefileStateByName(String receivestate, String filepath, String filename);

    @Query(value = "select id from Tb_oa_record")
    List<String> findIdAll();

//    @Query(value = "select Tb_oa_record from Tb_oa_record where id in (?1)")
    List<Tb_oa_record> findByIdIn(String[] s);

    Tb_oa_record findAllByFilename(String filename);
}
