package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_entry_index_capture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Rong on 2017/11/13.
 */
public interface SzhEntryIndexCaptureRepository extends JpaRepository<Szh_entry_index_capture, String>,JpaSpecificationExecutor<Szh_entry_index_capture> {

    Szh_entry_index_capture findByEntryid(String entryid);

    List<Szh_entry_index_capture> findByEntryidIn(String[] entryids);

    Page<Szh_entry_index_capture> findByEntryidIn(String[] entryidsData, Pageable pageRequest);

    List<Szh_entry_index_capture> findByNodeid(String nodeid);

    Page<Szh_entry_index_capture> findByNodeid(Pageable pageable, String nodeid);

    List<Szh_entry_index_capture> findByArchivecode(String archivecode);

    @Query(value = "select t from Szh_entry_index_capture t where entryid in (?1) and title like concat('%',?2,'%') escape '/'")
    List<Szh_entry_index_capture> MfindByEntryidInAndTitle(String[] entryid, String title);

    @Query(value = "select t from Szh_entry_index_capture t where entryid in (?1) and title like concat('%',?2,'%')")
    List<Szh_entry_index_capture> OfindByEntryidInAndTitle(String[] entryid, String title);

    @Query(value = "select nodeid from Szh_entry_index_capture where entryid=?1")
    String findNodeidByEntryid(String entryid);

    @Query(value = "select title from Szh_entry_index_capture where entryid=?1")
    String findTitleByEntryid(String entryid);

    @Query(value = "select archivecode from Szh_entry_index_capture where entryid=?1")
    String findArchivecodeByEntryid(String entryid);

    @Query(value = "select e from Szh_entry_index_capture e where e.nodeid=?1 and e.archivecode like concat(?2,'%')")
    List<Szh_entry_index_capture> findAllByNodeidAndArchivecodeLike(String nodeid, String archivecode);

    @Modifying
    @Transactional
    @Query(value = "update Szh_entry_index_capture set innerfile=?1,archivecode =?2,pageno=?3,pages=?4  where entryid=?5")
    Integer updateInfoByEntryid(String innerfile1, String newArchivecode, String pageno, String pages, String entryid);

    Integer deleteByEntryidIn(String[] entryidData);

    @Query(value = "select archivecode from Szh_entry_index_capture where nodeid=?1 ORDER BY archivecode desc")
    List<String> findCodeByNodeid(String nodeid);

    @Query(value = "select archivecode from Szh_entry_index_capture where archivecode = ?1 and nodeid=?2 ORDER BY archivecode desc")
    List<String> findArchivecodeByNodeid(String archivecode, String nodeid);

    @Modifying
    @Transactional
    @Query(value = "update Szh_entry_index_capture set filingyear = ?2 where entryid = ?1")
    Integer updateYear(String entryid, String year);

    @Modifying
    @Transactional
    @Query(value = "update Szh_entry_index_capture set entryretention = ?2 where entryid = ?1")
    Integer updateRetention(String entryid, String retention);

    @Modifying
    @Transactional
    @Query(value = "update Szh_entry_index_capture set organ = ?2 where entryid = ?1")
    Integer updateOrgan(String entryid, String organ);

    @Modifying
    @Transactional
    @Query(value = "update Szh_entry_index_capture set EleID = ?1 where entryid = ?2")
    Integer updateEleId(String count, String entryid);

    @Query(value = "select entryid from Szh_entry_index_capture where archivecode=?1 and nodeid in (?2)")
    List<String> findEntryidByArchivecodeAndNodeidIn(String archivecode, String[] childrenNodeidArr);

    @Query(value = "select e from Szh_entry_index_capture e where e.nodeid in (?1)")
    List<Szh_entry_index_capture> getByNodeidIn(String[] nodeIds);

    Integer deleteByNodeidIn(String[] nodeids);

    @Query(value = "select t from Szh_entry_index_capture t where t.archivecode like concat(?1,'%') and t.nodeid=?2 order by t.archivecode")
    List<Szh_entry_index_capture> findAllByArchivecodeLikeAndNodeidOrderByArchivecode(String archivecode, String nodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set pages=?2 where archivecode=?1 and nodeid=?3")
    int updatePagesByArchivecode(String archivecode, String pages, String nodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set nodeid=?1,archivecode = null  where archivecode like concat(?2,'%')  and nodeid=?4 ")
    Integer updateNodeidAndArchivecode(String targetNodeid, String archivecode, String jnNodeid);

    @Query(value = "select t from Szh_entry_index_capture t where nodeid=?3 and archivecode like concat(?1,'%') and filecode>?2 and innerfile is null order by archivecode")
    List<Szh_entry_index_capture> findAllByArchivecodeLikeAndNext(String archivecode, String filecode, String nodeid);

    @Query(value = "select t from Szh_entry_index_capture t where archivecode like concat(?1,'%')and recordcode>?2 and nodeid=?3  order by archivecode")
    List<Szh_entry_index_capture> findAllByArchivecodeLikeAndNextRecord(String archivecode, String recordcode, String nodeid);

    @Query(value = "select t from Szh_entry_index_capture t where  archivecode like concat(?1,'%') and filecode=?2 and (nodeid=?3 or nodeid=?4)  order by archivecode")
    List<Szh_entry_index_capture> findAllByArchivecodeAndFilecode(String archivecode, String filecode, String nodeid, String jnNodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set filecode=?1,archivecode =?2  where archivecode=?3  and (nodeid=?4 or nodeid=?5)")
    Integer updateFilecodeAndArchivecode(String filecode, String newArchivecode, String oldArchivecode, String nodeid, String jnNodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set filecode=?1,archivecode =?2  where archivecode=?3  and nodeid=?4 ")
    Integer updateFilecodeAndArchivecode(String filecode, String newArchivecode, String oldArchivecode, String nodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set recordcode=?1,archivecode =?2  where archivecode=?3 and nodeid=?4")
    Integer updateRecordcodeAndArchivecode(String recordcode, String newArchivecode, String oldArchivecode, String nodeid);

    @Query(value = "select e from Szh_entry_index_capture e where e.nodeid=?2 and e.archivecode =?1 order by e.descriptiondate")
    List<Szh_entry_index_capture> findCopyByArchivecode(String archivecode, String nodeid);

    //查找档号重复的案卷的子件
    @Query(value = "select t from Szh_entry_index_capture t where archivecode like concat(?1,'%') and filecode=?2 and innerfile is not null and nodeid=?3 order by archivecode")
    List<Szh_entry_index_capture>  findAllByCopyArchivecode(String parentArchivecode, String filecodeY, String jnNodeid);

    Integer deleteByEntryid(String entryid);

    //删除所有卷内文件
    @Modifying
    @Query(value = "delete from Szh_entry_index_capture  where archivecode like concat(?1,'%')")
    Integer deleteAllLikeArchivecode(String archivecode);

    //查找相关的卷内文件的entryid
    @Query(value = "select t.entryid from Tb_entry_index t where t.archivecode like concat(?1,'%') and nodeid=?2 ")
    List<String> findAllByNodeid(String archivecode, String nodeid);

    @Query(value = "select t from Szh_entry_index_capture t where archivecode like concat(?1,'%') and innerfile>?2 and innerfile is not null  and nodeid=?3  order by archivecode")
    List<Szh_entry_index_capture> findInnerByArchivecodeLikeAndNext(String archivecode, String innerfile, String nodeid);

    @Query(value = "select t from Szh_entry_index_capture t where entryid in (select entryid from Szh_callout_capture where calloutid = ?1)")
    List<Szh_entry_index_capture> findEntryBycallout(String calloutId);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set innerfile=?1,archivecode =?2  where archivecode=?3 and nodeid=?4 ")
    Integer updateInnerfileAndArchivecode(String innerfile1, String newArchivecode, String archivecodeOne, String nodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set filecode=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecode(String filecodeY1, String newArchivecode, String archivecodeOne, String copyEntryid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set recordcode=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecodeAndRecordcode(String recordcode, String newArchivecode, String archivecode, String entryid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set innerfile=?1,archivecode =?2  where archivecode=?3 and entryid=?4")
    Integer updateCopyArchivecodeAndInnerfile(String innerfile, String newArchivecode, String archivecode, String entryid);

    Szh_entry_index_capture findEntryByArchivecode(String archivecode);

    @Query(value = "select count(t) from Szh_entry_index_capture t where archivecode like concat(?1,'%')")
    Integer countInnerfileNumberByArchivecode(String archivecode);

    @Query(value = "select sum(pages) from Szh_entry_index_capture t where archivecode like concat(?1,'%')")
    Object sumInnerfilePagesByArchivecode(String archivecode);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set pageno=?2 where entryid=?1")
    int updatePagenoByEntryid(String zjEntryid, String zjPageNo);

    @Query(value = "select count(entryid) from Szh_entry_index_capture  where archivecode=?1 and nodeid=?2")
    Long findEntryidCount(String archivecode, String nodeid);
    @Query(value = "select entryid from Szh_entry_index_capture where archivecode=?1 and nodeid=?2")
    String findAllByArchivecode(String archivecode, String nodeid);

    @Modifying
    @Query(value = "update Szh_entry_index_capture set eleid=?1 where entryid=?2")
    Integer updateEleidByEntryid(String ELEID, String entryid);

    @Query(value = "select entryid from Szh_entry_index_capture where nodeid=?1")
    String[] FindEntryidByNodeid(String nodeid);

    Szh_entry_index_capture findByArchivecodeAndNodeidAndUserid (String arch,String nodeid,String userid);

    @Query(value = "select t from Szh_entry_index_capture t where entryid in (select entryid from Szh_callout_capture where calloutid in(?1))")
    List<Szh_entry_index_capture>  findEntryByCalloutIds(List<String> calloutIds);

    @Modifying
    @Query(value = "delete from Szh_entry_index_capture  where entryid in (select entryid from Szh_callout_capture where calloutid in(?1))")
    Integer deleteEntryByCalloutIds(String[] calloutIds);
}