package com.wisdom.web.repository;

import com.wisdom.web.entity.Entry;
import com.wisdom.web.entity.Tb_entry_index;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.transaction.Transactional;

/**
 * Created by yl on 2017/10/26.
 */
public interface EntryIndexRepository extends JpaRepository<Tb_entry_index,String>,JpaSpecificationExecutor<Tb_entry_index> {
	
//    Page<Tb_entry_index> findByNodeid(Pageable pageable, String nodeid);

//    @Query(value = "select tb_entry_index from Tb_entry_index tb_entry_index where  tb_entry_index.nodeid = ?1 and" +
//            " tb_entry_index.retention <> '长久'")
//    Page<Tb_entry_index> findByNodeid( String nodeid,Pageable pageable);

//    @Query(value = "select * from tb_entry_index where DATE_ADD(FileDate, INTERVAL left(entryretention,2) YEAR) < date(now()) " +
//            " and entryretention !='永久' and NodeID = ?1 ORDER BY ?#{#pageable}", countQuery = "select count(*) from " +
//            " tb_entry_index where DATE_ADD(FileDate, INTERVAL left(entryretention,2) YEAR) < date(now()) " +
//            " and entryretention !='永久' and NodeID = ?1 ", nativeQuery = true)
//    Page<Tb_entry_index> findByNodeid(String nodeid,Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index set entryretention=?1 where entryid in (?2) ")
    Integer updateEntryIndex(String approvaldate, String[] entryid);
    
    @Query(value = "select title from Tb_entry_index where entryid=?1")
    String findTitleByEntryid(String entryid);
    
    @Query(value = "select count(u) from Tb_entry_index u where nodeid in (?1)")
    String findCountByNodeidIn(String[] nodeids);
    
	@Modifying
	@Transactional
	@Query(value = "update Tb_entry_index set opendate = ?1 where entryid in (?2)")
	Integer updateOpenDateByEntryidIn(String opendate, String[] entryid);
	
    @Query(value = "select u from Tb_entry_index u where nodeid = ?1")
    List<Tb_entry_index> findInfoByNodeid(String nodeid);
    
	@Modifying
	@Transactional
	@Query(value = "update Tb_entry_index set archivecode =?1,pageno=?2,pages=?3  where entryid=?4")
	Integer updateInfoByEntryid(String newArchivecode, String pageno, String pages, String entryid);

    Tb_entry_index findByEntryid(String entryid);

    @Query(value = "select t from Tb_entry_index t where t.entryid in (?1) order by t.sortsequence asc,t.serial asc, filenumber asc, descriptiondate asc, title asc")
    List<Tb_entry_index> findByEntryidInOrderByInfo(String[] entryids);
    
    @Modifying
    @Transactional
    Integer deleteByEntryidIn(String[] entryidData);

    @Modifying
    @Query(value = "delete from tb_entry_index where entryid in ?1" , nativeQuery = true)
    Integer deleteByEntryids(String[] entryidData);

    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index set sortsequence=?1 where entryid=?2")
    Integer updatesortsequenceByEntryid(Integer sortsequence, String entryid);

    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index set entrystorage=?1 where entryid=?2")
    Integer updateEntrystorage(String entrystorage,String entryid);


    Page<Tb_entry_index> findByEntryidIn(String[] entryids,Pageable pageable);

    @Query(value = "select t from Tb_entry_index t where entryid in (?1) order by t.entryretention,t.archivecode")
    List<Tb_entry_index> findByEntryidIn(String[] entryids);

    @Query(value = "select * from tb_entry_index t where t.nodeid=?1 and  t.entryid in (?2)",nativeQuery = true)
    List<Tb_entry_index> getWithNodeidAndEntryid(String nodeid, String[] entryids);

    List<Tb_entry_index> findByKccountAndEntryidIn(String kccount,String[] entryids);
    
    @Query(value = "select t from Tb_entry_index t where nodeid in (?1)")
    List<Tb_entry_index> findByNodeidIn(String[] nodeid);

    @Query(value = "select entryid from Tb_entry_index where nodeid=?1")
    List<String> findByNodeidIn(String nodeid);

    Page<Tb_entry_index> findByNodeid(Pageable pageable, String nodeid);

    List<Tb_entry_index> findByArchivecode(String archivecode);
    
    @Query(value = "select t from Tb_entry_index t where archivecode = ?1 and nodeid=?2")
    List<Tb_entry_index> findByArchivecodeAndNodeid(String archivecode,String nodeid);
    
    @Query(value = "select t from Tb_entry_index t where entryid in (?1) and title like concat('%',?2,'%') escape '/'")
    List<Tb_entry_index> MfindByEntryidInAndTitle(String[] entryid, String title);
    
    @Query(value = "select t from Tb_entry_index t where entryid in (?1) and title like concat('%',?2,'%')")
    List<Tb_entry_index> OfindByEntryidInAndTitle(String[] entryid, String title);
    
//    @Query(value = "select t from Tb_entry_index t where funds = ?3 and nodeid = ?1 and entryretention in (?2)")
//    List<Tb_entry_index> findByEntryretention(String nodeid, String[] entryretention, String funds);
    
    @Query(value = "select count(t) from Tb_entry_index t where funds = ?3 and nodeid in (?1) and entryretention in (?2)")
    String findByEntryretention(String[] nodeid, String[] entryretention, String funds);
    
    @Query(value = "select count(u) from Tb_entry_index u where funds = ?2 and nodeid = ?1")
    String findByFunds(String nodeid, String funds);
    
    @Query(value = "select count(u) from Tb_entry_index u where funds = ?2 and nodeid in (?1)")
    String findByFundsIn(String[] nodeid, String funds);

    @Query(value = "select tdn.nodeid from Tb_entry_index where entryid=?1")
    String findNodeidByEntryid(String entryid);

    @Query(value = "select archivecode from Tb_entry_index where entryid=?1")
    String findArchivecodeByEntryid(String entryid);
    
    @Query(value = "select archivecode from Tb_entry_index where archivecode = ?1 and nodeid=?2")
    List<String> findArchivecodeByNodeid(String archivecode, String nodeid);
    
    @Query(value = "select archivecode from Tb_entry_index where nodeid=?1")
    List<String> findCodeByNodeid(String nodeid);

    @Query(value = "select archivecode from Tb_entry_index where nodeid=?1 and archivecode=?2")
    List<String> findCodeByNodeidAndCode(String nodeid,String archivecode);
    
    List<Tb_entry_index> findAllByArchivecodeLike(String archivecode);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and nodeid=?2 order by archivecode")
    List<Tb_entry_index> findAllByArchivecodeLikeAndNodeidOrderByArchivecode(String archivecode,String nodeid);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and (nodeid=?2 or nodeid=?3) order by archivecode")
    List<Tb_entry_index> findAllByArchivecodeLikeAndNodeidOrderByArchivecode(String archivecode,String nodeid,String jnNodeid);

    @Query(value = "select t from Tb_entry_index t where nodeid=?1 and archivecode like concat(?2,'%')")
    List<Tb_entry_index> findAllByNodeidAndArchivecodeLike(String nodeid,String archivecode);

    Page<Tb_entry_index> findByNodeidAndFlagopenIn(Pageable pageable, String nodeid,String[] open);

//    @Query(value="select * from tb_entry_index where entryid in (select entryid from tb_entry_bookmarks where UserId=?1 ) ORDER BY ?#{#pageable}", nativeQuery=true)
//    Page<Tb_entry_index> findAllByUserBookmark(String userid,Pageable pageable);

    @Query(value = "select t from Tb_entry_index t where entryid =?1 order by archivecode")
    Tb_entry_index findAllByAddstate(String entryid);

    @Query(value = "select t from Tb_entry_index t where entryid in ?1 order by archivecode")
    List<Tb_entry_index> findAllByAddstateIn(String[] entryid);

    Page<Tb_entry_index> findByFlagopenIn(String[] openType, Pageable pageable);

    @Query(value = "select entryid from Tb_entry_index where archivecode=?1 and nodeid in (?2)")
    List<String> findEntryidByArchivecodeAndNodeidIn(String archivecode,String[] childrenNodeidArr);

    @Modifying
    @Query(value = "insert into tb_entry_index (EntryID,NodeID,Eleid,Title,FileNumber,ArchiveCode,Funds,Catalog,FileCode,InnerFile,FilingYear,EntryRetention,Organ,RecordCode,EntrySecurity,Pages,PageNo,FileDate,Responsible,Serial,FlagOpen,EntryStorage,DescriptionDate,DescriptionUser,fscount,kccount,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5) "
    			 + "select EntryID,NodeID,Eleid,Title,FileNumber,ArchiveCode,Funds,Catalog,FileCode,InnerFile,FilingYear,EntryRetention,Organ,RecordCode,EntrySecurity,Pages,PageNo,FileDate,Responsible,Serial,FlagOpen,EntryStorage,DescriptionDate,DescriptionUser,1,1,sparefield1,sparefield2,sparefield3,sparefield4,sparefield5 from tb_entry_index_capture where entryid in ?1", nativeQuery = true)
    int moveindexes(String[] entryidData);

    @Modifying
    @Query(value = "update Tb_entry_index set pages=?2 where archivecode=?1 and nodeid=?3")
    int updatePagesByArchivecode(String archivecode,String pages,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set pageno=?2 where entryid=?1")
    int updatePagenoByEntryid(String zjEntryid,String zjPageNo);
    
    @Modifying
    @Query(value = "update Tb_entry_index set nodeid = ?1 where entryid = ?2")
    Integer updateNodeid(String newNodeid, String entryid);

    @Modifying
    @Query(value = "update Tb_entry_index set nodeid=?1,archivecode = null  where archivecode like concat(?2,'%')  and nodeid=?3 ")
    Integer updateNodeidAndArchivecode(String targetNodeid,String archivecode,String jnNodeid);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and filecode>?2 and nodeid=?3 and innerfile is null order by archivecode")
    List<Tb_entry_index> findAllByArchivecodeLikeAndNext(String archivecode,String filecode,String nodeid);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and recordcode>?2 and nodeid=?3 order by archivecode")
    List<Tb_entry_index> findAllByArchivecodeLikeAndNextRecord(String archivecode, String recordcode,String nodeid);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and innerfile>?2 and innerfile is not null and nodeid=?3 order by archivecode")
    List<Tb_entry_index> findInnerByArchivecodeLikeAndNext(String archivecode,String innerfile,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set filecode=?1,archivecode =?2  where archivecode=?3 and (nodeid=?4 or nodeid=?5)")
    Integer updateFilecodeAndArchivecode(String filecode,String newArchivecode, String oldArchivecode,String nodeid,String jnNodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set filecode=?1,archivecode =?2  where archivecode=?3 and nodeid=?4 ")
    Integer updateFilecodeAndArchivecode(String filecode,String newArchivecode, String oldArchivecode,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set recordcode=?1,archivecode =?2  where archivecode=?3 and nodeid=?4 ")
    Integer updateRecordcodeAndArchivecode(String recordcode,String newArchivecode, String oldArchivecode,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set innerfile=?1,archivecode =?2  where archivecode=?3 and nodeid=?4 ")
    Integer updateInnerfileAndArchivecode(String innerfile1,String newArchivecode,String archivecodeOne,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set innerfile=?1,archivecode =?2,pageno=?5  where archivecode=?3 and nodeid=?4 ")
    Integer updateInnerfileAndArchivecodeAndPageNo(String innerfile1,String newArchivecode,String archivecodeOne,String nodeid,String pageNo);

    @Modifying
    @Query(value = "update Tb_entry_index set filecode=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecode(String filecodeY1,String newArchivecode,String archivecodeOne,String copyEntryid);

    @Modifying
    @Query(value = "update Tb_entry_index set recordcode=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecodeAndRecordcode(String recordcode,String newArchivecode,String archivecode,String entryid);

    @Modifying
    @Query(value = "update Tb_entry_index set innerfile=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecodeAndInnerfile(String innerfile,String newArchivecode,String archivecode,String entryid);

    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and filecode=?2 and (nodeid=?3 or nodeid=?4) order by archivecode")
    List<Tb_entry_index> findAllByArchivecodeAndFilecode(String archivecode,String filecode,String nodeid,String jnNodeid);

    @Query(value = "select * from tb_entry_index t where t.archivecode=?1 and t.nodeid=?2 order by t.descriptiondate",nativeQuery = true)
    List<Tb_entry_index> findDoubleArchivecodes(String archivecode,String nodeid);

    //查找档号重复的案卷的子件
    @Query(value = "select t from Tb_entry_index t where archivecode like concat(?1,'%') and filecode=?2 and innerfile is not null and nodeid=?3 order by archivecode")
    List<Tb_entry_index>  findAllByCopyArchivecode(String parentArchivecode,String filecodeY,String jnNodeid);

    Integer deleteByEntryid(String entryid);

    //删除所有卷内文件
    @Modifying
    @Query(value = "delete from Tb_entry_index  where archivecode like concat(?1,'%') ")
    Integer deleteAllLikeArchivecode(String archivecode);

    //查找相关的卷内文件的entryid
    @Query(value = "select t.entryid from Tb_entry_index t where t.archivecode like concat(?1,'%') and nodeid=?2 ")
    List<String> findAllByNodeid(String archivecode,String nodeid);

    //实体库房
    @Query(value = "select * from tb_entry_index where entryid not in (select entry from st_storage where entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getStorageNoEntries();
    /*@Query(value = "select * from tb_entry_index where nodeid=?1 and entryid not in (select entry from st_storage where entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getStorageNoEntries(String nodeid);*/

    //实体库房
    @Query(value = "select * from tb_entry_index where entryid  in (select entry from st_storage where storestatus=?1 and entry is not null)",nativeQuery=true)
    List<Tb_entry_index> findStorageEntries(String staIn);
    /*@Query(value = "select * from tb_entry_index where nodeid=?1 and entryid  in (select entry from st_storage where storestatus='已入库' and entry is not null)",nativeQuery=true)
    List<Tb_entry_index> findStorageEntries(String nodeid);*/

    //实体库房
    @Query(value = "select * from tb_entry_index where  entryid  in (select entry from st_storage where storestatus=?1 and  entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getOutwares(String staOut);
    /*Query(value = "select * from tb_entry_index where nodeid=?1 and entryid  in (select entry from st_storage where storestatus='已出库' and  entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getOutwares(String nodeid);*/

    //实体库房
    @Query(value = "select * from tb_entry_index where  entryid  in (select entry from st_storage where storestatus=?1 and entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getInwares(String staIn);
    /*@Query(value = "select * from tb_entry_index where nodeid=?1 and entryid  in (select entry from st_storage where storestatus='已入库' and entry is not null)",nativeQuery=true)
    List<Tb_entry_index> getInwares(String nodeid);*/

    @Query(value = "select t from Tb_entry_index t where nodeid in (?1)")
    List<Tb_entry_index> getByNodeidIn(String[] nodeIds);

    Tb_entry_index findEntryByArchivecode(String nextCode);

    @Query(value = "select count(t) from Tb_entry_index t where archivecode like concat(?1,'%')")
    Integer countInnerfileNumberByArchivecode(String archivecode);

    @Query(value = "select sum(pages) from Tb_entry_index  where archivecode like concat(?1,'%')")
    Object sumInnerfilePagesByArchivecode(String archivecode);

    @Modifying
    @Query(value = "update Tb_entry_index set flagopen=?1 where entryid in (?2)")
    Integer setOpenLock(String flagopen,String[] entryids);

    @Query(value = "select count(entryid) from Tb_entry_index  where archivecode=?1 and nodeid=?2")
    Long findEntryidCount(String archivecode,String nodeid);

    @Query(value = "select entryid from Tb_entry_index where archivecode=?1 and nodeid=?2")
    List<String> findAllByArchivecode(String archivecode,String nodeid);

    @Modifying
    @Query(value = "update Tb_entry_index set eleid=?1 where archivecode=?2")
    Integer updateEleid(String ELEID,String ARCHIVECODE);

    @Modifying
    @Query(value = "update Tb_entry_index set pages=?1 where entryid=?2")
    Integer updatePagesAndEleid(String PAGES,String ENTRYID);

    @Modifying
    @Transactional
    @Query(value = "update Tb_entry_index set eleid=?1 where entryid=?2")
    Integer updateEleidByEntryid(String ELEID,String entryid);

    @Modifying
    @Query(value = "update Tb_entry_index set pages=?1 where entryid=?2")
    Integer updatePagesByEntryid(String pages,String entryid);
    
    @Query(value = "select archivecode from Tb_entry_index where entryid in (?1)")
    String[] findArchivecodeByEntryid(String[] entryid);
    
    @Query(value = "select entryid from Tb_entry_index where nodeid = ?1")
    String[] findEntryidByNodeid(String nodeid);
    
	@Query(value = "select entryid from Tb_entry_index where nodeid in (?1)")
	String[] findEntryidByNodeidIn(String[] nodeid);
	
	@Query(value = "select distinct tdn.nodeid from Tb_entry_index where entryid in (?1)")
	String[] findNodeidByEntryidIn(String[] entryid);

    @Query(value = "select t from Tb_entry_index t where entryid in (select entryid from Tb_borrowmsg where entryid =?1 and state = '未归还')")
    List<Tb_entry_index> findEntryByBorrowmsg(String entryid);

    @Query(value = "select t from Tb_entry_index t where entryid in (select entryid from Tb_borrowmsg where entryid in ?1 and state = '未归还')")
    List<Tb_entry_index> findEntryByBorrowmsg(String[] entryid);

    //数字化
    @Query(value = "select * from tb_entry_index where archivecode in (select archivecode from szh_batch_entry where batchcode in (?1))",nativeQuery=true)
    List<Tb_entry_index> findBatchcodesByAll(String[] batchcodes);

    @Query(nativeQuery=true, value = "select title,entryretention from tb_entry_index where title is not null and entryretention is not null limit 0, 40000")
    List<Object []> findTitleNotNullByAll();

    @Query(nativeQuery=true, value = "select distinct nodeid from tb_entry_index where  entryretention is not null and entryretention!=?1")
    String[] findNodeidByEntryretention(String entryretention);

    List<Tb_entry_index> findByArchivecodeIn(String[] dhCode);

    @Query(value = "select * from tb_entry_index where  entryid  in(select t1.entry from (SELECT o.waretime,os.storages_stid,o.waretype,s.storestatus,s.entry FROM st_outware_storages os  " +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
            "  storestatus = ?1 and s.entry is not null ) t1,(SELECT max(o.waretime) waretime,os.storages_stid FROM st_outware_storages os  " +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
            "  storestatus = ?1 and s.entry is not null  GROUP BY os.storages_stid )t2 where t1.waretime =t2.waretime " +
            "  and t1.storages_stid = t2.storages_stid  and waretype != '转递出库')",nativeQuery=true)
    List<Tb_entry_index> getreturnOutwares(String staOut);

    @Query(value = "select count(*) from tb_entry_index where  entryid  in(select t1.entry from (SELECT o.waretime,os.storages_stid,o.waretype,s.storestatus,s.entry FROM st_outware_storages os  " +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
            "  storestatus = ?1 and s.entry is not null ) t1,(SELECT max(o.waretime) waretime,os.storages_stid FROM st_outware_storages os  " +
            "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
            "  storestatus = ?1 and s.entry is not null  GROUP BY os.storages_stid )t2 where t1.waretime =t2.waretime " +
            "  and t1.storages_stid = t2.storages_stid  and waretype != '转递出库')",nativeQuery=true)
    int getreturnOutwaresNums(String staOut);

    @Query(value = "select count(*) from tb_entry_index where  entryid  in (select entry from st_storage where storestatus=?1 and  entry is not null)",nativeQuery=true)
    int getOutwaresNums(String staOut);

    @Query(nativeQuery=true, value = "select tcl.classname from tb_entry_index tei \n" +
            "LEFT JOIN tb_data_node tdn on tei.nodeid = tdn.nodeid\n" +
            "LEFT JOIN tb_classification tcl on tdn.classid = tcl.classid\n" +
            "LEFT JOIN tb_funds tfs on tei.funds = tfs.funds\n" +
            "where tei.funds is not null and tei.funds != '' and classname!=''\n" +
            "GROUP BY tcl.classname")
    String[] findAllClassId();

    @Query(nativeQuery=true, value = "select tei.funds from tb_entry_index tei \n" +
            "LEFT JOIN tb_data_node tdn on tei.nodeid = tdn.nodeid\n" +
            "LEFT JOIN tb_classification tcl on tdn.classid = tcl.classid\n" +
            "LEFT JOIN tb_funds tfs on tei.funds = tfs.funds\n" +
            "where tei.funds is not null and tei.funds != '' and classname!=''\n" +
            "and tcl.classname=?1 GROUP BY tei.funds")
    String[] findFundsByClassName(String id);

    @Query(nativeQuery=true, value = "select tei.filingyear from tb_entry_index tei \n" +
            "LEFT JOIN tb_data_node tdn on tei.nodeid = tdn.nodeid\n" +
            "LEFT JOIN tb_classification tcl on tdn.classid = tcl.classid\n" +
            "LEFT JOIN tb_funds tfs on tei.funds = tfs.funds\n" +
            "where tei.funds is not null and tei.funds != '' and classname!='' and tei.funds=?1 \n" +
            "and tcl.classname=?2 GROUP BY tei.filingyear")
    String[] findFilingYearByFundsAndClassName(String fundsId,String className);

    @Query(value = "select count(entryid) from Tb_entry_index t where nodeid in (select nodeid from Tb_data_node where organid=?1)")
    Long getNumByNodeid(String nodeid);

    @Query(value = "select count(entryid) from Tb_entry_index t where nodeid in (select nodeid from Tb_data_node where organid=?1) and entryid in (select entryid from Tb_transdoc_entry where status = ?2)")
    Long getNumByNodeidAndEntryid(String nodeid,String state);

    @Query(value = "select distinct substring(filedate,0,5) filedate from tb_entry_index order by filedate", nativeQuery=true)
    List<String> getFiledates();

    @Query(value = "select count(entryid) from Tb_entry_index t where t.filedate like concat(?1,'%')")
    Long getNumByFiledateLike(String filedate);

    @Query(value = "select count(entryid) from tb_entry_index t where filedate like concat(?1,'%') and entryid in (select entryid from tb_transdoc_entry where status = ?2)", nativeQuery=true)
    Long getNumByFiledateAndEntryidLike(String filedate,String state);

    @Query(value = "select count(entryid) from tb_entry_index t where filedate =?1 and entryid in (select entryid from tb_transdoc_entry where status = ?2)", nativeQuery=true)
    Long getNumByFiledateAndEntryid(String filedate,String state);

    @Query(value = "select count(entryid) from Tb_entry_index t where t.filedate =?1")
    Long getNumByFiledate(String filedate);

    @Query(value = "select count(entryid) from Tb_entry_index")
    Long getAllCount();

    List<Tb_entry_index> findByEntryidIn(String[] entryids, Sort sort);


}