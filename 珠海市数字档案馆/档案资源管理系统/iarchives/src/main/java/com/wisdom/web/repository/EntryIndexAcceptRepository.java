package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_index_accept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/6/24.
 */
public interface EntryIndexAcceptRepository extends JpaRepository<Tb_entry_index_accept,String>,
        JpaSpecificationExecutor<Tb_entry_index_accept> {

    Tb_entry_index_accept findByEntryid(String entryid);

    int deleteByEntryidIn(String[] entryids);

    @Query(value = "select archivecode from Tb_entry_index_accept where nodeid=?1 ORDER BY archivecode desc")
    List<String> findCodeByNodeid(String nodeid);

    List<Tb_entry_index_accept> findByEntryidIn(String[] entryids);

    @Query(value = "SELECT e.entryid FROM Tb_entry_index_accept e WHERE  e.nodeid=?1")
    String[] findEntryidsByNodeid(String nodeid);

    List<Tb_entry_index_accept> findByArchivecode(String archivecode);

    @Modifying
    @Query(value = "insert into tb_entry_index_accept(EntryID,ArchiveCode,Catalog,DescriptionDate,DescriptionUser,Eleid,EntryRetention,EntrySecurity,EntryStorage,FileCode,FileDate,FileNumber,FilingYear,Keyword,FlagOpen,fscount,Funds,InnerFile,kccount,Organ,PageNo,Pages,RecordCode,Responsible,Serial,Title,NodeID,Opendate,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5,Modifydate)" +
            " select EntryID,ArchiveCode,Catalog,DescriptionDate,DescriptionUser,Eleid,EntryRetention,EntrySecurity,EntryStorage,FileCode,FileDate,FileNumber,FilingYear,Keyword,FlagOpen,1,Funds,InnerFile,1,Organ,PageNo,Pages,RecordCode,Responsible,Serial,Title,NodeID,Opendate,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5,Modifydate " +
            "from tb_entry_index_manage where entryid in ?1", nativeQuery = true)
    int moveAccepts(String[] entryidData);
}
