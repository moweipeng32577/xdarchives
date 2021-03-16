package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_index_manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/6/25.
 */
public interface EntryIndexManageRepository extends JpaRepository<Tb_entry_index_manage,String>,
        JpaSpecificationExecutor<Tb_entry_index_manage> {


    Tb_entry_index_manage findByEntryid(String entryid);

    int deleteByEntryidIn(String[] entryids);

    @Query(value = "select archivecode from Tb_entry_index_accept where nodeid=?1 ORDER BY archivecode desc")
    List<String> findCodeByNodeid(String nodeid);

    List<Tb_entry_index_manage> findByEntryidIn(String[] entryids);

    List<Tb_entry_index_manage> findByArchivecode(String archivecode);

    @Modifying
    @Query(value = "insert into tb_entry_index_manage (EntryID,NodeID,Eleid,Title,FileNumber,ArchiveCode,Funds,Catalog,FileCode,InnerFile,FilingYear,EntryRetention,Organ,RecordCode,EntrySecurity,Pages,PageNo,FileDate,Responsible,Serial,FlagOpen,EntryStorage,DescriptionDate,DescriptionUser,fscount,kccount,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5) "
            + "select EntryID,NodeID,Eleid,Title,FileNumber,ArchiveCode,Funds,Catalog,FileCode,InnerFile,FilingYear,EntryRetention,Organ,RecordCode,EntrySecurity,Pages,PageNo,FileDate,Responsible,Serial,FlagOpen,EntryStorage,DescriptionDate,DescriptionUser,1,1,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5 from tb_entry_index_accept where entryid in ?1", nativeQuery = true)
    int moveindexes(String[] entryidData);

    @Query(value = "SELECT e.entryid FROM Tb_entry_index_manage e WHERE e.tdn.nodeid=?1")
    String[] findEntryidsByNodeid(String nodeid);
}
